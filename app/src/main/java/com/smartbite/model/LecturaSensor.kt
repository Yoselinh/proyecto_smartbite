package com.smartbite.model

data class LecturaSensor(
    val id: Int,
    val fecha_hora: String,
    val peso_carbohidrato: Float,
    val peso_proteina: Float,
    val peso_vegetal: Float,
    val usuario_id: Int
)
