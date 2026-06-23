package com.stdy4u.study4u.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stdy4u.study4u.domain.model.*
import com.stdy4u.study4u.domain.repository.*
import com.stdy4u.study4u.domain.usecase.AttendanceAnalyticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class TrackerUiState(
    val attendanceAnalytics: AttendanceAnalytics = AttendanceAnalytics(0, 0, 0, 0, 0.0, false),
    val todayCourses: List<Course> = emptyList(),
    val selectedDate: Long = System.currentTimeMillis(),
    val attendanceByDate: Map<String, AttendanceStatus> = emptyMap(),
    val isLoading: Boolean = false
)

@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val attendanceRepository: AttendanceRepository,
    private val attendanceAnalyticsUseCase: AttendanceAnalyticsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackerUiState())
    val uiState: StateFlow<TrackerUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    private val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val calendar = Calendar.getInstance()
            val currentDay = dayNames[calendar.get(Calendar.DAY_OF_WEEK) - 1]

            courseRepository.getCoursesForDay(currentDay).collect { courses ->
                _uiState.update { it.copy(todayCourses = courses) }
            }

            attendanceRepository.getAllAttendance().collect { records ->
                val analytics = attendanceAnalyticsUseCase(records)
                _uiState.update { it.copy(attendanceAnalytics = analytics) }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun selectDate(date: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedDate = date) }

            val cal = Calendar.getInstance().apply { timeInMillis = date }
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val startOfDay = cal.timeInMillis

            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            val endOfDay = cal.timeInMillis

            val records = attendanceRepository.getAttendanceForDate(startOfDay, endOfDay)
            val map = records.associate { it.courseId to it.status }
            _uiState.update { it.copy(attendanceByDate = map) }
        }
    }

    fun markAttendance(courseId: String, status: AttendanceStatus) {
        viewModelScope.launch {
            try {
                val record = AttendanceRecord(
                    id = "${courseId}_${_uiState.value.selectedDate}",
                    courseId = courseId,
                    date = _uiState.value.selectedDate,
                    status = status
                )
                attendanceRepository.insertAttendance(record)
                _toastMessage.emit("Attendance marked ${status.name}")
                selectDate(_uiState.value.selectedDate)
            } catch (e: Exception) {
                _toastMessage.emit("Failed to mark attendance")
            }
        }
    }
}
