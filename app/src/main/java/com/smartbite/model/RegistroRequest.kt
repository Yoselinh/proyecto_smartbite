package com.smartbite.model

data class RegistroRequest(
    val nombre: String,
    val correo: String,
    val password: String,
    val rol: String
)
