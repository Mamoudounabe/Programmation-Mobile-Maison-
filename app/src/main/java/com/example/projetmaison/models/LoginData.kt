package com.example.projetmaison.models

/**
 * Données nécessaires pour la connexion d'un utilisateur.
 * @property login Identifiant de connexion.
 * @property password Mot de passe de l'utilisateur.
 */
data class LoginData(
    val login: String, // Identifiant de connexion
    val password: String // Mot de passe de l'utilisateur
)
