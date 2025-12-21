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

                                    // 3. Calculate Stats
                                    val latestMetric = metrics.maxByOrNull { it.recordedAt }
                                    val currentWeight = latestMetric?.weight?.toString() ?: "--"

                                    // Calculate Change (simplification: compare last 2 weights)
                                    val sortedWeights = metrics.sortedByDescending { it.recordedAt }
                                    val weightChange =
                                            if (sortedWeights.size >= 2) {
                                                val current = sortedWeights[0].weight
                                                val prev = sortedWeights[1].weight
                                                val diff = current - prev
                                                val percent = (diff / prev) * 100
                                                val sign = if (diff > 0) "+" else ""
                                                "$sign${"%.1f".format(percent)}%"
                                            } else {
                                                "0%"
                                            }
                                    val isTrendingDown =
                                            (sortedWeights.size >= 2 &&
                                                    sortedWeights[0].weight <
                                                            sortedWeights[1].weight)

                                    // 4. Chart Data
                                    // Filter by range logic here (omitted for brevity, showing all
                                    // for now)
                                    val chartData =
                                            sortedWeights.take(10).reversed().map { it.weight }
                                    val chartLabels =
                                            sortedWeights.take(10).reversed().map {
                                                formatDateShort(it.recordedAt)
                                            }

                                    BodyMetricsUiState(
                                            selectedTimeRange = range,
                                            selectedMetric = selectedMetric,
                                            currentWeight = currentWeight,
                                            weightChange = weightChange,
                                            isWeightTrendingDown = isTrendingDown,
                                            chartData = chartData,
                                            chartLabels = chartLabels,
                                            recentMeasurements =
                                                    allItems.take(5) // Show top 5 recent mixed
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
        val currentWeight: String = "--",
        val weightChange: String = "--",
        val isWeightTrendingDown: Boolean = false,
        val chartData: List<Float> = emptyList(),
        val chartLabels: List<String> = emptyList(),
        val recentMeasurements: List<MeasurementItem> = emptyList()
)

enum class TimeRange(val label: String) {
    ONE_WEEK("1W"),
    ONE_MONTH("1M"),
    THREE_MONTHS("3M"),
    ONE_YEAR("1Y")
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
