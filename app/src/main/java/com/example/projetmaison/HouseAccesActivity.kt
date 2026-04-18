package com.example.projetmaison

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class HouseAccesActivity : AppCompatActivity() {

    private var houseId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_house_acces)

        houseId = intent.getIntExtra("HOUSE_ID", -1)
        if (houseId == -1) {
            Toast.makeText(this, "HOUSE_ID manquant", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Affiche le houseId (si le TextView existe dans le XML)
        val txtHouseIdInfo = findViewById<TextView?>(R.id.txtHouseIdInfo)
        txtHouseIdInfo?.text = "HouseId: $houseId"

        // Option 1 : ouvrir dans Chrome/Safari (navigateur externe)
        findViewById<Button>(R.id.btnOpenInBrowserExternal).setOnClickListener {
            val url = "https://polyhome.lesmoulinsdudev.com?houseId=$houseId"
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(i)
        }

        // Option 2 : ouvrir dans l'app (WebView)
        findViewById<Button>(R.id.btnOpenInBrowserInApp).setOnClickListener {
            val i = Intent(this, BrowserActivity::class.java)
            i.putExtra("HOUSE_ID", houseId)
            startActivity(i)
        }

        // Donner accès
        findViewById<Button>(R.id.btnAddUser).setOnClickListener { addUser() }

        // Supprimer accès
        findViewById<Button>(R.id.btnRemoveUser).setOnClickListener { removeUser() }

        // Liste des utilisateurs
        findViewById<Button>(R.id.btnUsers).setOnClickListener {
            val i = Intent(this, HouseUserActivity::class.java)
            i.putExtra("HOUSE_ID", houseId)
            startActivity(i)
        }

        // Liste des périphériques
        findViewById<Button>(R.id.btnDevices).setOnClickListener {
            val prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
            val token = prefs.getString("TOKEN", "") ?: ""
            val i = Intent(this, HouseDevicesActivity::class.java)
            i.putExtra("HOUSE_ID", houseId)
            i.putExtra("TOKEN", token)
            startActivity(i)
        }





        // Retour
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            startActivity(Intent(this, HouseActivity::class.java))
            finish()
        }
    }

    private fun addUserSuccess(code: Int) {
        runOnUiThread {
            when (code) {
                200 -> {
                    Toast.makeText(this, "Accès accordé", Toast.LENGTH_LONG).show()
                    findViewById<EditText>(R.id.txtUserLogin).text.clear()
                }
                400 -> Toast.makeText(this, "Les données fournies sont incorrectes", Toast.LENGTH_LONG).show()
                403 -> Toast.makeText(this, "Accès interdit (token invalide ou pas propriétaire)", Toast.LENGTH_LONG).show()
                409 -> Toast.makeText(this, "L’utilisateur est déjà associé à la maison", Toast.LENGTH_LONG).show()
                500 -> Toast.makeText(this, "Erreur serveur", Toast.LENGTH_LONG).show()
                else -> Toast.makeText(this, "Erreur: $code", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addUser() {
        val login = findViewById<EditText>(R.id.txtUserLogin).text.toString().trim()
        if (login.isEmpty()) {
            Toast.makeText(this, "Login requis", Toast.LENGTH_LONG).show()
            return
        }

        val token = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
            .getString("TOKEN", null)

        if (token.isNullOrBlank()) {
            Toast.makeText(this, "Token manquant", Toast.LENGTH_LONG).show()
            return
        }

        val data = AddUserData(userLogin = login)

        Api().post<AddUserData>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users",
            data,
            ::addUserSuccess,
            token
        )
    }

    private fun removeUserSuccess(code: Int) {
        runOnUiThread {
            when (code) {
                200 -> {
                    Toast.makeText(this, "Suppression réalisée", Toast.LENGTH_LONG).show()
                    findViewById<EditText>(R.id.txtUserLogin).text.clear()
                }
                400 -> Toast.makeText(this, "Les données fournies sont incorrectes", Toast.LENGTH_LONG).show()
                403 -> Toast.makeText(this, "Accès interdit (token invalide ou pas propriétaire)", Toast.LENGTH_LONG).show()
                500 -> Toast.makeText(this, "Erreur serveur", Toast.LENGTH_LONG).show()
                else -> Toast.makeText(this, "Erreur: $code", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun removeUser() {
        val login = findViewById<EditText>(R.id.txtUserLogin).text.toString().trim()
        if (login.isEmpty()) {
            Toast.makeText(this, "Login requis", Toast.LENGTH_LONG).show()
            return
        }

        val token = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
            .getString("TOKEN", null)

        if (token.isNullOrBlank()) {
            Toast.makeText(this, "Token manquant", Toast.LENGTH_LONG).show()
            return
        }

        val data = AddUserData(userLogin = login)

        Api().delete<AddUserData>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users",
            data,
            ::removeUserSuccess,
            token
        )
    }
}