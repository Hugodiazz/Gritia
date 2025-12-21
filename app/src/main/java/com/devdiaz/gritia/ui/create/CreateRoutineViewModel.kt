package com.devdiaz.gritia.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdiaz.gritia.data.repository.RoutineRepository
import com.devdiaz.gritia.data.repository.UserRepository
import com.devdiaz.gritia.model.Routine
import com.devdiaz.gritia.model.RoutineExercise
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@HiltViewModel
class CreateRoutineViewModel
@Inject
constructor(
        private val routineRepository: RoutineRepository,
        private val userRepository: UserRepository
) : ViewModel() {

    fun saveRoutine(name: String, exercises: List<RoutineExerciseState>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val user = userRepository.getUser().firstOrNull()
            if (user != null) {
                // Infer muscles from exercises (distinct muscle groups)
                val muscles = exercises.map { it.muscle }.distinct().take(3).joinToString(" • ")

                // Mock schedule (all false or default)
                val schedule = List(7) { false }

                val domainExercises =
                        exercises.map { state ->
                            RoutineExercise(
                                    exerciseId = state.id.toLongOrNull() ?: 0L,
                                    name = state.name,
                                    sets = state.sets.size,
                                    reps = state.sets.firstOrNull()?.reps?.toIntOrNull() ?: 0,
                                    weight = state.sets.firstOrNull()?.kg?.toFloatOrNull()
                            )
                        }

                val newRoutine =
                        Routine(
                                title = name,
                                muscles = muscles.ifEmpty { "Mixed" },
                                schedule = schedule,
                                imageUrl = "",
                                exercises = domainExercises
                        )

                // Note: The Repository uses userId (Long) and Routine (Domain)
                // Routine Entity generation in Repository handles the ID creation
                routineRepository.addRoutine(user.id, newRoutine)
                onSuccess()
            } else {
                // Handle error
            }
        }
    }
}
