package com.smartbite.model


data class LecturaSensor(
    val id: Long,
    val usuarioId: Int,
    val nombreProteina: String,
    val pesoProteina: Double,
    val nombreCarbohidrato: String,
    val pesoCarbohidrato: Double,
    val nombreVegetal: String,
    val pesoVegetal: Double,
    val fechaHora: String
)