package com.devdiaz.gritia.ui.workout

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.devdiaz.gritia.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun WorkoutSummaryScreen(
        routineName: String,
        duration: String,
        totalVolume: String,
        onSaveClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = 100.dp) // Space for bottom button
        ) {
            // 1. Header with Pulse Effect
            Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 48.dp, bottom = 24.dp),
                    contentAlignment = Alignment.Center
            ) {
                // Pulse Animation
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val alpha by
                        infiniteTransition.animateFloat(
                                initialValue = 0.2f,
                                targetValue = 0.5f,
                                animationSpec =
                                        infiniteRepeatable(
                                                animation = tween(2000),
                                                repeatMode = RepeatMode.Reverse
                                        ),
                                label = "alpha"
                        )

                Box(
                        modifier =
                                Modifier.size(120.dp)
                                        .alpha(alpha)
                                        .background(Primary, CircleShape)
                                        .blur(
                                                30.dp
                                        ) // Note: Modifier.blur requires API 31+ or specific
                        // implementation.
                        // For compatibility, we might just use alpha/gradient if blur isn't
                        // available, but standard Compose has it now or we can skip.
                        // Actually, simple alpha circle is fine for "glow".
                        )

                Box(
                        modifier =
                                Modifier.size(80.dp)
                                        .background(
                                                brush =
                                                        Brush.linearGradient(
                                                                colors =
                                                                        listOf(
                                                                                Primary,
                                                                                Color(0xFF059669)
                                                                        ) // Emerald 600
                                                        ),
                                                shape = CircleShape
                                        )
                                        .shadow(10.dp, CircleShape, spotColor = Primary),
                        contentAlignment = Alignment.Center
                ) {
                    Text(
                            text = "🏆", // Using emoji as equivalent to material icon
                            // 'emoji_events' for simplicity or load icon
                            fontSize = 40.sp
                    )
                }
            }

            Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                        text = "Resumen de Victoria",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextDark,
                        fontWeight = FontWeight.ExtraBold
                )
                Text(
                        text = "¡Excelente trabajo hoy!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark,
                        fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Routine Card
            Box(
                    modifier =
                            Modifier.fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(SurfaceDark)
                                    .border(
                                            1.dp,
                                            Color.White.copy(alpha = 0.05f),
                                            RoundedCornerShape(20.dp)
                                    )
                                    .padding(16.dp)
            ) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = "RUTINA COMPLETADA",
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                                text = routineName,
                                style = MaterialTheme.typography.titleLarge,
                                color = TextDark,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Calendar Icon placeholder
                            Text(text = "📅", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            // Date logic
                            val dateStr = remember {
                                val now = LocalDate.now()
                                val formatter =
                                        DateTimeFormatter.ofPattern(
                                                "EEE, d MMM",
                                                Locale("es", "ES")
                                        ) // Spanish locale
                                now.format(formatter).replaceFirstChar { it.uppercase() }
                            }
                            Text(
                                    text = "$dateStr • HOY",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Grid Stats
            Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Time
                Column(
                        modifier =
                                Modifier.weight(1f)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(SurfaceDark)
                                        .border(
                                                1.dp,
                                                Color.White.copy(alpha = 0.05f),
                                                RoundedCornerShape(20.dp)
                                        )
                                        .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                            modifier =
                                    Modifier.background(
                                                    Color.White.copy(alpha = 0.05f),
                                                    CircleShape
                                            )
                                            .padding(8.dp)
                    ) {
                        Text(text = "⏱️", fontSize = 20.sp) // Timer icon
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                            text = "TIEMPO TOTAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                            text = duration,
                            style = MaterialTheme.typography.headlineSmall, // 2xl
                            color = TextDark,
                            fontWeight = FontWeight.ExtraBold
                    )
                }

                // Volume
                Column(
                        modifier =
                                Modifier.weight(1f)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(SurfaceDark)
                                        .border(
                                                1.dp,
                                                Color.White.copy(alpha = 0.05f),
                                                RoundedCornerShape(20.dp)
                                        )
                                        .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                            modifier =
                                    Modifier.background(
                                                    Color.White.copy(alpha = 0.05f),
                                                    CircleShape
                                            )
                                            .padding(8.dp)
                    ) {
                        Text(text = "🏋️", fontSize = 20.sp) // Dumbbell icon
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                            text = "VOLUMEN TOTAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                            text = totalVolume,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Primary,
                            fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Elephant Text
            Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                        text = "¡Bien hecho!",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDark,
                        fontWeight = FontWeight.Medium
                )
            }
        }

        // Bottom Button
        Box(
                modifier =
                        Modifier.align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(
                                        brush =
                                                Brush.verticalGradient(
                                                        colors =
                                                                listOf(
                                                                        Color.Transparent,
                                                                        BackgroundDark,
                                                                        BackgroundDark
                                                                ),
                                                        startY = 0f
                                                )
                                )
                                .padding(24.dp)
        ) {
            Button(
                    onClick = onSaveClick,
                    modifier =
                            Modifier.fillMaxWidth()
                                    .height(56.dp),
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor = Primary,
                                    contentColor = BackgroundDark
                            ),
                    shape = CircleShape
            ) {
                Text(
                        text = "Guardar Entrenamiento",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Check, contentDescription = null)
            }
        }
    }
}
