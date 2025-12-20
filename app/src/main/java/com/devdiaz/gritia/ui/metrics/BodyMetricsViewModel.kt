package com.devdiaz.gritia.ui.metrics

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class BodyMetricsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(BodyMetricsUiState())
    val uiState: StateFlow<BodyMetricsUiState> = _uiState.asStateFlow()

    fun onTimeRangeSelected(range: TimeRange) {
        _uiState.update { it.copy(selectedTimeRange = range) }
        // In a real app, load data for range
    }

    fun onMetricSelected(metric: MetricType) {
        _uiState.update { it.copy(selectedMetric = metric) }
    }
}

data class BodyMetricsUiState(
        val selectedTimeRange: TimeRange = TimeRange.ONE_MONTH,
        val selectedMetric: MetricType = MetricType.WEIGHT,
        val currentWeight: String = "75.4",
        val weightChange: String = "-2.1%",
        val isWeightTrendingDown: Boolean = true,
        val chartData: List<Float> = listOf(76.5f, 76.2f, 75.8f, 76.0f, 75.4f), // Dummy data
        val chartLabels: List<String> = listOf("Oct 12", "Oct 19", "Oct 26", "Nov 02", "Today"),
        val recentMeasurements: List<MeasurementItem> =
                listOf(
                        MeasurementItem(
                                MetricType.WAIST,
                                "82",
                                "cm",
                                "Oct 30",
                                "No change",
                                ChangeType.NEUTRAL
                        ),
                        MeasurementItem(
                                MetricType.BODY_FAT,
                                "14.2",
                                "%",
                                "Oct 30",
                                "-0.8% change",
                                ChangeType.POSITIVE
                        ), // Positive meaning good (green)
                        MeasurementItem(
                                MetricType.CHEST,
                                "102",
                                "cm",
                                "Oct 28",
                                "+1.2 cm gain",
                                ChangeType.NEGATIVE
                        ) // Gaining chest might be good or neutral, but using color logic
                )
)

enum class TimeRange(val label: String) {
    ONE_WEEK("1W"),
    ONE_MONTH("1M"),
    THREE_MONTHS("3M"),
    ONE_YEAR("1Y")
}

enum class MetricType(val label: String) {
    WEIGHT("Weight"),
    BODY_FAT("Body Fat %"),
    WAIST("Waist"),
    CHEST("Chest"),
    ARMS("Arms")
}

data class MeasurementItem(
        val type: MetricType,
        val value: String,
        val unit: String,
        val date: String,
        val changeLabel: String,
        val changeType: ChangeType
)

enum class ChangeType {
    POSITIVE, // Good (Green)
    NEGATIVE, // Bad/Attention (e.g. Red/Orange, or Primary color depending on context)
    NEUTRAL // Grey
}
