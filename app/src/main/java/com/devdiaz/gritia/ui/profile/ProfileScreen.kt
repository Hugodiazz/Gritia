package com.devdiaz.gritia.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devdiaz.gritia.ui.theme.BackgroundDark
import com.devdiaz.gritia.ui.theme.Primary
import com.devdiaz.gritia.ui.theme.SurfaceDark
import com.devdiaz.gritia.ui.theme.TextDark
import com.devdiaz.gritia.ui.theme.TextSecondaryDark
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
        viewModel: ProfileViewModel = hiltViewModel(),
        onBackClick: () -> Unit = {},
        onLogout: () -> Unit = {}
) {
        val uiState by viewModel.uiState.collectAsState()

        // Handle navigation events
        androidx.compose.runtime.LaunchedEffect(viewModel.navigationEvents) {
            viewModel.navigationEvents.collect { event: ProfileNavigationEvent ->
                when (event) {
                    is ProfileNavigationEvent.NavigateToLogin -> onLogout()
                }
            }
        }

        Scaffold(containerColor = BackgroundDark) { innerPadding ->
                LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        item { ProfileHeader(uiState) }
                        item { StatsDashboard(uiState) }
                        item { PersonalDetailsSection(uiState, viewModel) }
                        item { SettingsSection() }
                        item { SupportSection() }
                        item { DangerZone(viewModel, onLogout) }
                }

                if (uiState.activeDialog != null) {
                        val dismissAction = { viewModel.dismissDialog() }

                        when (uiState.activeDialog) {
                                ProfileDialogType.GENDER -> {
                                        EditProfileDialog(
                                                title = "Editar Género",
                                                onDismiss = dismissAction,
                                                onConfirm = {
                                                } // Confirm handled inside content for single
                                                // selection
                                                ) {
                                                GenderSelectionContent(
                                                        currentGender = uiState.gender,
                                                        onGenderSelected = {
                                                                viewModel.updateGender(it)
                                                        }
                                                )
                                        }
                                }
                                ProfileDialogType.BIRTH_DATE -> {
                                        val dateState =
                                                rememberDatePickerState(
                                                        initialSelectedDateMillis =
                                                                try {
                                                                        // Parse current date or
                                                                        // default to now
                                                                        if (uiState.birthDate !=
                                                                                        "No definida"
                                                                        ) {
                                                                                // Assuming format
                                                                                // YYYY-MM-DD
                                                                                LocalDate.parse(
                                                                                                uiState.birthDate
                                                                                        )
                                                                                        .atStartOfDay(
                                                                                                ZoneId.of(
                                                                                                        "UTC"
                                                                                                )
                                                                                        )
                                                                                        .toInstant()
                                                                                        .toEpochMilli()
                                                                        } else null
                                                                } catch (e: Exception) {
                                                                        null
                                                                }
                                                )

                                        DatePickerDialog(
                                                onDismissRequest = dismissAction,
                                                confirmButton = {
                                                        TextButton(
                                                                onClick = {
                                                                        dateState.selectedDateMillis
                                                                                ?.let {
                                                                                        viewModel
                                                                                                .updateBirthDate(
                                                                                                        it
                                                                                                )
                                                                                }
                                                                }
                                                        ) { Text("Guardar", color = Primary) }
                                                },
                                                dismissButton = {
                                                        TextButton(onClick = dismissAction) {
                                                                Text(
                                                                        "Cancelar",
                                                                        color = TextSecondaryDark
                                                                )
                                                        }
                                                },
                                                colors =
                                                        DatePickerDefaults.colors(
                                                                containerColor = SurfaceDark
                                                        )
                                        ) { DatePicker(state = dateState) }
                                }
                                ProfileDialogType.HEIGHT -> {
                                        var heightInput by
                                                androidx.compose.runtime.remember {
                                                        androidx.compose.runtime.mutableStateOf(
                                                                uiState.height.replace(" cm", "")
                                                        )
                                                }
                                        EditProfileDialog(
                                                title = "Editar Altura (cm)",
                                                onDismiss = dismissAction,
                                                onConfirm = {
                                                        heightInput.toFloatOrNull()?.let {
                                                                viewModel.updateHeight(it)
                                                        }
                                                }
                                        ) {
                                                ValueInputContent(
                                                        value = heightInput,
                                                        onValueChange = { heightInput = it },
                                                        suffix = "cm"
                                                )
                                        }
                                }
                                ProfileDialogType.WEIGHT -> {
                                        var weightInput by
                                                androidx.compose.runtime.remember {
                                                        androidx.compose.runtime.mutableStateOf(
                                                                uiState.currentWeight.replace(
                                                                        " kg",
                                                                        ""
                                                                )
                                                        )
                                                }
                                        EditProfileDialog(
                                                title = "Registrar Peso Actual",
                                                onDismiss = dismissAction,
                                                onConfirm = {
                                                        weightInput.toFloatOrNull()?.let {
                                                                viewModel.updateWeight(it)
                                                        }
                                                }
                                        ) {
                                                ValueInputContent(
                                                        value = weightInput,
                                                        onValueChange = { weightInput = it },
                                                        suffix = "kg"
                                                )
                                        }
                                }
                                null -> {}
                        }
                }
        }
}

@Composable
fun ProfileHeader(state: ProfileUiState) {
        Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                // Name
                Text(
                        text = state.name,
                        style =
                                MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold
                                ),
                        color = TextDark,
                        modifier = Modifier.padding(bottom = 4.dp)
                )

                // Status
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                ) {
                        Surface(
                                color = Primary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.padding(end = 8.dp)
                        ) {
                                Text(
                                        text = state.memberStatus,
                                        color = Primary,
                                        style =
                                                MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold
                                                ),
                                        modifier =
                                                Modifier.padding(
                                                        horizontal = 12.dp,
                                                        vertical = 4.dp
                                                )
                                )
                        }
                        Text(
                                text = state.joinDate,
                                color = TextSecondaryDark,
                                style = MaterialTheme.typography.bodySmall
                        )
                }
        }
}

@Composable
fun StatsDashboard(state: ProfileUiState) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                Row(
                        modifier =
                                Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Text(
                                "RESUMEN",
                                style =
                                        MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                        ),
                                color = TextDark
                        )
                        Text(
                                "Ver historial",
                                style =
                                        MaterialTheme.typography.labelLarge.copy(
                                                color = Primary,
                                                fontWeight = FontWeight.Medium
                                        ),
                                modifier = Modifier.clickable {}
                        )
                }

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        // Workouts Card
                        StatsCard(
                                modifier = Modifier.weight(1f),
                                title = "Entrenamientos",
                                value = state.workoutsCount.toString(),
                                trend = "+${state.workoutsTrend} esta semana",
                                icon = Icons.Default.FitnessCenter
                        )
                        // Volume Card
                        StatsCard(
                                modifier = Modifier.weight(1f),
                                title = "Volúmen",
                                value = state.volumeTotal,
                                unit = state.volumeUnit,
                                trend = "+${state.volumeTrendPercent}% este mes.",
                                icon = Icons.Default.FitnessCenter // Should be weight icon ideally
                        )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Activity Chart Card (Simplified Visual)
                Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                ) {
                                        Column {
                                                Text(
                                                        "Actividad Semanal",
                                                        color = TextSecondaryDark,
                                                        style =
                                                                MaterialTheme.typography.bodySmall
                                                                        .copy(
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Medium
                                                                        )
                                                )
                                                Text(
                                                        state.weeklyActivity,
                                                        color = TextDark,
                                                        style =
                                                                MaterialTheme.typography.titleLarge
                                                                        .copy(
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Bold
                                                                        )
                                                )
                                        }
                                }

                                // Bars (Visual Only)
                                Row(
                                        modifier = Modifier.fillMaxWidth().height(160.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                ) {
                                        val heights =
                                                listOf(0.4f, 0.7f, 0.55f, 0.9f, 0.3f, 0.6f, 0.1f)
                                        val days = listOf("L", "M", "M", "J", "V", "S", "D")

                                        heights.forEachIndexed { index, h ->
                                                Column(
                                                        horizontalAlignment =
                                                                Alignment.CenterHorizontally,
                                                        modifier = Modifier.weight(1f)
                                                ) {
                                                        Box(
                                                                modifier =
                                                                        Modifier.width(
                                                                                        8.dp
                                                                                ) // Thinner bars
                                                                                .fillMaxHeight()
                                                                                .clip(
                                                                                        RoundedCornerShape(
                                                                                                50
                                                                                        )
                                                                                )
                                                                                .background(
                                                                                        Color.Gray
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.2f
                                                                                                )
                                                                                ) // Track
                                                        ) {
                                                                Box(
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                                        .fillMaxHeight(
                                                                                                h
                                                                                        )
                                                                                        .align(
                                                                                                Alignment
                                                                                                        .BottomCenter
                                                                                        )
                                                                                        .background(
                                                                                                if (index ==
                                                                                                                3
                                                                                                )
                                                                                                        Primary
                                                                                                else
                                                                                                        Primary.copy(
                                                                                                                alpha =
                                                                                                                        0.4f
                                                                                                        )
                                                                                        ) // Highlight Thursday
                                                                )
                                                        }
                                                        Text(
                                                                days[index],
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelSmall,
                                                                color =
                                                                        if (index == 3) Primary
                                                                        else TextSecondaryDark
                                                        )
                                                }
                                        }
                                }
                        }
                }
        }
}

@Composable
fun StatsCard(
        modifier: Modifier = Modifier,
        title: String,
        value: String,
        unit: String? = null,
        trend: String,
        icon: ImageVector
) {
        Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp),
                modifier = modifier.height(140.dp)
        ) {
                Box(modifier = Modifier.padding(20.dp).fillMaxSize()) {
                        Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Primary.copy(alpha = 0.1f), // Faded background icon
                                modifier =
                                        Modifier.align(Alignment.TopEnd)
                                                .size(60.dp)
                                                .offset(x = 10.dp, y = (-10).dp)
                        )

                        Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                        ) {
                                Column {
                                        Text(
                                                title,
                                                style =
                                                        MaterialTheme.typography.bodySmall.copy(
                                                                fontWeight = FontWeight.Medium
                                                        ),
                                                color = TextSecondaryDark
                                        )
                                        Row(verticalAlignment = Alignment.Bottom) {
                                                Text(
                                                        value,
                                                        style =
                                                                MaterialTheme.typography
                                                                        .headlineMedium.copy(
                                                                        fontWeight = FontWeight.Bold
                                                                ),
                                                        color = TextDark
                                                )
                                                if (unit != null) {
                                                        Text(
                                                                unit,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                color = TextSecondaryDark,
                                                                modifier =
                                                                        Modifier.padding(
                                                                                bottom = 4.dp,
                                                                                start = 2.dp
                                                                        )
                                                        )
                                                }
                                        }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                                Icons.Default.TrendingUp,
                                                contentDescription = null,
                                                tint = Primary,
                                                modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                                trend,
                                                style =
                                                        MaterialTheme.typography.labelSmall.copy(
                                                                fontWeight = FontWeight.Bold
                                                        ),
                                                color = Primary,
                                                modifier = Modifier.padding(start = 4.dp)
                                        )
                                }
                        }
                }
        }
}

@Composable
fun PersonalDetailsSection(state: ProfileUiState, viewModel: ProfileViewModel) {
        SectionContainer(title = "DATOS PERSONALES") {
                SettingsItem(
                        icon = Icons.Default.Chat,
                        iconColor = Color(0xFF10B981),
                        title = "Email",
                        value = state.email,
                        onClick = {} // Email usually not editable here or needs separate flow
                )
                SettingsItem(
                        icon = Icons.Default.TrendingUp,
                        iconColor = Color(0xFFF59E0B),
                        title = "Género",
                        value = state.gender,
                        onClick = { viewModel.showDialog(ProfileDialogType.GENDER) }
                )
                SettingsItem(
                        icon = Icons.Default.Edit,
                        iconColor = Color(0xFFEC4899),
                        title = "Fecha de Nacimiento",
                        value = state.birthDate,
                        onClick = { viewModel.showDialog(ProfileDialogType.BIRTH_DATE) }
                )
                SettingsItem(
                        icon = Icons.Default.Straighten,
                        iconColor = Color(0xFF3B82F6),
                        title = "Altura",
                        value = "${state.height} cm",
                        onClick = { viewModel.showDialog(ProfileDialogType.HEIGHT) }
                )
                SettingsItem(
                        icon = Icons.Default.FitnessCenter,
                        iconColor = Color(0xFFA855F7),
                        title = "Peso Actual",
                        value = "${state.currentWeight} kg",
                        isLast = true,
                        onClick = { viewModel.showDialog(ProfileDialogType.WEIGHT) }
                )
        }
}

@Composable
fun SettingsSection() {
        SectionContainer(title = "CONFIGURACIÓN") {
                SettingsItem(
                        icon = Icons.Default.Settings,
                        iconColor = Color(0xFF3B82F6),
                        title = "Preferencias",
                        onClick = {}
                )
                SettingsItem(
                        icon = Icons.Default.Straighten,
                        iconColor = Color(0xFFA855F7),
                        title = "Unidades",
                        value = "Métrico",
                        isLast = true,
                        onClick = {}
                )
        }
}

@Composable
fun SupportSection() {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                        text = "AYUDA",
                        style =
                                MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 2.sp
                                ),
                        color = TextSecondaryDark,
                        modifier = Modifier.padding(start = 12.dp, bottom = 12.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SupportButton(
                                icon = Icons.Default.Help,
                                label = "FAQ",
                                modifier = Modifier.weight(1f)
                        )
                        SupportButton(
                                icon = Icons.Default.Chat,
                                label = "Chat",
                                modifier = Modifier.weight(1f)
                        )
                        SupportButton(
                                icon = Icons.Default.BugReport,
                                label = "Reportar",
                                modifier = Modifier.weight(1f)
                        )
                }
        }
}

@Composable
fun SupportButton(icon: ImageVector, label: String, modifier: Modifier = Modifier) {
        Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp),
                modifier = modifier.height(100.dp).clickable {}
        ) {
                Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                ) {
                        Icon(
                                icon,
                                contentDescription = null,
                                tint = TextSecondaryDark,
                                modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                label,
                                style =
                                        MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold
                                        ),
                                color = TextDark
                        )
                }
        }
}

@Composable
fun SectionContainer(title: String, content: @Composable ColumnScope.() -> Unit) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                        text = title.uppercase(),
                        style =
                                MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 2.sp
                                ),
                        color = TextSecondaryDark,
                        modifier = Modifier.padding(start = 12.dp, bottom = 12.dp)
                )
                Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                ) { content() }
        }
}

@Composable
fun SettingsItem(
        icon: ImageVector,
        iconColor: Color,
        title: String,
        value: String? = null,
        isLast: Boolean = false,
        onClick: () -> Unit
) {
        Row(
                modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
        ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                                modifier =
                                        Modifier.size(36.dp)
                                                .background(
                                                        iconColor.copy(alpha = 0.1f),
                                                        CircleShape
                                                )
                                                .padding(8.dp),
                                contentAlignment = Alignment.Center
                        ) { Icon(icon, contentDescription = null, tint = iconColor) }
                        Text(
                                text = title,
                                style =
                                        MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Medium
                                        ),
                                color = TextDark,
                                modifier = Modifier.padding(start = 12.dp)
                        )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                        if (value != null) {
                                Surface(
                                        color = Color.White.copy(alpha = 0.05f),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.padding(end = 8.dp)
                                ) {
                                        Text(
                                                value,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextSecondaryDark,
                                                modifier =
                                                        Modifier.padding(
                                                                horizontal = 8.dp,
                                                                vertical = 4.dp
                                                        )
                                        )
                                }
                        }
                        Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextSecondaryDark
                        )
                }
        }
        if (!isLast) {
                HorizontalDivider(
                        modifier = Modifier.padding(start = 64.dp),
                        color = Color.White.copy(alpha = 0.05f)
                )
        }
}

@Composable
fun DangerZone(viewModel: ProfileViewModel, onLogout: () -> Unit) {

        var showDeleteConfirmDialog by
                androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

        if (showDeleteConfirmDialog) {
                AlertDialog(
                        onDismissRequest = { showDeleteConfirmDialog = false },
                        title = { Text("¿Eliminar cuenta?", color = TextDark) },
                        text = {
                                Text(
                                        "Esta acción eliminará permanentemente todos tus datos y no se puede deshacer.",
                                        color = TextSecondaryDark
                                )
                        },
                        confirmButton = {
                                TextButton(
                                        onClick = {
                                                showDeleteConfirmDialog = false
                                                viewModel.deleteAccount()
                                        }
                                ) { Text("Eliminar", color = Color(0xFFEF4444)) }
                        },
                        dismissButton = {
                                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                                        Text("Cancelar", color = TextSecondaryDark)
                                }
                        },
                        containerColor = SurfaceDark,
                        titleContentColor = TextDark,
                        textContentColor = TextSecondaryDark
                )
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
                OutlinedButton(
                        onClick = {
                                viewModel.logout()
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors =
                                ButtonDefaults.outlinedButtonColors(
                                        contentColor = TextSecondaryDark
                                ),
                        border =
                                androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        Color.White.copy(alpha = 0.1f)
                                )
                ) {
                        Text(
                                "Cerrar sesión",
                                style =
                                        MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold
                                        )
                        )
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier.fillMaxWidth()
                ) {
                        Text(
                                "Eliminar cuenta",
                                color = Color(0xFFEF4444),
                                style = MaterialTheme.typography.labelMedium
                        )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                        "Version 1.0.0",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark.copy(alpha = 0.5f)
                )
        }
}

@Composable
fun EditProfileDialog(
        title: String,
        onDismiss: () -> Unit,
        onConfirm: () -> Unit,
        content: @Composable () -> Unit
) {
        AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                        Text(title, style = MaterialTheme.typography.titleLarge, color = TextDark)
                },
                text = { content() },
                confirmButton = {
                        TextButton(onClick = onConfirm) {
                                Text("Guardar", color = Primary, fontWeight = FontWeight.Bold)
                        }
                },
                dismissButton = {
                        TextButton(onClick = onDismiss) {
                                Text("Cancelar", color = TextSecondaryDark)
                        }
                },
                containerColor = SurfaceDark,
                textContentColor = TextDark,
                titleContentColor = TextDark
        )
}

@Composable
fun GenderSelectionContent(currentGender: String, onGenderSelected: (String) -> Unit) {
        Column {
                val genders = listOf("Masculino", "Femenino", "Otro")
                genders.forEach { gender ->
                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .clickable { onGenderSelected(gender) }
                                                .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                RadioButton(
                                        selected = (gender == currentGender),
                                        onClick = { onGenderSelected(gender) },
                                        colors =
                                                RadioButtonDefaults.colors(
                                                        selectedColor = Primary,
                                                        unselectedColor = TextSecondaryDark
                                                )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = gender, color = TextDark)
                        }
                }
        }
}

@Composable
fun ValueInputContent(value: String, onValueChange: (String) -> Unit, suffix: String) {
        Column {
                OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        suffix = { Text(suffix) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors =
                                OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextDark,
                                        unfocusedTextColor = TextDark,
                                        focusedBorderColor = Primary,
                                        unfocusedBorderColor = TextSecondaryDark
                                ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                )
        }
}
