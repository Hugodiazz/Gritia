package com.devdiaz.gritia.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.devdiaz.gritia.ui.theme.BackgroundDark
import com.devdiaz.gritia.ui.theme.Primary
import com.devdiaz.gritia.ui.theme.SurfaceDark
import com.devdiaz.gritia.ui.theme.TextDark
import com.devdiaz.gritia.ui.theme.TextSecondaryDark

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel(), onBackClick: () -> Unit = {}) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
            containerColor = BackgroundDark
    ) { innerPadding ->
        LazyColumn(
                modifier =
                        Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { ProfileHeader(uiState) }
            item { StatsDashboard(uiState) }
            item { SettingsSection() }
            item { SupportSection() }
            item { DangerZone() }
        }
    }
}

@Composable
fun ProfileHeader(state: ProfileUiState) {
    Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(modifier = Modifier.padding(bottom = 24.dp)) {
            Box(
                    modifier =
                            Modifier.size(128.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, SurfaceDark, CircleShape)
                                    .border(2.dp, Primary.copy(alpha = 0.5f), CircleShape)
            ) {
                AsyncImage(
                        model = state.profileImageUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                )
            }
            // Edit Button
            Box(
                    modifier =
                            Modifier.align(Alignment.BottomEnd)
                                    .background(Primary, CircleShape)
                                    .border(2.dp, BackgroundDark, CircleShape)
                                    .padding(8.dp)
            ) {
                Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = BackgroundDark,
                        modifier = Modifier.size(16.dp)
                )
            }
        }

        // Name
        Text(
                text = state.name,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = TextDark,
                modifier = Modifier.padding(bottom = 4.dp)
        )

        // Status
        Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
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
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
            Text(
                    text = state.joinDate,
                    color = TextSecondaryDark,
                    style = MaterialTheme.typography.bodySmall
            )
        }

        // Edit Details Button
        OutlinedButton(
                onClick = { /* TODO */},
                colors =
                        ButtonDefaults.outlinedButtonColors(
                                contentColor = TextDark,
                                containerColor = SurfaceDark
                        ),
                border = null, // Using Surface color instead of border as per design interpretation
                modifier = Modifier.height(48.dp).fillMaxWidth(0.6f)
        ) {
            Text(
                    "Editar perfil",
                    style =
                            MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                            ),
                    modifier = Modifier.padding(end = 8.dp)
            )
            Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = TextSecondaryDark,
                    modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun StatsDashboard(state: ProfileUiState) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                    "RESUMEN",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
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
                                        MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Medium
                                        )
                        )
                        Text(
                                state.weeklyActivity,
                                color = TextDark,
                                style =
                                        MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold
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
                    val heights = listOf(0.4f, 0.7f, 0.55f, 0.9f, 0.3f, 0.6f, 0.1f)
                    val days = listOf("L", "M", "M", "J", "V", "S", "D")

                    heights.forEachIndexed { index, h ->
                        Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                    modifier =
                                            Modifier.width(8.dp) // Thinner bars
                                                    .fillMaxHeight()
                                                    .clip(RoundedCornerShape(50))
                                                    .background(
                                                            Color.Gray.copy(alpha = 0.2f)
                                                    ) // Track
                            ) {
                                Box(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .fillMaxHeight(h)
                                                        .align(Alignment.BottomCenter)
                                                        .background(
                                                                if (index == 3) Primary
                                                                else Primary.copy(alpha = 0.4f)
                                                        ) // Highlight Thursday
                                )
                            }
                            Text(
                                    days[index],
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (index == 3) Primary else TextSecondaryDark
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
                                        MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.Bold
                                        ),
                                color = TextDark
                        )
                        if (unit != null) {
                            Text(
                                    unit,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondaryDark,
                                    modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
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
fun SettingsSection() {
    SectionContainer(title = "CONFIGURACIÓN") {
        SettingsItem(
                icon = Icons.Default.Settings,
                iconColor = Color(0xFF3B82F6),
                title = "Preferencias"
        )
        SettingsItem(
                icon = Icons.Default.Straighten,
                iconColor = Color(0xFFA855F7),
                title = "Unidades",
                value = "Métrico",
                isLast = true
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
            SupportButton(icon = Icons.Default.Help, label = "FAQ", modifier = Modifier.weight(1f))
            SupportButton(icon = Icons.Default.Chat, label = "Chat", modifier = Modifier.weight(1f))
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
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
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
        isLast: Boolean = false
) {
    Row(
            modifier = Modifier.fillMaxWidth().clickable {}.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                    modifier =
                            Modifier.size(36.dp)
                                    .background(iconColor.copy(alpha = 0.1f), CircleShape)
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
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondaryDark)
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
fun DangerZone() {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
        OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondaryDark),
                border =
                        androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Color.White.copy(alpha = 0.1f)
                        )
        ) {
            Text(
                    "Cerrar sesión",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
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
