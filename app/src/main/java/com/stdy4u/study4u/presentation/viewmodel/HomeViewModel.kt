package com.stdy4u.study4u.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stdy4u.study4u.domain.model.*
import com.stdy4u.study4u.domain.repository.*
import com.stdy4u.study4u.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val userName: String = "",
    val upNextCourse: Course? = null,
    val cgpaResult: CgpaResult = CgpaResult(0.0, 0.0, "N/A"),
    val attendancePercentage: Double = 0.0,
    val pendingTasks: List<StudyTask> = emptyList(),
    val pendingTaskCount: Int = 0,
    val courses: List<Course> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val taskRepository: TaskRepository,
    private val attendanceRepository: AttendanceRepository,
    private val settingsRepository: SettingsRepository,
    private val calculateCgpaUseCase: CalculateCgpaUseCase,
    private val attendanceAnalyticsUseCase: AttendanceAnalyticsUseCase,
    private val upNextUseCase: UpNextUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Load settings
                settingsRepository.getSettings().collect { settings ->
                    _uiState.update { it.copy(userName = settings?.userName ?: "") }
                }
            } catch (_: Exception) {}

            // Load courses
            courseRepository.getAllCourses().collect { courses ->
                _uiState.update { it.copy(courses = courses) }
            }

            // Load pending tasks
            taskRepository.getPendingTasks(3).collect { tasks ->
                _uiState.update { it.copy(pendingTasks = tasks) }
            }

            taskRepository.getPendingTaskCount().collect { count ->
                _uiState.update { it.copy(pendingTaskCount = count) }
            }

            // Load up next
            try {
                val upNext = upNextUseCase()
                _uiState.update { it.copy(upNextCourse = upNext) }
            } catch (_: Exception) {}

            // Load CGPA
            try {
                val cgpa = calculateCgpaUseCase()
                _uiState.update { it.copy(cgpaResult = cgpa) }
            } catch (_: Exception) {}

            // Load attendance
            attendanceRepository.getAllAttendance().collect { records ->
                val analytics = attendanceAnalyticsUseCase(records)
                _uiState.update { it.copy(attendancePercentage = analytics.percentage) }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun toggleTaskCompletion(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                taskRepository.toggleTaskCompletion(taskId, isCompleted)
            } catch (e: Exception) {
                _toastMessage.emit("Failed to update task")
            }
        }
    }
}
