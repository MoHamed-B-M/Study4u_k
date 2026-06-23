package com.stdy4u.study4u.presentation.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stdy4u.study4u.domain.model.*
import com.stdy4u.study4u.presentation.components.*
import com.stdy4u.study4u.presentation.theme.*
import com.stdy4u.study4u.presentation.viewmodel.CourseDetailViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CourseDetailScreen(
    courseId: String,
    onNavigateBack: () -> Unit,
    viewModel: CourseDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(courseId) {
        viewModel.loadCourse(courseId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 4 })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.course?.name ?: "Course Details",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Edit */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            LoadingState(modifier = Modifier.padding(innerPadding))
        } else if (uiState.course == null) {
            ErrorState(
                message = "Course not found",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Course Info Header
                CourseHeader(course = uiState.course!!)

                // Tab Row
                TabRow(
                    selectedTabIndex = uiState.selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    tabItems.forEachIndexed { index, tab ->
                        Tab(
                            selected = uiState.selectedTab == index,
                            onClick = { viewModel.selectTab(index) },
                            text = { Text(tab.title, fontSize = 12.sp) },
                            icon = { Icon(tab.icon, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }

                // Tab Content
                when (uiState.selectedTab) {
                    0 -> InfoTab(
                        course = uiState.course!!,
                        analytics = uiState.attendanceAnalytics
                    )
                    1 -> MaterialsTab(
                        materials = uiState.materials,
                        onAddMaterial = { title, type, content ->
                            viewModel.addMaterial(title, type, content)
                        },
                        onDeleteMaterial = { viewModel.deleteMaterial(it) }
                    )
                    2 -> TasksTab(
                        tasks = uiState.tasks.filter { it.type == TaskType.NOTE },
                        onToggleTask = { id, completed -> viewModel.toggleTask(id, completed) }
                    )
                    3 -> TasksTab(
                        tasks = uiState.tasks.filter { it.type == TaskType.TASK },
                        onToggleTask = { id, completed -> viewModel.toggleTask(id, completed) }
                    )
                }
            }
        }
    }
}

private data class TabItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val tabItems = listOf(
    TabItem("Info", Icons.Default.Info),
    TabItem("Materials", Icons.Default.Folder),
    TabItem("Notes", Icons.Default.Note),
    TabItem("Tasks", Icons.Default.Checklist)
)

@Composable
private fun CourseHeader(course: Course) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(course.colorValue).copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(course.colorValue).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = course.code.take(2),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color(course.colorValue)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = course.code,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(course.colorValue)
                )
                Text(
                    text = course.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${course.creditHours} Credits",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun InfoTab(
    course: Course,
    analytics: AttendanceAnalytics
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            AppCard(cardStyle = CardStyle.Solid) {
                Text(
                    text = "Course Info",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow("Professor", course.professor)
                InfoRow("Room", course.room)
                InfoRow("Schedule", "${course.startTime} - ${course.endTime}")
                InfoRow("Days", course.weekDays.joinToString(", "))
            }
        }

        item {
            AppCard(cardStyle = CardStyle.Solid) {
                Text(
                    text = "Grade Progress",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                GradeProgressBar(
                    current = course.currentGrade,
                    target = course.targetGrade
                )
            }
        }

        item {
            AppCard(cardStyle = CardStyle.Solid) {
                Text(
                    text = "Attendance Summary",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AttendanceStat("Present", analytics.present, Color(0xFF4ADE80))
                    AttendanceStat("Late", analytics.late, Color(0xFFFFC107))
                    AttendanceStat("Absent", analytics.absent, Color(0xFFFF5252))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Attendance: ${"%.1f".format(analytics.percentage)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (analytics.isBelowThreshold) MaterialTheme.colorScheme.error
                           else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun GradeProgressBar(current: Double, target: Double) {
    val currentPercent = (current / 4.0).toFloat()
    val targetPercent = (target / 4.0).toFloat()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Current: $current", style = MaterialTheme.typography.bodySmall)
            Text("Target: $target", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            LinearProgressIndicator(
                progress = 1f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            LinearProgressIndicator(
                progress = currentPercent,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = if (current >= target) Color(0xFF4ADE80) else MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Grade: ${(currentPercent * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun AttendanceStat(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$count",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun MaterialsTab(
    materials: List<CourseMaterial>,
    onAddMaterial: (String, MaterialType, String) -> Unit,
    onDeleteMaterial: (String) -> Unit
) {
    var showAddSheet by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (materials.isEmpty()) {
            item {
                EmptyStateView(
                    title = "No materials yet",
                    subtitle = "Add your first material"
                )
            }
        } else {
            items(materials) { material ->
                MaterialItem(material = material, onDelete = { onDeleteMaterial(material.id) })
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // FAB
    FloatingActionButton(
        onClick = { showAddSheet = true },
        containerColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(20.dp)
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add Material")
    }

    if (showAddSheet) {
        AddMaterialSheet(
            onDismiss = { showAddSheet = false },
            onAdd = { title, type, content ->
                onAddMaterial(title, type, content)
                showAddSheet = false
            }
        )
    }
}

@Composable
private fun MaterialItem(
    material: CourseMaterial,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (material.type) {
                    MaterialType.LINK -> Icons.Default.Link
                    MaterialType.FILE -> Icons.Default.Description
                    MaterialType.NOTE -> Icons.Default.Note
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = material.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = when (material.type) {
                        MaterialType.LINK -> "Link"
                        MaterialType.FILE -> "File"
                        MaterialType.NOTE -> "Note"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMaterialSheet(
    onDismiss: () -> Unit,
    onAdd: (String, MaterialType, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MaterialType.LINK) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Add Material",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MaterialType.entries.forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text(if (selectedType == MaterialType.LINK) "URL" else "Content") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(20.dp))

            SquishButton(
                onClick = { onAdd(title, selectedType, content) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "ADD",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TasksTab(
    tasks: List<StudyTask>,
    onToggleTask: (String, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (tasks.isEmpty()) {
            item {
                EmptyStateView(
                    title = "No items yet",
                    subtitle = "Add tasks or notes to this course"
                )
            }
        } else {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onToggle = { onToggleTask(task.id, !task.isCompleted) }
                )
            }
        }
    }
}
