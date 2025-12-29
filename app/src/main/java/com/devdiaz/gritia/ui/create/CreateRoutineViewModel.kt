package com.devdiaz.gritia.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdiaz.gritia.data.repository.RoutineRepository
import com.devdiaz.gritia.data.repository.UserRepository
import com.devdiaz.gritia.model.Routine
import com.devdiaz.gritia.model.RoutineExercise
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CreateRoutineViewModel
@Inject
constructor(
        private val routineRepository: RoutineRepository,
        private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateRoutineUiState())
    val uiState: StateFlow<CreateRoutineUiState> = _uiState.asStateFlow()

    fun setRoutineName(name: String) {
        _uiState.update { it.copy(routineName = name) }
    }

    fun toggleDay(index: Int) {
        _uiState.update { currentState ->
            val newSchedule = currentState.schedule.toMutableList()
            if (index in newSchedule.indices) {
                newSchedule[index] = !newSchedule[index]
            }
            currentState.copy(schedule = newSchedule)
        }
    }

    fun addExercises(newExercises: List<com.devdiaz.gritia.model.Exercise>) {
        _uiState.update { currentState ->
            val additionalExercises =
                    newExercises.map { exercise ->
                        RoutineExerciseState(
                                id = java.util.UUID.randomUUID().toString(),
                                originalExerciseId = exercise.id,
                                name = exercise.name,
                                muscle = exercise.muscleGroup.displayName,
                                equipment = exercise.equipment.displayName,
                                sets = listOf(RoutineSetState("1", 1, "0", ""))
                        )
                    }
            currentState.copy(exercises = currentState.exercises + additionalExercises)
        }
    }

    fun removeExercise(exerciseId: String) {
        _uiState.update { currentState ->
            currentState.copy(exercises = currentState.exercises.filterNot { it.id == exerciseId })
        }
    }

    fun addSet(exerciseId: String) {
        _uiState.update { currentState ->
            val updatedExercises =
                    currentState.exercises.map { exercise ->
                        if (exercise.id == exerciseId) {
                            val newSetNumber = exercise.sets.size + 1
                            val newSet =
                                    RoutineSetState(
                                            id = java.util.UUID.randomUUID().toString(),
                                            setNumber = newSetNumber
                                    )
                            exercise.copy(sets = exercise.sets + newSet)
                        } else {
                            exercise
                        }
                    }
            currentState.copy(exercises = updatedExercises)
        }
    }

    fun removeSet(exerciseId: String, setIndex: Int) {
        _uiState.update { currentState ->
            val updatedExercises =
                    currentState.exercises.map { exercise ->
                        if (exercise.id == exerciseId && setIndex in exercise.sets.indices) {
                            // Filter out the set at setIndex
                            val remainingSets =
                                    exercise.sets.filterIndexed { index, _ -> index != setIndex }
                            // Reorder set numbers
                            val reorderedSets =
                                    remainingSets.mapIndexed { index, set ->
                                        set.copy(setNumber = index + 1)
                                    }
                            exercise.copy(sets = reorderedSets)
                        } else {
                            exercise
                        }
                    }
            currentState.copy(exercises = updatedExercises)
        }
    }

    fun updateSet(exerciseId: String, setIndex: Int, kg: String? = null, reps: String? = null) {
        _uiState.update { currentState ->
            val updatedExercises =
                    currentState.exercises.map { exercise ->
                        if (exercise.id == exerciseId && setIndex in exercise.sets.indices) {
                            val currentSets = exercise.sets.toMutableList()
                            val currentSet = currentSets[setIndex]
                            val updatedSet =
                                    currentSet.copy(
                                            kg = kg ?: currentSet.kg,
                                            reps = reps ?: currentSet.reps
                                    )
                            currentSets[setIndex] = updatedSet
                            exercise.copy(sets = currentSets)
                        } else {
                            exercise
                        }
                    }
            currentState.copy(exercises = updatedExercises)
        }
    }

    fun saveRoutine(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val user = userRepository.getUser().firstOrNull()

            if (user != null) {
                // Infer muscles from exercises (distinct muscle groups)
                val muscles =
                        currentState
                                .exercises
                                .map { it.muscle }
                                .distinct()
                                .take(3)
                                .joinToString(" • ")

                val domainExercises =
                        currentState.exercises.map { state ->
                            RoutineExercise(
                                    exerciseId = state.originalExerciseId.toLong(),
                                    name = state.name,
                                    sets = state.sets.size,
                                    reps = state.sets.firstOrNull()?.reps?.toIntOrNull() ?: 0,
                                    weight = state.sets.firstOrNull()?.kg?.toFloatOrNull()
                            )
                        }

                val newRoutine =
                        Routine(
                                title = currentState.routineName,
                                muscles = muscles.ifEmpty { "Mixed" },
                                schedule = currentState.schedule,
                                imageUrl = "",
                                exercises = domainExercises
                        )

                routineRepository.addRoutine(user.id, newRoutine)
                onSuccess()
            }
        }
    }
}

data class CreateRoutineUiState(
        val routineName: String = "",
        val exercises: List<RoutineExerciseState> = emptyList(),
        val schedule: List<Boolean> = List(7) { false }
)

data class RoutineExerciseState(
    val id: String,
    val originalExerciseId: String,
    val name: String,
    val muscle: String,
    val equipment: String,
    val sets: List<RoutineSetState>
)

data class RoutineSetState(
        val id: String,
        val setNumber: Int,
        val kg: String = "",
        val reps: String = ""
)
