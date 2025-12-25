package com.devdiaz.gritia.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.devdiaz.gritia.model.entities.RoutineEntity
import com.devdiaz.gritia.model.entities.RoutineExerciseEntity

data class RoutineWithExercises(
        @Embedded val routine: RoutineEntity,
        @Relation(
                parentColumn = "id",
                entityColumn = "routine_id",
                entity = RoutineExerciseEntity::class
        )
        val exercises: List<RoutineExerciseWithDetails>
)
