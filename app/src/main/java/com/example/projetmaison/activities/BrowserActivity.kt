package com.example.projetmaison.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.projetmaison.R

// Activité affichant une page web liée à une maison spécifique via WebView
class BrowserActivity : AppCompatActivity() {

    // Méthode appelée à la création de l'activité
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)

        // Récupération de l'identifiant de la maison depuis l'intent
        val houseId = intent.getIntExtra("HOUSE_ID", -1)

        // Vérification de la validité de l'identifiant
        if (houseId == -1) {
            Toast.makeText(this, "Erreur : HOUSE_ID manquant", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Initialisation de la WebView
        val web = findViewById<WebView>(R.id.webViewHouse)

        web.setBackgroundColor(Color.WHITE)
        web.webChromeClient = WebChromeClient()
        web.webViewClient = WebViewClient()
        web.settings.javaScriptEnabled = true // Activation de JavaScript
        web.settings.domStorageEnabled = true // Activation du stockage DOM

        // Construction de l'URL avec l'identifiant de la maison
        val url = "https://polyhome.lesmoulinsdudev.com?houseId=$houseId"

        println("URL chargée: $url") // debug

        web.loadUrl(url)

        // 🔙 bouton retour UI
        findViewById<Button>(R.id.btnBackBrowser).setOnClickListener {
            finish()
        }

        // 🔙 bouton retour téléphone
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (web.canGoBack()) {
                    web.goBack()
                } else {
                    finish()
                }
            }
        })
    }
}