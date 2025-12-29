package com.devdiaz.gritia.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdiaz.gritia.data.repository.ExerciseRepository
import com.devdiaz.gritia.model.Exercise
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ExerciseSelectorViewModel
@Inject
constructor(private val exerciseRepository: ExerciseRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val exercises: StateFlow<List<Exercise>> =
            exerciseRepository
                    .getAllExercises()
                    .combine(_searchQuery) { exercises, query ->
                        if (query.isBlank()) {
                            exercises
                        } else {
                            exercises.filter { it.name.contains(query, ignoreCase = true) }
                        }
                    }
                    .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue = emptyList()
                    )

    init {
        viewModelScope.launch { exerciseRepository.seedInitialExercises() }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
