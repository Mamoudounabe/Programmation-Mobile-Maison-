package com.example.projetmaison

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DeviceActivity : AppCompatActivity() {

    private var houseId: Int = -1
    private lateinit var recycler: RecyclerView
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_device)

        recycler = findViewById(R.id.recyclerDevices)
        recycler.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.btnBackDevices).setOnClickListener { finish() }

        houseId = intent.getIntExtra("HOUSE_ID", -1)
        if (houseId == -1) {
            Toast.makeText(this, "HOUSE_ID manquant", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        token = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
            .getString("TOKEN", null)

        if (token.isNullOrBlank()) {
            Toast.makeText(this, "Token manquant", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        loadDevices()
    }

    private fun loadDevices() {
        Api().get<DeviceResponse>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices",
            ::devicesSuccess,
            token!!
        )
    }

    private fun devicesSuccess(code: Int, response: DeviceResponse?) {
        runOnUiThread {
            when (code) {
                200 -> {
                    val devices = response?.devices ?: emptyList()
                    recycler.adapter = DeviceAdapter(devices) { device ->
                        showCommandDialog(device)
                    }
                    Toast.makeText(this, "Devices: ${devices.size}", Toast.LENGTH_LONG).show()
                }

                400 -> Toast.makeText(this, "Erreur 400: données incorrectes", Toast.LENGTH_LONG).show()
                403 -> Toast.makeText(this, "Erreur 403: accès interdit", Toast.LENGTH_LONG).show()
                500 -> Toast.makeText(this, "Erreur 500: serveur", Toast.LENGTH_LONG).show()
                else -> Toast.makeText(this, "Erreur: $code", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showCommandDialog(device: Device) {
        val commands = device.availableCommands

        if (commands.isEmpty()) {
            Toast.makeText(this, "Aucune commande disponible", Toast.LENGTH_LONG).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Commande pour ${device.id}")
            .setItems(commands.toTypedArray()) { _, which ->
                val command = commands[which]
                sendCommand(device.id, command)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun sendCommand(deviceId: String, command: String) {
        val encodedDeviceId = Uri.encode(deviceId) // important si espaces
        val data = CommandData(command = command)

        Api().post<CommandData>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$encodedDeviceId/command",
            data,
            ::commandSuccess,
            token!!
        )
    }

    private fun commandSuccess(code: Int) {
        runOnUiThread {
            when (code) {
                200 -> {
                    Toast.makeText(this, "Commande envoyée", Toast.LENGTH_LONG).show()
                    loadDevices() // refresh pour voir le nouvel état
                }
                403 -> Toast.makeText(this, "403: accès interdit (token/permissions)", Toast.LENGTH_LONG).show()
                500 -> Toast.makeText(this, "500: erreur serveur", Toast.LENGTH_LONG).show()
                else -> Toast.makeText(this, "Erreur: $code", Toast.LENGTH_LONG).show()
            }
        }
    }
}