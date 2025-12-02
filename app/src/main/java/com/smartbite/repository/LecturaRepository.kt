package com.smartbite.repository

import com.smartbite.api.ApiService
import com.smartbite.model.LecturaSensor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.smartbite.model.LecturaRequest

class LecturaRepository(private val api: ApiService) {

    /** Obtener TODAS las lecturas */
    suspend fun obtenerTodas(): List<LecturaSensor>? =
        withContext(Dispatchers.IO) {
            val response = api.listarLecturas()
            if (response.isSuccessful) response.body() else null
        }

    /** Obtener lecturas del usuario autenticado  */
    suspend fun obtenerMisLecturas(token: String): List<LecturaSensor>? =
        withContext(Dispatchers.IO) {
            val response = api.listarMisLecturas("Bearer $token")
            if (response.isSuccessful) response.body() else null
        }

    /** Obtener lecturas por ID de usuario */
    suspend fun obtenerPorUsuario(id: Long): List<LecturaSensor>? =
        withContext(Dispatchers.IO) {
            val response = api.getLecturasByUsuario(id)
            if (response.isSuccessful) response.body() else null
        }

    suspend fun registrarLectura(token: String, request: LecturaRequest): Boolean {
        return try {
            val response = api.registrarLectura("Bearer $token", request)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
