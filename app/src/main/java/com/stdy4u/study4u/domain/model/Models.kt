package com.stdy4u.study4u.domain.model

data class Course(
    val id: String,
    val code: String,
    val name: String,
    val room: String,
    val professor: String,
    val startTime: String,
    val endTime: String,
    val colorValue: Int,
    val targetGrade: Double,
    val currentGrade: Double,
    val creditHours: Double,
    val weekDays: List<String>
)

data class StudyTask(
    val id: String,
    val courseId: String,
    val title: String,
    val dueDate: Long,
    val urgency: TaskUrgency,
    val isCompleted: Boolean,
    val content: String,
    val type: TaskType
)

enum class TaskUrgency(val value: Int) {
    URGENT(0),
    NORMAL(1);

    companion object {
        fun fromValue(value: Int) = entries.first { it.value == value }
    }
}

enum class TaskType(val value: Int) {
    TASK(0),
    NOTE(1);

    companion object {
        fun fromValue(value: Int) = entries.first { it.value == value }
    }
}

data class AttendanceRecord(
    val id: String,
    val courseId: String,
    val date: Long,
    val status: AttendanceStatus
)

enum class AttendanceStatus(val value: Int) {
    PRESENT(0),
    ABSENT(1),
    LATE(2);

    companion object {
        fun fromValue(value: Int) = entries.first { it.value == value }
    }
}

data class CourseMaterial(
    val id: String,
    val courseId: String,
    val title: String,
    val type: MaterialType,
    val content: String,
    val createdAt: Long
)

enum class MaterialType(val value: Int) {
    LINK(0),
    FILE(1),
    NOTE(2);

    companion object {
        fun fromValue(value: Int) = entries.first { it.value == value }
    }
}

data class PomodoroSession(
    val id: String,
    val courseId: String?,
    val durationSeconds: Long,
    val timestamp: Long,
    val completed: Boolean
)

data class AppSettings(
    val primaryColorValue: Int?,
    val themeMode: ThemeMode,
    val notificationEnabled: Boolean,
    val userName: String,
    val onboardingComplete: Boolean,
    val pomodoroFocusDuration: Int,
    val pomodoroShortBreak: Int,
    val pomodoroLongBreak: Int
)

enum class ThemeMode(val value: Int) {
    SYSTEM(0),
    LIGHT(1),
    DARK(2);

    companion object {
        fun fromValue(value: Int) = entries.first { it.value == value }
    }
}

data class ScreenTimeLog(
    val id: String,
    val appPackageName: String,
    val date: Long,
    val durationMinutes: Int
)

data class CgpaResult(
    val cgpa: Double,
    val percentage: Double,
    val letterGrade: String
)

data class AttendanceAnalytics(
    val present: Int,
    val absent: Int,
    val late: Int,
    val total: Int,
    val percentage: Double,
    val isBelowThreshold: Boolean
)
