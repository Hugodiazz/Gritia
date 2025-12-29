package com.devdiaz.gritia.ui.add

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdiaz.gritia.data.repository.BodyMetricsRepository
import com.devdiaz.gritia.data.repository.UserRepository
import com.devdiaz.gritia.di.IoDispatcher
import com.devdiaz.gritia.model.Gender
import com.devdiaz.gritia.model.entities.BodyMeasurementEntity
import com.devdiaz.gritia.model.entities.UserMetricEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AddViewModel
@Inject
constructor(
        private val userRepository: UserRepository,
        private val bodyMetricsRepository: BodyMetricsRepository,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddUiState())
    val uiState: StateFlow<AddUiState> = _uiState.asStateFlow()

    private val _saveSuccess = Channel<Unit>()
    val saveSuccess = _saveSuccess.receiveAsFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val user = userRepository.getUser().firstOrNull() ?: return@launch
            val timestamp = System.currentTimeMillis()
            // Identify "start of today" to define what "previous" means (strictly before today)
            val zoneId = java.time.ZoneId.systemDefault()
            val now = java.time.Instant.ofEpochMilli(timestamp).atZone(zoneId)
            val startOfDay = now.toLocalDate().atStartOfDay(zoneId).toInstant().toEpochMilli()

            // Fetch previous records (strictly before start of today)
            val prevMetric = bodyMetricsRepository.getLatestMetricBeforeDate(user.id, startOfDay)
            val prevMsmt = bodyMetricsRepository.getLatestMeasurementBeforeDate(user.id, startOfDay)

            // Map safe previous values
            val prevMeasurements = mutableMapOf<MeasurementType, String>()
            prevMsmt?.let {
                it.neck?.let { v -> prevMeasurements[MeasurementType.NECK] = v.toString() }
                it.chest?.let { v -> prevMeasurements[MeasurementType.CHEST] = v.toString() }
                it.arm?.let { v -> prevMeasurements[MeasurementType.BICEP] = v.toString() }
                it.forearm?.let { v -> prevMeasurements[MeasurementType.FOREARM] = v.toString() }
                it.waist?.let { v -> prevMeasurements[MeasurementType.WAIST] = v.toString() }
                it.hip?.let { v -> prevMeasurements[MeasurementType.HIP] = v.toString() }
                it.leg?.let { v -> prevMeasurements[MeasurementType.QUAD] = v.toString() }
                it.calf?.let { v -> prevMeasurements[MeasurementType.CALF] = v.toString() }
            }

            _uiState.update {
                it.copy(
                        previousWeight = prevMetric?.weight?.toString() ?: "",
                        previousBodyFat = prevMetric?.bodyFatPercentage?.toString() ?: "",
                        previousMeasurements = prevMeasurements
                )
            }
        }
    }

    fun updateWeight(value: String) {
        _uiState.update { it.copy(weight = value) }
        calculateBodyFatIfAuto()
    }

    fun updateBodyFat(value: String) {
        if (_uiState.value.isBodyFatManual) {
            _uiState.update { it.copy(bodyFat = value) }
        }
    }

    fun toggleBodyFatManual(isManual: Boolean) {
        _uiState.update { it.copy(isBodyFatManual = isManual) }
        if (!isManual) {
            calculateBodyFatIfAuto()
        }
    }

    fun updateMeasurement(type: MeasurementType, value: String) {
        _uiState.update { currentState ->
            val newMeasurements = currentState.measurements.toMutableMap()
            newMeasurements[type] = value
            currentState.copy(measurements = newMeasurements)
        }
        calculateBodyFatIfAuto()
    }

    private fun calculateBodyFatIfAuto() {
        if (_uiState.value.isBodyFatManual) return

        viewModelScope.launch {
            val user = userRepository.getUser().firstOrNull() ?: return@launch
            val state = _uiState.value

            if (user.height != null && user.gender != null) {
                // Need Waist and Neck (and Hip for female)
                // Need to use CURRENT input if available, else fallback to... previous? No,
                // strictly input for calc.
                val waistStr = state.measurements[MeasurementType.WAIST]
                val neckStr = state.measurements[MeasurementType.NECK]
                val hipStr = state.measurements[MeasurementType.HIP]

                val waist = waistStr?.toFloatOrNull()
                val neck = neckStr?.toFloatOrNull()
                val hip = hipStr?.toFloatOrNull()

                var calcFat: Float? = null
                if (waist != null && neck != null) {
                    calcFat = calculateBodyFat(user.gender, waist, neck, hip, user.height)
                }

                if (calcFat != null) {
                    _uiState.update { it.copy(bodyFat = "%.1f".format(calcFat)) }
                }
            }
        }
    }

    fun saveProgress() {
        viewModelScope.launch {
            val user = userRepository.getUser().firstOrNull() ?: return@launch
            val state = uiState.value
            val timestamp = System.currentTimeMillis()

            // Calculate start and end of day for updates
            val zoneId = java.time.ZoneId.systemDefault()
            val now = java.time.Instant.ofEpochMilli(timestamp).atZone(zoneId)
            val startOfDay = now.toLocalDate().atStartOfDay(zoneId).toInstant().toEpochMilli()
            val endOfDay =
                    now.toLocalDate().plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() -
                            1

            // 1. Check for existing records TODAY
            val existingMetric =
                    bodyMetricsRepository.getMetricOnDate(user.id, startOfDay, endOfDay)
            val existingMeasurement =
                    bodyMetricsRepository.getMeasurementOnDate(user.id, startOfDay, endOfDay)

            // Calculate BMI
            var calculatedBmi: Float? = null
            val weightVal = state.weight.toFloatOrNull()
            if (weightVal != null && user.height != null && user.height > 0) {
                calculatedBmi = calculateBmi(weightVal, user.height)
            }

            // Determine Body Fat to Save
            val finalBodyFat = state.bodyFat.toFloatOrNull()

            Log.d(
                    "AddViewModel",
                    "Saving Metrics: weight=$weightVal, bodyFat=$finalBodyFat, bmi=$calculatedBmi"
            )

            // 2. Save Metrics (Weight, Fat) - Update or Insert
            if (weightVal != null || finalBodyFat != null) {
                // Even if weight is null but fat is set? Usually weight is required for a metric
                // entry.
                // If weight is missing but user entered body fat manually... we might need to fetch
                // latest weight?
                // For now, let's assume weight is entered or we update existing today.

                val metricToSave =
                        if (existingMetric != null) {
                            existingMetric.copy(
                                    weight = weightVal ?: existingMetric.weight,
                                    bodyFatPercentage = finalBodyFat
                                                    ?: existingMetric.bodyFatPercentage,
                                    bmi = calculatedBmi ?: existingMetric.bmi,
                                    recordedAt = timestamp
                            )
                        } else {
                            if (weightVal == null)
                                    return@launch // Cannot create new metric without weight
                            // typically? Or allow null weight? entity
                            // probably requires it or allows null. Exisiting
                            // code required weightVal != null

                            UserMetricEntity(
                                    id = 0,
                                    userId = user.id,
                                    measurementId = null,
                                    weight = weightVal,
                                    bodyFatPercentage = finalBodyFat,
                                    bmi = calculatedBmi,
                                    recordedAt = timestamp
                            )
                        }
                bodyMetricsRepository.addMetric(metricToSave)
            }

            // 3. Save Body Measurements - Update or Insert
            if (state.measurements.values.any { it.isNotBlank() }) {
                val measurements = state.measurements
                val measurementEntity =
                        BodyMeasurementEntity(
                                id = existingMeasurement?.id ?: 0, // Use existing ID if found
                                userId = user.id,
                                neck = measurements[MeasurementType.NECK]?.toFloatOrNull()
                                                ?: existingMeasurement?.neck,
                                chest = measurements[MeasurementType.CHEST]?.toFloatOrNull()
                                                ?: existingMeasurement?.chest,
                                arm = measurements[MeasurementType.BICEP]?.toFloatOrNull()
                                                ?: existingMeasurement?.arm,
                                forearm = measurements[MeasurementType.FOREARM]?.toFloatOrNull()
                                                ?: existingMeasurement?.forearm,
                                waist = measurements[MeasurementType.WAIST]?.toFloatOrNull()
                                                ?: existingMeasurement?.waist,
                                hip = measurements[MeasurementType.HIP]?.toFloatOrNull()
                                                ?: existingMeasurement?.hip,
                                leg = measurements[MeasurementType.QUAD]?.toFloatOrNull()
                                                ?: existingMeasurement?.leg,
                                calf = measurements[MeasurementType.CALF]?.toFloatOrNull()
                                                ?: existingMeasurement?.calf,
                                recordedAt = timestamp
                        )
                bodyMetricsRepository.addMeasurement(measurementEntity)
            }

            _saveSuccess.send(Unit)
        }
    }

    private fun calculateBmi(weightKg: Float, heightCm: Float): Float {
        val heightM = heightCm / 100f
        return if (heightM > 0) weightKg / (heightM * heightM) else 0f
    }

    private fun calculateBodyFat(
            gender: Gender,
            waist: Float,
            neck: Float,
            hip: Float?,
            height: Float
    ): Float? {
        // US Navy Method
        // Measurements in cm
        return try {
            if (gender.equals(Gender.MALE)) {
                // Men: 495 / (1.0324 - 0.19077 * log10(waist - neck) + 0.15456 * log10(height)) -
                // 450
                if (waist - neck <= 0) return null // Invalid for log
                (495 /
                                (1.0324 - 0.19077 * kotlin.math.log10(waist - neck) +
                                        0.15456 * kotlin.math.log10(height)) - 450)
                        .toFloat()
            } else if (gender.equals(Gender.FEMALE)) {
                // Women: 495 / (1.29579 - 0.35004 * log10(waist + hip - neck) + 0.22100 *
                // log10(height)) - 450
                if (hip == null) return null
                if (waist + hip - neck <= 0) return null
                (495 /
                                (1.29579 - 0.35004 * kotlin.math.log10(waist + hip - neck) +
                                        0.22100 * kotlin.math.log10(height)) - 450)
                        .toFloat()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.d("AddViewModel", "Error calculating body fat: ${e.message}")
            null
        }
    }
}

data class AddUiState(
        val weight: String = "",
        val bodyFat: String = "",
        val isBodyFatManual: Boolean = false,
        val previousWeight: String = "",
        val previousBodyFat: String = "",
        val measurements: Map<MeasurementType, String> = emptyMap(),
        val previousMeasurements: Map<MeasurementType, String> = emptyMap()
)

enum class MeasurementType {
    NECK,
    CHEST,
    BICEP,
    FOREARM,
    WAIST,
    HIP,
    QUAD,
    CALF
}
