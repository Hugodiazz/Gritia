package com.devdiaz.gritia.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.devdiaz.gritia.ui.theme.*

@Composable
fun WorkoutLogScreen() {
    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        Column(
                modifier =
                        Modifier.fillMaxSize().padding(bottom = 100.dp) // Space for floating timer
        ) {
            WorkoutHeader()

            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    ExerciseHeader()
                    Spacer(modifier = Modifier.height(24.dp))
                    SetHeaders()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Set 1 (Completed)
                    CompletedSetRow(setNumber = 1, weight = 135, reps = 12)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Set 2 (Completed)
                    CompletedSetRow(setNumber = 2, weight = 155, reps = 10)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Set 3 (Active)
                    ActiveSetRow(
                            setNumber = 3,
                            predictedWeight = 165,
                            predictedReps = 8,
                            previousBest = "165 lbs x 7"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Set 4 (Next)
                    NextSetRow(setNumber = 4)

                    Spacer(modifier = Modifier.height(32.dp))

                    AddSetButton()

                    Spacer(modifier = Modifier.height(32.dp))

                    UpNextSection()

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        RestTimerOverlay(modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp))
    }
}

@Composable
fun WorkoutHeader() {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .background(BackgroundDark.copy(alpha = 0.95f))
                            .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { /* TODO */}) {
                Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextSecondaryDark
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                        text = "Upper Body Power",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDark,
                        fontWeight = FontWeight.SemiBold
                )
                Text(
                        text = "Week 3 • Day 2",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark
                )
            }
        }

        Surface(
                color = Primary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(50),
                onClick = { /* TODO */}
        ) {
            Text(
                    text = "Finish",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Primary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ExerciseHeader() {
    Column {
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
        ) {
            Text(
                    text = "Bench Press",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextDark,
                    fontWeight = FontWeight.Bold
            )
            IconButton(
                    onClick = { /* TODO */},
                    modifier = Modifier.background(SurfaceDark, CircleShape).size(40.dp)
            ) {
                Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "Options",
                        tint = TextSecondaryDark
                )
            }
        }

        Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        ) {
            // Trophy Icon placeholder
            Icon(
                    painter =
                            painterResource(
                                    id = android.R.drawable.ic_menu_myplaces
                            ), // Placeholder
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                    text = "PB: 225 lbs",
                    style = MaterialTheme.typography.bodySmall,
                    color = Primary,
                    fontWeight = FontWeight.Medium
            )
        }

        // Video Preview
        Box(
                modifier =
                        Modifier.fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black),
                contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                    model =
                            "https://lh3.googleusercontent.com/aida-public/AB6AXuDz-xTrhbBXUeZFt5a6IiPI_VPsJRWANv5dwEKmXrFSvfOWlon7AQeSNhsgQbTN7qutmv9-phHI4YUpIl6mQavpnFc0Y9UbN-RyS4f8qpaP4RnKf015-z9NCFkYoxhQjCSmrtDDSnY6OQ3eJH-DVVbjU4brRDVlrLIvEYD2KKao52pc06ZErsrVWl0tN2Fx-vNRHgPt_gsUQoDnlsF6-gNjrpSM_GzBbEfwyQ3wr1kNrI4O-AOxCFug59SbIlEI7WlLAoIdtdlbRE0",
                    contentDescription = "Exercise Instruction",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().alpha(0.6f)
            )

            Box(
                    modifier =
                            Modifier.size(48.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
            ) {
                Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Video",
                        tint = Color.White
                )
            }
        }
    }
}

@Composable
fun SetHeaders() {
    Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
                text = "SET",
                modifier = Modifier.width(32.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark,
                fontWeight = FontWeight.Bold
        )
        Text(
                text = "LBS",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark,
                fontWeight = FontWeight.Bold
        )
        Text(
                text = "REPS",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark,
                fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(32.dp))
    }
}

@Composable
fun CompletedSetRow(setNumber: Int, weight: Int, reps: Int) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Primary.copy(alpha = 0.1f))
                            .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        // Green indicator line
        /* Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(Primary)
        )*/
        // Can be added with IntrinsicSize

        Text(
                text = setNumber.toString(),
                modifier = Modifier.width(32.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                color = Primary,
                fontWeight = FontWeight.Bold
        )

        Text(
                text = weight.toString(),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = TextDark,
                fontWeight = FontWeight.Bold
        )

        Text(
                text = reps.toString(),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = TextDark,
                fontWeight = FontWeight.Bold
        )

        Box(
                modifier = Modifier.size(32.dp).background(Primary, CircleShape),
                contentAlignment = Alignment.Center
        ) {
            Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ActiveSetRow(setNumber: Int, predictedWeight: Int, predictedReps: Int, previousBest: String) {
    Column(
            modifier =
                    Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceDark)
                            .border(2.dp, Primary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                    text = setNumber.toString(),
                    modifier = Modifier.width(32.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondaryDark,
                    fontWeight = FontWeight.Bold
            )

            // Weight Input
            Box(
                    modifier =
                            Modifier.weight(1f)
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BackgroundDark),
                    contentAlignment = Alignment.Center
            ) {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    Text(
                            text = predictedWeight.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = TextDark,
                            fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                            text = "LBS",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                    )
                }
            }

            // Reps Input
            Box(
                    modifier =
                            Modifier.weight(1f)
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BackgroundDark),
                    contentAlignment = Alignment.Center
            ) {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    Text(
                            text = predictedReps.toString(), // Placeholder
                            style = MaterialTheme.typography.titleLarge,
                            color = TextSecondaryDark, // Placeholder color
                            fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                            text = "REPS",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                    )
                }
            }

            Box(
                    modifier =
                            Modifier.size(32.dp)
                                    .background(
                                            TextSecondaryDark.copy(alpha = 0.3f),
                                            CircleShape
                                    ), // Checkbox unchecked style
                    contentAlignment = Alignment.Center
            ) {
                Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Complete Set",
                        tint = TextSecondaryDark.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
                text = "Previous: $previousBest",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark
        )
    }
}

@Composable
fun NextSetRow(setNumber: Int) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceDark.copy(alpha = 0.4f)) // Ghost/Glass feel
                            .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
                text = setNumber.toString(),
                modifier = Modifier.width(32.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondaryDark,
                fontWeight = FontWeight.Bold
        )

        // Weight Placeholder
        Text(
                text = "165",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = TextSecondaryDark.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium
        )

        // Reps Placeholder
        Text(
                text = "-",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = TextSecondaryDark.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium
        )

        // Dashed circle for check
        Box(
                modifier =
                        Modifier.size(32.dp)
                                .background(Color.Transparent, CircleShape)
                                .border(
                                        2.dp,
                                        TextSecondaryDark.copy(alpha = 0.3f),
                                        CircleShape
                                ), // Should be dashed ideally
                contentAlignment = Alignment.Center
        ) {}
    }
}

@Composable
fun AddSetButton() {
    Surface(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            // .border(2.dp, TextSecondaryDark.copy(alpha = 0.3f), RoundedCornerShape(16.dp)), //
            // Dashed?
            color = Color.Transparent,
            shape = RoundedCornerShape(16.dp),
            onClick = { /* TODO */}
    ) {
        // Dotted border simulation
        Box(
                modifier =
                        Modifier.fillMaxSize()
                                .border(
                                        1.dp,
                                        TextSecondaryDark.copy(alpha = 0.5f),
                                        RoundedCornerShape(16.dp)
                                ), // Standard border for now
                contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = TextSecondaryDark
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                        text = "Add Set",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondaryDark,
                        fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun UpNextSection() {
    Column {
        Text(
                text = "UP NEXT",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Surface(
                color = SurfaceDark,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                onClick = { /* TODO */}
        ) {
            Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                            text = "Incline Dumbbell Press",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextDark,
                            fontWeight = FontWeight.Bold
                    )
                    Text(
                            text = "3 sets x 10-12 reps",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryDark
                    )
                }
                Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = TextSecondaryDark
                )
            }
        }
    }
}

@Composable
fun RestTimerOverlay(modifier: Modifier = Modifier) {
    Surface(
            modifier = modifier.fillMaxWidth(),
            color = Color(0xFF0C1610), // Very dark background
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                            modifier =
                                    Modifier.size(40.dp)
                                            .background(
                                                    Color.White.copy(alpha = 0.05f),
                                                    CircleShape
                                            ),
                            contentAlignment = Alignment.Center
                    ) {
                        // Timer icon
                        Icon(
                                painter =
                                        painterResource(
                                                id = android.R.drawable.ic_menu_recent_history
                                        ), // Placeholder
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                                text = "REST TIMER",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                        )
                        Text(
                                text = "01:45",
                                style = MaterialTheme.typography.headlineSmall, // Mono font ideally
                                color = Primary,
                                fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row() {
                    Surface(
                            color = Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(50),
                            onClick = { /* TODO */}
                    ) {
                        Text(
                                text = "+30s",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Surface(
                            color = Primary,
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp),
                            onClick = { /* TODO */}
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                    imageVector = Icons.Default.Pause,
                                    contentDescription = "Pause",
                                    tint = Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            Box(
                    modifier =
                            Modifier.fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Box(
                        modifier =
                                Modifier.fillMaxWidth(0.45f) // 45%
                                        .fillMaxHeight()
                                        .background(Primary)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                        text = "Total Time: 24:12",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark,
                        fontWeight = FontWeight.Medium
                )
                Text(
                        text = "45% Complete",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark,
                        fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview
@Composable
fun WorkoutLogScreenPreview() {
    GritiaTheme { WorkoutLogScreen() }
}
