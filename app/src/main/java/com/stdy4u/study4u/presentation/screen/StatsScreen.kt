package com.stdy4u.study4u.presentation.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stdy4u.study4u.domain.model.Course
import com.stdy4u.study4u.presentation.components.*
import com.stdy4u.study4u.presentation.viewmodel.PomodoroEvent
import com.stdy4u.study4u.presentation.viewmodel.PomodoroState
import com.stdy4u.study4u.presentation.viewmodel.StatsViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Statistics",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onNavigateToSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        }

        // CGPA Card
        item {
            AppCard(cardStyle = CardStyle.Gradient, gradientColors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.tertiary
            )) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.cgpaResult.letterGrade,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    AnimatedCounter(
                        targetValue = uiState.cgpaResult.cgpa.toFloat(),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Current CGPA",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Performance Bar Chart
        item {
            AppCard(cardStyle = CardStyle.Solid) {
                Text(
                    text = "Course Performance",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))

                uiState.courses.forEach { course ->
                    CoursePerformanceBar(
                        course = course,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    )
                }
            }
        }

        // Pomodoro Timer Card
        item {
            PomodoroCard(
                state = uiState.pomodoroState,
                secondsLeft = uiState.pomodoroSecondsLeft,
                isRunning = uiState.isTimerRunning,
                completedSessions = uiState.completedSessions,
                onStart = { viewModel.startPomodoro() },
                onPause = { viewModel.pausePomodoro() },
                onReset = { viewModel.resetPomodoro() }
            )
        }

        // Subject Performance Summary
        item {
            Text(
                text = "Subject Performance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(uiState.courses) { course ->
            SubjectPerformanceItem(course = course)
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // Handle timer events
    LaunchedEffect(Unit) {
        viewModel.pomodoroEvent.collect { event ->
            when (event) {
                is PomodoroEvent.StartTimer -> {
                    var secondsLeft = event.totalSeconds
                    while (secondsLeft > 0 && uiState.isTimerRunning) {
                        delay(1000)
                        secondsLeft--
                        viewModel.onTimerTick(secondsLeft)
                    }
                    if (secondsLeft <= 0) {
                        viewModel.onTimerComplete()
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun CoursePerformanceBar(
    course: Course,
    modifier: Modifier = Modifier
) {
    val targetFraction = (course.targetGrade / 4.0).toFloat()
    val currentFraction = (course.currentGrade / 4.0).toFloat()

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = course.code,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
            Row {
                Text(
                    text = "Target: ${course.targetGrade}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Current: ${course.currentGrade}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            // Background bar
            LinearProgressIndicator(
                progress = 1f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            // Target marker
            Box(
                modifier = Modifier
                    .offset(
                        x = (targetFraction * 1000).dp,
                        y = (-4).dp
                    )
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.8f))
            )
            // Current progress
            val animatedProgress by animateFloatAsState(
                targetValue = currentFraction,
                animationSpec = tween(1000),
                label = "progress"
            )
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = if (course.currentGrade >= course.targetGrade)
                    Color(0xFF4ADE80)
                else
                    MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent
            )
        }
    }
}

@Composable
private fun PomodoroCard(
    state: PomodoroState,
    secondsLeft: Int,
    isRunning: Boolean,
    completedSessions: Int,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit
) {
    AppCard(cardStyle = CardStyle.Solid) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Focus Timer",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$completedSessions sessions",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Timer display
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressRing(
                percentage = when (state) {
                    PomodoroState.IDLE -> 100f
                    else -> {
                        val total = when (state) {
                            PomodoroState.FOCUSING -> 25 * 60
                            PomodoroState.SHORT_BREAK -> 5 * 60
                            PomodoroState.LONG_BREAK -> 15 * 60
                            else -> 1
                        }
                        (secondsLeft.toFloat() / total) * 100f
                    }
                },
                size = 150.dp,
                strokeWidth = 8.dp
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = when (state) {
                            PomodoroState.IDLE -> "Ready"
                            PomodoroState.FOCUSING -> "Focus"
                            PomodoroState.SHORT_BREAK -> "Break"
                            PomodoroState.LONG_BREAK -> "Long Break"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = when {
                            state == PomodoroState.IDLE -> "${25}:00"
                            else -> "${secondsLeft / 60}:${String.format("%02d", secondsLeft % 60)}"
                        },
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                !isRunning && state == PomodoroState.IDLE -> {
                    SquishButton(onClick = onStart) {
                        Text(
                            text = "START",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp)
                        )
                    }
                }
                isRunning -> {
                    SquishButton(onClick = onPause) {
                        Text(
                            text = "PAUSE",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    SquishButton(onClick = onReset) {
                        Text(
                            text = "STOP",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                        )
                    }
                }
                else -> {
                    SquishButton(onClick = onStart) {
                        Text(
                            text = "RESUME",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectPerformanceItem(course: Course) {
    val progress by animateFloatAsState(
        targetValue = (course.currentGrade / 4.0).toFloat(),
        animationSpec = tween(1000),
        label = "subjectProgress"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(course.colorValue).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = course.code.take(2),
                    fontWeight = FontWeight.Bold,
                    color = Color(course.colorValue)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = when {
                        course.currentGrade >= course.targetGrade -> Color(0xFF4ADE80)
                        course.currentGrade >= course.targetGrade * 0.8 -> Color(0xFFFFC107)
                        else -> MaterialTheme.colorScheme.error
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${(course.currentGrade / course.targetGrade * 100).toInt()}%",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
