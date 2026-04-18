package com.example.projetmaison.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.projetmaison.Api
import com.example.projetmaison.models.DeviceResponse
import com.example.projetmaison.models.HouseData
import com.example.projetmaison.R

// Activité principale pour la gestion des maisons
class HouseActivity : AppCompatActivity() {

    // ListeView pour afficher les maisons partagées
    private lateinit var listViewHouses: ListView
    // Carte affichant la maison principale du propriétaire
    private lateinit var ownerHouseCard: CardView
    // Titre de la maison principale
    private lateinit var ownerHouseTitle: TextView
    // Nombre d'appareils dans la maison principale
    private lateinit var deviceCount: TextView
    // Bouton pour rafraîchir la liste
    private lateinit var buttonRefresh: Button
    // Bouton pour gérer la maison principale
    private lateinit var buttonManageOwnerHouse: Button
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
        ownerHouseCard.visibility = View.GONE

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
            getSharedPreferences("APP_PREFS", MODE_PRIVATE)
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
                        // Séparer la maison principale (propriétaire) et les maisons partagées
                        mainHouse = response.find { it.owner }
                        sharedHouses = response.filter { !it.owner }

                        // Affichage maison principale
                        if (mainHouse != null) {
                            ownerHouseCard.visibility = View.VISIBLE
                            ownerHouseTitle.text = "Maison ${mainHouse!!.houseId}"
                            // Charger dynamiquement le nombre de périphériques
                            val token = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
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
                            ownerHouseCard.visibility = View.GONE
                        }

                        // Affichage des maisons partagées dans le ListView
                        val adapter = ArrayAdapter<String>(
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
                403 -> Toast.makeText(this, "Accès interdit (token invalide)", Toast.LENGTH_LONG).show()
                500 -> Toast.makeText(this, "Erreur serveur", Toast.LENGTH_LONG).show()
                else -> Toast.makeText(this, "Erreur: $code", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadHouse() {
        val token = getSharedPreferences("APP_PREFS",MODE_PRIVATE)
            .getString("TOKEN", null)

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