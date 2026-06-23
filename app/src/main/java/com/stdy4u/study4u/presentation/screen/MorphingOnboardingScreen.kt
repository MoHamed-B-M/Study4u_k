package com.stdy4u.study4u.presentation.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stdy4u.study4u.presentation.components.SquishButton
import com.stdy4u.study4u.presentation.theme.SplashBackground
import kotlinx.coroutines.delay

data class MorphingPage(
    val emoji: String,
    val title: String,
    val description: String,
    val color: Color
)

private val morphingPages = listOf(
    MorphingPage(
        emoji = "\uD83C\uDF1F",
        title = "Focus Timer",
        description = "Stay productive with Pomodoro study sessions. Track your focus time and build better study habits.",
        color = Color(0xFF4ADE80)
    ),
    MorphingPage(
        emoji = "\uD83D\uDCC5",
        title = "Smart Schedule",
        description = "Manage your courses, track attendance, and never miss a class with intelligent reminders.",
        color = Color(0xFF6750A4)
    ),
    MorphingPage(
        emoji = "\uD83D\uDCDD",
        title = "Task & Notes",
        description = "Organize assignments, take notes, and calculate your CGPA effortlessly.",
        color = Color(0xFF2196F3)
    )
)

@Composable
fun MorphingOnboardingScreen(
    onComplete: () -> Unit
) {
    var currentPage by remember { mutableIntStateOf(0) }
    val pageCount = morphingPages.size

    val infiniteTransition = rememberInfiniteTransition()
    val morphProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "morph"
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentPage = (currentPage + 1) % pageCount
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            MorphingShape(
                page = morphingPages[currentPage],
                morphProgress = morphProgress,
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = morphingPages[currentPage].emoji,
                fontSize = 48.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = morphingPages[currentPage].title,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = morphingPages[currentPage].description,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pageCount) { index ->
                    val alpha by animateFloatAsState(
                        targetValue = if (index == currentPage) 1f else 0.3f,
                        animationSpec = tween(300),
                        label = "dotAlpha"
                    )
                    val width by animateDpAsState(
                        targetValue = if (index == currentPage) 32.dp else 8.dp,
                        animationSpec = tween(300),
                        label = "dotWidth"
                    )
                    Box(
                        modifier = Modifier
                            .width(width)
                            .height(8.dp)
                            .background(
                                color = morphingPages[currentPage].color.copy(alpha = alpha),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (currentPage == pageCount - 1) {
                SquishButton(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "GET STARTED",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 14.dp)
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onComplete) {
                        Text("Skip", color = Color.White.copy(alpha = 0.6f))
                    }
                    SquishButton(
                        onClick = { currentPage = (currentPage + 1).coerceAtMost(pageCount - 1) }
                    ) {
                        Text(
                            text = "Next",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MorphingShape(
    page: MorphingPage,
    morphProgress: Float,
    modifier: Modifier = Modifier
) {
    val cornerRadius = morphProgress * 60f
    val rotation = morphProgress * 180f

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val rectSize = Size(canvasWidth * 0.6f, canvasHeight * 0.6f)
        val topLeft = Offset(
            (canvasWidth - rectSize.width) / 2,
            (canvasHeight - rectSize.height) / 2
        )

        rotate(rotation, pivot = Offset(canvasWidth / 2, canvasHeight / 2)) {
            drawRoundRect(
                color = page.color.copy(alpha = 0.15f),
                topLeft = topLeft,
                size = rectSize,
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )
            val innerSize = Size(rectSize.width * 0.5f, rectSize.height * 0.5f)
            val innerTopLeft = Offset(
                (canvasWidth - innerSize.width) / 2,
                (canvasHeight - innerSize.height) / 2
            )
            drawRoundRect(
                color = page.color.copy(alpha = 0.3f),
                topLeft = innerTopLeft,
                size = innerSize,
                cornerRadius = CornerRadius(cornerRadius * 0.5f, cornerRadius * 0.5f)
            )
        }
    }
}

@Composable
private fun TextButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    SquishButton(onClick = onClick) {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            content()
        }
    }
}
