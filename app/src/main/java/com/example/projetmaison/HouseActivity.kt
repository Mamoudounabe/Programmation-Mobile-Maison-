package com.example.projetmaison

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HouseActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_house)

        recycler = findViewById(R.id.recyclerHouses)
        recycler.layoutManager = LinearLayoutManager(this)

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
                        val adapter = HouseAdapter(response) { house ->
                            val intent = Intent(this, HouseAccesActivity::class.java)
                            intent.putExtra("HOUSE_ID", house.houseId)
                            startActivity(intent)
                        }
                        recycler.adapter = adapter
                    }
                }

                403 -> Toast.makeText(this, "Accès interdit (token invalide)", Toast.LENGTH_LONG).show()
                500 -> Toast.makeText(this, "Erreur serveur", Toast.LENGTH_LONG).show()
                else -> Toast.makeText(this, "Erreur: $code", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadHouse() {
        val token = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
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