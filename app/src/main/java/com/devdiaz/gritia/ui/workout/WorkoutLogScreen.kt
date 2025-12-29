package com.devdiaz.gritia.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devdiaz.gritia.ui.theme.*

@Composable
fun WorkoutLogScreen(
        onNavigateBack: () -> Unit,
        onNavigateToSummary: (String, Long, Float) -> Unit = { _, _, _ -> },
        viewModel: WorkoutLogViewModel = hiltViewModel()
) {
        val uiState by viewModel.uiState.collectAsState()

        // Handle timer updates (side effect?) - ViewModel logic handles timer internally via
        // coroutines
        // Coroutine Effect for Navigation
        LaunchedEffect(Unit) {
                viewModel.navigationEvent.collect { event ->
                        when (event) {
                                is WorkoutNavigationEvent.NavigateToSummary -> {
                                        onNavigateToSummary(
                                                event.routineName,
                                                event.durationSeconds,
                                                event.totalVolume
                                        )
                                }
                        }
                }
        }

        Box(
                modifier =
                        Modifier.fillMaxSize()
                                .background(BackgroundDark)
                                .systemBarsPadding() // Fix for margins
        ) {
                Column(
                        modifier =
                                if (uiState.isRestTimerActive) {
                                        Modifier.fillMaxSize().padding(bottom = 100.dp)
                                } else {
                                        Modifier.fillMaxSize()
                                }
                ) {
                        WorkoutHeader(
                                routineName = uiState.routineName,
                                isWorkoutActive = uiState.isWorkoutActive,
                                elapsedTimeSeconds = uiState.elapsedTimeSeconds,
                                onToggleWorkout = { viewModel.toggleWorkoutState() },
                                onNavigateBack = onNavigateBack
                        )

                        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                                item { Spacer(modifier = Modifier.height(24.dp)) }

                                items(uiState.exercises.size) { index ->
                                        val exercise = uiState.exercises[index]
                                        ExerciseSection(
                                                exercise = exercise,
                                                onUpdateSet = { setIndex, weight, reps ->
                                                        viewModel.updateSet(
                                                                index,
                                                                setIndex,
                                                                weight,
                                                                reps
                                                        )
                                                },
                                                onCompleteSet = { setIndex, isCompleted ->
                                                        viewModel.onSetCompleted(
                                                                index,
                                                                setIndex,
                                                                isCompleted
                                                        )
                                                },
                                                onUpdateRestTime = { seconds ->
                                                        viewModel.updateRestTime(index, seconds)
                                                },
                                                onAddSet = { viewModel.addSet(index) },
                                                isWorkoutActive = uiState.isWorkoutActive
                                        )
                                        Spacer(modifier = Modifier.height(32.dp))
                                }
                        }
                }

                if (uiState.isRestTimerActive) {
                        RestTimerOverlay(
                                secondsRemaining = uiState.restTimerSeconds,
                                onAdd30s = { viewModel.addTimeRest(30) },
                                onStop = { viewModel.stopRestTimer() },
                                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                        )
                }

                if (uiState.isStarting) {
                        StartTimerOverlay(
                                seconds = uiState.startTimerSeconds,
                                modifier = Modifier.align(Alignment.Center)
                        )
                }
        }
}

@Composable
fun WorkoutHeader(
        routineName: String,
        isWorkoutActive: Boolean,
        elapsedTimeSeconds: Long,
        onToggleWorkout: () -> Unit,
        onNavigateBack: () -> Unit
) {
        // Format elapsed time MM:SS
        val minutes = elapsedTimeSeconds / 60
        val seconds = elapsedTimeSeconds % 60
        val timeString = String.format("%02d:%02d", minutes, seconds)

        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(BackgroundDark.copy(alpha = 0.95f))
                                .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
        ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateBack) {
                                Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = TextSecondaryDark
                                )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                                Text(
                                        text = routineName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextDark,
                                        fontWeight = FontWeight.SemiBold
                                )
                                if (isWorkoutActive) {
                                        Text(
                                                text = timeString,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Primary
                                        )
                                }
                        }
                }

                Surface(
                        color = if (isWorkoutActive) Primary.copy(alpha = 0.2f) else Primary,
                        shape = RoundedCornerShape(50),
                        onClick = onToggleWorkout
                ) {
                        Text(
                                text = if (isWorkoutActive) "Finish" else "Start",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = if (isWorkoutActive) Primary else BackgroundDark,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                        )
                }
        }
}

@Composable
fun ExerciseSection(
        exercise: WorkoutExerciseUiState,
        onUpdateSet: (Int, String, String) -> Unit,
        onCompleteSet: (Int, Boolean) -> Unit,
        onUpdateRestTime: (Int) -> Unit,
        onAddSet: () -> Unit,
        isWorkoutActive: Boolean
) {
        Column {
                ExerciseHeader(
                        exerciseName = exercise.name,
                        restTimeSeconds = exercise.restTimeSeconds,
                        onUpdateRestTime = onUpdateRestTime
                )
                Spacer(modifier = Modifier.height(24.dp))
                SetHeaders()
                Spacer(modifier = Modifier.height(12.dp))

                exercise.sets.forEachIndexed { index, set ->
                        // Logic to determine row type
                        // For now, let's use ActiveSetRow for everything but change style based on
                        // completion?
                        // Or separate CompletedSetRow vs ActiveSetRow as designed.

                        if (set.isCompleted) {
                                CompletedSetRow(
                                        setNumber = set.setNumber,
                                        weight = set.actualWeight,
                                        reps = set.actualReps
                                )
                        } else {
                                ActiveSetRow(
                                        setNumber = set.setNumber,
                                        weight = set.actualWeight,
                                        reps = set.actualReps,
                                        onWeightChange = { onUpdateSet(index, it, set.actualReps) },
                                        onRepsChange = { onUpdateSet(index, set.actualWeight, it) },
                                        onComplete = { onCompleteSet(index, true) },
                                        isNext = false, // Simplified logic: if not completed, it's
                                        // active. Refine later for "Up Next"
                                        enabled = isWorkoutActive
                                )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                }
                AddSetButton(onClick = onAddSet, enabled = isWorkoutActive)
        }
}

@Composable
fun ExerciseHeader(exerciseName: String, restTimeSeconds: Int, onUpdateRestTime: (Int) -> Unit) {
        var showRestTimeDialog by remember { mutableStateOf(false) }

        if (showRestTimeDialog) {
                RestTimeSelectorDialog(
                        currentSeconds = restTimeSeconds,
                        onDismiss = { showRestTimeDialog = false },
                        onTimeSelected = {
                                onUpdateRestTime(it)
                                showRestTimeDialog = false
                        }
                )
        }

        Column {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                ) {
                        Text(
                                text = exerciseName,
                                style = MaterialTheme.typography.headlineMedium,
                                color = TextDark,
                                fontWeight = FontWeight.Bold
                        )
                }

                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                ) {
                        Icon(
                                painter =
                                        painterResource(
                                                id = android.R.drawable.ic_menu_myplaces
                                        ), // Placeholder
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        val minutes = restTimeSeconds / 60
                        val seconds = restTimeSeconds % 60
                        val timeString =
                                if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"

                        Text(
                                text = "Rest: $timeString",
                                style = MaterialTheme.typography.bodySmall,
                                color = Primary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable { showRestTimeDialog = true }
                        )
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
                        text = "KG",
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
fun CompletedSetRow(setNumber: Int, weight: String, reps: String) {
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Primary.copy(alpha = 0.1f))
                                .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
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
fun ActiveSetRow(
        setNumber: Int,
        weight: String,
        reps: String,
        onWeightChange: (String) -> Unit,
        onRepsChange: (String) -> Unit,
        onComplete: () -> Unit,
        isNext: Boolean,
        enabled: Boolean = true
) {
        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                        if (isNext) SurfaceDark.copy(alpha = 0.4f) else SurfaceDark
                                )
                                .border(
                                        width = if (isNext) 0.dp else 2.dp,
                                        color =
                                                if (isNext) Color.Transparent
                                                else Primary.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(16.dp)
                                )
                                .padding(12.dp)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Text(
                                text = setNumber.toString(),
                                modifier = Modifier.width(32.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isNext) TextSecondaryDark else TextSecondaryDark,
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
                                        if (isNext) {
                                                Text(
                                                        text = weight,
                                                        style = MaterialTheme.typography.titleLarge,
                                                        color =
                                                                TextSecondaryDark.copy(
                                                                        alpha = 0.5f
                                                                ),
                                                        fontWeight = FontWeight.Bold
                                                )
                                        } else {
                                                BasicTextField(
                                                        value = weight,
                                                        onValueChange = onWeightChange,
                                                        textStyle =
                                                                MaterialTheme.typography.titleLarge
                                                                        .copy(
                                                                                color = TextDark,
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Bold,
                                                                                textAlign =
                                                                                        TextAlign
                                                                                                .Center
                                                                        ),
                                                        keyboardOptions =
                                                                KeyboardOptions(
                                                                        keyboardType =
                                                                                KeyboardType.Number
                                                                ),
                                                        cursorBrush = SolidColor(Primary),
                                                        modifier =
                                                                Modifier.width(IntrinsicSize.Min),
                                                        enabled = enabled
                                                )
                                        }
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
                                        if (isNext) {
                                                Text(
                                                        text = reps,
                                                        style = MaterialTheme.typography.titleLarge,
                                                        color =
                                                                TextSecondaryDark.copy(
                                                                        alpha = 0.5f
                                                                ),
                                                        fontWeight = FontWeight.Bold
                                                )
                                        } else {
                                                BasicTextField(
                                                        value = reps,
                                                        onValueChange = onRepsChange,
                                                        textStyle =
                                                                MaterialTheme.typography.titleLarge
                                                                        .copy(
                                                                                color =
                                                                                        TextSecondaryDark,
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Bold,
                                                                                textAlign =
                                                                                        TextAlign
                                                                                                .Center
                                                                        ),
                                                        keyboardOptions =
                                                                KeyboardOptions(
                                                                        keyboardType =
                                                                                KeyboardType.Number
                                                                ),
                                                        cursorBrush = SolidColor(Primary),
                                                        modifier =
                                                                Modifier.width(IntrinsicSize.Min),
                                                        enabled = enabled
                                                )
                                        }
                                }
                        }

                        Box(
                                modifier =
                                        Modifier.size(32.dp)
                                                .background(
                                                        TextSecondaryDark.copy(alpha = 0.3f),
                                                        CircleShape
                                                )
                                                .clickable(enabled = enabled) { onComplete() },
                                contentAlignment = Alignment.Center
                        ) {
                                if (!isNext) {
                                        Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Complete Set",
                                                tint = TextSecondaryDark.copy(alpha = 0.5f),
                                                modifier = Modifier.size(20.dp)
                                        )
                                }
                        }
                }
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
fun AddSetButton(onClick: () -> Unit, enabled: Boolean = true) {
        Surface(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                color = Color.Transparent,
                shape = RoundedCornerShape(16.dp),
                onClick = onClick,
                enabled = enabled
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
fun RestTimerOverlay(
        secondsRemaining: Int,
        onAdd30s: () -> Unit,
        onStop: () -> Unit,
        modifier: Modifier = Modifier
) {
        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60
        val timeString = String.format("%02d:%02d", minutes, seconds)

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
                                                                        Color.White.copy(
                                                                                alpha = 0.05f
                                                                        ),
                                                                        CircleShape
                                                                ),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                // Timer icon
                                                Icon(
                                                        painter =
                                                                painterResource(
                                                                        id =
                                                                                android.R
                                                                                        .drawable
                                                                                        .ic_menu_recent_history
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
                                                        text = timeString,
                                                        style =
                                                                MaterialTheme.typography
                                                                        .headlineSmall, // Mono font
                                                        // ideally
                                                        color = Primary,
                                                        fontWeight = FontWeight.Bold
                                                )
                                        }
                                }

                                Row() {
                                        Surface(
                                                color = Color.White.copy(alpha = 0.05f),
                                                shape = RoundedCornerShape(50),
                                                onClick = onAdd30s
                                        ) {
                                                Text(
                                                        text = "+30s",
                                                        modifier =
                                                                Modifier.padding(
                                                                        horizontal = 16.dp,
                                                                        vertical = 8.dp
                                                                ),
                                                        color = Color.White,
                                                        style = MaterialTheme.typography.labelMedium
                                                )
                                        }

                                        Surface(
                                                color = Primary,
                                                shape = CircleShape,
                                                modifier = Modifier.size(40.dp),
                                                onClick = onStop
                                        ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                                imageVector =
                                                                        Icons.Default
                                                                                .Pause, // Or stop
                                                                contentDescription = "Stop",
                                                                tint = Color.Black
                                                        )
                                                }
                                        }
                                }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress Bar simulation (assuming 2 mins max for visual?)
                        val progress = (secondsRemaining / 120f).coerceIn(0f, 1f)
                        Box(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(50))
                                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                                Box(
                                        modifier =
                                                Modifier.fillMaxWidth(progress)
                                                        .fillMaxHeight()
                                                        .background(Primary)
                                )
                        }
                }
        }
}

@Preview
@Composable
fun WorkoutLogScreenPreview() {
        GritiaTheme { WorkoutLogScreen(onNavigateBack = {}) }
}

@Composable
fun RestTimeSelectorDialog(
        currentSeconds: Int,
        onDismiss: () -> Unit,
        onTimeSelected: (Int) -> Unit
) {
        AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                        Text(
                                text = "Select Rest Time",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextDark
                        )
                },
                text = {
                        Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                // Options: 10s to 4m (240s)
                                val options = listOf(10, 30, 60, 90, 120, 150, 180, 240)

                                LazyColumn(modifier = Modifier.height(300.dp)) {
                                        items(options.size) { index ->
                                                val seconds = options[index]
                                                val minutes = seconds / 60
                                                val secs = seconds % 60
                                                val label =
                                                        if (minutes > 0 && secs > 0)
                                                                "${minutes}m ${secs}s"
                                                        else if (minutes > 0) "${minutes} min"
                                                        else "$secs sec"

                                                TextButton(
                                                        onClick = { onTimeSelected(seconds) },
                                                        modifier = Modifier.fillMaxWidth()
                                                ) {
                                                        Text(
                                                                text = label,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyLarge,
                                                                color =
                                                                        if (seconds ==
                                                                                        currentSeconds
                                                                        )
                                                                                Primary
                                                                        else TextSecondaryDark,
                                                                fontWeight =
                                                                        if (seconds ==
                                                                                        currentSeconds
                                                                        )
                                                                                FontWeight.Bold
                                                                        else FontWeight.Normal
                                                        )
                                                }
                                        }
                                }
                        }
                },
                confirmButton = {
                        TextButton(onClick = onDismiss) {
                                Text("Cancel", color = TextSecondaryDark)
                        }
                },
                containerColor = SurfaceDark,
                textContentColor = TextSecondaryDark
        )
}
