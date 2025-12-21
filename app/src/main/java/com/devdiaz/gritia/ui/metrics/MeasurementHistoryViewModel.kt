package com.devdiaz.gritia.ui.metrics

import androidx.lifecycle.ViewModel
import com.devdiaz.gritia.model.Measurement
import com.devdiaz.gritia.model.MeasurementType
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MeasurementHistoryViewModel : ViewModel() {

    private val _measurements = MutableStateFlow<List<Measurement>>(emptyList())
    val measurements: StateFlow<List<Measurement>> = _measurements.asStateFlow()

    private val _selectedFilter = MutableStateFlow<MeasurementType?>(null)
    val selectedFilter: StateFlow<MeasurementType?> = _selectedFilter.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val today = LocalDate.now()
        val mockData =
                listOf(
                        Measurement("1", MeasurementType.Biceps, 16.5f, "in", today, 16.4f),
                        Measurement(
                                "2",
                                MeasurementType.Biceps,
                                16.4f,
                                "in",
                                today,
                                16.3f
                        ), // Assuming left/right but keeping simple for now
                        Measurement("3", MeasurementType.Waist, 32.0f, "in", today, 32.2f),
                        Measurement(
                                "4",
                                MeasurementType.Biceps,
                                16.4f,
                                "in",
                                today.minusDays(7),
                                16.3f
                        ),
                        Measurement(
                                "5",
                                MeasurementType.Biceps,
                                16.3f,
                                "in",
                                today.minusDays(7),
                                16.2f
                        ),
                        Measurement(
                                "6",
                                MeasurementType.Waist,
                                32.2f,
                                "in",
                                today.minusDays(7),
                                32.5f
                        ),
                        Measurement(
                                "7",
                                MeasurementType.Weight,
                                180.5f,
                                "lbs",
                                today.minusDays(26)
                        ),
                        Measurement("8", MeasurementType.Weight, 182.0f, "lbs", today.minusDays(40))
                )
        _measurements.value = mockData
    }

    fun setFilter(type: MeasurementType?) {
        _selectedFilter.value = type
    }

    fun deleteMeasurement(id: String) {
        val currentList = _measurements.value.toMutableList()
        currentList.removeIf { it.id == id }
        _measurements.value = currentList
    }
}
