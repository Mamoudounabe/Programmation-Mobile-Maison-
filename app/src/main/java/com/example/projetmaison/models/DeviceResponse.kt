package com.example.projetmaison.models

import com.example.projetmaison.models.Device

// Classe de données représentant la réponse d'une API contenant une liste d'appareils
data class DeviceResponse(
    // Liste des appareils retournés par l'API
    val devices: List<Device> = emptyList()
)