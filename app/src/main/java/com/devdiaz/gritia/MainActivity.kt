package com.devdiaz.gritia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.devdiaz.gritia.ui.navigation.GritiaNavigation
import com.devdiaz.gritia.ui.theme.GritiaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GritiaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GritiaNavigation()
                }
            }
        }
    }
}
