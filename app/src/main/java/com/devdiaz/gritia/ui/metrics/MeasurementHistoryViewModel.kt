package com.devdiaz.gritia.ui.metrics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdiaz.gritia.data.repository.BodyMetricsRepository
import com.devdiaz.gritia.data.repository.UserRepository
import com.devdiaz.gritia.model.Measurement
import com.devdiaz.gritia.model.MeasurementType
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MeasurementHistoryViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val bodyMetricsRepository: BodyMetricsRepository
) : ViewModel() {

    enum class TimeRange(val label: String) {
        ONE_MONTH("1M"),
        THREE_MONTHS("3M"),
        SIX_MONTHS("6M"),
        ONE_YEAR("1Y"),
        ALL("Todo")
    }

    private val _selectedFilter = MutableStateFlow<MeasurementType?>(null)
    val selectedFilter: StateFlow<MeasurementType?> = _selectedFilter.asStateFlow()

    private val _timeRange = MutableStateFlow(TimeRange.ALL)
    val timeRange: StateFlow<TimeRange> = _timeRange.asStateFlow()

    val measurements: StateFlow<List<Measurement>> = userRepository.getUser()
        .flatMapLatest { user ->
            if (user == null) {
                MutableStateFlow(emptyList())
            } else {
                combine(
                    bodyMetricsRepository.getUserMetrics(user.id),
                    bodyMetricsRepository.getBodyMeasurements(user.id),
                    _selectedFilter,
                    _timeRange
                ) { userMetrics, bodyMeasurements, filter, timeRange ->
                    val allMeasurements = mutableListOf<Measurement>()

                    // Calculate cutoff timestamp based on TimeRange
                    val now = Instant.now()
                    val cutoff = when (timeRange) {
                        TimeRange.ONE_MONTH -> now.minus(30, ChronoUnit.DAYS).toEpochMilli()
                        TimeRange.THREE_MONTHS -> now.minus(90, ChronoUnit.DAYS).toEpochMilli()
                        TimeRange.SIX_MONTHS -> now.minus(180, ChronoUnit.DAYS).toEpochMilli()
                        TimeRange.ONE_YEAR -> now.minus(365, ChronoUnit.DAYS).toEpochMilli()
                        TimeRange.ALL -> 0L
                    }

                    // Map UserMetricEntity
                    userMetrics.filter { it.recordedAt >= cutoff }.forEach { entity ->
                        val date = Instant.ofEpochMilli(entity.recordedAt)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        // Weight
                        if (entity.weight > 0) {
                            allMeasurements.add(
                                Measurement(
                                    id = "weight_${entity.id}",
                                    type = MeasurementType.Weight,
                                    value = entity.weight,
                                    unit = "kg",
                                    date = date
                                )
                            )
                        }

                        // BMI
                        entity.bmi?.let {
                            allMeasurements.add(
                                Measurement(
                                    id = "bmi_${entity.id}",
                                    type = MeasurementType.BMI,
                                    value = it,
                                    unit = "",
                                    date = date
                                )
                            )
                        }

                        // Body Fat
                        entity.bodyFatPercentage?.let {
                            allMeasurements.add(
                                Measurement(
                                    id = "fat_${entity.id}",
                                    type = MeasurementType.BodyFat,
                                    value = it,
                                    unit = "%",
                                    date = date
                                )
                            )
                        }
                    }

                    // Map BodyMeasurementEntity
                    bodyMeasurements.filter { it.recordedAt >= cutoff }.forEach { entity ->
                        val date = Instant.ofEpochMilli(entity.recordedAt)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        val mapping = listOf(
                            entity.neck to MeasurementType.Neck,
                            entity.chest to MeasurementType.Chest,
                            entity.arm to MeasurementType.Biceps,
                            entity.forearm to MeasurementType.Forearm,
                            entity.waist to MeasurementType.Waist,
                            entity.hip to MeasurementType.Hips,
                            entity.leg to MeasurementType.Legs,
                            entity.calf to MeasurementType.Calves
                        )

                        mapping.forEach { (value, type) ->
                            value?.let {
                                allMeasurements.add(
                                    Measurement(
                                        id = "${type.name}_${entity.id}",
                                        type = type,
                                        value = it,
                                        unit = "cm",
                                        date = date
                                    )
                                )
                            }
                        }
                    }

                    // Filter if needed
                    val filtered = if (filter != null) {
                        allMeasurements.filter { it.type == filter }
                    } else {
                        allMeasurements
                    }

                    // Calculate Deltas (Previous Value)
                    // Sort by Type then Date Ascending
                    val sortedForDelta = filtered.sortedWith(compareBy({ it.type }, { it.date }))

                    val result = mutableListOf<Measurement>()
                    val previousValues = mutableMapOf<MeasurementType, Float>()

                    sortedForDelta.forEach { measurement ->
                        val prev = previousValues[measurement.type]
                        result.add(measurement.copy(previousValue = prev))
                        previousValues[measurement.type] = measurement.value
                    }

                    // Final Sort: Date Descending for Display
                    result.sortedByDescending { it.date }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setFilter(type: MeasurementType?) {
        _selectedFilter.value = type
    }

    fun setTimeRange(range: TimeRange) {
        _timeRange.value = range
    }

    fun deleteMeasurement(idString: String) {
        viewModelScope.launch {
            try {
                // ID format: type_id (e.g. weight_123, NECK_456)
                val parts = idString.split("_")
                if (parts.size >= 2) {
                    val typePrefix = parts[0]
                    val id = parts[1].toLongOrNull()

                    if (id != null) {
                        when (typePrefix) {
                            "weight", "bmi", "fat" -> {
                                bodyMetricsRepository.deleteUserMetric(id)
                            }
                            else -> {
                                bodyMetricsRepository.deleteBodyMeasurement(id)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MeasurementHistoryVM", "Error deleting measurement", e)
            }
        }
    }
}

