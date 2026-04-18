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

class HouseDevicesLumiereActivity : AppCompatActivity() {

    private var houseId = -1
    private lateinit var token: String
    private var devices: List<Device> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_house_devices_lumiere)

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
            fetchLights()
        }
    }

    /**
     * Récupère tous les périphériques de la maison et filtre ceux de type "light".
     */
    private fun fetchLights() {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices"
        Api().get<DeviceResponse>(url, ::onDevicesFetched, token)
    }

    /**
     * Callback appelé après récupération des périphériques.
     *
     * @param responseCode Code HTTP reçu.
     * @param response Données de réponse contenant les périphériques.
     */
    private fun onDevicesFetched(responseCode: Int, response: DeviceResponse?) {
        runOnUiThread {
            if (responseCode == 200 && response != null) {
                devices = response.devices
                Toast.makeText(this, "${devices.size} périphériques récupérés", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * Allume toutes les lumières de la maison (commande "TURN ON").
     */
    fun onButtonLightOnClic(view: View) {
        val lights = devices.filter { it.type == "light" }
        if (lights.isEmpty()) {
            Toast.makeText(this, "Aucune lumière trouvée", Toast.LENGTH_SHORT).show()
            return
        }
        lights.forEach {
            sendCommand(it.id, "TURN ON", ::onLightCommandResult)
        }
    }

    /**
     * Éteint toutes les lumières de la maison (commande "TURN OFF").
     */
    fun onButtonLightOffClic(view: View) {
        val lights = devices.filter { it.type == "light" }
        if (lights.isEmpty()) {
            Toast.makeText(this, "Aucune lumière trouvée", Toast.LENGTH_SHORT).show()
            return
        }
        lights.forEach {
            sendCommand(it.id, "TURN OFF", ::onLightCommandResult)
        }
    }


    /**
     * Callback appelée après envoi de commande à une lumière.
     *
     * @param responseCode Le code HTTP de la réponse.
     */
    private fun onLightCommandResult(responseCode: Int) {
        runOnUiThread {
            if (responseCode == 200) {
                Toast.makeText(this, "Commande envoyée - LUMIÈRES", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Erreur lors de l'envoi de la commande", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Envoie une commande à une lumière.
     *
     * @param deviceId Identifiant du périphérique.
     * @param command Commande à exécuter ("TURN ON" ou "TURN OFF").
     * @param callback Fonction de retour appelée après réponse de l’API.
     */
    private fun sendCommand(deviceId: String, command: String, callback: (Int) -> Unit) {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command"
        val body = CommandData(command)
        Api().post(url, body, callback, token)
    }

    // Gère la flèche de retour
    fun goBack(view: View) {
        finish()
    }

}