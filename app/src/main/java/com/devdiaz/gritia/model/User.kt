package com.devdiaz.gritia.model

import java.time.LocalDate

data class User(
        val id: Long,
        val name: String,
        val email: String,
        val gender: Gender,
        val birthDate: LocalDate?,
        val height: Float?,
        val currentWeight: Float?,
        val createdAt: Long = 0L
)

enum class Gender {
    MALE,
    FEMALE,
    OTHER
}
