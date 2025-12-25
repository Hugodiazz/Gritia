package com.devdiaz.gritia.ui.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.outlined.PlaylistAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.devdiaz.gritia.model.Exercise
import com.devdiaz.gritia.ui.theme.BackgroundDark
import com.devdiaz.gritia.ui.theme.Primary
import com.devdiaz.gritia.ui.theme.SurfaceDark

// Temporary data models for UI state
@Composable
fun CreateRoutineScreen(
        onNavigateBack: () -> Unit,
        onNavigateToExerciseSelector: () -> Unit = {},
        newExercises: List<Exercise>? = null,
        viewModel: CreateRoutineViewModel = hiltViewModel()
) {
        // UI State from ViewModel
        val uiState by viewModel.uiState.collectAsState()

        // Handle new exercises from selector
        LaunchedEffect(newExercises) {
                if (!newExercises.isNullOrEmpty()) {
                        viewModel.addExercises(newExercises)
                }
        }

        Scaffold(
                containerColor = BackgroundDark,
                topBar = {
                        CreateRoutineTopBar(
                                onBack = onNavigateBack,
                                onSave = {
                                        // Validation
                                        val isNameValid = uiState.routineName.isNotBlank()
                                        val isExercisesValid = uiState.exercises.isNotEmpty()
                                        val areSetsValid =
                                                uiState.exercises.all { exercise ->
                                                        exercise.sets.all { set ->
                                                                set.kg.isNotBlank() &&
                                                                        set.reps.isNotBlank()
                                                        }
                                                }

                                        if (isNameValid && isExercisesValid && areSetsValid) {
                                                viewModel.saveRoutine { onNavigateBack() }
                                        }
                                }
                        )
                },
                bottomBar = {
                        Box(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .background(
                                                        Brush.verticalGradient(
                                                                colors =
                                                                        listOf(
                                                                                BackgroundDark.copy(
                                                                                        alpha = 0f
                                                                                ),
                                                                                BackgroundDark,
                                                                                BackgroundDark
                                                                        )
                                                        )
                                                )
                                                .padding(16.dp)
                                                .padding(
                                                        bottom = 16.dp
                                                ) // Extra padding for nav bar if needed
                        ) {
                                Button(
                                        onClick = onNavigateToExerciseSelector,
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .height(56.dp)
                                                        .shadow(
                                                                elevation = 8.dp,
                                                                spotColor =
                                                                        Primary.copy(alpha = 0.2f),
                                                                ambientColor =
                                                                        Primary.copy(alpha = 0.2f)
                                                        ),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = Primary,
                                                        contentColor = BackgroundDark
                                                ),
                                        shape = RoundedCornerShape(12.dp)
                                ) {
                                        Icon(
                                                Icons.Default.Add,
                                                contentDescription = null,
                                                modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(
                                                "Add Exercise",
                                                style =
                                                        MaterialTheme.typography.titleMedium.copy(
                                                                fontWeight = FontWeight.Bold
                                                        )
                                        )
                                }
                        }
                }
        ) { paddingValues ->
                LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentPadding = PaddingValues(bottom = 100.dp) // Space for bottom bar
                ) {
                        // Routine Name Input
                        item {
                                Box(modifier = Modifier.padding(16.dp)) {
                                        BasicTextField(
                                                value = uiState.routineName,
                                                onValueChange = { viewModel.setRoutineName(it) },
                                                textStyle =
                                                        TextStyle(
                                                                color = Color.White,
                                                                fontSize = 20.sp,
                                                                fontWeight = FontWeight.Bold
                                                        ),
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .height(64.dp)
                                                                .background(
                                                                        SurfaceDark,
                                                                        RoundedCornerShape(12.dp)
                                                                )
                                                                .padding(horizontal = 16.dp),
                                                decorationBox = { innerTextField ->
                                                        Box(
                                                                contentAlignment =
                                                                        Alignment.CenterStart
                                                        ) {
                                                                if (uiState.routineName.isEmpty()) {
                                                                        Text(
                                                                                "Routine Name e.g. Leg Day A",
                                                                                color =
                                                                                        Color(
                                                                                                        0xFF9db8a8
                                                                                                )
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.5f
                                                                                                ),
                                                                                fontSize = 20.sp,
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Bold
                                                                        )
                                                                }
                                                                innerTextField()
                                                        }
                                                },
                                                cursorBrush = SolidColor(Primary)
                                        )
                                }
                        }

                        // Day Selector
                        item {
                                Row(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .padding(
                                                                horizontal = 16.dp,
                                                                vertical = 8.dp
                                                        ),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        val days = listOf("M", "T", "W", "T", "F", "S", "S")
                                        days.forEachIndexed { index, day ->
                                                DaySelectorItem(
                                                        text = day,
                                                        selected = uiState.schedule[index],
                                                        onClick = { viewModel.toggleDay(index) }
                                                )
                                        }
                                }
                        }

                        // Header "Routine Exercises"
                        item {
                                Row(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .padding(
                                                                horizontal = 16.dp,
                                                                vertical = 8.dp
                                                        ),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                ) {
                                        Text(
                                                "Routine Exercises",
                                                color = Color.White,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                                "${uiState.exercises.size} exercises",
                                                color = Color(0xFF9db8a8),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                        )
                                }
                        }

                        itemsIndexed(uiState.exercises) { index, exercise ->
                                RoutineExerciseCard(
                                        exercise = exercise,
                                        onAddSet = { viewModel.addSet(exercise.id) },
                                        onDeleteSet = { setIndex ->
                                                viewModel.removeSet(exercise.id, setIndex)
                                        },
                                        onRemoveExercise = {
                                                viewModel.removeExercise(exercise.id)
                                        },
                                        onUpdateSet = { setIndex, kg, reps ->
                                                viewModel.updateSet(exercise.id, setIndex, kg, reps)
                                        }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Empty State / CTA
                        item {
                                Column(
                                        modifier =
                                                Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                        Icon(
                                                Icons.Outlined.PlaylistAdd,
                                                contentDescription = null,
                                                tint = Color(0xFF9db8a8).copy(alpha = 0.6f),
                                                modifier = Modifier.size(40.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                "Add more exercises to build\nyour perfect workout.",
                                                color = Color(0xFF9db8a8),
                                                textAlign = TextAlign.Center,
                                                fontSize = 14.sp
                                        )
                                }
                        }
                }
        }
}

@Composable
fun CreateRoutineTopBar(onBack: () -> Unit, onSave: () -> Unit) {
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(BackgroundDark.copy(alpha = 0.9f))
                                .statusBarsPadding()
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
        ) {
                IconButton(onClick = onBack) {
                        Icon(
                                Icons.Default.ArrowBackIosNew,
                                contentDescription = "Back",
                                tint = Color(0xFF9db8a8) // text-secondary
                        )
                }

                Text(
                        "Create Routine",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onSave) {
                        Text(
                                "Save",
                                color = Primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                        )
                }
        }
}

@Composable
fun RoutineExerciseCard(
        exercise: RoutineExerciseState,
        onAddSet: () -> Unit,
        onDeleteSet: (Int) -> Unit,
        onRemoveExercise: () -> Unit,
        onUpdateSet: (Int, String?, String?) -> Unit
) {
        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .background(SurfaceDark, RoundedCornerShape(12.dp))
                                .border(
                                        1.dp,
                                        Color.White.copy(alpha = 0.05f),
                                        RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp)
        ) {
                // Exercise Header
                Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                        modifier =
                                                Modifier.size(48.dp)
                                                        .background(
                                                                Color(0xFF29382f),
                                                                RoundedCornerShape(8.dp)
                                                        ), // input-dark
                                        contentAlignment = Alignment.Center
                                ) {
                                        Icon(
                                                Icons.Default.FitnessCenter,
                                                contentDescription = null,
                                                tint = Color(0xFF9db8a8)
                                        )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                        Text(
                                                exercise.name,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                        )
                                        Text(
                                                "${exercise.muscle} • ${exercise.equipment}",
                                                color = Color(0xFF9db8a8),
                                                fontSize = 14.sp
                                        )
                                }
                        }
                        IconButton(onClick = onRemoveExercise, modifier = Modifier.size(32.dp)) {
                                Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Remove Exercise",
                                        tint = Color(0xFF9db8a8)
                                )
                        }
                }

                // Set Headers
                Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                ) {
                        Text(
                                "SET",
                                color = Color(0xFF9db8a8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.width(40.dp).padding(start = 8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                                "KG",
                                color = Color(0xFF9db8a8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                                "REPS",
                                color = Color(0xFF9db8a8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(40.dp)) // Delete button space
                }

                // Sets
                exercise.sets.forEachIndexed { index, set ->
                        Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                // Set Number
                                Box(
                                        modifier = Modifier.width(40.dp).padding(start = 8.dp),
                                        contentAlignment = Alignment.CenterStart
                                ) {
                                        Box(
                                                modifier =
                                                        Modifier.size(24.dp)
                                                                .background(
                                                                        Color.White.copy(
                                                                                alpha = 0.05f
                                                                        ),
                                                                        androidx.compose.foundation
                                                                                .shape.CircleShape
                                                                ),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Text(
                                                        set.setNumber.toString(),
                                                        color = Color(0xFF9db8a8),
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Kg Input
                                SetInput(
                                        value = set.kg,
                                        onValueChange = { onUpdateSet(index, it, null) },
                                        modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Reps Input
                                SetInput(
                                        value = set.reps,
                                        onValueChange = { onUpdateSet(index, null, it) },
                                        modifier = Modifier.weight(1f)
                                )

                                // Delete Button
                                Box(
                                        modifier = Modifier.width(40.dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        IconButton(onClick = { onDeleteSet(index) }) {
                                                Icon(
                                                        Icons.Default.Delete,
                                                        contentDescription = "Delete set",
                                                        tint =
                                                                Color(
                                                                        0xFF9db8a8
                                                                ), // Or red hover as in web
                                                        modifier = Modifier.size(20.dp)
                                                )
                                        }
                                }
                        }
                }

                // Add Set Button
                Button(
                        onClick = onAddSet,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = Primary.copy(alpha = 0.1f),
                                        contentColor = Primary
                                ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                        Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Set", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
        }
}

@Composable
fun SetInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
        BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle =
                        TextStyle(
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                        ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                cursorBrush = SolidColor(Primary),
                modifier =
                        modifier.background(
                                        Color(0xFF29382f),
                                        RoundedCornerShape(8.dp)
                                ) // input-dark
                                .height(32.dp)
                                .wrapContentHeight(Alignment.CenterVertically),
                decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.Center) {
                                if (value.isEmpty()) {
                                        Text("-", color = Color(0xFF9db8a8))
                                }
                                innerTextField()
                        }
                }
        )
}

fun Modifier.shadow(
        elevation: androidx.compose.ui.unit.Dp,
        shape: androidx.compose.ui.graphics.Shape = androidx.compose.ui.graphics.RectangleShape,
        clip: Boolean = elevation > 0.dp,
        ambientColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Black,
        spotColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Black,
): Modifier = this // simplified for preview/compilation, or use actual shadow modifier

@Composable
fun DaySelectorItem(text: String, selected: Boolean, onClick: () -> Unit) {
        Box(
                modifier =
                        Modifier.size(40.dp)
                                .background(
                                        color = if (selected) Primary else SurfaceDark,
                                        shape = androidx.compose.foundation.shape.CircleShape
                                )
                                .border(
                                        width = 1.dp,
                                        color =
                                                if (selected) Primary
                                                else Color.White.copy(alpha = 0.1f),
                                        shape = androidx.compose.foundation.shape.CircleShape
                                )
                                .clickable { onClick() },
                contentAlignment = Alignment.Center
        ) {
                Text(
                        text = text,
                        fontSize = 14.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) BackgroundDark else Color.White.copy(alpha = 0.6f)
                )
        }
}
