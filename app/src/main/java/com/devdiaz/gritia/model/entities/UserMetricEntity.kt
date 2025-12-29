package com.devdiaz.gritia.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
        tableName = "user_metrics",
        foreignKeys =
                [
                        ForeignKey(
                                entity = UserEntity::class,
                                parentColumns = ["id"],
                                childColumns = ["user_id"],
                                onDelete = ForeignKey.CASCADE
                        ),
                        ForeignKey(
                                entity = BodyMeasurementEntity::class,
                                parentColumns = ["id"],
                                childColumns = ["measurement_id"],
                                onDelete = ForeignKey.SET_NULL
                        )]
)
data class UserMetricEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo(name = "user_id", index = true) val userId: Long,
        @ColumnInfo(name = "measurement_id", index = true) val measurementId: Long?,
        val weight: Float,
        val bmi: Float?,
        @ColumnInfo(name = "body_fat_percentage") val bodyFatPercentage: Float?,
        @ColumnInfo(name = "recorded_at") val recordedAt: Long = System.currentTimeMillis()
)
