package com.devdiaz.gritia.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.devdiaz.gritia.model.Exercise
import com.devdiaz.gritia.model.MuscleGroup
import com.devdiaz.gritia.ui.theme.BackgroundDark
import com.devdiaz.gritia.ui.theme.Primary
import com.devdiaz.gritia.ui.theme.SurfaceDark
import com.devdiaz.gritia.ui.theme.TextDark
import com.devdiaz.gritia.ui.theme.TextSecondaryDark

@Composable
fun LibraryScreen(viewModel: LibraryViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
            modifier =
                    Modifier.fillMaxSize()
                            .background(BackgroundDark)
    ) {
        // Top App Bar
        TopBar("Ejercicios")

        // Search Bar
        SearchBar(query = uiState.searchQuery, onQueryChange = viewModel::onSearchQueryChanged)

        // Filters
        MuscleGroupFilters(
                selectedGroup = uiState.selectedMuscleGroup,
                onSelectGroup = viewModel::onMuscleGroupSelected
        )

        LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Popular Exercises Section
            item { PopularExercisesSection(uiState.popularExercises) }

            // All Exercises Header
            item {
                Text(
                        text = "Todos los ejercicios",
                        color = TextDark,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // All Exercises List
            items(uiState.filteredExercises) { exercise -> ExerciseListItem(exercise) }

        }
    }
}

@Composable
fun TopBar(string: String) {
    Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
                text = string,
                color = TextDark,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Buscar ejercicio...", color = TextSecondaryDark) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondaryDark)
            },
            modifier =
                    Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(SurfaceDark),
            colors =
                    TextFieldDefaults.colors(
                            focusedContainerColor = SurfaceDark,
                            unfocusedContainerColor = SurfaceDark,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Primary,
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark
                    ),
            singleLine = true
    )
}


@Composable
fun MuscleGroupFilters(selectedGroup: MuscleGroup, onSelectGroup: (MuscleGroup) -> Unit) {
    LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(MuscleGroup.values()) { group ->
            val isSelected = group == selectedGroup
            Box(
                    modifier =
                            Modifier.clip(RoundedCornerShape(50))
                                    .background(if (isSelected) Primary else SurfaceDark)
                                    .clickable { onSelectGroup(group) }
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                        text = group.displayName,
                        color = if (isSelected) BackgroundDark else TextSecondaryDark,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun PopularExercisesSection(exercises: List<Exercise>) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                    text = "Ejercicios populares",
                    color = TextDark,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
            )
        }
        LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) { items(exercises) { exercise -> PopularExerciseCard(exercise) } }
    }
}

@Composable
fun PopularExerciseCard(exercise: Exercise) {
    Card(
            modifier = Modifier.size(width = 280.dp, height = 160.dp).clickable {},
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                    model = exercise.imageUrl,
                    contentDescription = exercise.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
            )
            Box(modifier = Modifier.fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent,
                                    Color.Black.copy(alpha = 0.9f)),
                                startY = 0f,
                                endY = 400f // Approximate
                            )
                        )
            )
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                Box(
                        modifier =
                                Modifier.clip(RoundedCornerShape(4.dp))
                                        .background(Primary)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                            text = exercise.muscleGroup.displayName.uppercase(),
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                        text = exercise.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ExerciseListItem(exercise: Exercise) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceDark)
                            .clickable {}
                            .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
                model = exercise.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier =
                        Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(Color.Gray)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                    text = exercise.name,
                    color = TextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                        text = exercise.muscleGroup.displayName,
                        color = Primary,
                        fontSize = 12.sp
                )
                Box(
                        modifier =
                                Modifier.padding(horizontal = 6.dp)
                                        .size(4.dp)
                                        .background(TextSecondaryDark, CircleShape)
                )
                Text(
                        text = exercise.equipment.displayName,
                        color = TextSecondaryDark,
                        fontSize = 12.sp
                )
            }
        }
    }
}
