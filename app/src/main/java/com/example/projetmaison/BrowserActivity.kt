package com.example.projetmaison

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
            // Gestionnaire d'erreur pour afficher un Toast si la page ne charge pas
            web.webViewClient = object : WebViewClient() {
                override fun onReceivedError(
                    view: WebView?,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?
                ) {
                    Toast.makeText(this@BrowserActivity, "Erreur de chargement: $description", Toast.LENGTH_LONG).show()
                }
                override fun onPageFinished(view: WebView?, url: String?) {
                    // Réactive le chargement des images après le chargement initial
                    web.settings.loadsImagesAutomatically = true
                }
            }
            // Optimisations WebView
            val settings = web.settings
            settings.javaScriptEnabled = true // Activation de JavaScript
            settings.domStorageEnabled = true // Activation du stockage DOM
            settings.cacheMode = android.webkit.WebSettings.LOAD_DEFAULT // Utilise le cache si possible
            settings.loadsImagesAutomatically = false // Désactive le chargement auto des images pour accélérer l'affichage initial

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
