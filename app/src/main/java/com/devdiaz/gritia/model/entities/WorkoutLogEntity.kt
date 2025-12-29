
package com.devdiaz.gritia.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.ColumnInfo

@Entity(
    tableName = "workout_logs",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class WorkoutLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "user_id", index = true)
    val userId: Long,
    @ColumnInfo(name = "routine_name_snapshot")
    val routineNameSnapshot: String?,
    @ColumnInfo(name = "start_time")
    val startTime: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "end_time")
    val endTime: Long?,
    @ColumnInfo(name = "total_volume")
    val totalVolume: Float? = 0f,
    @ColumnInfo(name = "user_notes")
    val userNotes: String?,
    @ColumnInfo(name = "hours_active")
    val hoursActive: Float?
)
