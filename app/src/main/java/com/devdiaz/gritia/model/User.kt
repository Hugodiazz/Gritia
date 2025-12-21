package com.devdiaz.gritia.model

import java.time.LocalDate

data class User(
        val id: Long,
        val name: String,
        val email: String,
        val gender: Gender,
        val birthDate: LocalDate?,
        val height: Float?,
        val currentWeight: Float?
)

enum class Gender {
    MALE,
    FEMALE,
    OTHER
}
