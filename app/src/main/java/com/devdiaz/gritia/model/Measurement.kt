package com.devdiaz.gritia.model

import java.time.LocalDate

data class Measurement(
        val id: String,
        val type: MeasurementType,
        val value: Float,
        val unit: String = "in",
        val date: LocalDate,
        val previousValue: Float? = null
) {
    val delta: Float?
        get() = previousValue?.let { value - it }
}

enum class MeasurementType(val displayName: String) {
    Weight("Weight"),
    Biceps("Biceps"),
    Waist("Waist"),
    Chest("Chest"),
    Thighs("Thighs"),
    Hips("Hips"),
    Neck("Neck")
}
