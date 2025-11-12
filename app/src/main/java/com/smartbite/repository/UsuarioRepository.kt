package com.smartbite.repository

import com.smartbite.api.ApiClient
import com.smartbite.model.LoginRequest
import com.smartbite.model.LoginResponse
import com.smartbite.model.RegistroRequest

class UsuarioRepository {

    private val api = ApiClient.apiService

    // ---------- LOGIN ----------
    suspend fun login(request: LoginRequest): LoginResponse? {
        val response = api.login(request)
        return if (response.isSuccessful) response.body() else null
    }

    // ---------- REGISTRO ----------
    suspend fun registrar(request: RegistroRequest): LoginResponse? {
        val response = api.registrar(request)
        return if (response.isSuccessful) response.body() else null
    }
}
