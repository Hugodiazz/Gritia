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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.devdiaz.gritia.ui.theme.*

// Mock Data Model to map to Enum
data class BodyMeasurementUiItem(
        val type: MeasurementType,
        val name: String,
        val unit: String,
        val icon: ImageVector
)

val upperBodyItems =
        listOf(
                BodyMeasurementUiItem(
                        MeasurementType.NECK,
                        "Cuello",
                        "cm",
                        Icons.Outlined.AccessibilityNew
                ),
                BodyMeasurementUiItem(
                        MeasurementType.CHEST,
                        "Pecho",
                        "cm",
                        Icons.Outlined.AccessibilityNew
                ),
                BodyMeasurementUiItem(
                        MeasurementType.BICEP,
                        "Bicep",
                        "cm",
                        Icons.Outlined.FitnessCenter
                ),
                BodyMeasurementUiItem(
                        MeasurementType.FOREARM,
                        "Antebrazo",
                        "cm",
                        Icons.Outlined.FitnessCenter
                )
        )

val coreLegsItems =
        listOf(
                BodyMeasurementUiItem(
                        MeasurementType.WAIST,
                        "Cintura",
                        "cm",
                        Icons.Outlined.Straighten
                ),
                BodyMeasurementUiItem(
                        MeasurementType.HIP,
                        "Cadera",
                        "cm",
                        Icons.Outlined.Straighten
                ),
                BodyMeasurementUiItem(
                        MeasurementType.QUAD,
                        "Pierna",
                        "cm",
                        Icons.Outlined.DirectionsWalk
                ),
                BodyMeasurementUiItem(
                        MeasurementType.CALF,
                        "Pantorrilla",
                        "cm",
                        Icons.Outlined.DirectionsRun
                )
        )

@Composable
fun AddScreen(onNavigateBack: () -> Unit = {}, viewModel: AddViewModel = hiltViewModel()) {
        val uiState by viewModel.uiState.collectAsState()

        // Handle one-time events
        LaunchedEffect(Unit) { viewModel.saveSuccess.collect { onNavigateBack() } }

        Scaffold(
                containerColor = BackgroundDark,
                topBar = { TopBar(onNavigateBack) },
                bottomBar = { SaveButton(onSave = viewModel::saveProgress) }
        ) { innerPadding ->
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(innerPadding)
                                        .verticalScroll(rememberScrollState())
                ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        GeneralStatsSection(
                                weight = uiState.weight,
                                previousWeight = uiState.previousWeight,
                                onWeightChange = viewModel::updateWeight,
                                bodyFat = uiState.bodyFat,
                                previousBodyFat = uiState.previousBodyFat,
                                isBodyFatManual = uiState.isBodyFatManual,
                                onBodyFatChange = viewModel::updateBodyFat,
                                onToggleBodyFatManual = viewModel::toggleBodyFatManual,
                        )

                        BodySection(
                                title = "Upper Body",
                                items = upperBodyItems,
                                currentValues = uiState.measurements,
                                previousValues = uiState.previousMeasurements,
                                onValueChange = viewModel::updateMeasurement
                        )

                        BodySection(
                                title = "Core & Legs",
                                items = coreLegsItems,
                                currentValues = uiState.measurements,
                                previousValues = uiState.previousMeasurements,
                                onValueChange = viewModel::updateMeasurement
                        )
                }
        }
}

@Composable
fun TopBar(onClose: () -> Unit) {
        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(BackgroundDark.copy(alpha = 0.95f))
                                .border(width = 1.dp, color = Color.White.copy(alpha = 0.05f))
        ) {
                Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                Row(
                        modifier =
                                Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        IconButton(
                                onClick = onClose,
                                modifier =
                                        Modifier.size(40.dp)
                                                .background(
                                                        Color.White.copy(alpha = 0.1f),
                                                        CircleShape
                                                )
                        ) {
                                Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                )
                        }

                        Text(
                                text = "Registrar Medidas",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextDark,
                                fontWeight = FontWeight.Bold
                        )

                        // Placeholder to balance the layout if needed, or just Spacer
                        Spacer(modifier = Modifier.size(40.dp))
                }
        }
}

@Composable
fun GeneralStatsSection(
        weight: String,
        previousWeight: String,
        onWeightChange: (String) -> Unit,
        bodyFat: String,
        previousBodyFat: String,
        isBodyFatManual: Boolean,
        onBodyFatChange: (String) -> Unit,
        onToggleBodyFatManual: (Boolean) -> Unit
) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                        text = "ESTADÍSTICAS GENERALES",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondaryLight,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
                )
                Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.height(IntrinsicSize.Max)
                ) {
                        StatCard(
                                label = "Peso",
                                unit = "kg",
                                value = weight,
                                previousValue = previousWeight,
                                onValueChange = onWeightChange,
                                modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                        StatCard(
                                label = "% Grasa",
                                unit = "%",
                                value = bodyFat,
                                previousValue = previousBodyFat,
                                onValueChange = onBodyFatChange,
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                isBodyFat = true,
                                isManual = isBodyFatManual,
                                onToggleManual = onToggleBodyFatManual
                        )
                }
        }
}

@Composable
fun StatCard(
        label: String,
        unit: String,
        value: String,
        previousValue: String = "",
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        isBodyFat: Boolean = false,
        isManual: Boolean = true,
        onToggleManual: ((Boolean) -> Unit)? = null
) {
        Column(
                modifier =
                        modifier.background(SurfaceDark, RoundedCornerShape(16.dp))
                                .border(
                                        width = 1.dp,
                                        color = Color.White.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
        ) {
                Column {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondaryLight,
                                        fontWeight = FontWeight.Medium
                                )
                                if (isBodyFat && onToggleManual != null) {
                                        Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(start = 4.dp)
                                        ) {
                                                Text(
                                                        text = if (isManual) "MAN" else "AUTO",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color =
                                                                if (isManual) Primary
                                                                else TextSecondaryLight,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(end = 4.dp)
                                                )
                                                Switch(
                                                        checked = isManual,
                                                        onCheckedChange = onToggleManual,
                                                        modifier =
                                                                Modifier.scale(0.6f).height(30.dp)
                                                )
                                        }
                                }
                        }

                        Row(verticalAlignment = Alignment.Bottom) {
                                BasicTextField(
                                        value = value,
                                        onValueChange = onValueChange,
                                        enabled = isManual,
                                        textStyle =
                                                MaterialTheme.typography.headlineMedium.copy(
                                                        color =
                                                                if (isManual) TextDark
                                                                else TextDark.copy(alpha = 0.5f),
                                                        fontWeight = FontWeight.Bold,
                                                        textAlign = TextAlign.Start
                                                ),
                                        keyboardOptions =
                                                KeyboardOptions(keyboardType = KeyboardType.Number),
                                        decorationBox = { innerTextField ->
                                                Box(contentAlignment = Alignment.CenterStart) {
                                                        if (value.isEmpty()) {
                                                                Text(
                                                                        text = "0.0",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .headlineMedium,
                                                                        color =
                                                                                Color.White.copy(
                                                                                        alpha = 0.2f
                                                                                ),
                                                                        fontWeight = FontWeight.Bold
                                                                )
                                                        }
                                                        innerTextField()
                                                }
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
                if (previousValue.isNotEmpty()) {
                        Text(
                                text = "Prev: $previousValue",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryLight.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 4.dp)
                        )
                }
        }
}

@Composable
fun BodySection(
        title: String,
        items: List<BodyMeasurementUiItem>,
        currentValues: Map<MeasurementType, String>,
        previousValues: Map<MeasurementType, String>,
        onValueChange: (MeasurementType, String) -> Unit
) {
        Column(modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp)) {
                Text(
                        text = title.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondaryLight,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items.forEach { item ->
                                MeasurementInputItem(
                                        item = item,
                                        value = currentValues[item.type] ?: "",
                                        previousValue = previousValues[item.type] ?: "",
                                        onValueChange = { onValueChange(item.type, it) }
                                )
                        }
                }
        }
}

@Composable
fun MeasurementInputItem(
        item: BodyMeasurementUiItem,
        value: String,
        previousValue: String = "",
        onValueChange: (String) -> Unit
) {
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(SurfaceDark, RoundedCornerShape(16.dp))
                                .border(
                                        1.dp,
                                        Color.White.copy(alpha = 0.1f),
                                        RoundedCornerShape(16.dp)
                                )
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
                        if (previousValue.isNotEmpty()) {
                                Text(
                                        text = "Prev: $previousValue ${item.unit}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondaryLight.copy(alpha = 0.7f)
                                )
                        }
                }

                // Input
                Row(modifier = Modifier.width(100.dp), verticalAlignment = Alignment.Bottom) {
                        BasicTextField(
                                value = value,
                                onValueChange = onValueChange,
                                textStyle =
                                        MaterialTheme.typography.titleLarge.copy(
                                                color = TextDark,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.End
                                        ),
                                keyboardOptions =
                                        KeyboardOptions(keyboardType = KeyboardType.Number),
                                decorationBox = { innerTextField ->
                                        Box(contentAlignment = Alignment.BottomEnd) {
                                                if (value.isEmpty()) {
                                                        Text(
                                                                text = "0",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleLarge,
                                                                color =
                                                                        Color.White.copy(
                                                                                alpha = 0.2f
                                                                        ),
                                                                fontWeight = FontWeight.Bold,
                                                                textAlign = TextAlign.End
                                                        )
                                                }
                                                innerTextField()
                                        }
                                },
                                modifier = Modifier.weight(1f).padding(bottom = 4.dp)
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
fun SaveButton(onSave: () -> Unit) {
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
                                .padding(bottom = 16.dp)
        ) {
                Button(
                        onClick = onSave,
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
                                text = "Guardar Progreso",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                        )
                }
        }
}
