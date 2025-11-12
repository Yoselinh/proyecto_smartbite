package com.smartbite.api

import com.smartbite.model.*
import retrofit2.Response
import retrofit2.http.*
import com.smartbite.model.PerfilResponse
import okhttp3.ResponseBody
interface ApiService {

    // ---------- AUTH ----------
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/registro")
    suspend fun registrar(@Body request: RegistroRequest): Response<LoginResponse>

    // ---------- PERFIL ----------

    @GET("api/profile")
    suspend fun obtenerPerfil(@Header("Authorization") token: String): Response<PerfilResponse>

    @PUT("api/profile")
    suspend fun actualizarPerfil(
        @Header("Authorization") token: String,
        @Body request: PerfilUpdateRequest
    ): Response<ResponseBody>


    @GET("api/sensores/todas")
    suspend fun listarLecturas(): Response<List<LecturaSensor>>

    /** Versión RAW del mismo endpoint, útil como fallback */
    @GET("api/sensores/todas")
    suspend fun listarLecturasRaw(): Response<ResponseBody>

    /** Registra una nueva lectura */
    @POST("api/sensores/registrar")
    suspend fun registrarLectura(
        @Body request: LecturaRequest
    ): Response<String>
}
