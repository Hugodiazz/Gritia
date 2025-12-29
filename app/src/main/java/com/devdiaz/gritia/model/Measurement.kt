package com.devdiaz.gritia.model

import java.time.LocalDate

data class Measurement(
        val id: String,
        val type: MeasurementType,
        val value: Float,
        val unit: String = "cm",
        val date: LocalDate,
        val previousValue: Float? = null
) {
    val delta: Float?
        get() = previousValue?.let { value - it }
}

enum class MeasurementType(val displayName: String) {
    Weight("Peso"),
    BMI("IMC"),
    BodyFat("% Grasa"),
    Neck("Cuello"),
    Chest("Pecho"),
    Biceps("Biceps"),
    Forearm("Antebrazo"),
    Waist("Cintura"),
    Hips("Cadera"),
    Legs("Piernas"),
    Calves("Pantorrillas")
}
