package com.stdy4u.study4u.presentation.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stdy4u.study4u.presentation.theme.SplashBackground
import com.stdy4u.study4u.presentation.theme.SplashGreenGlow
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2800)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        SplashGreenGlow.copy(alpha = 0.15f),
                        SplashBackground
                    ),
                    center = Offset(200f, 200f),
                    radius = 600f
                )
            )
            .clipToBounds(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Book icon from ASCII/Unicode
            Text(
                text = "\uD83D\uDCDA",
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = startAnimation,
                enter = fadeIn(animationSpec = tween(1000))
            ) {
                Text(
                    text = "stdy4u",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = startAnimation,
                enter = fadeIn(animationSpec = tween(1500))
            ) {
                Text(
                    text = "STUDY SMARTER",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    color = SplashGreenGlow.copy(alpha = 0.8f),
                    letterSpacing = 4.sp
                )
            }
        }
    }
}
