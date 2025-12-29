package com.devdiaz.gritia.ui.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdiaz.gritia.data.repository.RoutineRepository
import com.devdiaz.gritia.data.repository.UserRepository
import com.devdiaz.gritia.data.repository.WorkoutRepository
import com.devdiaz.gritia.model.entities.ExercisePerformanceLogEntity
import com.devdiaz.gritia.model.entities.WorkoutLogEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class WorkoutNavigationEvent {
    data class NavigateToSummary(
            val routineName: String,
            val durationSeconds: Long,
            val totalVolume: Float
    ) : WorkoutNavigationEvent()
}

@HiltViewModel
class WorkoutLogViewModel
@Inject
constructor(
        private val savedStateHandle: SavedStateHandle,
        private val routineRepository: RoutineRepository,
        private val workoutRepository: WorkoutRepository,
        private val userRepository: UserRepository
) : ViewModel() {

    private val routineId: Long = checkNotNull(savedStateHandle["routineId"])

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<WorkoutNavigationEvent>()
    val navigationEvent: SharedFlow<WorkoutNavigationEvent> = _navigationEvent.asSharedFlow()

    private var workoutTimerJob: Job? = null
    private var restTimerJob: Job? = null

    init {
        loadRoutine()
    }

    private fun loadRoutine() {
        viewModelScope.launch {
            routineRepository.getRoutine(routineId).collect { routine ->
                val exercises =
                        routine.exercises.map { routineExercise ->
                            WorkoutExerciseUiState(
                                    exerciseId = routineExercise.exerciseId,
                                    name = routineExercise.name,
                                    muscleGroup = routineExercise.muscleGroup,
                                    sets =
                                            (1..routineExercise.sets).map { setNum ->
                                                WorkoutSetUiState(
                                                        setNumber = setNum,
                                                        targetWeight = routineExercise.weight,
                                                        targetReps = routineExercise.reps,
                                                        actualWeight =
                                                                routineExercise.weight?.let {
                                                                    it.toInt().toString()
                                                                }
                                                                        ?: "",
                                                        actualReps = routineExercise.reps.toString()
                                                )
                                            },
                                    restTimeSeconds = routineExercise.restTimeSeconds
                            )
                        }
                _uiState.update { it.copy(routineName = routine.title, exercises = exercises) }
            }
        }
    }

    fun toggleWorkoutState() {
        if (_uiState.value.isWorkoutActive) {
            finishWorkout()
        } else {
            if (!_uiState.value.isStarting) {
                startWorkoutCountdown()
            }
        }
    }

    private fun startWorkoutCountdown() {
        _uiState.update { it.copy(isStarting = true, startTimerSeconds = 3) }
        viewModelScope.launch {
            while (_uiState.value.startTimerSeconds > 0) {
                delay(1000)
                _uiState.update { it.copy(startTimerSeconds = it.startTimerSeconds - 1) }
            }
            startWorkout()
        }
    }

    private fun startWorkout() {
        _uiState.update { it.copy(isWorkoutActive = true, isStarting = false) }
        startWorkoutTimer()
    }

    private fun finishWorkout() {
        stopWorkoutTimer()
        saveWorkout()
        _uiState.update { it.copy(isWorkoutActive = false) }
    }

    private fun calculateTotalVolume(): Float {
        var volume = 0f
        _uiState.value.exercises.forEach { exercise ->
            exercise.sets.forEach { set ->
                if (set.isCompleted) {
                    val weight = set.actualWeight.toFloatOrNull() ?: 0f
                    val reps = set.actualReps.toIntOrNull() ?: 0
                    volume += weight * reps
                }
            }
        }
        return volume
    }

    private fun startWorkoutTimer() {
        workoutTimerJob?.cancel()
        workoutTimerJob =
                viewModelScope.launch {
                    while (true) {
                        delay(1000)
                        _uiState.update { it.copy(elapsedTimeSeconds = it.elapsedTimeSeconds + 1) }
                    }
                }
    }

    private fun stopWorkoutTimer() {
        workoutTimerJob?.cancel()
    }

    fun updateSet(exerciseIndex: Int, setIndex: Int, weight: String, reps: String) {
        if (!_uiState.value.isWorkoutActive) return

        _uiState.update { currentState ->
            val updatedExercises = currentState.exercises.toMutableList()
            val exercise = updatedExercises[exerciseIndex]
            val updatedSets = exercise.sets.toMutableList()
            updatedSets[setIndex] =
                    updatedSets[setIndex].copy(actualWeight = weight, actualReps = reps)
            updatedExercises[exerciseIndex] = exercise.copy(sets = updatedSets)
            currentState.copy(exercises = updatedExercises)
        }
    }

    fun addSet(exerciseIndex: Int) {
        if (!_uiState.value.isWorkoutActive) return

        _uiState.update { currentState ->
            val updatedExercises = currentState.exercises.toMutableList()
            val exercise = updatedExercises[exerciseIndex]
            val previousSet = exercise.sets.lastOrNull()
            val newSetNumber = (previousSet?.setNumber ?: 0) + 1

            val newSet =
                    WorkoutSetUiState(
                            setNumber = newSetNumber,
                            targetWeight = previousSet?.targetWeight,
                            targetReps = previousSet?.targetReps ?: 0,
                            actualWeight = previousSet?.actualWeight ?: "",
                            actualReps = previousSet?.actualReps ?: "",
                            isCompleted = false
                    )

            val updatedSets = exercise.sets + newSet
            updatedExercises[exerciseIndex] = exercise.copy(sets = updatedSets)
            currentState.copy(exercises = updatedExercises)
        }
    }

    fun updateRestTime(exerciseIndex: Int, seconds: Int) {
        _uiState.update { currentState ->
            val updatedExercises = currentState.exercises.toMutableList()
            val exercise = updatedExercises[exerciseIndex]
            updatedExercises[exerciseIndex] = exercise.copy(restTimeSeconds = seconds)
            currentState.copy(exercises = updatedExercises)
        }
    }

    fun onSetCompleted(exerciseIndex: Int, setIndex: Int, isCompleted: Boolean) {
        if (!_uiState.value.isWorkoutActive) return

        val restSeconds =
                if (isCompleted) _uiState.value.exercises.getOrNull(exerciseIndex)?.restTimeSeconds
                else null

        _uiState.update { currentState ->
            val updatedExercises = currentState.exercises.toMutableList()
            val exercise = updatedExercises[exerciseIndex]
            val updatedSets = exercise.sets.toMutableList()

            // Only convert input to valid numbers for completion check if needed,
            // but for UI state just toggle
            updatedSets[setIndex] = updatedSets[setIndex].copy(isCompleted = isCompleted)
            updatedExercises[exerciseIndex] = exercise.copy(sets = updatedSets)

            currentState.copy(exercises = updatedExercises)
        }

        if (isCompleted && restSeconds != null) {
            startRestTimer(restSeconds)
        } else {
            // Cancel rest timer if unchecking? Maybe not desired, but let's keep it simple.
        }
    }

    private fun startRestTimer(restTime: Int) {
        // Use provided rest time
        _uiState.update { it.copy(restTimerSeconds = restTime, isRestTimerActive = true) }

        restTimerJob?.cancel()
        restTimerJob =
                viewModelScope.launch {
                    while (_uiState.value.restTimerSeconds > 0) {
                        delay(1000)
                        _uiState.update { it.copy(restTimerSeconds = it.restTimerSeconds - 1) }
                    }
                    _uiState.update { it.copy(isRestTimerActive = false) }
                }
    }

    fun stopRestTimer() {
        restTimerJob?.cancel()
        _uiState.update { it.copy(isRestTimerActive = false) }
    }

    fun addTimeRest(seconds: Int) {
        _uiState.update { it.copy(restTimerSeconds = it.restTimerSeconds + seconds) }
    }

    fun pauseRestTimer() {
        // Implement if needed
    }

    private fun saveWorkout() {
        viewModelScope.launch {
            val user = userRepository.getUser().firstOrNull() ?: return@launch
            val currentState = _uiState.value
            val totalVolume = calculateTotalVolume()

            // Create WorkoutLog
            val log =
                    WorkoutLogEntity(
                            userId = user.id,
                            routineNameSnapshot = currentState.routineName,
                            startTime =
                                    System.currentTimeMillis() -
                                            (currentState.elapsedTimeSeconds * 1000), // Approx
                            endTime = System.currentTimeMillis(),
                            userNotes = null,
                            hoursActive = currentState.elapsedTimeSeconds / 3600f,
                            totalVolume = totalVolume
                    )

            // Create Performance Logs
            val performanceLogs = mutableListOf<ExercisePerformanceLogEntity>()
            currentState.exercises.forEach { exercise ->
                exercise.sets.forEach { set ->
                    if (set.isCompleted) {
                        performanceLogs.add(
                                ExercisePerformanceLogEntity(
                                        workoutLogId = 0, // Set by Repository/Dao
                                        exerciseNameSnapshot = exercise.name,
                                        muscleGroupSnapshot = exercise.muscleGroup,
                                        setsCompleted = 1,
                                        repsCompleted = set.actualReps.toIntOrNull() ?: 0,
                                        weightUsed = set.actualWeight.toFloatOrNull() ?: 0f,
                                        restTimeUsed = exercise.restTimeSeconds
                                )
                        )
                    }
                }
            }

            // Perform persist logic for logs
            workoutRepository.saveCompleteWorkout(log, performanceLogs)

            // Update Rest Times in Routine
            currentState.exercises.forEach { exercise ->
                routineRepository.updateRestTime(
                        routineId = routineId,
                        exerciseId = exercise.exerciseId,
                        seconds = exercise.restTimeSeconds
                )
            }

            _navigationEvent.emit(
                    WorkoutNavigationEvent.NavigateToSummary(
                            routineName = _uiState.value.routineName,
                            durationSeconds = _uiState.value.elapsedTimeSeconds,
                            totalVolume = totalVolume
                    )
            )
        }
    }
}

data class WorkoutUiState(
        val routineName: String = "",
        val exercises: List<WorkoutExerciseUiState> = emptyList(),
        val isWorkoutActive: Boolean = false,
        val isStarting: Boolean = false,
        val startTimerSeconds: Int = 0,
        val elapsedTimeSeconds: Long = 0,
        val restTimerSeconds: Int = 0,
        val isRestTimerActive: Boolean = false
)

data class WorkoutExerciseUiState(
        val exerciseId: Long,
        val name: String,
        val muscleGroup: String,
        val sets: List<WorkoutSetUiState>,
        val restTimeSeconds: Int = 60
)

data class WorkoutSetUiState(
        val setNumber: Int,
        val targetWeight: Float?,
        val targetReps: Int,
        val actualWeight: String,
        val actualReps: String,
        val isCompleted: Boolean = false
)
