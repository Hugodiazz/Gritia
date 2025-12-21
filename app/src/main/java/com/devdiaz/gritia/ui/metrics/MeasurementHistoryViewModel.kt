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
class MeasurementHistoryViewModel
@Inject
constructor(
        private val userRepository: UserRepository,
        private val bodyMetricsRepository: BodyMetricsRepository
) : ViewModel() {

        private val _selectedFilter = MutableStateFlow<MeasurementType?>(null)
        val selectedFilter: StateFlow<MeasurementType?> = _selectedFilter.asStateFlow()

        val measurements: StateFlow<List<Measurement>> =
                userRepository
                        .getUser()
                        .flatMapLatest { user ->
                                if (user == null) {
                                        MutableStateFlow(emptyList())
                                } else {
                                        combine(
                                                bodyMetricsRepository.getUserMetrics(user.id),
                                                bodyMetricsRepository.getBodyMeasurements(user.id),
                                                _selectedFilter
                                        ) { userMetrics, bodyMeasurements, filter ->
                                                val allMeasurements = mutableListOf<Measurement>()

                                                // Map UserMetricEntity
                                                userMetrics.forEach { entity ->
                                                        val date =
                                                                Instant.ofEpochMilli(
                                                                                entity.recordedAt
                                                                        )
                                                                        .atZone(
                                                                                ZoneId.systemDefault()
                                                                        )
                                                                        .toLocalDate()

                                                        // Weight
                                                        if (entity.weight > 0) {
                                                                allMeasurements.add(
                                                                        Measurement(
                                                                                id =
                                                                                        "weight_${entity.id}",
                                                                                type =
                                                                                        MeasurementType
                                                                                                .Weight,
                                                                                value =
                                                                                        entity.weight,
                                                                                unit =
                                                                                        "kg", // Assuming kg based on typical usage
                                                                                date = date
                                                                        )
                                                                )
                                                        }

                                                        // BMI
                                                        entity.bmi?.let {
                                                                allMeasurements.add(
                                                                        Measurement(
                                                                                id =
                                                                                        "bmi_${entity.id}",
                                                                                type =
                                                                                        MeasurementType
                                                                                                .BMI,
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
                                                                                id =
                                                                                        "fat_${entity.id}",
                                                                                type =
                                                                                        MeasurementType
                                                                                                .BodyFat,
                                                                                value = it,
                                                                                unit = "%",
                                                                                date = date
                                                                        )
                                                                )
                                                        }
                                                }

                                                // Map BodyMeasurementEntity
                                                bodyMeasurements.forEach { entity ->
                                                        val date =
                                                                Instant.ofEpochMilli(
                                                                                entity.recordedAt
                                                                        )
                                                                        .atZone(
                                                                                ZoneId.systemDefault()
                                                                        )
                                                                        .toLocalDate()

                                                        val mapping =
                                                                listOf(
                                                                        entity.neck to
                                                                                MeasurementType
                                                                                        .Neck,
                                                                        entity.chest to
                                                                                MeasurementType
                                                                                        .Chest,
                                                                        entity.arm to
                                                                                MeasurementType
                                                                                        .Biceps,
                                                                        entity.forearm to
                                                                                MeasurementType
                                                                                        .Forearm,
                                                                        entity.waist to
                                                                                MeasurementType
                                                                                        .Waist,
                                                                        entity.hip to
                                                                                MeasurementType
                                                                                        .Hips,
                                                                        entity.leg to
                                                                                MeasurementType
                                                                                        .Legs,
                                                                        entity.calf to
                                                                                MeasurementType
                                                                                        .Calves
                                                                )

                                                        mapping.forEach { (value, type) ->
                                                                value?.let {
                                                                        allMeasurements.add(
                                                                                Measurement(
                                                                                        id =
                                                                                                "${type.name}_${entity.id}",
                                                                                        type = type,
                                                                                        value = it,
                                                                                        unit =
                                                                                                "cm", // Assuming cm
                                                                                        date = date
                                                                                )
                                                                        )
                                                                }
                                                        }
                                                }

                                                // Filter if needed
                                                val filtered =
                                                        if (filter != null) {
                                                                allMeasurements.filter {
                                                                        it.type == filter
                                                                }
                                                        } else {
                                                                allMeasurements
                                                        }

                                                // Calculate Deltas (Previous Value)
                                                // Sort by Type then Date Ascending
                                                val sortedForDelta =
                                                        filtered.sortedWith(
                                                                compareBy({ it.type }, { it.date })
                                                        )

                                                val result = mutableListOf<Measurement>()
                                                val previousValues =
                                                        mutableMapOf<MeasurementType, Float>()

                                                sortedForDelta.forEach { measurement ->
                                                        val prev = previousValues[measurement.type]
                                                        result.add(
                                                                measurement.copy(
                                                                        previousValue = prev
                                                                )
                                                        )
                                                        previousValues[measurement.type] =
                                                                measurement.value
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

        fun deleteMeasurement(idString: String) {
                viewModelScope.launch {
                        try {
                                // ID format: type_id (e.g. weight_123, NECK_456)
                                val parts = idString.split("_")
                                if (parts.size >= 2) {
                                        val typePrefix = parts[0] // weight, bmi, fat, NECK, etc.
                                        val id = parts[1].toLongOrNull()

                                        if (id != null) {
                                                when (typePrefix) {
                                                        "weight", "bmi", "fat" -> {
                                                                // These come from UserMetricEntity
                                                                // TODO: ideally we only clear the
                                                                // specific field (weight/bmi/fat)
                                                                // and delete entity only if all are
                                                                // null.
                                                                // For MVP simplify: If deleting
                                                                // weight, we delete the whole
                                                                // metric entry
                                                                // because usually they go together.
                                                                // Or simpler: delete by ID.
                                                                // UserMetricEntity is one row per
                                                                // timestamp.
                                                                bodyMetricsRepository
                                                                        .deleteUserMetric(id)
                                                        }
                                                        else -> {
                                                                // BodyMeasurementEntity (neck,
                                                                // chest, etc.)
                                                                // Similarly, simplify to delete the
                                                                // row.
                                                                // Or check if it's a specific
                                                                // measurement type?
                                                                // The ID is unique per entity, so
                                                                // deleting by ID deletes the whole
                                                                // row
                                                                // (all measurements for that
                                                                // timestamp).
                                                                // This might be "too aggressive" if
                                                                // user only wants to delete "Neck"
                                                                // but keep "Waist" from same
                                                                // session.
                                                                // Given the schema, they are
                                                                // grouped. Deleting "Neck_123"
                                                                // deletes entity 123.
                                                                bodyMetricsRepository
                                                                        .deleteBodyMeasurement(id)
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
