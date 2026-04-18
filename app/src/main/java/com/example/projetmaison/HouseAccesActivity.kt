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

// Activité permettant d'accéder à une maison via son identifiant
class HouseAccesActivity : AppCompatActivity() {
    // Liste des logins utilisateurs associés à la maison pour l'autocomplétion
    private var userLogins: List<String> = emptyList()

    // Identifiant de la maison à accéder
    private var houseId: Int = -1

    // Méthode appelée à la création de l'activité
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_house_acces)

        // Récupération de l'identifiant de la maison
        houseId = intent.getIntExtra("HOUSE_ID", -1)
        if (houseId == -1) {
            finish()
            return
        }

        // Préparation de l'autocomplétion dynamique
        val autoComplete = findViewById<android.widget.AutoCompleteTextView>(R.id.txtUserLogin)
        val adapter = android.widget.ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, mutableListOf())
        autoComplete.setAdapter(adapter)

        // Récupération du token pour l'appel API
        val token = getSharedPreferences("APP_PREFS", MODE_PRIVATE).getString("TOKEN", null)
        if (!token.isNullOrBlank()) {
            // Appel API pour récupérer les utilisateurs associés à la maison
            Api().get<List<HouseUser>>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", { code, users ->
                if (code == 200 && users != null) {
                    userLogins = users.map { it.userLogin }
                    runOnUiThread {
                        adapter.clear()
                        adapter.addAll(userLogins)
                        adapter.notifyDataSetChanged()
                    }
                }
            }, token)
        }

        // Mise à jour dynamique des suggestions selon la saisie
        autoComplete.threshold = 1 // Suggestions dès le 1er caractère
        autoComplete.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) autoComplete.showDropDown()
        }

        // Affiche le houseId (si le TextView existe dans le XML)
        val txtHouseIdInfo = findViewById<TextView?>(R.id.txtHouseIdInfo)
        txtHouseIdInfo?.text = "HouseId: $houseId"

        // Option 1 : ouvrir dans le navigateur externe (Chrome/Safari)
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
                    findViewById<EditText>(R.id.txtUserLogin).text.clear()
                }
                // Suppression des Toasts pour les autres cas
            }
        }
    }

    private fun addUser() {
        val login = findViewById<EditText>(R.id.txtUserLogin).text.toString().trim()
        if (login.isEmpty()) {
            return
        }

        val token = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
            .getString("TOKEN", null)

        if (token.isNullOrBlank()) {
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
                    findViewById<EditText>(R.id.txtUserLogin).text.clear()
                }
                // Suppression des Toasts pour les autres cas
            }
        }
    }

    private fun removeUser() {
        val login = findViewById<EditText>(R.id.txtUserLogin).text.toString().trim()
        if (login.isEmpty()) {
            return
        }

        val token = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
            .getString("TOKEN", null)

        if (token.isNullOrBlank()) {
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