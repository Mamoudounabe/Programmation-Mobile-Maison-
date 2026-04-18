package com.example.projetmaison

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class HouseActivity : AppCompatActivity() {

    private lateinit var listViewHouses: android.widget.ListView
    private lateinit var ownerHouseCard: androidx.cardview.widget.CardView
    private lateinit var ownerHouseTitle: android.widget.TextView
    private lateinit var deviceCount: android.widget.TextView
    private lateinit var buttonRefresh: android.widget.Button
    private lateinit var buttonManageOwnerHouse: android.widget.Button
    private var mainHouse: HouseData? = null
    private var sharedHouses: List<HouseData> = emptyList()

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

        // Déconnexion
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
                        // Séparer la maison principale (propriétaire) et les maisons partagées
                        mainHouse = response.find { it.owner }
                        sharedHouses = response.filter { !it.owner }

                        // Affichage maison principale
                        if (mainHouse != null) {
                            ownerHouseCard.visibility = android.view.View.VISIBLE
                            ownerHouseTitle.text = "Maison ${mainHouse!!.houseId}"
                            // Charger dynamiquement le nombre de périphériques
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

                        // Affichage des maisons partagées dans le ListView
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