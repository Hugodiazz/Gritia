package com.devdiaz.gritia.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.devdiaz.gritia.ui.home.HomeScreen
import com.devdiaz.gritia.ui.library.LibraryScreen
import com.devdiaz.gritia.ui.metrics.BodyMetricsScreen
import com.devdiaz.gritia.ui.profile.ProfileScreen
import com.devdiaz.gritia.ui.theme.BackgroundDark
import com.devdiaz.gritia.ui.theme.Primary
import com.devdiaz.gritia.ui.theme.TextSecondaryDark

@Composable
fun MainScreen() {
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    val items =
            listOf(
                    NavigationItem("Rutinas", Icons.Default.Home),
                    NavigationItem("Ejercicios", Icons.Default.FitnessCenter),
                    NavigationItem("Progreso", Icons.Default.BarChart),
                    NavigationItem("Perfil", Icons.Default.Person)
            )

    Scaffold(
            bottomBar = {
                NavigationBar(containerColor = BackgroundDark, contentColor = TextSecondaryDark) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
                                selected = selectedItem == index,
                                onClick = { selectedItem = index },
                                colors =
                                        NavigationBarItemDefaults.colors(
                                                selectedIconColor = Primary,
                                                selectedTextColor = Primary,
                                                indicatorColor =
                                                        BackgroundDark, // No pill background or
                                                // same as container
                                                unselectedIconColor = TextSecondaryDark,
                                                unselectedTextColor = TextSecondaryDark
                                        )
                        )
                    }
                }
            }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem) {
                0 -> HomeScreen()
                1 -> LibraryScreen()
                2 -> BodyMetricsScreen()
                3 -> ProfileScreen()
            }
        }
    }
}

data class NavigationItem(val label: String, val icon: ImageVector)

