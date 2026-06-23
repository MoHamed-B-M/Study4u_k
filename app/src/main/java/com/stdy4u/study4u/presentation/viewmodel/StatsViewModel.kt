package com.stdy4u.study4u.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stdy4u.study4u.domain.model.*
import com.stdy4u.study4u.domain.repository.*
import com.stdy4u.study4u.domain.usecase.CalculateCgpaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatsUiState(
    val cgpaResult: CgpaResult = CgpaResult(0.0, 0.0, "N/A"),
    val courses: List<Course> = emptyList(),
    val completedSessions: Int = 0,
    val totalFocusMinutes: Long = 0,
    val screenTimeMinutes: Int = 0,
    val pomodoroState: PomodoroState = PomodoroState.IDLE,
    val pomodoroSecondsLeft: Int = 0,
    val isTimerRunning: Boolean = false,
    val isLoading: Boolean = false
)

enum class PomodoroState {
    IDLE, FOCUSING, SHORT_BREAK, LONG_BREAK
}

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val pomodoroRepository: PomodoroRepository,
    private val screenTimeRepository: ScreenTimeRepository,
    private val calculateCgpaUseCase: CalculateCgpaUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    private val _pomodoroEvent = MutableSharedFlow<PomodoroEvent>()
    val pomodoroEvent: SharedFlow<PomodoroEvent> = _pomodoroEvent.asSharedFlow()

    private var focusDuration = 25
    private var shortBreakDuration = 5
    private var longBreakDuration = 15
    private var completedFocusCycles = 0

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Load settings
            settingsRepository.getSettings().collect { settings ->
                if (settings != null) {
                    focusDuration = settings.pomodoroFocusDuration
                    shortBreakDuration = settings.pomodoroShortBreak
                    longBreakDuration = settings.pomodoroLongBreak
                }
            }

            // Load CGPA
            try {
                val cgpa = calculateCgpaUseCase()
                _uiState.update { it.copy(cgpaResult = cgpa) }
            } catch (_: Exception) {}

            // Load courses
            courseRepository.getAllCourses().collect { courses ->
                _uiState.update { it.copy(courses = courses) }
            }

            // Load Pomodoro stats
            pomodoroRepository.getCompletedSessionCount().collect { count ->
                _uiState.update { it.copy(completedSessions = count) }
            }

            pomodoroRepository.getTotalFocusTime().collect { total ->
                _uiState.update { it.copy(totalFocusMinutes = (total ?: 0) / 60) }
            }

            // Load screen time for today
            val today = java.time.LocalDate.now(java.time.ZoneId.systemDefault())
            val startOfDay = today.toEpochDay() * 86400000L
            screenTimeRepository.getTotalScreenTimeForDate(startOfDay).collect { minutes ->
                _uiState.update { it.copy(screenTimeMinutes = minutes ?: 0) }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun startPomodoro() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    pomodoroState = PomodoroState.FOCUSING,
                    pomodoroSecondsLeft = focusDuration * 60,
                    isTimerRunning = true
                )
            }
            _pomodoroEvent.emit(PomodoroEvent.StartTimer(focusDuration * 60))
        }
    }

    fun pausePomodoro() {
        viewModelScope.launch {
            _uiState.update { it.copy(isTimerRunning = false) }
            _pomodoroEvent.emit(PomodoroEvent.PauseTimer)
        }
    }

    fun resetPomodoro() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    pomodoroState = PomodoroState.IDLE,
                    pomodoroSecondsLeft = 0,
                    isTimerRunning = false
                )
            }
            _pomodoroEvent.emit(PomodoroEvent.ResetTimer)
        }
    }

    fun onTimerTick(secondsLeft: Int) {
        _uiState.update { it.copy(pomodoroSecondsLeft = secondsLeft) }
    }

    fun onTimerComplete() {
        viewModelScope.launch {
            when (_uiState.value.pomodoroState) {
                PomodoroState.FOCUSING -> {
                    // Save completed session
                    pomodoroRepository.insertSession(
                        PomodoroSession(
                            id = java.util.UUID.randomUUID().toString(),
                            courseId = null,
                            durationSeconds = focusDuration * 60L,
                            timestamp = System.currentTimeMillis(),
                            completed = true
                        )
                    )
                    completedFocusCycles++
                    val nextState = when {
                        completedFocusCycles % 4 == 0 -> PomodoroState.LONG_BREAK
                        else -> PomodoroState.SHORT_BREAK
                    }
                    val duration = when (nextState) {
                        PomodoroState.SHORT_BREAK -> shortBreakDuration * 60
                        PomodoroState.LONG_BREAK -> longBreakDuration * 60
                        else -> 0
                    }
                    _uiState.update {
                        it.copy(
                            pomodoroState = nextState,
                            pomodoroSecondsLeft = duration,
                            isTimerRunning = true
                        )
                    }
                    _pomodoroEvent.emit(PomodoroEvent.StartTimer(duration))
                }
                PomodoroState.SHORT_BREAK, PomodoroState.LONG_BREAK -> {
                    _uiState.update {
                        it.copy(
                            pomodoroState = PomodoroState.IDLE,
                            pomodoroSecondsLeft = 0,
                            isTimerRunning = false
                        )
                    }
                    _pomodoroEvent.emit(PomodoroEvent.TimerComplete)
                }
                else -> {}
            }
        }
    }
}

sealed class PomodoroEvent {
    data class StartTimer(val totalSeconds: Int) : PomodoroEvent()
    object PauseTimer : PomodoroEvent()
    object ResetTimer : PomodoroEvent()
    object TimerComplete : PomodoroEvent()
}
