package com.example.projetmaison.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetmaison.Api
import com.example.projetmaison.models.CommandData
import com.example.projetmaison.models.Device
import com.example.projetmaison.models.DeviceResponse
import com.example.projetmaison.R

class HouseDeviceGarageActivity : AppCompatActivity() {

    private var houseId = -1
    private lateinit var token: String
    private var devices: List<Device> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_house_devices_garage)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        houseId = intent.getIntExtra("HOUSE_ID", -1)
        token = intent.getStringExtra("TOKEN") ?: ""

        if (houseId == -1 || token.isEmpty()) {
            Toast.makeText(this, "Données manquantes", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            fetchGarage()
        }
    }

    /**
     * Récupère tous les périphériques de la maison.
     */
    private fun fetchGarage() {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices"
        Api().get<DeviceResponse>(url, ::onDevicesFetched, token)
    }

    /**
     * Callback appelée après la récupération des périphériques.
     *
     * @param responseCode Code HTTP reçu.
     * @param response Réponse contenant la liste des périphériques.
     */
    private fun onDevicesFetched(responseCode: Int, response: DeviceResponse?) {
        runOnUiThread {
            if (responseCode == 200 && response != null) {
                devices = response.devices
                // Debug : affiche tous les types de devices récupérés
                val types = devices.joinToString { it.type }
                Toast.makeText(this, "Types: $types", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * Ouvre la porte du garage si disponible.
     */
    fun onButtonUpGarageClic(view: View) {
        val garage = devices.find { it.type == "garage door" }
        if (garage == null) {
            Toast.makeText(this, "Porte garage non trouvée", Toast.LENGTH_SHORT).show()
            return
        }
        sendCommand(garage.id, "OPEN", ::onGarageCommandResult)
    }

    /**
     * Ferme la porte du garage si disponible.
     */
    fun onButtonDownGarageClic(view: View) {
        val garage = devices.find { it.type == "garage door" }
        if (garage == null) {
            Toast.makeText(this, "Porte garage non trouvée", Toast.LENGTH_SHORT).show()
            return
        }
        sendCommand(garage.id, "CLOSE", ::onGarageCommandResult)
    }

    /**
     * Stoppe le mouvement de la porte du garage si disponible.
     */
    fun onButtonStopGarageClic(view: View) {
        val garage = devices.find { it.type == "garage door" }
        if (garage == null) {
            Toast.makeText(this, "Porte garage non trouvée", Toast.LENGTH_SHORT).show()
            return
        }
        sendCommand(garage.id, "STOP", ::onGarageCommandResult)
    }

    /**
     * Envoie une commande à la porte de garage.
     *
     * @param deviceId ID du périphérique.
     * @param command Commande à exécuter.
     * @param callback Fonction appelée après la réponse.
     */
    private fun sendCommand(deviceId: String, command: String, callback: (Int) -> Unit) {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command"
        val body = CommandData(command)
        Api().post(url, body, callback, token)
    }

    /**
     * Callback déclenché après envoi d'une commande au garage.
     *
     * @param responseCode Code HTTP de réponse.
     */
    private fun onGarageCommandResult(responseCode: Int) {
        runOnUiThread {
            if (responseCode == 200) {
                Toast.makeText(this, "Commande envoyée - GARAGE", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Erreur lors de l'envoi de la commande", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun goBack(view: View) {
        finish()
    }
}
