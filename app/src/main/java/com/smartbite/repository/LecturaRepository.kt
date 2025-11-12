package com.smartbite.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.smartbite.api.ApiClient
import com.smartbite.model.LecturaSensor
import com.smartbite.model.LecturaRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody

class LecturaRepository {

    private val api = ApiClient.apiService
    private val gson = Gson()

    // Obtener todas las lecturas desde la API (intenta parsing automático, si falla usa fallback)
    suspend fun obtenerLecturas(): List<LecturaSensor>? = withContext(Dispatchers.IO) {
        try {
            val response = api.listarLecturas()
            if (response.isSuccessful) {
                // Intento 1: Retrofit/Gson ya mapeó la lista
                val body = response.body()
                if (body != null) {
                    return@withContext body
                }
            }

            // Si llegamos aquí: body fue null o no fue successful -> intentamos fallback con raw
            // (llamamos al endpoint raw para leer JSON crudo y mapear flexible)
            val rawResp = api.listarLecturasRaw()
            if (rawResp.isSuccessful) {
                val rb: ResponseBody? = rawResp.body()
                val json = rb?.string() ?: return@withContext null
                // Parseamos JsonArray
                val jarr = gson.fromJson(json, JsonArray::class.java)

                val lista = mutableListOf<LecturaSensor>()
                for (je: JsonElement in jarr) {
                    val jo = je.asJsonObject
                    // Extraemos cada campo intentando varios nombres (snake_case y camelCase)
                    val id = getIntFlexible(jo, "id", "id_")
                    val fecha = getStringFlexible(jo, "fecha_hora", "fechaHora", "fecha_hh")
                    val carbo = getFloatFlexible(jo, "peso_carbohidrato", "pesoCarbohidrato", "peso_carbo")
                    val prote = getFloatFlexible(jo, "peso_proteina", "pesoProteina", "peso_proteinas")
                    val vegetal = getFloatFlexible(jo, "peso_vegetal", "pesoVegetal", "peso_veg")
                    val usuarioId = getIntFlexible(jo, "usuario_id", "usuarioId", "usuario_id_")

                    // Si alguno de los campos numéricos faltan, asignamos 0f por seguridad
                    val lectura = LecturaSensor(
                        id = id ?: 0,
                        fecha_hora = fecha ?: "",
                        peso_carbohidrato = carbo ?: 0f,
                        peso_proteina = prote ?: 0f,
                        peso_vegetal = vegetal ?: 0f,
                        usuario_id = usuarioId ?: 0
                    )
                    lista.add(lectura)
                }
                return@withContext lista
            } else {
                Log.e("LecturaRepository", "listarLecturasRaw failed: ${rawResp.code()} ${rawResp.message()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("LecturaRepository", "Error obtenerLecturas: ${e.message}")
        }
        return@withContext null
    }

    // Registrar una nueva lectura (usando LecturaRequest)
    suspend fun registrarLectura(request: LecturaRequest): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.registrarLectura(request)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ----------------- helpers de parsing flexible -----------------
    private fun getStringFlexible(jo: JsonObject, vararg keys: String): String? {
        for (k in keys) {
            if (jo.has(k) && !jo.get(k).isJsonNull) return jo.get(k).asString
        }
        return null
    }

    private fun getIntFlexible(jo: JsonObject, vararg keys: String): Int? {
        for (k in keys) {
            if (jo.has(k) && !jo.get(k).isJsonNull) {
                return try { jo.get(k).asInt } catch (_: Exception) { jo.get(k).asString.toDoubleOrNull()?.toInt() }
            }
        }
        return null
    }

    private fun getFloatFlexible(jo: JsonObject, vararg keys: String): Float? {
        for (k in keys) {
            if (jo.has(k) && !jo.get(k).isJsonNull) {
                return try { jo.get(k).asFloat } catch (_: Exception) {
                    jo.get(k).asString.replace(",", ".").toDoubleOrNull()?.toFloat()
                }
            }
        }
        return null
    }
}
