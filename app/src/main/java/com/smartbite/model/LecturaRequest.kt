package com.smartbite.model

data class LecturaRequest(
    val nombreProteina: String,
    val pesoProteina: Double,
    val nombreCarbohidrato: String,
    val pesoCarbohidrato: Double,
    val nombreVegetal: String,
    val pesoVegetal: Double,
    val usuarioId: Long
)