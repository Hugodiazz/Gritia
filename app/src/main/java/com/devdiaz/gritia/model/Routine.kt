package com.devdiaz.gritia.model

data class Routine(
        val title: String,
        val muscles: String,
        val schedule: List<Boolean>, // 7 booleans for M T W T F S S
        val imageUrl: String,
        val exercises: List<RoutineExercise> = emptyList()
)

data class RoutineExercise(
        val exerciseId: Long, // Or String? Exercise model had String id "1", "2". Check Exercise.kt
        val name: String,
        val sets: Int,
        val reps: Int,
        val weight: Float?
)
