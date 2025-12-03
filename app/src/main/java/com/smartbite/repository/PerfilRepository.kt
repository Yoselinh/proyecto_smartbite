package com.smartbite.repository

import com.smartbite.api.ApiClient
import com.smartbite.model.PerfilUsuario
import com.smartbite.model.PerfilUpdateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PerfilRepository {
    private val api = ApiClient.apiService

    suspend fun obtenerPerfil(token: String): PerfilUsuario? = withContext(Dispatchers.IO) {
        try {
            val response = api.obtenerPerfil("Bearer $token")
            if (response.isSuccessful) {
                //  Accedemos al objeto "perfil" dentro del body
                response.body()?.perfil
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun actualizarPerfil(token: String, perfil: PerfilUsuario): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = PerfilUpdateRequest(
                altura = perfil.altura,
                edad = perfil.edad,
                genero = perfil.genero,
                peso = perfil.peso
            )

            val response = api.actualizarPerfil("Bearer $token", request)
            if (response.isSuccessful) {
                // Leer texto plano de respuesta ("Perfil actualizado")
                val mensaje = response.body()?.string() ?: "Perfil actualizado"
                println(" $mensaje")
                true
            } else {
                val error = response.errorBody()?.string()
                println("Error: $error")
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

