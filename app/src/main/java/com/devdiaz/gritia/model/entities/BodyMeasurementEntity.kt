package com.devdiaz.gritia.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
        tableName = "body_measurements",
        foreignKeys =
                [
                        ForeignKey(
                                entity = UserEntity::class,
                                parentColumns = ["id"],
                                childColumns = ["user_id"],
                                onDelete = ForeignKey.CASCADE
                        )]
)
data class BodyMeasurementEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo(name = "user_id", index = true) val userId: Long,
        val neck: Float?,
        val chest: Float?,
        val arm: Float?,
        val forearm: Float?,
        val waist: Float?,
        val hip: Float?,
        val leg: Float?,
        val calf: Float?,
        @ColumnInfo(name = "recorded_at") val recordedAt: Long = System.currentTimeMillis()
)
