package com.devdiaz.gritia.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdiaz.gritia.data.repository.RoutineRepository
import com.devdiaz.gritia.data.repository.UserRepository
import com.devdiaz.gritia.model.Routine
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _routines = MutableStateFlow<List<Routine>>(emptyList())
    val routines: StateFlow<List<Routine>> = _routines.asStateFlow()

    init {
        loadRoutines()
    }

    private fun loadRoutines() {
        viewModelScope.launch {
            // Observe the user so updates (e.g. login) trigger refresh
            userRepository.getUser().collect { user ->
                if (user != null) {
                    routineRepository.getRoutines(user.id).collect { userRoutines ->
                        _routines.value = userRoutines
                    }
                } else {
                    _routines.value = emptyList()
                }
            }
        }
    }
}
