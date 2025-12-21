package com.devdiaz.gritia.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devdiaz.gritia.model.entities.BodyMeasurementEntity
import com.devdiaz.gritia.model.entities.UserMetricEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Query("SELECT * FROM body_measurements WHERE user_id = :userId ORDER BY recorded_at DESC")
    fun getBodyMeasurements(userId: Long): Flow<List<BodyMeasurementEntity>>

    @Query("SELECT * FROM user_metrics WHERE user_id = :userId ORDER BY recorded_at DESC")
    fun getUserMetrics(userId: Long): Flow<List<UserMetricEntity>>

    @Query(
            "SELECT * FROM user_metrics WHERE user_id = :userId AND recorded_at BETWEEN :start AND :end LIMIT 1"
    )
    suspend fun getUserMetricByDateRange(userId: Long, start: Long, end: Long): UserMetricEntity?

    @Query(
            "SELECT * FROM body_measurements WHERE user_id = :userId AND recorded_at BETWEEN :start AND :end LIMIT 1"
    )
    suspend fun getBodyMeasurementByDateRange(
            userId: Long,
            start: Long,
            end: Long
    ): BodyMeasurementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: BodyMeasurementEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetric(metric: UserMetricEntity)

    @Query("DELETE FROM user_metrics WHERE id = :id") suspend fun deleteMetricById(id: Long)

    @Query("DELETE FROM body_measurements WHERE id = :id")
    suspend fun deleteMeasurementById(id: Long)
}
