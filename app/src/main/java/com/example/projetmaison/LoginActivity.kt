package com.example.projetmaison

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        //  Gestion bouton connexion
        findViewById<Button>(R.id.btnConect).setOnClickListener {
            Connexion()
        }

        //  GESTION oeil MOT DE PASSE
        val passwordField = findViewById<EditText>(R.id.txtPassword)
        val toggle = findViewById<ImageView>(R.id.imgTogglePwd)

        var isVisible = false

        toggle.setOnClickListener {
            if (isVisible) {
                passwordField.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                toggle.setImageResource(R.drawable.eye_closed)
            } else {
                passwordField.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                toggle.setImageResource(R.drawable.eye_open)
            }

            // Garde le curseur à la fin
            passwordField.setSelection(passwordField.text.length)

            isVisible = !isVisible
        }
    }

    fun registerNewcompte(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun ConnexionSuccess(responseCode: Int, tokenD: TokenData?) {
        runOnUiThread {
            setLoading(false)

            when (responseCode) {
                200 -> {
                    Toast.makeText(this, "Connexion réussie", Toast.LENGTH_LONG).show()

                    val token = tokenD?.token
                    Log.d("LoginActivity", "Token recu: $token")

                    val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
                    prefs.edit().putString("TOKEN", token).apply()

                    val intent = Intent(this, HouseActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                400 -> Toast.makeText(this, "Les données fournies sont incorrectes", Toast.LENGTH_LONG).show()
                404 -> Toast.makeText(this, "Aucun utilisateur ne correspond aux identifiants donnés", Toast.LENGTH_LONG).show()
                500 -> Toast.makeText(this, "Une erreur serveur s’est produite", Toast.LENGTH_LONG).show()
                else -> Toast.makeText(this, "Erreur inconnue: $responseCode", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun Connexion() {
        setLoading(true)

        val login = findViewById<EditText>(R.id.txtLogin).text.toString().trim()
        val password = findViewById<EditText>(R.id.txtPassword).text.toString().trim()

        // 🔥 Correction importante : stop si vide
        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Tous les champs sont obligatoires", Toast.LENGTH_LONG).show()
            setLoading(false)
            return
        }

        val data = LoginData(login, password)

        Api().post<LoginData, TokenData>(
            "https://polyhome.lesmoulinsdudev.com/api/users/auth",
            data,
            ::ConnexionSuccess
        )
    }

    private fun setLoading(loading: Boolean) {
        val progress = findViewById<ProgressBar>(R.id.progressLogin)
        val btnLogin = findViewById<Button>(R.id.btnConect)

        progress.visibility = if (loading) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !loading
    }
}
