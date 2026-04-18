package com.example.projetmaison

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

// Activité principale pour la gestion des maisons
class HouseActivity : AppCompatActivity() {

    // ListeView pour afficher les maisons partagées
    private lateinit var listViewHouses: android.widget.ListView
    // Carte affichant la maison principale du propriétaire
    private lateinit var ownerHouseCard: androidx.cardview.widget.CardView
    // Titre de la maison principale
    private lateinit var ownerHouseTitle: android.widget.TextView
    // Nombre d'appareils dans la maison principale
    private lateinit var deviceCount: android.widget.TextView
    // Bouton pour rafraîchir la liste
    private lateinit var buttonRefresh: android.widget.Button
    // Bouton pour gérer la maison principale
    private lateinit var buttonManageOwnerHouse: android.widget.Button
    // Données de la maison principale
    private var mainHouse: HouseData? = null
    // Liste des maisons partagées
    private var sharedHouses: List<HouseData> = emptyList()

    // Méthode appelée à la création de l'activité
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_house)

        listViewHouses = findViewById(R.id.listViewHouses)

        ownerHouseCard = findViewById(R.id.ownerHouseCard)
        ownerHouseTitle = findViewById(R.id.ownerHouseTitle)
        deviceCount = findViewById(R.id.deviceCount)
        buttonRefresh = findViewById(R.id.buttonRefresh)
        buttonManageOwnerHouse = findViewById(R.id.buttonManageOwnerHouse)

        // Déconnexion : bouton pour se déconnecter de l'application
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            logout()
        }

        // Par défaut, cacher la CardView principale
        ownerHouseCard.visibility = android.view.View.GONE

        loadHouse()

        listViewHouses = findViewById(R.id.listViewHouses)

        // Déconnexion
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            logout()
        }





        loadHouse()
    }

    private fun logout() {
            // Supprimer token + éventuelles infos de session
            getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
                .edit()
                .remove("TOKEN")
                .apply()

        // Revenir à l'écran login et vider la pile (empêche de revenir avec "back")
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun houseSucces(code: Int, response: List<HouseData>?) {
        runOnUiThread {
            when (code) {
                200 -> {
                    if (response != null) {
                        mainHouse = response.find { it.owner }
                        sharedHouses = response.filter { !it.owner }

                        if (mainHouse != null) {
                            ownerHouseCard.visibility = android.view.View.VISIBLE
                            ownerHouseTitle.text = "Maison ${mainHouse!!.houseId}"
                            val token = getSharedPreferences("APP_PREFS",Context.MODE_PRIVATE)
                                .getString("TOKEN", null)
                            Api().get<DeviceResponse>(
                                "https://polyhome.lesmoulinsdudev.com/api/houses/${mainHouse!!.houseId}/devices",
                                { _, deviceResponse ->
                                    val count = deviceResponse?.devices?.size ?: 0
                                    runOnUiThread {
                                        deviceCount.text ="Périphériques : $count"
                                    }
                                },
                                token
                            )

                            buttonRefresh.setOnClickListener {
                                // Action de rafraîchissement (recharger les maisons)
                                loadHouse()
                            }
                            buttonManageOwnerHouse.setOnClickListener {
                                val intent = Intent(this, HouseAccesActivity::class.java)
                                intent.putExtra("HOUSE_ID", mainHouse!!.houseId)
                                startActivity(intent)
                            }
                        } else {
                            ownerHouseCard.visibility = android.view.View.GONE
                        }

                        val adapter = android.widget.ArrayAdapter<String>(
                            this,
                            android.R.layout.simple_list_item_1,
                            sharedHouses.map { "Maison ${it.houseId}" }
                        )
                        listViewHouses.adapter = adapter
                        listViewHouses.setOnItemClickListener { _, _, position, _ ->
                            val house = sharedHouses[position]
                            val intent = Intent(this, HouseAccesActivity::class.java)
                            intent.putExtra("HOUSE_ID", house.houseId)
                            startActivity(intent)
                        }
                    }
                }
                // Suppression des Toasts pour les autres cas
            }
        }
    }

    private fun loadHouse() {
        val token = getSharedPreferences("APP_PREFS",MODE_PRIVATE)
            .getString("TOKEN", null)

        // Suppression du Toast de vérification

        if (token.isNullOrBlank()) {
            // Si pas de token => renvoyer vers login (comme déconnexion)
            logout()
            return
        }

        Api().get<List<HouseData>>(
            "https://polyhome.lesmoulinsdudev.com/api/houses",
            ::houseSucces,
            token
        )
    }
}