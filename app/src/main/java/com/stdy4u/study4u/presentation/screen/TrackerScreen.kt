package com.stdy4u.study4u.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stdy4u.study4u.domain.model.AttendanceStatus
import com.stdy4u.study4u.domain.model.Course
import com.stdy4u.study4u.presentation.components.*
import com.stdy4u.study4u.presentation.viewmodel.TrackerViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen(
    viewModel: TrackerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Tracker",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Attendance Stats Card
        item {
            AppCard(cardStyle = CardStyle.Solid) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressRing(
                        percentage = uiState.attendanceAnalytics.percentage.toFloat(),
                        size = 100.dp,
                        strokeWidth = 8.dp
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${uiState.attendanceAnalytics.percentage.toInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        AttendanceStatItem(
                            label = "Present",
                            count = uiState.attendanceAnalytics.present,
                            color = Color(0xFF4ADE80)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AttendanceStatItem(
                            label = "Late",
                            count = uiState.attendanceAnalytics.late,
                            color = Color(0xFFFFC107)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AttendanceStatItem(
                            label = "Absent",
                            count = uiState.attendanceAnalytics.absent,
                            color = Color(0xFFFF5252)
                        )
                    }
                }
            }
        }

        // Today's Schedule
        item {
            Text(
                text = "Today's Schedule",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (uiState.todayCourses.isEmpty()) {
            item {
                EmptyStateView(
                    title = "No classes today",
                    subtitle = "Enjoy your free day!"
                )
            }
        } else {
            items(uiState.todayCourses) { course ->
                TodayCourseItem(
                    course = course,
                    currentStatus = uiState.attendanceByDate[course.id],
                    onMarkAttendance = { status -> viewModel.markAttendance(course.id, status) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun AttendanceStatItem(
    label: String,
    count: Int,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: $count",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun TodayCourseItem(
    course: Course,
    currentStatus: AttendanceStatus?,
    onMarkAttendance: (AttendanceStatus) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${course.startTime} - ${course.endTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Attendance status chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AttendanceStatus.entries.forEach { status ->
                    val isSelected = currentStatus == status
                    val chipColor = when (status) {
                        AttendanceStatus.PRESENT -> Color(0xFF4ADE80)
                        AttendanceStatus.LATE -> Color(0xFFFFC107)
                        AttendanceStatus.ABSENT -> Color(0xFFFF5252)
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = { onMarkAttendance(status) },
                        label = { Text(status.name, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = chipColor.copy(alpha = 0.2f),
                            selectedLabelColor = chipColor
                        )
                    )
                }
            }
        }
    }
}
