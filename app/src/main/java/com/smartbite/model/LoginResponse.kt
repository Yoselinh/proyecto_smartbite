package com.smartbite.model

import com.smartbite.model.Usuario
data class LoginResponse(
    val token: String,
    val userId: Int
)
