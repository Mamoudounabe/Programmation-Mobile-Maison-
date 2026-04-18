package com.example.projetmaison.models

/**
 * Modélise un utilisateur associé à une maison.
 * @property userLogin Identifiant de connexion de l'utilisateur.
 * @property owner Identifiant du propriétaire de la maison.
 */
data class HouseUser(
    val userLogin: String, // Identifiant de connexion de l'utilisateur
    val owner: Int // Identifiant du propriétaire de la maison
)