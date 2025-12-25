package com.devdiaz.gritia.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.devdiaz.gritia.model.entities.RoutineEntity
import com.devdiaz.gritia.model.entities.RoutineExerciseEntity
import com.devdiaz.gritia.model.relations.RoutineWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Transaction
    @Query("SELECT * FROM routines WHERE user_id = :userId ORDER BY created_at DESC")
    fun getRoutines(userId: Long): Flow<List<RoutineWithExercises>>

    @Transaction
    @Query("SELECT * FROM routines WHERE id = :routineId")
    fun getRoutine(routineId: Long): Flow<RoutineWithExercises>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineExercise(routineExercise: RoutineExerciseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineExercises(routineExercises: List<RoutineExerciseEntity>)

    @Query(
            "UPDATE routine_exercises SET rest_time_seconds = :restTime WHERE routine_id = :routineId AND exercise_id = :exerciseId"
    )
    suspend fun updateRestTime(routineId: Long, exerciseId: Long, restTime: Int)
}
