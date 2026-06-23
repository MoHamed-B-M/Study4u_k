package com.stdy4u.study4u.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Query("SELECT * FROM courses ORDER BY startTime ASC")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    fun getCourseById(courseId: String): Flow<CourseEntity?>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseByIdOnce(courseId: String): CourseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Delete
    suspend fun deleteCourse(course: CourseEntity)

    @Query("DELETE FROM courses WHERE id = :courseId")
    suspend fun deleteCourseById(courseId: String)

    @Query("SELECT * FROM courses WHERE weekDays LIKE :day ORDER BY startTime ASC")
    fun getCoursesForDay(day: String): Flow<List<CourseEntity>>
}

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY urgency ASC, dueDate ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE courseId = :courseId ORDER BY dueDate ASC")
    fun getTasksForCourse(courseId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY urgency ASC, dueDate ASC LIMIT :limit")
    fun getPendingTasks(limit: Int = 3): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    fun getPendingTaskCount(): Flow<Int>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun toggleTaskCompletion(taskId: String, isCompleted: Boolean)
}

@Dao
interface AttendanceDao {

    @Query("SELECT * FROM attendance WHERE courseId = :courseId ORDER BY date DESC")
    fun getAttendanceForCourse(courseId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance ORDER BY date DESC")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE courseId = :courseId AND date = :date")
    suspend fun getAttendanceRecord(courseId: String, date: Long): AttendanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)

    @Update
    suspend fun updateAttendance(attendance: AttendanceEntity)

    @Query("DELETE FROM attendance WHERE id = :attendanceId")
    suspend fun deleteAttendance(attendanceId: String)

    @Query("SELECT * FROM attendance WHERE date BETWEEN :startOfDay AND :endOfDay")
    suspend fun getAttendanceForDate(startOfDay: Long, endOfDay: Long): List<AttendanceEntity>
}

@Dao
interface MaterialDao {

    @Query("SELECT * FROM materials WHERE courseId = :courseId ORDER BY createdAt DESC")
    fun getMaterialsForCourse(courseId: String): Flow<List<MaterialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialEntity)

    @Update
    suspend fun updateMaterial(material: MaterialEntity)

    @Delete
    suspend fun deleteMaterial(material: MaterialEntity)

    @Query("DELETE FROM materials WHERE id = :materialId")
    suspend fun deleteMaterialById(materialId: String)
}

@Dao
interface PomodoroSessionDao {

    @Query("SELECT * FROM pomodoro_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<PomodoroSessionEntity>>

    @Query("SELECT COUNT(*) FROM pomodoro_sessions WHERE completed = 1")
    fun getCompletedSessionCount(): Flow<Int>

    @Query("SELECT SUM(durationSeconds) FROM pomodoro_sessions WHERE completed = 1")
    fun getTotalFocusTime(): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PomodoroSessionEntity)

    @Update
    suspend fun updateSession(session: PomodoroSessionEntity)

    @Query("DELETE FROM pomodoro_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: String)
}

@Dao
interface SettingsDao {

    @Query("SELECT * FROM settings WHERE id = 'default'")
    fun getSettings(): Flow<SettingsEntity?>

    @Query("SELECT * FROM settings WHERE id = 'default'")
    suspend fun getSettingsOnce(): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: SettingsEntity)

    @Update
    suspend fun updateSettings(settings: SettingsEntity)

    @Query("UPDATE settings SET primaryColorValue = :colorValue WHERE id = 'default'")
    suspend fun updatePrimaryColor(colorValue: Int)

    @Query("UPDATE settings SET themeMode = :themeMode WHERE id = 'default'")
    suspend fun updateThemeMode(themeMode: Int)

    @Query("UPDATE settings SET notificationEnabled = :enabled WHERE id = 'default'")
    suspend fun updateNotificationEnabled(enabled: Boolean)

    @Query("UPDATE settings SET userName = :userName WHERE id = 'default'")
    suspend fun updateUserName(userName: String)

    @Query("UPDATE settings SET onboardingComplete = :completed WHERE id = 'default'")
    suspend fun updateOnboardingComplete(completed: Boolean)

    @Query("UPDATE settings SET pomodoroFocusDuration = :duration WHERE id = 'default'")
    suspend fun updatePomodoroFocusDuration(duration: Int)

    @Query("UPDATE settings SET pomodoroShortBreak = :duration WHERE id = 'default'")
    suspend fun updatePomodoroShortBreak(duration: Int)

    @Query("UPDATE settings SET pomodoroLongBreak = :duration WHERE id = 'default'")
    suspend fun updatePomodoroLongBreak(duration: Int)
}

@Dao
interface ScreenTimeDao {

    @Query("SELECT * FROM screen_time ORDER BY date DESC")
    fun getAllScreenTime(): Flow<List<ScreenTimeEntity>>

    @Query("SELECT * FROM screen_time WHERE date BETWEEN :startOfDay AND :endOfDay")
    fun getScreenTimeForDateRange(startOfDay: Long, endOfDay: Long): Flow<List<ScreenTimeEntity>>

    @Query("SELECT SUM(durationMinutes) FROM screen_time WHERE date = :date")
    fun getTotalScreenTimeForDate(date: Long): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScreenTime(screenTime: ScreenTimeEntity)

    @Update
    suspend fun updateScreenTime(screenTime: ScreenTimeEntity)
}
