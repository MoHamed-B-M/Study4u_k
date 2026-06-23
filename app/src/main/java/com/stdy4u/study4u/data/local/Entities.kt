package com.stdy4u.study4u.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "courses",
    indices = [Index(value = ["code"], unique = true)]
)
data class CourseEntity(
    @PrimaryKey val id: String,
    val code: String,
    val name: String,
    val room: String = "",
    val professor: String = "",
    val startTime: String,
    val endTime: String,
    val colorValue: Int,
    val targetGrade: Double = 4.0,
    val currentGrade: Double = 0.0,
    val creditHours: Double = 3.0,
    val weekDays: String = "[]"
)

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["courseId"])]
)
data class TaskEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val title: String,
    val dueDate: Long,
    val urgency: Int = 1,
    val isCompleted: Boolean = false,
    val content: String = "",
    val type: Int = 0
)

@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["courseId"])]
)
data class AttendanceEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val date: Long,
    val status: Int = 0
)

@Entity(
    tableName = "materials",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["courseId"])]
)
data class MaterialEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val title: String,
    val type: Int = 0,
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "pomodoro_sessions")
data class PomodoroSessionEntity(
    @PrimaryKey val id: String,
    val courseId: String? = null,
    val durationSeconds: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val completed: Boolean = false
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: String = "default",
    val primaryColorValue: Int? = null,
    val themeMode: Int = 0,
    val notificationEnabled: Boolean = true,
    val userName: String = "",
    val onboardingComplete: Boolean = false,
    val pomodoroFocusDuration: Int = 25,
    val pomodoroShortBreak: Int = 5,
    val pomodoroLongBreak: Int = 15
)

@Entity(
    tableName = "screen_time",
    indices = [Index(value = ["appPackageName", "date"], unique = true)]
)
data class ScreenTimeEntity(
    @PrimaryKey val id: String,
    val appPackageName: String,
    val date: Long,
    val durationMinutes: Int = 0
)
