package com.devdiaz.gritia.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.devdiaz.gritia.model.entities.ExerciseEntity
import com.devdiaz.gritia.model.entities.RoutineExerciseEntity

data class RoutineExerciseWithDetails(
        @Embedded val routineExercise: RoutineExerciseEntity,
        @Relation(parentColumn = "exercise_id", entityColumn = "id") val exercise: ExerciseEntity
)
