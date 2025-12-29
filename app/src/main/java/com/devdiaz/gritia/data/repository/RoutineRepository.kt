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
    fun getRoutine(routineId: Long): Flow<Routine>
    suspend fun addRoutine(userId: Long, routine: Routine)
    suspend fun updateRestTime(routineId: Long, exerciseId: Long, seconds: Int)
}

class RoutineRepositoryImpl
@Inject
constructor(
        private val routineDao: RoutineDao,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RoutineRepository {

    override fun getRoutines(userId: Long): Flow<List<Routine>> =
            routineDao.getRoutines(userId).map { list ->
                list.map { routineWithExercises ->
                    val domainRoutine = routineWithExercises.routine.toDomain()
                    val domainExercises =
                            routineWithExercises.exercises.map { details ->
                                com.devdiaz.gritia.model.RoutineExercise(
                                        exerciseId = details.exercise.id,
                                        name = details.exercise.name,
                                        sets = details.routineExercise.targetSets,
                                        reps = details.routineExercise.targetReps,
                                        weight = details.routineExercise.targetWeight,
                                        muscleGroup = details.exercise.primaryMuscleGroup,
                                        restTimeSeconds = details.routineExercise.restTimeSeconds
                                )
                            }

                    val muscleGroups =
                            routineWithExercises
                                    .exercises
                                    .mapNotNull {
                                        try {
                                            com.devdiaz.gritia.model.MuscleGroup.valueOf(
                                                            it.exercise.primaryMuscleGroup
                                                                    .uppercase()
                                                    )
                                                    .displayName
                                        } catch (e: Exception) {
                                            null
                                        }
                                    }
                                    .distinct()
                                    .joinToString(" • ")

                    domainRoutine.copy(exercises = domainExercises, muscles = muscleGroups)
                }
            }

    override fun getRoutine(routineId: Long): Flow<Routine> =
            routineDao.getRoutine(routineId).map { routineWithExercises ->
                val domainRoutine = routineWithExercises.routine.toDomain()
                val domainExercises =
                        routineWithExercises.exercises.map { details ->
                            com.devdiaz.gritia.model.RoutineExercise(
                                    exerciseId = details.exercise.id,
                                    name = details.exercise.name,
                                    sets = details.routineExercise.targetSets,
                                    reps = details.routineExercise.targetReps,
                                    weight = details.routineExercise.targetWeight,
                                    restTimeSeconds = details.routineExercise.restTimeSeconds
                            )
                        }

                val muscleGroups =
                        routineWithExercises
                                .exercises
                                .mapNotNull {
                                    try {
                                        com.devdiaz.gritia.model.MuscleGroup.valueOf(
                                                        it.exercise.primaryMuscleGroup.uppercase()
                                                )
                                                .displayName
                                    } catch (e: Exception) {
                                        null
                                    }
                                }
                                .distinct()
                                .joinToString(" • ")

                domainRoutine.copy(exercises = domainExercises, muscles = muscleGroups)
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

    override suspend fun updateRestTime(routineId: Long, exerciseId: Long, seconds: Int) =
            withContext(ioDispatcher) { routineDao.updateRestTime(routineId, exerciseId, seconds) }
}
