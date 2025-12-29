package com.devdiaz.gritia.model.dto

import com.devdiaz.gritia.model.Gender
import com.devdiaz.gritia.model.User
import java.time.LocalDate
import java.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
        @SerialName("id") val id: String? = null,
        @SerialName("name") val name: String,
        @SerialName("email") val email: String,
        @SerialName("gender") val gender: String? = null,
        @SerialName("birth_date") val birthDate: String? = null,
        @SerialName("height") val height: Float? = null,
        @SerialName("current_weight") val currentWeight: Float? = null,
        @SerialName("created_at") val createdAt: String? = null
)

fun UserDto.toDomain(): User {
    return User(
        id = 0, // Local ID is managed by Room, remote UUID is not used as local ID
            name = name,
            email = email,
            gender =
                    when (gender) {
                        "Masculino" -> Gender.MALE
                        "Femenino" -> Gender.FEMALE
                        else -> Gender.OTHER
                    },
            birthDate =
                    birthDate?.let {
                        try {
                            LocalDate.parse(it)
                        } catch (e: Exception) {
                            null
                        }
                    },
            height = height,
            currentWeight = currentWeight,
            createdAt = createdAt?.let {
                try {
                    Instant.parse(it).toEpochMilli()
                } catch (e: Exception) {
                    System.currentTimeMillis()
                }
            } ?: System.currentTimeMillis()
    )
}

fun User.toDto(): UserDto {
    return UserDto(
            name = name,
            email = email,
            gender =
                    when (gender) {
                        Gender.MALE -> "Masculino"
                        Gender.FEMALE -> "Femenino"
                        else -> "Otro"
                    },
            birthDate = birthDate?.toString(),
            height = height,
            currentWeight = currentWeight,
            createdAt = try {
                Instant.ofEpochMilli(createdAt).toString()
            } catch (e: Exception) {
                null
            }
    )
}
