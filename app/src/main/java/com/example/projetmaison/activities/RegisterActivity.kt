package com.example.projetmaison.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.projetmaison.Api
import com.example.projetmaison.R
import com.example.projetmaison.models.RegisterData


class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        findViewById<Button>(R.id.btnRegister).setOnClickListener {

            Toast.makeText(this, "Bouton cliqué", Toast.LENGTH_SHORT).show()
            println("CREATION COMPTE CLICKED")
            creation()
        }

        val password = findViewById<EditText>(R.id.txtRegisterPassword)
        val confirm = findViewById<EditText>(R.id.txtConfirmPassword)

        val togglePwd = findViewById<ImageView>(R.id.imgTogglePwd)
        val toggleConfirm = findViewById<ImageView>(R.id.imgToggleConfirmPwd)

        var isPwdVisible = false
        var isConfirmVisible = false

        togglePwd.setOnClickListener {
            if (isPwdVisible) {
                password.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePwd.setImageResource(R.drawable.eye_closed)
            } else {
                password.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePwd.setImageResource(R.drawable.eye_open)
            }
            password.setSelection(password.text.length)
            isPwdVisible = !isPwdVisible
        }

        toggleConfirm.setOnClickListener {
            if (isConfirmVisible) {
                confirm.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                toggleConfirm.setImageResource(R.drawable.eye_closed)
            } else {
                confirm.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                toggleConfirm.setImageResource(R.drawable.eye_open)
            }
            confirm.setSelection(confirm.text.length)
            isConfirmVisible = !isConfirmVisible
        }



    }


 // Retour à l'activité
     fun RetourLogin(view: View)
    {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

    }

 // la fonction collback
    private fun registerSuccess(code: Int, response: String?) {
        runOnUiThread {
            when(code) {
                200 -> {
                    Toast.makeText(this, "Compte créé avec succès", Toast.LENGTH_LONG).show()

                    // VIDER LES CHAMPS
                    findViewById<EditText>(R.id.txtRegisterNom).text.clear()
                    findViewById<EditText>(R.id.txtRegisterMail).text.clear()
                    findViewById<EditText>(R.id.txtRegisterPassword).text.clear()
                    findViewById<EditText>(R.id.txtConfirmPassword).text.clear()
                }
                400 -> Toast.makeText(this, "Les données fournies sont incorrectes", Toast.LENGTH_LONG).show()
                409 -> Toast.makeText(this, "Le login est déjà utilisé", Toast.LENGTH_LONG).show()
                500 -> Toast.makeText(this, "Erreur serveur", Toast.LENGTH_LONG).show()
                else -> Toast.makeText(this, "Erreur inconnue: $code", Toast.LENGTH_LONG).show()
            }
        }
    }





    private fun creation() {
        val login = findViewById<EditText>(R.id.txtRegisterMail).text.toString().trim()
        val password = findViewById<EditText>(R.id.txtRegisterPassword).text.toString().trim()
        val confirm = findViewById<EditText>(R.id.txtConfirmPassword).text.toString().trim()

        if (login.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Tous les champs sont obligatoires", Toast.LENGTH_LONG).show()
            return
        }

        if (password != confirm) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_LONG).show()
            return
        }

        val data = RegisterData(login, password)

        Api().post<RegisterData, String>(
            "https://polyhome.lesmoulinsdudev.com/api/users/register",
            data,
            ::registerSuccess
        )
    }



// Création d'un compte utilisateur
 /*   private fun creation() {
        val login = findViewById<EditText>(R.id.txtRegisterMail).text.toString().trim()
        val password = findViewById<EditText>(R.id.txtRegisterPassword).text.toString().trim()

        val data = RegisterData(login, password)

        Api().post<RegisterData, String>(
            "https://polyhome.lesmoulinsdudev.com/api/users/register",
            data,
            ::registerSuccess
        )
    }
*/



}

