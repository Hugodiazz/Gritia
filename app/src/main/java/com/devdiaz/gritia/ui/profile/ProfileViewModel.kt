package com.devdiaz.gritia.ui.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileUiState(
        val name: String = "Hugo Zaid",
        val memberStatus: String = "Premium",
        val joinDate: String = "Miembro desde Nov 2025",
        val profileImageUrl: String =
                "https://lh3.googleusercontent.com/aida-public/AB6AXuAdHgc3hgvJSTP26FIhR7ewM_zbzM5n1zti1RoI37qSmURD8PCWe9z12rzZD767P8pth3-iqJXpb_RvnAI4HuuIY3wf8ElQO5unB4UceEVH3h5YS2imuBudtNlZIun5Q8C6juKoziDoBH-dqK2Ua26l8slJf4UUVmN4sgrfZx94W7SC4MDTR7jgGh7vumPzzZP8KP0eecW8B9565e35tzWNbXITyMJ1Qd4WVpeJxaIlz0QFxgIQNVwsNFOJi_MYRsQnCSq-YBHSMzs",
        val workoutsCount: Int = 142,
        val workoutsTrend: Int = 2, // +2 this week
        val volumeTotal: String = "45k",
        val volumeUnit: String = "kg",
        val volumeTrendPercent: Int = 15, // +15% mo.
        val weeklyActivity: String = "8 hrs 20 min",
        val activityStatus: String = "On Track"
)

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun updateName(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    // Placeholder for other actions
    fun onSettingsClicked() {
        // TODO: Navigation to settings
    }
}
