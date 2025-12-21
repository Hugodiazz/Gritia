package com.devdiaz.gritia.data.repository

import com.devdiaz.gritia.di.IoDispatcher
import com.devdiaz.gritia.model.Routine
import com.devdiaz.gritia.model.dao.RoutineDao
import com.devdiaz.gritia.model.mappers.toDomain
import com.devdiaz.gritia.model.mappers.toEntity
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface RoutineRepository {
    fun getRoutines(userId: Long): Flow<List<Routine>>
    suspend fun addRoutine(userId: Long, routine: Routine)
}

class RoutineRepositoryImpl
@Inject
constructor(
        private val routineDao: RoutineDao,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RoutineRepository {

    override fun getRoutines(userId: Long): Flow<List<Routine>> =
            routineDao.getRoutines(userId).map { list ->
                list.map {
                    it.routine.toDomain()
                } // Note: Currently ignoring exercises in Domain model mapper? Need to check.
            }

    override suspend fun addRoutine(userId: Long, routine: Routine) =
            withContext(ioDispatcher) {
                val routineEntity = routine.toEntity(userId)
                val routineId = routineDao.insertRoutine(routineEntity)

                val routineExercises =
                        routine.exercises.mapIndexed { index, exercise ->
                            com.devdiaz.gritia.model.entities.RoutineExerciseEntity(
                                    routineId = routineId,
                                    exerciseId = exercise.exerciseId,
                                    targetSets = exercise.sets,
                                    targetReps = exercise.reps,
                                    targetWeight = exercise.weight,
                                    orderInRoutine = index
                            )
                        }
                if (routineExercises.isNotEmpty()) {
                    routineDao.insertRoutineExercises(routineExercises)
                }
                Unit
            }
}
