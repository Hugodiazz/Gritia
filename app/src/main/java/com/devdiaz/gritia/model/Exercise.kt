package com.devdiaz.gritia.model

enum class MuscleGroup(val displayName: String) {
    ALL("Todos"),
    CHEST("Pecho"),
    BACK("Espalda"),
    LEGS("Piernas"),
    ARMS("Brazos"),
    SHOULDERS("Hombros"),
    CORE("Core")

}

enum class Equipment(val displayName: String) {
    DUMBBELL("Mancuernas"),
    BARBELL("Barra"),
    MACHINE("Máquina"),
    BODYWEIGHT("Peso corporal"),
    CABLE("Cable")
}

data class Exercise(
        val id: String,
        val name: String,
        val muscleGroup: MuscleGroup,
        val equipment: Equipment,
        val imageUrl: String
)
