package com.devdiaz.gritia.data.repository

import com.devdiaz.gritia.model.dao.WorkoutDao
import com.devdiaz.gritia.model.entities.WorkoutLogEntity
import com.devdiaz.gritia.model.relations.WorkoutLogWithDetails
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import com.devdiaz.gritia.di.IoDispatcher


interface WorkoutRepository {
    fun getWorkoutLogs(userId: Long): Flow<List<WorkoutLogEntity>>
    fun getWorkoutDetails(logId: Long): Flow<WorkoutLogWithDetails>
    suspend fun saveWorkoutLog(log: WorkoutLogEntity): Long
}


class WorkoutRepositoryImpl
@Inject
constructor(
        private val workoutDao: WorkoutDao,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : WorkoutRepository {

    override fun getWorkoutLogs(userId: Long): Flow<List<WorkoutLogEntity>> =
            workoutDao.getWorkoutLogs(userId)

    override fun getWorkoutDetails(logId: Long): Flow<WorkoutLogWithDetails> =
            workoutDao.getWorkoutDetails(logId)

    override suspend fun saveWorkoutLog(log: WorkoutLogEntity): Long =
            withContext(ioDispatcher) { workoutDao.insertWorkoutLog(log) }
}
