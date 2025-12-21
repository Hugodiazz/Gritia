package com.devdiaz.gritia.ui.metrics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.outlined.AccessibilityNew
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devdiaz.gritia.model.Measurement
import com.devdiaz.gritia.model.MeasurementType
import com.devdiaz.gritia.ui.theme.BackgroundDark
import com.devdiaz.gritia.ui.theme.Primary
import com.devdiaz.gritia.ui.theme.SurfaceDark
import com.devdiaz.gritia.ui.theme.TextDark
import com.devdiaz.gritia.ui.theme.TextSecondaryDark
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementHistoryScreen(
        onNavigateBack: () -> Unit,
        viewModel: MeasurementHistoryViewModel = hiltViewModel()
) {
    val measurements by viewModel.measurements.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    // Group measurements by date
    val groupedMeasurements =
            remember(measurements) {
                measurements.groupBy { it.date }.toSortedMap(compareByDescending { it })
            }

    Scaffold(
            containerColor = BackgroundDark, // Force dark mode for premium look
            topBar = {
                TopAppBar(
                        title = {
                            Text(
                                    "Measurement History",
                                    color = TextDark,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                        imageVector = Icons.Default.ArrowBackIosNew,
                                        contentDescription = "Back",
                                        tint = TextDark,
                                        modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
                )
            }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Filter Chips
            MeasurementFilterChips(
                    selectedType = selectedFilter,
                    onSelectType = viewModel::setFilter
            )

            // History List
            LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp),
                    modifier = Modifier.fillMaxSize()
            ) {
                var isFirstGroup = true

                // Group by Month for headers
                val months =
                        groupedMeasurements.entries.groupBy {
                            it.key.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US))
                        }

                months.forEach { (monthName, days) ->
                    item {
                        Text(
                                text = monthName,
                                style = MaterialTheme.typography.titleLarge,
                                color = TextDark,
                                fontWeight = FontWeight.Bold,
                                modifier =
                                        Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
                        )
                    }

                    days.forEach { (date, dailyMeasurements) ->
                        val initialExpanded = isFirstGroup
                        isFirstGroup = false

                        item {
                            DailyMeasurementCard(
                                    date = date,
                                    measurements = dailyMeasurements,
                                    initiallyExpanded = initialExpanded
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MeasurementFilterChips(
        selectedType: MeasurementType?,
        onSelectType: (MeasurementType?) -> Unit
) {
    LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                    label = "All",
                    isSelected = selectedType == null,
                    onClick = { onSelectType(null) }
            )
        }
        items(MeasurementType.values()) { type ->
            FilterChip(
                    label = type.displayName,
                    isSelected = selectedType == type,
                    onClick = { onSelectType(type) }
            )
        }
    }
}

@Composable
fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Primary else SurfaceDark
    val textColor = if (isSelected) BackgroundDark else TextSecondaryDark

    Box(
            modifier =
                    Modifier.clip(CircleShape)
                            .background(backgroundColor)
                            .border(
                                    width = 1.dp,
                                    color =
                                            if (isSelected) Color.Transparent
                                            else Color.White.copy(alpha = 0.1f),
                                    shape = CircleShape
                            )
                            .clickable(onClick = onClick)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
    ) {
        Text(
                text = label,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun DailyMeasurementCard(
        date: LocalDate,
        measurements: List<Measurement>,
        initiallyExpanded: Boolean
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }

    // Header for the card (expanded state equivalent to HTML "Top part with image")
    // Or collapsed state equivalent to HTML "Sep 28" card.

    // To strictly follow "default only first is expanded",
    // and based on HTML where collapsed items look different:

    if (isExpanded) {
        ExpandedDailyCard(date, measurements, onToggle = { isExpanded = false })
    } else {
        CollapsedDailyCard(date, measurements, onToggle = { isExpanded = true })
    }
}

@Composable
fun ExpandedDailyCard(date: LocalDate, measurements: List<Measurement>, onToggle: () -> Unit) {
    Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable { onToggle() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column {
            // Banner Section
            Box(
                    modifier =
                            Modifier.fillMaxWidth()
                                    .height(100.dp)
                                    .background(
                                            Brush.verticalGradient(
                                                    colors =
                                                            listOf(
                                                                    Color(0xFF1F2937),
                                                                    Color(0xFF111827)
                                                            )
                                            )
                                    )
            ) {
                // Background image placeholder gradient since we don't have the asset handy in code
                // Using a semi-transparent overlay to match look
                Box(modifier = Modifier.fillMaxSize().background(Primary.copy(alpha = 0.05f)))

                Row(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                                text =
                                        if (date == LocalDate.now()) "TODAY"
                                        else
                                                date.dayOfWeek
                                                        .getDisplayName(TextStyle.FULL, Locale.US)
                                                        .uppercase(),
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                        )
                        Text(
                                text = date.format(DateTimeFormatter.ofPattern("MMM dd")),
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                            modifier =
                                    Modifier.background(Primary.copy(alpha = 0.2f), CircleShape)
                                            .border(1.dp, Primary.copy(alpha = 0.3f), CircleShape)
                                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                                text = "${measurements.size} LOGS",
                                color = Primary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // List Items
            Column(modifier = Modifier.fillMaxWidth()) {
                measurements.forEach { measurement ->
                    MeasurementItemRow(measurement)
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), thickness = 1.dp)
                }
            }

            // Footer "See Less" or similar if we wanted, but HTML has "See Details" for collapsed
            Box(
                    modifier =
                            Modifier.fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.2f))
                                    .clickable { onToggle() }
                                    .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                            text = "Hide Details",
                            color = TextSecondaryDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                    )
                    Icon(
                            imageVector = Icons.Default.ArrowDropUp, // Up arrow to collapse
                            contentDescription = null,
                            tint = TextSecondaryDark,
                            modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CollapsedDailyCard(date: LocalDate, measurements: List<Measurement>, onToggle: () -> Unit) {
    Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable { onToggle() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Date Box
                Column(
                        modifier =
                                Modifier.size(50.dp)
                                        .background(
                                                Color.White.copy(alpha = 0.05f),
                                                RoundedCornerShape(8.dp)
                                        ),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                            text = date.month.name.take(3).uppercase(),
                            color = TextSecondaryDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                    )
                    Text(
                            text = date.dayOfMonth.toString(),
                            color = TextDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                    )
                }

                // Info
                Column {
                    val summaryTitle =
                            if (measurements.size > 1) "Log Summary"
                            else measurements.first().type.displayName
                    Text(
                            text = summaryTitle,
                            color = TextDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                    )
                    Text(
                            text = "${measurements.size} Measurements Recorded",
                            color = TextSecondaryDark,
                            fontSize = 12.sp
                    )
                }
            }

            Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Expand",
                    tint = TextSecondaryDark
            )
        }
    }
}

@Composable
fun MeasurementItemRow(measurement: Measurement) {
    val delta = measurement.delta ?: 0f

    Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Icon + Title
        Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            Box(
                    modifier =
                            Modifier.size(40.dp)
                                    .background(
                                            Color.White.copy(alpha = 0.05f),
                                            RoundedCornerShape(8.dp)
                                    ),
                    contentAlignment = Alignment.Center
            ) {
                val icon =
                        when (measurement.type) {
                            MeasurementType.Waist -> Icons.Outlined.AccessibilityNew
                            else -> Icons.Outlined.FitnessCenter
                        }
                Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TextSecondaryDark,
                        modifier = Modifier.size(20.dp)
                )
            }

            // Text
            Column {
                Text(
                        text = measurement.type.displayName,
                        color = TextDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                )
                Text(
                        text = "Last: ${measurement.previousValue ?: "-"} ${measurement.unit}",
                        color = TextSecondaryDark,
                        fontSize = 12.sp
                )
            }
        }

        // Right: Value + Delta + Actions
        Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                            text = measurement.value.toString(),
                            color = Primary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                    )
                    Text(
                            text = " ${measurement.unit}",
                            color = TextSecondaryDark,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Delta Chip
                if (measurement.previousValue != null) {
                    val isPositive = delta > 0
                    // For body measurements, gain might be good or bad depending on context,
                    // but usually color coding implies change. let's match HTML (positive =
                    // green/primary, negative = red)
                    val color = if (delta > 0) Primary else Color(0xFFF87171)
                    val bg = color.copy(alpha = 0.1f)

                    Row(
                            modifier =
                                    Modifier.background(bg, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                                imageVector =
                                        if (delta > 0) Icons.Default.ArrowDropUp
                                        else Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(12.dp)
                        )
                        Text(
                                text = String.format("%.1f", kotlin.math.abs(delta)),
                                color = color,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Actions
            Row(
                    modifier =
                            Modifier.height(32.dp) // Match height roughly
                                    .border(
                                            width = 0.dp,
                                            color = Color.Transparent
                                    ) // No border, just separator
            ) {
                // Vertical separator
                Box(
                        modifier =
                                Modifier.width(1.dp)
                                        .height(32.dp)
                                        .background(Color.White.copy(alpha = 0.1f))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = TextSecondaryDark.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp).clickable {}
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = TextSecondaryDark.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp).clickable {}
                )
            }
        }
    }
}
