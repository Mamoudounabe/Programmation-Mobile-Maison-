package com.example.projetmaison.models

// Classe de données représentant un appareil connecté dans la maison
data class Device(
    // Identifiant unique de l'appareil
    val id: String,
    // Type d'appareil (ex: "volet", "lumière", etc.)
    val type: String,
    // Liste des commandes disponibles pour cet appareil
    val availableCommands: List<String> = emptyList(),
    // État d'ouverture (pour les volets, etc.)
    val opening: Int? = null,
    // Puissance (pour les lumières, etc.)
    val power: Int? = null
)