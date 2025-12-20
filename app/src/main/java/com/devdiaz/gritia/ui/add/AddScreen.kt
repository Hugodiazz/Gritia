package com.devdiaz.gritia.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devdiaz.gritia.ui.theme.*

// Mock Data Models
data class BodyMeasurement(
        val name: String,
        val lastValue: String,
        val unit: String,
        val icon: ImageVector
)

val upperBodyItems =
        listOf(
                BodyMeasurement("Cuello", "30", "cm", Icons.Outlined.AccessibilityNew),
                BodyMeasurement("Pecho", "102", "cm", Icons.Outlined.AccessibilityNew),
                BodyMeasurement("Bicep", "34", "cm", Icons.Outlined.FitnessCenter),
                BodyMeasurement("Antebrazo", "34.5", "cm", Icons.Outlined.FitnessCenter)
        )

val coreLegsItems =
        listOf(
                BodyMeasurement("Cintura", "82", "cm", Icons.Outlined.Straighten),
                BodyMeasurement("Cadera", "82", "cm", Icons.Outlined.Straighten),
                BodyMeasurement("Cuadricep", "58", "cm", Icons.Outlined.DirectionsWalk),
                BodyMeasurement("Pantorrilla", "38", "cm", Icons.Outlined.DirectionsRun)
        )

@Composable
fun AddScreen(onNavigateBack: () -> Unit = {}) {
    Scaffold(
            containerColor = BackgroundDark,
            topBar = { TopBar(onNavigateBack) },
            bottomBar = { SaveButton() }
    ) { innerPadding ->
        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .padding(innerPadding)
                                .verticalScroll(rememberScrollState())
        ) {
            DateSelector()

            GeneralStatsSection()

            BodySection(title = "Upper Body", items = upperBodyItems)

            BodySection(title = "Core & Legs", items = coreLegsItems)
        }
    }
}

@Composable
fun TopBar(onClose: () -> Unit) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .height(64.dp)
                            .background(BackgroundDark.copy(alpha = 0.95f))
                            .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.05f)
                            ) // Bottom border
                            .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
                onClick = onClose,
                modifier =
                        Modifier.size(40.dp).background(Color.White.copy(alpha = 0.1f), CircleShape)
        ) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White) }

        Text(
                text = "Body Measurements",
                style = MaterialTheme.typography.titleMedium,
                color = TextDark,
                fontWeight = FontWeight.Bold
        )

        IconButton(
                onClick = { /* TODO: History */},
                modifier = Modifier.size(40.dp).background(Primary.copy(alpha = 0.1f), CircleShape)
        ) { Icon(Icons.Default.History, contentDescription = "History", tint = Primary) }
    }
}

@Composable
fun DateSelector() {
    Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                    onClick = { /* TODO: Prev Date */},
                    modifier =
                            Modifier.size(40.dp)
                                    .background(SurfaceDark, CircleShape)
                                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Previous",
                        tint = TextSecondaryLight
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                        text = "ENTRY DATE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                            text = "Today, Oct 24", // Dynamic date would go here
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextDark,
                            fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = "Calendar",
                            tint = TextSecondaryLight,
                            modifier = Modifier.size(20.dp)
                    )
                }
            }

            IconButton(
                    onClick = { /* TODO: Next Date */},
                    enabled = false,
                    modifier =
                            Modifier.size(40.dp)
                                    .background(SurfaceDark, CircleShape)
                                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                                    .alpha(0.5f) // Visual disabled state
            ) {
                Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Next",
                        tint = TextSecondaryLight
                )
            }
        }
    }
}

@Composable
fun GeneralStatsSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
                text = "GENERAL STATS",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondaryLight,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(label = "Weight", unit = "kg", modifier = Modifier.weight(1f))
            StatCard(label = "Body Fat", unit = "%", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCard(label: String, unit: String, modifier: Modifier = Modifier) {
    var value by remember { mutableStateOf("") }

    // Focus state for border color change
    // Using simple boolean for demo
    val isFocused = false

    Column(
            modifier =
                    modifier.background(SurfaceDark, RoundedCornerShape(16.dp))
                            .border(
                                    width = 1.dp,
                                    color =
                                            if (isFocused) Primary
                                            else Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
    ) {
        Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryLight,
                fontWeight = FontWeight.Medium
        )
        Row(verticalAlignment = Alignment.Bottom) {
            BasicTextField(
                    value = value,
                    onValueChange = { value = it },
                    textStyle =
                            MaterialTheme.typography.headlineMedium.copy(
                                    color = TextDark,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Start
                            ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            Text(
                                    text = "0.0",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White.copy(alpha = 0.2f),
                                    fontWeight = FontWeight.Bold
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier.weight(1f)
            )
            Text(
                    text = unit,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryLight,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}

@Composable
fun BodySection(title: String, items: List<BodyMeasurement>) {
    Column(modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp)) {
        Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondaryLight,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items.forEach { item -> MeasurementInputItem(item = item) }
        }
    }
}

@Composable
fun MeasurementInputItem(item: BodyMeasurement) {
    var value by remember { mutableStateOf("") }

    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .background(SurfaceDark, RoundedCornerShape(16.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                            .padding(4.dp)
                            .padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Box
        Box(
                modifier =
                        Modifier.size(48.dp)
                                .background(
                                        Color.White.copy(alpha = 0.05f),
                                        RoundedCornerShape(12.dp)
                                ),
                contentAlignment = Alignment.Center
        ) {
            Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = TextSecondaryLight,
                    modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDark,
                    fontWeight = FontWeight.Medium
            )
            Text(
                    text = "Last: ${item.lastValue} ${item.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryLight,
                    fontSize = 12.sp
            )
        }

        // Input
        Row(modifier = Modifier.width(100.dp), verticalAlignment = Alignment.Bottom) {
            BasicTextField(
                    value = value,
                    onValueChange = { value = it },
                    textStyle =
                            MaterialTheme.typography.titleLarge.copy(
                                    color = TextDark,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.End
                            ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.BottomEnd) {
                            if (value.isEmpty()) {
                                Text(
                                        text = "0",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White.copy(alpha = 0.2f),
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.End
                                )
                            }
                            innerTextField()
                        }
                    },
                    modifier =
                            Modifier.weight(1f)
                                    .padding(bottom = 4.dp)
                                    .border(
                                            width = 0.dp,
                                            color = Color.Transparent
                                    ) // Remove default border if any
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                    text = item.unit,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryLight,
                    modifier = Modifier.padding(bottom = 6.dp)
            )
        }
    }
}

@Composable
fun SaveButton() {
    Box(
            modifier =
                    Modifier.fillMaxWidth()
                            .background(
                                    Brush.verticalGradient(
                                            colors =
                                                    listOf(
                                                            BackgroundDark.copy(alpha = 0f),
                                                            BackgroundDark.copy(alpha = 0.95f),
                                                            BackgroundDark
                                                    )
                                    )
                            )
                            .padding(16.dp)
                            .padding(bottom = 16.dp) // Extra safe area
    ) {
        Button(
                onClick = { /* TODO: Save */},
                modifier =
                        Modifier.fillMaxWidth()
                                .height(56.dp)
                                .shadow(
                                        elevation = 8.dp,
                                        spotColor = Primary.copy(alpha = 0.5f),
                                        shape = CircleShape
                                ),
                colors =
                        ButtonDefaults.buttonColors(
                                containerColor = Primary,
                                contentColor = BackgroundDark
                        ),
                shape = CircleShape
        ) {
            Text(
                    text = "Save Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
            )
        }
    }
}
