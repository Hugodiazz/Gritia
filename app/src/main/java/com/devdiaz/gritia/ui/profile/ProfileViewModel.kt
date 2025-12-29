package com.devdiaz.gritia.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdiaz.gritia.data.repository.BodyMetricsRepository
import com.devdiaz.gritia.data.repository.UserRepository
import com.devdiaz.gritia.data.repository.WorkoutRepository
import com.devdiaz.gritia.model.Gender
import com.devdiaz.gritia.model.entities.UserMetricEntity
import com.devdiaz.gritia.model.entities.WorkoutLogEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ProfileDialogType {
    GENDER,
    BIRTH_DATE,
    HEIGHT,
    WEIGHT
}

data class ProfileUiState(
        val name: String = "",
        val email: String = "",
        val memberStatus: String = "Miembro", // Could be dynamic later
        val joinDate: String = "",
        val profileImageUrl: String = "", // User entity might not have this yet
        val gender: String = "",
        val birthDate: String = "",
        val height: String = "",
        val currentWeight: String = "",
        // Stats
        val workoutsCount: Int = 0,
        val workoutsTrend: Int = 0,
        val volumeTotal: String = "0",
        val volumeUnit: String = "kg",
        val volumeTrendPercent: Int = 0,
        val weeklyActivity: String = "0 hrs",
        val activityStatus: String = "Sin actividad",
        // Dialog State
        val activeDialog: ProfileDialogType? = null
)

@HiltViewModel
class ProfileViewModel
@Inject
constructor(
        private val userRepository: UserRepository,
        private val workoutRepository: WorkoutRepository,
        private val bodyMetricsRepository: BodyMetricsRepository,
        private val authRepository: com.devdiaz.gritia.data.repository.AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _navigationEvents = kotlinx.coroutines.channels.Channel<ProfileNavigationEvent>()
    val navigationEvents = _navigationEvents.receiveAsFlow()

    init {
        loadUserProfile()
        loadUserStats()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            userRepository.getUser().collect { user ->
                user?.let {
                    _uiState.update { currentState ->
                        currentState.copy(
                                name = it.name,
                                email = it.email,
                                joinDate = formatJoinDate(it.createdAt),
                                gender =
                                        when (it.gender) {
                                            com.devdiaz.gritia.model.Gender.MALE -> "Masculino"
                                            com.devdiaz.gritia.model.Gender.FEMALE -> "Femenino"
                                            else -> "Otro"
                                        },
                                birthDate = it.birthDate?.toString() ?: "No definida",
                                height = it.height?.toString() ?: "-",
                                currentWeight = it.currentWeight?.toString() ?: "-"
                        )
                    }
                }
            }
        }
    }

    private fun loadUserStats() {
        viewModelScope.launch {
            userRepository.getUser().collect { user ->
                if (user != null) {
                    workoutRepository.getWorkoutLogs(user.id).collect { logs ->
                        calculateAndEmitStats(logs)
                    }
                }
            }
        }
    }

    private fun calculateAndEmitStats(logs: List<WorkoutLogEntity>) {
        val now = LocalDateTime.now()
        val startOfWeek = now.minusDays(7)
        val startOfLastWeek = startOfWeek.minusDays(7)
        val startOfMonth = now.withDayOfMonth(1)
        val startOfLastMonth = startOfMonth.minusMonths(1)

        // Workouts
        val totalWorkouts = logs.size
        val workoutsThisWeek = logs.count { isAfter(it.startTime, startOfWeek) }
        val workoutsLastWeek =
                logs.count {
                    isAfter(it.startTime, startOfLastWeek) && isBefore(it.startTime, startOfWeek)
                }
        val workoutsTrend = workoutsThisWeek - workoutsLastWeek

        // Volume
        val totalVolume = logs.sumOf { it.totalVolume?.toDouble() ?: 0.0 }
        val volumeThisMonth =
                logs.filter { isAfter(it.startTime, startOfMonth) }.sumOf {
                    it.totalVolume?.toDouble() ?: 0.0
                }
        val volumeLastMonth =
                logs
                        .filter {
                            isAfter(it.startTime, startOfLastMonth) &&
                                    isBefore(it.startTime, startOfMonth)
                        }
                        .sumOf { it.totalVolume?.toDouble() ?: 0.0 }

        val volumeTrendPercent =
                if (volumeLastMonth > 0) {
                    (((volumeThisMonth - volumeLastMonth) / volumeLastMonth) * 100).toInt()
                } else {
                    if (volumeThisMonth > 0) 100 else 0
                }

        // Activity (Duration)
        val weeklyActivityHours =
                logs.filter { isAfter(it.startTime, startOfWeek) }.sumOf {
                    it.hoursActive?.toDouble() ?: 0.0
                }

        _uiState.update { currentState ->
            currentState.copy(
                    workoutsCount = totalWorkouts,
                    workoutsTrend = workoutsTrend,
                    volumeTotal = String.format("%.1f", totalVolume), // Simplify volume display
                    volumeTrendPercent = volumeTrendPercent,
                    weeklyActivity = String.format("%.1f hrs", weeklyActivityHours),
                    activityStatus = if (weeklyActivityHours > 0) "Activo" else "Sin actividad"
            )
        }
    }

    private fun isAfter(timestamp: Long, dateTime: LocalDateTime): Boolean {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .isAfter(dateTime)
    }

    private fun isBefore(timestamp: Long, dateTime: LocalDateTime): Boolean {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .isBefore(dateTime)
    }

    private fun formatJoinDate(createdAtMillis: Long): String {
        if (createdAtMillis == 0L) return "Reciente"
        return try {
            val instant = java.time.Instant.ofEpochMilli(createdAtMillis)
            val date = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
            val formatter =
                    java.time.format.DateTimeFormatter.ofPattern(
                            "MMM yyyy",
                            java.util.Locale("es", "ES")
                    )
            "Miembro desde ${date.format(formatter).replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }}"
        } catch (e: Exception) {
            "Miembro"
        }
    }

    fun updateName(newName: String) {
        // implementation for local update if needed, but usually goes through repository
    }

    fun showDialog(type: ProfileDialogType) {
        _uiState.update { it.copy(activeDialog = type) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(activeDialog = null) }
    }

    fun updateGender(gender: String) {
        viewModelScope.launch {
            val currentUser = userRepository.getUser().firstOrNull() ?: return@launch
            val updatedUser =
                    currentUser.copy(
                            gender =
                                    when (gender) {
                                        "Masculino" -> com.devdiaz.gritia.model.Gender.MALE
                                        "Femenino" -> com.devdiaz.gritia.model.Gender.FEMALE
                                        else -> com.devdiaz.gritia.model.Gender.OTHER
                                    }
                    )
            userRepository.saveUser(updatedUser)
            dismissDialog()
        }
    }

    fun updateBirthDate(dateMillis: Long) {
        viewModelScope.launch {
            val currentUser = userRepository.getUser().firstOrNull() ?: return@launch
            val instant = java.time.Instant.ofEpochMilli(dateMillis)
            val zonedDateTime = instant.atZone(java.time.ZoneId.of("UTC"))
            val localDate = zonedDateTime.toLocalDate()

            val updatedUser = currentUser.copy(birthDate = localDate)
            userRepository.saveUser(updatedUser)
            dismissDialog()
        }
    }

    fun updateHeight(height: Float) {
        viewModelScope.launch {
            val currentUser = userRepository.getUser().firstOrNull() ?: return@launch
            val updatedUser = currentUser.copy(height = height)
            userRepository.saveUser(updatedUser)
            dismissDialog()
        }
    }

    fun updateWeight(weight: Float) {
        viewModelScope.launch {
            val currentUser = userRepository.getUser().firstOrNull() ?: return@launch

            // 1. Update user current weight
            val updatedUser = currentUser.copy(currentWeight = weight)
            userRepository.saveUser(updatedUser)

            // 2. Add new metric entry
            val newMetric =
                    UserMetricEntity(
                            userId = currentUser.id,
                            weight = weight,
                            bmi = calculateBmi(weight, currentUser.height),
                            bodyFatPercentage = null, // Can't calculate without more data
                            measurementId = null,
                            recordedAt = System.currentTimeMillis()
                    )
            bodyMetricsRepository.addMetric(newMetric)

            dismissDialog()
        }
    }

    private fun calculateBmi(weightKg: Float, heightCm: Float?): Float? {
        if (heightCm == null || heightCm <= 0) return null
        val heightM = heightCm / 100f
        return weightKg / (heightM * heightM)
    }

    fun onSettingsClicked() {
        // TODO: Navigation to settings
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
            _navigationEvents.send(ProfileNavigationEvent.NavigateToLogin)
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            authRepository.deleteAccount()
            _navigationEvents.send(ProfileNavigationEvent.NavigateToLogin)
        }
    }
}

sealed class ProfileNavigationEvent {
    data object NavigateToLogin : ProfileNavigationEvent()
}
