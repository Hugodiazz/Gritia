package com.devdiaz.gritia.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.devdiaz.gritia.model.entities.ExercisePerformanceLogEntity
import com.devdiaz.gritia.model.entities.WorkoutLogEntity
import com.devdiaz.gritia.model.relations.WorkoutLogWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_logs WHERE user_id = :userId ORDER BY start_time DESC")
    fun getWorkoutLogs(userId: Long): Flow<List<WorkoutLogEntity>>

    @Transaction
    @Query("SELECT * FROM workout_logs WHERE id = :logId")
    fun getWorkoutDetails(logId: Long): Flow<WorkoutLogWithDetails>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(workoutLog: WorkoutLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerformanceLog(performanceLog: ExercisePerformanceLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerformanceLogs(performanceLogs: List<ExercisePerformanceLogEntity>)
}
