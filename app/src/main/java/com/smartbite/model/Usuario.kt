package com.smartbite.model

data class Usuario(
    val id: Long? = null,
    val nombre: String? = null,
    val correo: String,
    val password: String,
    val rol: String? = null
)