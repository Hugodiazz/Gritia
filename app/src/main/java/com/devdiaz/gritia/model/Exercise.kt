package com.devdiaz.gritia.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
enum class MuscleGroup(val displayName: String) : Parcelable {
    ALL("Todos"),
    CHEST("Pecho"),
    BACK("Espalda"),
    LEGS("Piernas"),
    ARMS("Brazos"),
    SHOULDERS("Hombros"),
    CORE("Core")
}

@Parcelize
enum class Equipment(val displayName: String) : Parcelable {
    DUMBBELL("Mancuernas"),
    BARBELL("Barra"),
    MACHINE("Máquina"),
    BODYWEIGHT("Peso corporal"),
    CABLE("Cable")
}

@Parcelize
data class Exercise(
        val id: String,
        val name: String,
        val muscleGroup: MuscleGroup,
        val equipment: Equipment,
        val imageUrl: String
) : Parcelable
