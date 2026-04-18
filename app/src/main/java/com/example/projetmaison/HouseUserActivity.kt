package com.example.projetmaison

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
class HouseUserActivity : AppCompatActivity() {

    private var houseId: Int = -1
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_house_user)

        recycler = findViewById(R.id.recyclerUsers)
        recycler.layoutManager = LinearLayoutManager(this)

        houseId = intent.getIntExtra("HOUSE_ID", -1)
        if (houseId == -1) {
            Toast.makeText(this, "HOUSE_ID manquant", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        loadUsers()

        findViewById<Button>(R.id.btnBackUsers).setOnClickListener {
            finish()
        }
    }

    private fun loadUsers() {
        val token = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
            .getString("TOKEN", null)

        if (token.isNullOrBlank()) {
            Toast.makeText(this, "Token manquant. Connecte-toi.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users"

        Api().get<List<HouseUser>>(
            url,
            ::onUsersLoaded,
            token
        )
    }

    private fun onUsersLoaded(code: Int, users: List<HouseUser>?) {
        runOnUiThread {
            when (code) {
                200 -> {
                    if (users == null) {
                        Toast.makeText(this, "Réponse vide (parsing échoué)", Toast.LENGTH_LONG).show()
                        recycler.adapter = HouseUserAdapter(emptyList())
                        return@runOnUiThread
                    }

                    if (users.isEmpty()) {
                        Toast.makeText(this, "Aucun utilisateur.", Toast.LENGTH_LONG).show()
                        recycler.adapter = HouseUserAdapter(emptyList())
                        return@runOnUiThread
                    }

                    recycler.adapter = HouseUserAdapter(users)
                }

                400 -> Toast.makeText(this, "Les données fournies sont incorrectes", Toast.LENGTH_LONG).show()
                403 -> Toast.makeText(this, "Accès interdit", Toast.LENGTH_LONG).show()
                500 -> Toast.makeText(this, "Une erreur s’est produite au niveau du serveur", Toast.LENGTH_LONG).show()
                else -> Toast.makeText(this, "Erreur inconnue: $code", Toast.LENGTH_LONG).show()
            }
        }
    }
}