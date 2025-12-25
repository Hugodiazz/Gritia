package com.devdiaz.gritia.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devdiaz.gritia.model.Routine
import com.devdiaz.gritia.ui.library.TopBar
import com.devdiaz.gritia.ui.theme.BackgroundDark
import com.devdiaz.gritia.ui.theme.Primary
import com.devdiaz.gritia.ui.theme.SurfaceDark

@Composable
fun HomeScreen(
        viewModel: HomeViewModel = hiltViewModel(),
        onRoutineClick: (Long) -> Unit = {},
        onCreateRoutineClick: () -> Unit = {}
) {
        val routines by viewModel.routines.collectAsState()

        Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
                Column(modifier = Modifier.fillMaxSize()) {
                        TopBar("Mis rutinas")

                        // Routines List
                        LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                                items(routines) { routine ->
                                        RoutineCard(routine) {
                                                onRoutineClick(routine.id)
                                        } // Wait, onRoutineClick takes a Long, so pass routine.id
                                }
                        }
                }

                // Bottom Gradient Overlay
                Box(
                        modifier =
                                Modifier.align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .height(96.dp)
                                        .background(
                                                Brush.verticalGradient(
                                                        colors =
                                                                listOf(
                                                                        Color.Transparent,
                                                                        BackgroundDark
                                                                )
                                                )
                                        )
                )

                // FAB
                FloatingActionButton(
                        onClick = onCreateRoutineClick,
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
                                Text(text = "Nueva", fontWeight = FontWeight.Bold)
                        }
                }
        }
}

@Composable
fun RoutineCard(routine: Routine, onClick: () -> Unit) {
        Card(
                modifier =
                        Modifier.fillMaxWidth()
                                .height(120.dp) // Adjusted height
                                .clickable(onClick = onClick),
                shape = RoundedCornerShape(24.dp), // Approx 2rem
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
                Row(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        // Info
                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = routine.title,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                )
                                Text(
                                        text = routine.muscles,
                                        color = Primary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier =
                                                Modifier.padding(top = 4.dp).padding(bottom = 8.dp)
                                )

                                // Days
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        val days = listOf("L", "M", "M", "J", "V", "S", "D")
                                        routine.schedule.forEachIndexed { index, active ->
                                                DayCircle(text = days[index], active = active)
                                        }
                                }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Play Button
                        IconButton(
                                onClick = { onClick() },
                                modifier =
                                        Modifier.size(40.dp)
                                                .background(
                                                        Color.White.copy(alpha = 0.05f),
                                                        CircleShape
                                                )
                        ) {
                                Icon(
                                        Icons.Default.PlayArrow,
                                        contentDescription = "Start",
                                        tint = Color.White
                                )
                        }
                }
        }
}

@Composable
fun DayCircle(text: String, active: Boolean) {
        Box(
                modifier =
                        Modifier.size(20.dp)
                                .background(
                                        color =
                                                if (active) Primary
                                                else Color.White.copy(alpha = 0.05f),
                                        shape = CircleShape
                                ),
                contentAlignment = Alignment.Center
        ) {
                Text(
                        text = text,
                        fontSize = 10.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                        color = if (active) BackgroundDark else Color.White.copy(alpha = 0.4f)
                )
        }
}
