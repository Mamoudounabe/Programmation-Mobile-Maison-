package com.example.projetmaison

// Classe de données représentant une maison
data class HouseData(
    // Identifiant unique de la maison
    val houseId: Int,
    // Indique si l'utilisateur est propriétaire de la maison
    val owner: Boolean,
    // Nombre d'appareils dans la maison
    val deviceCount: Int
)
