package com.devdiaz.gritia.ui.metrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdiaz.gritia.data.repository.BodyMetricsRepository
import com.devdiaz.gritia.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class BodyMetricsViewModel
@Inject
constructor(
        private val userRepository: UserRepository,
        private val bodyMetricsRepository: BodyMetricsRepository
) : ViewModel() {

    private val _selectedTimeRange = MutableStateFlow(TimeRange.ONE_MONTH)
    private val _selectedMetric = MutableStateFlow(MetricType.WEIGHT)

    // Collect user to get ID, then metrics
    val uiState: StateFlow<BodyMetricsUiState> =
            userRepository
                    .getUser()
                    .combine(_selectedTimeRange) { user, range -> Pair(user, range) }
                    .combine(_selectedMetric) { (user, range), metric ->
                        Triple(user, range, metric)
                    }
                    .combine(
                            // We need a way to chain flow collections safely.
                            // Since we can't easily flatMap calls inside combine without more
                            // complexity,
                            // let's assume we fetch for the *current* user.
                            // For simplicity, we'll assume the logged in user is retrievable.
                            // But here we depend on `userRepository.getUser()`.
                            // A better pattern:
                            userRepository.getUser()
                    ) { triple, userResp ->
                        triple // Just to trigger update if user changes.
                        // Actually we need to combine the data flows *after* we have the user ID.
                        // Let's use a flatMapLatest approach ideally, but for now let's construct
                        // logic differently.
                        userResp
                    }
                    .let { _ ->
                        // Re-architecting the flow collection to be more robust
                        // We will use a separate initialization in init block or flatMapLatest
                        // But `combine` is cleaner for UI State if we verify user.

                        // Simplified approach: Collect generic flows and combine them.
                        // Since we need userId for repo calls, we FlatMap the user flow.
                        userRepository.getUser().flatMapLatest { user ->
                            if (user == null) {
                                MutableStateFlow(BodyMetricsUiState()) // Empty state
                            } else {
                                combine(
                                        bodyMetricsRepository.getUserMetrics(user.id),
                                        bodyMetricsRepository.getBodyMeasurements(user.id),
                                        _selectedTimeRange,
                                        _selectedMetric
                                ) { metrics, measurements, range, selectedMetric ->

                                    // 1. Process Metrics (Weight, Fat, BMI)
                                    val weightMetrics =
                                            metrics.map {
                                                MeasurementItem(
                                                        type = MetricType.WEIGHT,
                                                        value = it.weight.toString(),
                                                        unit = "kg",
                                                        date = formatDate(it.recordedAt),
                                                        changeLabel = "", // Calculate later
                                                        changeType = ChangeType.NEUTRAL,
                                                        timestamp = it.recordedAt
                                                )
                                            }

                                    // 2. Process Measurements (Waist, Chest, etc.)
                                    val bodyMeasurements =
                                            measurements.flatMap { m ->
                                                listOfNotNull(
                                                        m.neck?.let {
                                                            createItem(
                                                                    MetricType.NECK,
                                                                    it,
                                                                    m.recordedAt
                                                            )
                                                        },
                                                        m.chest?.let {
                                                            createItem(
                                                                    MetricType.CHEST,
                                                                    it,
                                                                    m.recordedAt
                                                            )
                                                        },
                                                        m.waist?.let {
                                                            createItem(
                                                                    MetricType.WAIST,
                                                                    it,
                                                                    m.recordedAt
                                                            )
                                                        },
                                                        m.hip?.let {
                                                            createItem(
                                                                    MetricType.HIP,
                                                                    it,
                                                                    m.recordedAt
                                                            )
                                                        },
                                                        // Add others as needed
                                                        )
                                            }

                                    val allItems =
                                            (weightMetrics + bodyMeasurements).sortedByDescending {
                                                it.timestamp
                                            }



                                    // 4. Chart Data
                                    // Filter by range logic here (omitted for brevity, showing all
                                    // for now)
                                    // 4. Chart Data
                                    val cutoff = when (range) {
                                        TimeRange.ONE_MONTH -> Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS).toEpochMilli()
                                        TimeRange.THREE_MONTHS -> Instant.now().minus(90, java.time.temporal.ChronoUnit.DAYS).toEpochMilli()
                                        TimeRange.SIX_MONTHS -> Instant.now().minus(180, java.time.temporal.ChronoUnit.DAYS).toEpochMilli()
                                        TimeRange.ONE_YEAR -> Instant.now().minus(365, java.time.temporal.ChronoUnit.DAYS).toEpochMilli()
                                        TimeRange.ALL -> 0L
                                    }

                                    // Helper to extract value and time for generic handling
                                    data class ChartPoint(val value: Float, val time: Long)

                                    val rawPoints = when (selectedMetric) {
                                        // User Metrics
                                        MetricType.WEIGHT -> metrics.map { ChartPoint(it.weight, it.recordedAt) }
                                        MetricType.BODY_FAT -> metrics.mapNotNull { it.bodyFatPercentage?.let { v -> ChartPoint(v, it.recordedAt) } }
                                        MetricType.IMC -> metrics.mapNotNull { it.bmi?.let { v -> ChartPoint(v, it.recordedAt) } }
                                        
                                        // Body Measurements (Tapes)
                                        MetricType.NECK -> measurements.mapNotNull { it.neck?.let { v -> ChartPoint(v, it.recordedAt) } }
                                        MetricType.CHEST -> measurements.mapNotNull { it.chest?.let { v -> ChartPoint(v, it.recordedAt) } }
                                        MetricType.ARM -> measurements.mapNotNull { it.arm?.let { v -> ChartPoint(v, it.recordedAt) } }
                                        MetricType.FOREARM -> measurements.mapNotNull { it.forearm?.let { v -> ChartPoint(v, it.recordedAt) } }
                                        MetricType.WAIST -> measurements.mapNotNull { it.waist?.let { v -> ChartPoint(v, it.recordedAt) } }
                                        MetricType.HIP -> measurements.mapNotNull { it.hip?.let { v -> ChartPoint(v, it.recordedAt) } }
                                        MetricType.LEG -> measurements.mapNotNull { it.leg?.let { v -> ChartPoint(v, it.recordedAt) } }
                                        MetricType.CALF -> measurements.mapNotNull { it.calf?.let { v -> ChartPoint(v, it.recordedAt) } }
                                    }.filter { it.time >= cutoff }

                                    // Group by day and take latest
                                    val dailyPoints = rawPoints
                                        .groupBy { 
                                            Instant.ofEpochMilli(it.time)
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDate()
                                        }
                                        .mapValues { (_, points) ->
                                            points.maxByOrNull { it.time }!!
                                        }
                                        .values
                                        .sortedBy { it.time }

                                    val chartData = dailyPoints.map { it.value }
                                    val chartLabels = dailyPoints.map { formatDateShort(it.time) }

                                    // 5. Current Value & Trend for Selected Metric
                                    // We can reuse 'dailyPoints' which is already sorted by time
                                    val currentVal = dailyPoints.lastOrNull()?.value
                                    val previousVal = if (dailyPoints.size > 1) dailyPoints[dailyPoints.size - 2].value else null
                                    
                                    val currentValueStr = currentVal?.toString() ?: "--"
                                    
                                    val (changeStr, isTrendingDown) = if (currentVal != null && previousVal != null) {
                                        val diff = currentVal - previousVal
                                        val absDiff = kotlin.math.abs(diff)
                                        val formatted = String.format("%.1f", absDiff)
                                        val trend = diff < 0
                                        formatted to trend
                                    } else {
                                        "--" to false
                                    }

                                    BodyMetricsUiState(
                                            selectedTimeRange = range,
                                            selectedMetric = selectedMetric,
                                            currentValue = currentValueStr,
                                            valueChange = changeStr,
                                            isValueTrendingDown = isTrendingDown,
                                            chartData = chartData,
                                            chartLabels = chartLabels,
                                            recentMeasurements =
                                                    allItems.take(5)
                                    )
                                }
                            }
                        }
                    }
                    .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue = BodyMetricsUiState()
                    )

    fun onTimeRangeSelected(range: TimeRange) {
        _selectedTimeRange.value = range
    }

    fun onMetricSelected(metric: MetricType) {
        _selectedMetric.value = metric
    }

    private fun createItem(type: MetricType, value: Float, timestamp: Long): MeasurementItem {
        return MeasurementItem(
                type = type,
                value = value.toString(),
                unit = "cm", // Default unit
                date = formatDate(timestamp),
                changeLabel = "",
                changeType = ChangeType.NEUTRAL,
                timestamp = timestamp
        )
    }

    private fun formatDate(timestamp: Long): String {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("MMM dd"))
    }

    private fun formatDateShort(timestamp: Long): String {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("MM/dd"))
    }
}

data class BodyMetricsUiState(
        val selectedTimeRange: TimeRange = TimeRange.ONE_MONTH,
        val selectedMetric: MetricType = MetricType.WEIGHT,
        val currentValue: String = "--",
        val valueChange: String = "--",
        val isValueTrendingDown: Boolean = false,
        val chartData: List<Float> = emptyList(),
        val chartLabels: List<String> = emptyList(),
        val recentMeasurements: List<MeasurementItem> = emptyList()
)

enum class TimeRange(val label: String) {
    ONE_MONTH("1M"),
    THREE_MONTHS("3M"),
    SIX_MONTHS("6M"),
    ONE_YEAR("1Y"),
    ALL("Todo")
}

enum class MetricType(val label: String) {
    WEIGHT("Peso"),
    BODY_FAT("% Grasa"),
    IMC("IMC"),
    NECK("Cuello"),
    CHEST("Pecho"),
    ARM("Brazo"),
    FOREARM("Antebrazo"),
    WAIST("Cintura"),
    HIP("Cadera"),
    LEG("Pierna"),
    CALF("Pantorrilla")
}

data class MeasurementItem(
        val type: MetricType,
        val value: String,
        val unit: String,
        val date: String,
        val changeLabel: String,
        val changeType: ChangeType,
        val timestamp: Long = 0L
)

enum class ChangeType {
    POSITIVE, // Good (Green)
    NEGATIVE, // Bad/Attention (e.g. Red/Orange, or Primary color depending on context)
    NEUTRAL // Grey
}
