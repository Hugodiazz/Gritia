package com.devdiaz.gritia.model.mappers

import com.devdiaz.gritia.model.Equipment
import com.devdiaz.gritia.model.Exercise
import com.devdiaz.gritia.model.Gender
import com.devdiaz.gritia.model.MuscleGroup
import com.devdiaz.gritia.model.Routine
import com.devdiaz.gritia.model.User
import com.devdiaz.gritia.model.entities.ExerciseEntity
import com.devdiaz.gritia.model.entities.RoutineEntity
import com.devdiaz.gritia.model.entities.UserEntity
import java.time.LocalDate

// User Mappers
fun UserEntity.toDomain(): User {
    return User(
            id = this.id,
            name = this.name,
            email = this.email,
            gender =
                    when (this.gender) {
                        "Masculino" -> Gender.MALE
                        "Femenino" -> Gender.FEMALE
                        else -> Gender.OTHER
                    },
            birthDate =
                    this.birthDate?.let {
                        try {
                            LocalDate.parse(it)
                        } catch (e: Exception) {
                            null
                        }
                    },
            height = this.height,
            currentWeight = this.currentWeight
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
            id = this.id, // 0 if new
            name = this.name,
            email = this.email,
            gender =
                    when (this.gender) {
                        Gender.MALE -> "Masculino"
                        Gender.FEMALE -> "Femenino"
                        Gender.OTHER -> "Otro"
                    },
            birthDate = this.birthDate?.toString(),
            height = this.height,
            currentWeight = this.currentWeight
    )
}

// Exercise Mappers
fun ExerciseEntity.toDomain(): Exercise {
    return Exercise(
            id = this.id.toString(),
            name = this.name,
            muscleGroup =
                    try {
                        MuscleGroup.valueOf(this.primaryMuscleGroup.uppercase())
                    } catch (e: Exception) {
                        MuscleGroup.ALL
                    }, // Default fallback
            equipment =
                    try {
                        Equipment.valueOf(this.toolType?.uppercase() ?: "BODYWEIGHT")
                    } catch (e: Exception) {
                        Equipment.BODYWEIGHT
                    },
            imageUrl = "" // Not in DB yet
    )
}

fun Exercise.toEntity(): ExerciseEntity {
    return ExerciseEntity(
            id = this.id.toLongOrNull() ?: 0L,
            name = this.name,
            primaryMuscleGroup = this.muscleGroup.name,
            movementPattern = null, // Logic needed or default
            toolType = this.equipment.name
    )
}

// Routine Mappers
fun RoutineEntity.toDomain(): Routine {
    return Routine(
            title = this.name,
            muscles = "", // Logic needed to derive from exercises? Or just empty/placeholder
            schedule = listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday),
            imageUrl = ""
    )
}

fun Routine.toEntity(userId: Long): RoutineEntity {
    val days = this.schedule
    return RoutineEntity(
            userId = userId,
            name = this.title,
            monday = days.getOrElse(0) { false },
            tuesday = days.getOrElse(1) { false },
            wednesday = days.getOrElse(2) { false },
            thursday = days.getOrElse(3) { false },
            friday = days.getOrElse(4) { false },
            saturday = days.getOrElse(5) { false },
            sunday = days.getOrElse(6) { false }
    )
}
