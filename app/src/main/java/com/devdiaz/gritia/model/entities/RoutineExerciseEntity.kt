package com.devdiaz.gritia.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
        tableName = "routine_exercises",
        foreignKeys =
                [
                        ForeignKey(
                                entity = RoutineEntity::class,
                                parentColumns = ["id"],
                                childColumns = ["routine_id"],
                                onDelete = ForeignKey.CASCADE
                        ),
                        ForeignKey(
                                entity = ExerciseEntity::class,
                                parentColumns = ["id"],
                                childColumns = ["exercise_id"],
                                onDelete = ForeignKey.NO_ACTION // Prevent accidental deletion of
                                // exercise history if exercise is
                                // deleted (though typically exercises
                                // aren't deleted)
                                )]
)
data class RoutineExerciseEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo(name = "routine_id", index = true) val routineId: Long,
        @ColumnInfo(name = "exercise_id", index = true)
        val exerciseId:
                Long?, // Nullable if exercise is deleted? SQL says references exercises(id), so
        // usually not null unless set null. SQL didn't specify on delete.
        @ColumnInfo(name = "target_sets") val targetSets: Int,
        @ColumnInfo(name = "target_reps") val targetReps: Int,
        @ColumnInfo(name = "target_weight") val targetWeight: Float?,
        @ColumnInfo(name = "rest_time_seconds") val restTimeSeconds: Int = 60,
        @ColumnInfo(name = "order_in_routine") val orderInRoutine: Int?
)
