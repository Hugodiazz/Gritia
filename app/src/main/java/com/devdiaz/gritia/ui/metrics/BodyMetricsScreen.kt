package com.devdiaz.gritia.ui.metrics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.outlined.AccessibilityNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devdiaz.gritia.ui.library.TopBar
import com.devdiaz.gritia.ui.theme.BackgroundDark
import com.devdiaz.gritia.ui.theme.BackgroundLight
import com.devdiaz.gritia.ui.theme.Primary
import com.devdiaz.gritia.ui.theme.SurfaceDark
import com.devdiaz.gritia.ui.theme.SurfaceLight
import com.devdiaz.gritia.ui.theme.TextSecondaryDark
import com.devdiaz.gritia.ui.theme.TextSecondaryLight

@Composable
fun BodyMetricsScreen(
        onNavigateToAddProgress: () -> Unit,
        onNavigateToHistory: () -> Unit,
        viewModel: BodyMetricsViewModel = hiltViewModel()
) {
        val state by viewModel.uiState.collectAsState()
        val isDark = true // Force dark mode look

        val textPrimary = if (isDark) Color.White else Color(0xFF0F172A)
        val textSecondary = if (isDark) TextSecondaryDark else TextSecondaryLight
        val surfaceColor = if (isDark) SurfaceDark else SurfaceLight

        Box(
                modifier =
                        Modifier.fillMaxSize()
                                .background(if (isDark) BackgroundDark else BackgroundLight)
        ) {
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        // Header
                        TopBar("Progreso")

                        // Time Range Selector
                        TimeRangeSelector(
                                isDark = isDark,
                                selectedRange = state.selectedTimeRange,
                                onRangeSelected = viewModel::onTimeRangeSelected
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Metric Chips
                        MetricChipsRow(
                                isDark = isDark,
                                selectedMetric = state.selectedMetric,
                                onMetricSelected = viewModel::onMetricSelected
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Main Chart Section
                        ChartSection(
                                isDark = isDark,
                                state = state,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                onNavigateToHistory = onNavigateToHistory
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Stats Grid
                        StatsGrid(
                                isDark = isDark,
                                measurements = state.recentMeasurements,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                surfaceColor = surfaceColor
                        )

                        Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom/fab
                }

                // FAB
                FloatingActionButton(
                        onClick = onNavigateToAddProgress,
                        containerColor = Primary,
                        contentColor = Color.Black,
                        elevation = FloatingActionButtonDefaults.elevation(8.dp),
                        modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
                ) {
                        Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Icon(Icons.Default.Edit, contentDescription = null)
                                Text(text = "Registrar progreso", fontWeight = FontWeight.Bold)
                        }
                }
        }
}

@Composable
fun TimeRangeSelector(
        isDark: Boolean,
        selectedRange: TimeRange,
        onRangeSelected: (TimeRange) -> Unit
) {
        val containerColor = if (isDark) SurfaceDark else Color(0xFFE2E8F0)

        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .background(containerColor, CircleShape)
                                .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
        ) {
                TimeRange.values().forEach { range ->
                        val isSelected = range == selectedRange
                        val textColor =
                                when {
                                        isSelected -> if (isDark) Color.Black else Color.Black
                                        else ->
                                                if (isDark) TextSecondaryDark
                                                else TextSecondaryLight
                                }
                        val backgroundColor =
                                when {
                                        isSelected -> if (isDark) Primary else Color.White
                                        else -> Color.Transparent
                                }

                        Box(
                                modifier =
                                        Modifier.weight(1f)
                                                .clip(CircleShape)
                                                .background(backgroundColor)
                                                .clickable { onRangeSelected(range) }
                                                .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                        ) {
                                Text(
                                        text = range.label,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = textColor
                                )
                        }
                }
        }
}

@Composable
fun MetricChipsRow(
        isDark: Boolean,
        selectedMetric: MetricType,
        onMetricSelected: (MetricType) -> Unit
) {
        LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                items(MetricType.values()) { metric ->
                        val isSelected = metric == selectedMetric
                        val bgColor =
                                if (isSelected) Primary
                                else if (isDark) SurfaceDark else Color.White
                        val contentColor =
                                if (isSelected) Color.Black
                                else if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                        val borderColor =
                                if (isSelected) Color.Transparent
                                else if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)

                        Box(
                                modifier =
                                        Modifier.clip(CircleShape)
                                                .background(bgColor)
                                                .border(1.dp, borderColor, CircleShape)
                                                .clickable { onMetricSelected(metric) }
                                                .padding(horizontal = 20.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                        ) {
                                Text(
                                        text = metric.label,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = contentColor
                                )
                        }
                }
        }
}

@Composable
fun ChartSection(
        isDark: Boolean,
        state: BodyMetricsUiState,
        textPrimary: Color,
        textSecondary: Color,
        onNavigateToHistory: () -> Unit
) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Text(
                                "Progreso",
                                style =
                                        MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                        ),
                                color = textPrimary,
                        )
                        Text(
                                "Ver historial",
                                style =
                                        MaterialTheme.typography.labelLarge.copy(
                                                color = Primary,
                                                fontWeight = FontWeight.Medium
                                        ),
                                modifier = Modifier.clickable { onNavigateToHistory() }
                        )
                }
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                ) {
                        Column {
                                Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                                text = state.currentWeight,
                                                fontSize = 36.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textPrimary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                                text = "kg",
                                                fontSize = 20.sp,
                                                color = textSecondary,
                                                modifier = Modifier.padding(bottom = 6.dp)
                                        )
                                }
                        }

                        // Trend Badge
                        val badgeBg =
                                if (isDark) Color(0xFF14532D).copy(alpha = 0.4f)
                                else Color(0xFFDCFCE7)
                        val badgeContent = if (isDark) Primary else Color(0xFF15803D)
                        Row(
                                modifier =
                                        Modifier.background(badgeBg, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Icon(
                                        imageVector = Icons.Default.TrendingDown,
                                        contentDescription = null,
                                        tint = badgeContent,
                                        modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                        text = state.weightChange,
                                        color = badgeContent,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                )
                        }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Canvas Chart
                Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                        // Grid Lines layered behind
                        Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                        ) {
                                repeat(5) {
                                        Box(modifier = Modifier.fillMaxWidth()
                                                .height(1.dp)
                                                .background(color =
                                                        if (isDark) Color(0xFF334155)
                                                                .copy(alpha = 0.5f)
                                                        else Color(0xFFE2E8F0)
                                                )
                                        )
                                }
                        }

                        SmoothLineChart(
                                data = state.chartData,
                                lineColor = Primary,
                                modifier = Modifier.fillMaxSize()
                        )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // X Axis Labels
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                        state.chartLabels.forEach { label ->
                                Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        color = textSecondary,
                                        fontWeight = FontWeight.Medium
                                )
                        }
                }
        }
}

@Composable
fun SmoothLineChart(data: List<Float>, lineColor: Color, modifier: Modifier = Modifier) {
        Canvas(modifier = modifier) {
                if (data.isEmpty()) return@Canvas

                val spacePerPoint = size.width / (data.size - 1)
                val minVal = data.minOrNull() ?: 0f
                val maxVal = data.maxOrNull() ?: 100f
                val range = maxVal - minVal
                // Add some padding to range so it doesn't hit edges exactly
                val paddedRange = if (range == 0f) 1f else range * 1.5f
                val yOffset = if (range == 0f) minVal * 0.5f else (minVal - range * 0.25f)

                val points =
                        data.mapIndexed { index, value ->
                                Offset(
                                        x = index * spacePerPoint,
                                        y =
                                                size.height -
                                                        ((value - yOffset) / paddedRange *
                                                                size.height)
                                )
                        }

                // Draw Gradient Fill
                val path = Path()
                path.moveTo(points.first().x, size.height)
                path.lineTo(points.first().x, points.first().y)

                for (i in 0 until points.size - 1) {
                        val p1 = points[i]
                        val p2 = points[i + 1]
                        val controlPoint1 = Offset(p1.x + (p2.x - p1.x) / 2, p1.y)
                        val controlPoint2 = Offset(p1.x + (p2.x - p1.x) / 2, p2.y)
                        path.cubicTo(
                                controlPoint1.x,
                                controlPoint1.y,
                                controlPoint2.x,
                                controlPoint2.y,
                                p2.x,
                                p2.y
                        )
                }

                path.lineTo(points.last().x, size.height)
                path.close()

                drawPath(
                        path = path,
                        brush =
                                Brush.verticalGradient(
                                        colors =
                                                listOf(
                                                        lineColor.copy(alpha = 0.25f),
                                                        lineColor.copy(alpha = 0f)
                                                ),
                                        startY = 0f,
                                        endY = size.height
                                )
                )

                // Draw Line with Glow
                val linePath = Path()
                linePath.moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                        val p1 = points[i]
                        val p2 = points[i + 1]
                        val controlPoint1 = Offset(p1.x + (p2.x - p1.x) / 2, p1.y)
                        val controlPoint2 = Offset(p1.x + (p2.x - p1.x) / 2, p2.y)
                        linePath.cubicTo(
                                controlPoint1.x,
                                controlPoint1.y,
                                controlPoint2.x,
                                controlPoint2.y,
                                p2.x,
                                p2.y
                        )
                }

                // Glow effect (simulated with multiple strokes)
                drawPath(
                        path = linePath,
                        color = lineColor.copy(alpha = 0.5f),
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )
                drawPath(
                        path = linePath,
                        color = lineColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw Dots
                points.forEachIndexed { index, point ->
                        val isLast = index == points.lastIndex
                        val radius = if (isLast) 6.dp.toPx() else 4.dp.toPx()

                        // Outer ring
                        if (!isLast) {
                                drawCircle(
                                        color = lineColor,
                                        radius = radius,
                                        center = point,
                                        style = Stroke(width = 2.dp.toPx())
                                )
                                drawCircle(
                                        color = Color.Black, // Should match background
                                        radius = radius - 1.dp.toPx(),
                                        center = point
                                )
                        } else {
                                drawCircle(color = lineColor, radius = radius, center = point)
                                drawCircle(
                                        color = Color.White,
                                        radius = 2.dp.toPx(),
                                        center = point
                                )
                        }
                }
        }
}

@Composable
fun StatsGrid(
        isDark: Boolean,
        measurements: List<MeasurementItem>,
        textPrimary: Color,
        textSecondary: Color,
        surfaceColor: Color,
) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Text(
                                "Últimas mediciones",
                                style =
                                        MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                        ),
                                color = textPrimary,
                        )
                }
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Row 1
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                // Card 1 (Waist)
                                if (measurements.isNotEmpty()) {
                                        MetricCard(
                                                modifier = Modifier.weight(1f),
                                                item = measurements[0],
                                                isDark = isDark,
                                                textPrimary = textPrimary,
                                                textSecondary = textSecondary,
                                                surfaceColor = surfaceColor
                                        )
                                }

                                // Card 2 (Body Fat)
                                if (measurements.size > 1) {
                                        MetricCard(
                                                modifier = Modifier.weight(1f),
                                                item = measurements[1],
                                                isDark = isDark,
                                                textPrimary = textPrimary,
                                                textSecondary = textSecondary,
                                                surfaceColor = surfaceColor
                                        )
                                } else {
                                        // If only 1 item, second slot is Add button or empty?
                                        // Let's handle generic grid logic simpler:
                                        AddMetricCard(Modifier.weight(1f), isDark, textSecondary)
                                }
                        }

                        // Row 2
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                // Card 3 (Chest)
                                if (measurements.size > 2) {
                                        MetricCard(
                                                modifier = Modifier.weight(1f),
                                                item = measurements[2],
                                                isDark = isDark,
                                                textPrimary = textPrimary,
                                                textSecondary = textSecondary,
                                                surfaceColor = surfaceColor
                                        )
                                } else if (measurements.size == 2) {
                                        AddMetricCard(Modifier.weight(1f), isDark, textSecondary)
                                } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                }

                                // Card 4 (Add Button if not placed yet)
                                if (measurements.size > 2) {
                                        AddMetricCard(Modifier.weight(1f), isDark, textSecondary)
                                } else if (measurements.size == 2) {
                                        Spacer(modifier = Modifier.weight(1f))
                                }
                        }
                }
        }
}

@Composable
fun MetricCard(
        modifier: Modifier,
        item: MeasurementItem,
        isDark: Boolean,
        textPrimary: Color,
        textSecondary: Color,
        surfaceColor: Color
) {
        Card(
                modifier = modifier.height(160.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(0.dp),
                border =
                        BorderStroke(
                                1.dp,
                                if (isDark) Color(0xFF334155).copy(0.2f) else Color(0xFFE2E8F0)
                        )
        ) {
                Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                ) {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                        ) {
                                // Icon Box
                                val (iconBg, iconTint, icon) =
                                        when (item.type) {
                                                MetricType.WEIGHT ->
                                                        Triple(
                                                                if (isDark) Color(0xFF431407)
                                                                else
                                                                        Color(
                                                                                0xFFFFEDD5
                                                                        ), // Orange-ish
                                                                if (isDark) Color(0xFFFB923C)
                                                                else Color(0xFFEA580C),
                                                                Icons.Default.Straighten
                                                        )
                                                MetricType.BODY_FAT ->
                                                        Triple(
                                                                if (isDark) Color(0xFF172554)
                                                                else Color(0xFFDBEAFE), // Blue-ish
                                                                if (isDark) Color(0xFF60A5FA)
                                                                else Color(0xFF2563EB),
                                                                Icons.Default.MonitorHeart
                                                        )
                                                MetricType.IMC ->
                                                        Triple(
                                                                if (isDark) Color(0xFF581C87)
                                                                else
                                                                        Color(
                                                                                0xFFF3E8FF
                                                                        ), // Purple-ish
                                                                if (isDark) Color(0xFFC084FC)
                                                                else Color(0xFF9333EA),
                                                                Icons.Outlined
                                                                        .AccessibilityNew // close
                                                                // match
                                                                )
                                                else ->
                                                        Triple(
                                                                Color.Gray,
                                                                Color.White,
                                                                Icons.Default.Straighten
                                                        )
                                        }

                                Box(
                                        modifier =
                                                Modifier.size(36.dp)
                                                        .background(
                                                                iconBg,
                                                                RoundedCornerShape(8.dp)
                                                        ),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = iconTint,
                                                modifier = Modifier.size(20.dp)
                                        )
                                }

                                Text(
                                        text = item.date,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = textSecondary,
                                        fontSize = 11.sp
                                )
                        }

                        Column {
                                Text(
                                        text = item.type.label,
                                        color = textSecondary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                )
                                Row(
                                        Modifier.padding(top = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.Bottom
                                ) {
                                        Text(
                                                text = item.value,
                                                color = textPrimary,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                                text = item.unit,
                                                color = textSecondary,
                                                fontSize = 14.sp,
                                                modifier = Modifier.padding(bottom = 3.dp)
                                        )
                                }

                                // Change label color
                                val changeColor =
                                        when (item.changeType) {
                                                ChangeType.POSITIVE ->
                                                        if (isDark) Primary else Color(0xFF16A34A)
                                                ChangeType.NEGATIVE ->
                                                        if (isDark) Color(0xFFF87171)
                                                        else Color(0xFFDC2626) // Red
                                                ChangeType.NEUTRAL -> textSecondary
                                        }

                                Text(
                                        text = item.changeLabel,
                                        color = changeColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(top = 4.dp)
                                )
                        }
                }
        }
}

@Composable
fun AddMetricCard(modifier: Modifier, isDark: Boolean, textSecondary: Color) {
        val borderColor = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)

        Box(
                modifier =
                        modifier.height(160.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                        if (isDark) Color.White.copy(0.05f) else Color(0xFFF1F5F9)
                                )
                                // Dashed border is tricky in pure Compose, standard border for now
                                // or
                                // custom draw
                                .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                                .clickable { /* TODO */}
                                .padding(16.dp),
                contentAlignment = Alignment.Center
        ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                                modifier =
                                        Modifier.size(48.dp)
                                                .background(
                                                        if (isDark) Color.White.copy(0.1f)
                                                        else Color.White,
                                                        CircleShape
                                                )
                                                .shadow(
                                                        elevation = if (isDark) 0.dp else 2.dp,
                                                        shape = CircleShape
                                                ),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        Icons.Default.Add,
                                        contentDescription = null,
                                        tint = textSecondary
                                )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                                text = "Add Metric",
                                color = textSecondary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                        )
                }
        }
}
