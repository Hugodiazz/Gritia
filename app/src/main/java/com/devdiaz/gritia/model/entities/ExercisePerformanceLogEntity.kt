package com.devdiaz.gritia.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
        tableName = "exercise_performance_logs",
        foreignKeys =
                [
                        ForeignKey(
                                entity = WorkoutLogEntity::class,
                                parentColumns = ["id"],
                                childColumns = ["workout_log_id"],
                                onDelete = ForeignKey.CASCADE
                        )]
)
data class ExercisePerformanceLogEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo(name = "workout_log_id", index = true) val workoutLogId: Long,
        @ColumnInfo(name = "exercise_name_snapshot") val exerciseNameSnapshot: String?,
        @ColumnInfo(name = "muscle_group_snapshot") val muscleGroupSnapshot: String?,
        @ColumnInfo(name = "sets_completed") val setsCompleted: Int?,
        @ColumnInfo(name = "reps_completed") val repsCompleted: Int?,
        @ColumnInfo(name = "weight_used") val weightUsed: Float?,
        @ColumnInfo(name = "rest_time_used") val restTimeUsed: Int?
)
