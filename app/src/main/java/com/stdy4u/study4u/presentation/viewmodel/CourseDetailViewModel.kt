package com.stdy4u.study4u.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stdy4u.study4u.domain.model.*
import com.stdy4u.study4u.domain.repository.*
import com.stdy4u.study4u.domain.usecase.AttendanceAnalyticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CourseDetailUiState(
    val course: Course? = null,
    val attendanceAnalytics: AttendanceAnalytics = AttendanceAnalytics(0, 0, 0, 0, 0.0, false),
    val materials: List<CourseMaterial> = emptyList(),
    val tasks: List<StudyTask> = emptyList(),
    val selectedTab: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val attendanceRepository: AttendanceRepository,
    private val taskRepository: TaskRepository,
    private val materialRepository: MaterialRepository,
    private val attendanceAnalyticsUseCase: AttendanceAnalyticsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseDetailUiState())
    val uiState: StateFlow<CourseDetailUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    fun loadCourse(courseId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            courseRepository.getCourseById(courseId).collect { course ->
                _uiState.update { it.copy(course = course) }
            }

            attendanceRepository.getAttendanceForCourse(courseId).collect { records ->
                val analytics = attendanceAnalyticsUseCase(records)
                _uiState.update { it.copy(attendanceAnalytics = analytics) }
            }

            materialRepository.getMaterialsForCourse(courseId).collect { materials ->
                _uiState.update { it.copy(materials = materials) }
            }

            taskRepository.getTasksForCourse(courseId).collect { tasks ->
                _uiState.update { it.copy(tasks = tasks) }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun selectTab(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun addMaterial(title: String, type: MaterialType, content: String) {
        viewModelScope.launch {
            val course = _uiState.value.course ?: return@launch
            val material = CourseMaterial(
                id = UUID.randomUUID().toString(),
                courseId = course.id,
                title = title,
                type = type,
                content = content,
                createdAt = System.currentTimeMillis()
            )
            materialRepository.insertMaterial(material)
            _toastMessage.emit("Material added")
        }
    }

    fun deleteMaterial(materialId: String) {
        viewModelScope.launch {
            materialRepository.deleteMaterial(materialId)
            _toastMessage.emit("Material deleted")
        }
    }

    fun addTask(title: String, content: String, dueDate: Long, urgency: TaskUrgency) {
        viewModelScope.launch {
            val course = _uiState.value.course ?: return@launch
            val task = StudyTask(
                id = UUID.randomUUID().toString(),
                courseId = course.id,
                title = title,
                dueDate = dueDate,
                urgency = urgency,
                isCompleted = false,
                content = content,
                type = TaskType.TASK
            )
            taskRepository.insertTask(task)
            _toastMessage.emit("Task added")
        }
    }

    fun toggleTask(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            taskRepository.toggleTaskCompletion(taskId, isCompleted)
        }
    }

    fun deleteTask(task: StudyTask) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
            _toastMessage.emit("Task deleted")
        }
    }

    fun updateCourseGrade(currentGrade: Double) {
        viewModelScope.launch {
            val course = _uiState.value.course ?: return@launch
            courseRepository.updateCourse(course.copy(currentGrade = currentGrade))
        }
    }
}
