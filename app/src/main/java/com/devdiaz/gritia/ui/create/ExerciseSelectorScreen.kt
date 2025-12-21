package com.devdiaz.gritia.ui.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.devdiaz.gritia.model.Equipment
import com.devdiaz.gritia.model.Exercise
import com.devdiaz.gritia.model.MuscleGroup
import com.devdiaz.gritia.ui.theme.BackgroundDark
import com.devdiaz.gritia.ui.theme.Primary
import com.devdiaz.gritia.ui.theme.SurfaceDark

@Composable
fun ExerciseSelectorScreen(
        onNavigateBack: () -> Unit,
        onExercisesSelected: (List<Exercise>) -> Unit
) {
    // Mock Data
    val allExercises = remember {
        listOf(
                Exercise(
                        id = "1",
                        name = "Arnold Press",
                        muscleGroup = MuscleGroup.SHOULDERS,
                        equipment = Equipment.DUMBBELL,
                        imageUrl =
                                "https://lh3.googleusercontent.com/aida-public/AB6AXuDTZTfpIwLPQcBzghnH9jiM_TvFpmfWprFAhUF9li2eAtsjSlU-V0mUXimjQO9O9IBfiyYDNj-dNGZogaDOEdTEhjKrDh0u9_M5pQWciRq2rC9CLLXX0qm159u7156DeSscjg7zzOYEET_U8D9TK2-0mtc3ZKaGm7gTvlt6NNHQlzYUPTCIkXl5ZIWg2vqT9s8WK9pniGzKKLPVI_GqI5et-_kS4rkUkkY8vCdhcgHh5KcWXAIvvtPMgrPgp7jajAVIGq8rXgLzEbM"
                ),
                Exercise(
                        id = "2",
                        name = "Bicep Curl",
                        muscleGroup = MuscleGroup.ARMS,
                        equipment = Equipment.DUMBBELL,
                        imageUrl =
                                "https://lh3.googleusercontent.com/aida-public/AB6AXuDsZJHUzqZLFESuwodpjP89zwcLasHOO8BMsjb3Rp32KqjKMeuOC9rbbd5fkoGFm3WrokLf1ykfUeBbW6pDXVpeBd0-Ih1GeohOrDw450itcUVwq58OwTbnf73wNhOaQ1xHESEOrWfJo9FVE0Sg79phyL_Xyl0QRfUyHXLBd1ObmxPj_isKVElvf817sQMlBCvtfCQiyRfWxXOawJF5HKrCqisxDm5UuHJhmRSCuQBIOvnah6CbedDVaNPYv7p6Y5Yc2mQ_Q6fa-Ck"
                ),
                Exercise(
                        id = "3",
                        name = "Cable Fly",
                        muscleGroup = MuscleGroup.CHEST,
                        equipment = Equipment.MACHINE, // Mapped to closest available
                        imageUrl =
                                "https://lh3.googleusercontent.com/aida-public/AB6AXuCpkASJQns2Y7lceKpWt8xPImpyRh-0TShgEgZxX9yPuOs1T-D7l4EvXoP27NqUbD4POPJR-cUMzaQR_XiTWVkK1jEKuOXzchHtpntyz59bD9L6JfTu7PfL_tF2TElQRzVM5ROQKqS4-NaWSB_KHDgHdpNPr3JsEK54rC1kmEmF4o2NmQmPUnUQpu0oHjQsIpKdRt3sABd1pAv6sJEnBlGcNLCtdN_HkidnvckCJP24n7_s7E5qo6kaT2qahKLLmtpbBw7uZfMKzI8"
                ),
                Exercise(
                        id = "4",
                        name = "Back Squat",
                        muscleGroup = MuscleGroup.LEGS,
                        equipment = Equipment.BARBELL,
                        imageUrl =
                                "https://lh3.googleusercontent.com/aida-public/AB6AXuB-iO-Hbr8vHva3MGszAPSPNczOCTKDcLr2IHvXx1jd4gXZ_ML6BOOvQIvulCORmkMDbzDXymbfFkrzhdnlP_a3COhkdLnkqmAPDr82mKAdiZ_cQJUdpS0Zjw-RgjuQGJFSbuIXhrryw6wQOPLFWfsdNqxTSvvlUv-NpMXUTQjaYsY704FH51veVkudtyJ1Wf4XkGMlH73bd4Lau5lT9ppdLwdkLyxQPGMw58cuL03ERlwcjImIUiY4yk4ZZKoOOrUOlhJ2O26Ntng"
                ),
                Exercise(
                        id = "5",
                        name = "Tricep Pushdown",
                        muscleGroup = MuscleGroup.ARMS,
                        equipment = Equipment.MACHINE,
                        imageUrl =
                                "https://lh3.googleusercontent.com/aida-public/AB6AXuAOJQYw6LLv-oeWVPvGmokhWr510lKbzM0kCHa23L0JM9pH7byFeq7PeaEDGJXLcPjC8vBk7F7n6UnaEaUs7GWDzVlbKqKNoT8n-k0fjgMidxgmzRGInojFisY-gmQCvbx7dXoM_XKMfaw5q-4fErogRvfaUgG5x0UtMGmB4stoLDVtubYhvOv8q9Qixyyhtgdqinb49sZzR8-aouJBxsU2oOH8O0rdeh2W8I3QKse4Wpd-PAap9bWEgz89fKO2ZVrh__DIJJFg2AA"
                )
        )
    }

    var selectedExercises = remember { mutableStateListOf<Exercise>() }
    var searchQuery by remember { mutableStateOf("") }

    // Filters logic (simplified)
    val filteredExercises = allExercises.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Scaffold(
            containerColor = BackgroundDark,
            topBar = {
                Column(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .background(BackgroundDark.copy(alpha = 0.9f))
                                        .statusBarsPadding()
                ) {
                    // Header Row
                    Row(
                            modifier =
                                    Modifier.fillMaxWidth()
                                            .padding(horizontal = 4.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                            )
                        }
                        Text(
                                "Exercise Library",
                                style =
                                        MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                        ),
                                modifier = Modifier.weight(1f)
                        )

                        if (selectedExercises.isNotEmpty()) {
                            TextButton(onClick = { onExercisesSelected(selectedExercises) }) {
                                Text(
                                        "Add (${selectedExercises.size})",
                                        color = Primary,
                                        fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Search Bar
                    Box(
                            modifier =
                                    Modifier.fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                textStyle =
                                        TextStyle(
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Normal
                                        ),
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .height(48.dp)
                                                .background(
                                                        SurfaceDark,
                                                        RoundedCornerShape(50)
                                                ) // pill shape
                                                .padding(horizontal = 16.dp),
                                decorationBox = { innerTextField ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                                Icons.Default.Search,
                                                contentDescription = null,
                                                tint = Color(0xFF9db8a8)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(modifier = Modifier.weight(1f)) {
                                            if (searchQuery.isEmpty()) {
                                                Text(
                                                        "Search exercises...",
                                                        color = Color(0xFF9db8a8),
                                                        fontSize = 16.sp
                                                )
                                            }
                                            innerTextField()
                                        }
                                    }
                                },
                                cursorBrush = SolidColor(Primary)
                        )
                    }

                    // Filters Row
                    Row(
                            modifier =
                                    Modifier.fillMaxWidth()
                                            .padding(bottom = 16.dp)
                                            .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        FilterChip(label = "All", selected = true, onClick = {})
                        MuscleGroup.entries.forEach {
                            FilterChip(label = it.displayName, selected = false, onClick = {})
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
            }
    ) { paddingValues ->
        LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                        "All Exercises",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            items(filteredExercises) { exercise ->
                val isSelected = selectedExercises.contains(exercise)
                ExerciseItem(
                        exercise = exercise,
                        isSelected = isSelected,
                        onToggleSelection = {
                            if (isSelected) {
                                selectedExercises.remove(exercise)
                            } else {
                                selectedExercises.add(exercise)
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
            modifier =
                    Modifier.height(36.dp)
                            .background(
                                    color = if (selected) Primary else SurfaceDark,
                                    shape = CircleShape
                            )
                            .border(
                                    width = 1.dp,
                                    color =
                                            if (selected) Primary
                                            else Color.White.copy(alpha = 0.1f),
                                    shape = CircleShape
                            )
                            .clip(CircleShape)
                            .clickable(onClick = onClick)
                            .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
    ) {
        Text(
                text = label,
                color = if (selected) BackgroundDark else Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun ExerciseItem(exercise: Exercise, isSelected: Boolean, onToggleSelection: () -> Unit) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .background(SurfaceDark, RoundedCornerShape(16.dp))
                            .border(
                                    width = 1.dp,
                                    color =
                                            if (isSelected) Primary
                                            else Color.White.copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(16.dp)
                            )
                            .clickable(onClick = onToggleSelection)
                            .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        // Image
        Box(
                modifier =
                        Modifier.size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.1f))
        ) {
            AsyncImage(
                    model = exercise.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(exercise.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                        exercise.muscleGroup.displayName,
                        color = Color(0xFF9db8a8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                )
                Box(
                        modifier =
                                Modifier.padding(horizontal = 6.dp)
                                        .size(4.dp)
                                        .background(Color(0xFF9db8a8), CircleShape)
                )
                Text(
                        exercise.equipment.displayName,
                        color = Color(0xFF9db8a8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                )
            }
        }

        // Add Button / Checkmark
        Box(
                modifier =
                        Modifier.size(40.dp)
                                .background(
                                        color =
                                                if (isSelected) Primary
                                                else Primary.copy(alpha = 0.1f),
                                        shape = CircleShape
                                ),
                contentAlignment = Alignment.Center
        ) {
            Icon(
                    imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = if (isSelected) "Selected" else "Add",
                    tint = if (isSelected) BackgroundDark else Primary,
                    modifier = Modifier.size(24.dp)
            )
        }
    }
}
