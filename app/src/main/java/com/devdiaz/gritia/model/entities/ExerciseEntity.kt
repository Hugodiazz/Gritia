package com.devdiaz.gritia.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val name: String,
        @ColumnInfo(name = "primary_muscle_group") val primaryMuscleGroup: String,
        @ColumnInfo(name = "movement_pattern")
        val movementPattern: String?, // Empuje, Tracción, etc.
        @ColumnInfo(name = "tool_type") val toolType: String? // Mancuerna, Polea, etc.
)
