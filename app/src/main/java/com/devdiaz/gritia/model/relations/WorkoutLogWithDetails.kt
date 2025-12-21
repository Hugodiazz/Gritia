package com.devdiaz.gritia.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.devdiaz.gritia.model.entities.ExercisePerformanceLogEntity
import com.devdiaz.gritia.model.entities.WorkoutLogEntity

data class WorkoutLogWithDetails(
        @Embedded val workoutLog: WorkoutLogEntity,
        @Relation(parentColumn = "id", entityColumn = "workout_log_id")
        val performanceLogs: List<ExercisePerformanceLogEntity>
)
