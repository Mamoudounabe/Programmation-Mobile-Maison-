package com.example.projetmaison

data class Device(
    val id: String,
    val type: String,
    val availableCommands: List<String> = emptyList(),
    val opening: Int? = null,
    val power: Int? = null

)
