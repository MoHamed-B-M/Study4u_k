package com.stdy4u.study4u.domain.repository

import com.stdy4u.study4u.domain.model.*
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun getAllCourses(): Flow<List<Course>>
    fun getCourseById(courseId: String): Flow<Course?>
    suspend fun getCourseByIdOnce(courseId: String): Course?
    suspend fun insertCourse(course: Course)
    suspend fun updateCourse(course: Course)
    suspend fun deleteCourse(courseId: String)
    fun getCoursesForDay(day: String): Flow<List<Course>>
}

interface TaskRepository {
    fun getAllTasks(): Flow<List<StudyTask>>
    fun getTasksForCourse(courseId: String): Flow<List<StudyTask>>
    fun getPendingTasks(limit: Int): Flow<List<StudyTask>>
    fun getPendingTaskCount(): Flow<Int>
    suspend fun insertTask(task: StudyTask)
    suspend fun updateTask(task: StudyTask)
    suspend fun deleteTask(task: StudyTask)
    suspend fun toggleTaskCompletion(taskId: String, isCompleted: Boolean)
}

interface AttendanceRepository {
    fun getAttendanceForCourse(courseId: String): Flow<List<AttendanceRecord>>
    fun getAllAttendance(): Flow<List<AttendanceRecord>>
    suspend fun insertAttendance(record: AttendanceRecord)
    suspend fun updateAttendance(record: AttendanceRecord)
    suspend fun getAttendanceForDate(startOfDay: Long, endOfDay: Long): List<AttendanceRecord>
}

interface MaterialRepository {
    fun getMaterialsForCourse(courseId: String): Flow<List<CourseMaterial>>
    suspend fun insertMaterial(material: CourseMaterial)
    suspend fun updateMaterial(material: CourseMaterial)
    suspend fun deleteMaterial(materialId: String)
}

interface PomodoroRepository {
    fun getAllSessions(): Flow<List<PomodoroSession>>
    fun getCompletedSessionCount(): Flow<Int>
    fun getTotalFocusTime(): Flow<Long?>
    suspend fun insertSession(session: PomodoroSession)
    suspend fun updateSession(session: PomodoroSession)
}

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings?>
    suspend fun getSettingsOnce(): AppSettings?
    suspend fun insertSettings(settings: AppSettings)
    suspend fun updatePrimaryColor(colorValue: Int)
    suspend fun updateThemeMode(mode: Int)
    suspend fun updateNotificationEnabled(enabled: Boolean)
    suspend fun updateUserName(userName: String)
    suspend fun updateOnboardingComplete(completed: Boolean)
    suspend fun updatePomodoroSettings(focus: Int, shortBreak: Int, longBreak: Int)
}

interface ScreenTimeRepository {
    fun getAllScreenTime(): Flow<List<ScreenTimeLog>>
    fun getScreenTimeForDateRange(startOfDay: Long, endOfDay: Long): Flow<List<ScreenTimeLog>>
    fun getTotalScreenTimeForDate(date: Long): Flow<Int?>
    suspend fun insertScreenTime(log: ScreenTimeLog)
}
