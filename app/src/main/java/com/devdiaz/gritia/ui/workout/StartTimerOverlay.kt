package com.devdiaz.gritia.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.devdiaz.gritia.ui.theme.Primary
import com.devdiaz.gritia.ui.theme.TextSecondaryDark

@Composable
fun StartTimerOverlay(seconds: Int, modifier: Modifier = Modifier) {
    if (seconds > 0) {
        Box(
                modifier =
                        modifier.size(200.dp)
                                .background(
                                        Color.Black.copy(alpha = 0.8f),
                                        RoundedCornerShape(16.dp)
                                )
                                .border(2.dp, Primary, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
        ) {
            Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
            ) {
                Text(
                        text = "GET READY",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondaryDark,
                        fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                            progress = { 1f }, // Full circle background hint if wanted
                            modifier = Modifier.size(80.dp),
                            color = TextSecondaryDark.copy(alpha = 0.2f),
                            trackColor = Color.Transparent,
                    )
                    CircularProgressIndicator(
                            progress = { seconds / 3f }, // Animated decrement
                            modifier = Modifier.size(80.dp),
                            color = Primary,
                            trackColor = Color.Transparent,
                    )
                    Text(
                            text = seconds.toString(),
                            style = MaterialTheme.typography.displayMedium,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
