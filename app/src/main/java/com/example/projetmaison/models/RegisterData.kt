package com.example.projetmaison.models

/**
 * Données nécessaires pour l'inscription d'un utilisateur.
 * @property login Identifiant de connexion souhaité.
 * @property password Mot de passe choisi.
 */
data class RegisterData(
    val login: String, // Identifiant de connexion souhaité
    val password: String // Mot de passe choisi
)
