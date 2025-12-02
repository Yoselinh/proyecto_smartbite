package com.smartbite.api

import com.smartbite.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ---------- AUTH ----------
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/registro")
    suspend fun registrar(@Body request: RegistroRequest): Response<LoginResponse>

    // ---------- PERFIL ----------
    @GET("api/profile")
    suspend fun obtenerPerfil(
        @Header("Authorization") token: String
    ): Response<PerfilResponse>

    @PUT("api/profile")
    suspend fun actualizarPerfil(
        @Header("Authorization") token: String,
        @Body request: PerfilUpdateRequest
    ): Response<ResponseBody>


    // ---------- SENSORES ----------
    /** OBTENER TODAS LAS LECTURAS (sin auth obligatoria) */
    @GET("api/sensores/todas")
    suspend fun listarLecturas(): Response<List<LecturaSensor>>

    /** OBTENER LECTURAS POR USUARIO */
    @GET("api/sensores/usuario/{id}")
    suspend fun getLecturasByUsuario(
        @Path("id") usuarioId: Long
    ): Response<List<LecturaSensor>>


    /** OBTENER MIS LECTURAS (usuario autenticado) */
    @GET("api/sensores/me")
    suspend fun listarMisLecturas(
        @Header("Authorization") token: String
    ): Response<List<LecturaSensor>>


    /** REGISTRAR UNA NUEVA LECTURA */
    @POST("api/sensores/registrar")
    suspend fun registrarLectura(
        @Header("Authorization") token: String,
        @Body request: LecturaRequest
    ): Response<String>


}
