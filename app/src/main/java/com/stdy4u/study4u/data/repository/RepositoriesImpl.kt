package com.stdy4u.study4u.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stdy4u.study4u.data.local.*
import com.stdy4u.study4u.domain.model.*
import com.stdy4u.study4u.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepositoryImpl @Inject constructor(
    private val courseDao: CourseDao
) : CourseRepository {

    private val gson = Gson()

    override fun getAllCourses(): Flow<List<Course>> =
        courseDao.getAllCourses().map { entities -> entities.map { it.toDomain() } }

    override fun getCourseById(courseId: String): Flow<Course?> =
        courseDao.getCourseById(courseId).map { it?.toDomain() }

    override suspend fun getCourseByIdOnce(courseId: String): Course? =
        courseDao.getCourseByIdOnce(courseId)?.toDomain()

    override suspend fun insertCourse(course: Course) =
        courseDao.insertCourse(course.toEntity())

    override suspend fun updateCourse(course: Course) =
        courseDao.updateCourse(course.toEntity())

    override suspend fun deleteCourse(courseId: String) =
        courseDao.deleteCourseById(courseId)

    override fun getCoursesForDay(day: String): Flow<List<Course>> =
        courseDao.getCoursesForDay(day).map { entities -> entities.map { it.toDomain() } }

    private fun CourseEntity.toDomain() = Course(
        id = id, code = code, name = name, room = room, professor = professor,
        startTime = startTime, endTime = endTime, colorValue = colorValue,
        targetGrade = targetGrade, currentGrade = currentGrade,
        creditHours = creditHours, weekDays = parseWeekDays(weekDays)
    )

    private fun Course.toEntity() = CourseEntity(
        id = id, code = code, name = name, room = room, professor = professor,
        startTime = startTime, endTime = endTime, colorValue = colorValue,
        targetGrade = targetGrade, currentGrade = currentGrade,
        creditHours = creditHours, weekDays = gson.toJson(weekDays)
    )

    private fun parseWeekDays(json: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getAllTasks(): Flow<List<StudyTask>> =
        taskDao.getAllTasks().map { entities -> entities.map { it.toDomain() } }

    override fun getTasksForCourse(courseId: String): Flow<List<StudyTask>> =
        taskDao.getTasksForCourse(courseId).map { entities -> entities.map { it.toDomain() } }

    override fun getPendingTasks(limit: Int): Flow<List<StudyTask>> =
        taskDao.getPendingTasks(limit).map { entities -> entities.map { it.toDomain() } }

    override fun getPendingTaskCount(): Flow<Int> =
        taskDao.getPendingTaskCount()

    override suspend fun insertTask(task: StudyTask) =
        taskDao.insertTask(task.toEntity())

    override suspend fun updateTask(task: StudyTask) =
        taskDao.updateTask(task.toEntity())

    override suspend fun deleteTask(task: StudyTask) =
        taskDao.deleteTask(task.toEntity())

    override suspend fun toggleTaskCompletion(taskId: String, isCompleted: Boolean) =
        taskDao.toggleTaskCompletion(taskId, isCompleted)

    private fun TaskEntity.toDomain() = StudyTask(
        id = id, courseId = courseId, title = title, dueDate = dueDate,
        urgency = TaskUrgency.fromValue(urgency), isCompleted = isCompleted,
        content = content, type = TaskType.fromValue(type)
    )

    private fun StudyTask.toEntity() = TaskEntity(
        id = id, courseId = courseId, title = title, dueDate = dueDate,
        urgency = urgency.value, isCompleted = isCompleted,
        content = content, type = type.value
    )
}

@Singleton
class AttendanceRepositoryImpl @Inject constructor(
    private val attendanceDao: AttendanceDao
) : AttendanceRepository {

    override fun getAttendanceForCourse(courseId: String): Flow<List<AttendanceRecord>> =
        attendanceDao.getAttendanceForCourse(courseId).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getAllAttendance(): Flow<List<AttendanceRecord>> =
        attendanceDao.getAllAttendance().map { entities -> entities.map { it.toDomain() } }

    override suspend fun insertAttendance(record: AttendanceRecord) =
        attendanceDao.insertAttendance(record.toEntity())

    override suspend fun updateAttendance(record: AttendanceRecord) =
        attendanceDao.updateAttendance(record.toEntity())

    override suspend fun getAttendanceForDate(startOfDay: Long, endOfDay: Long): List<AttendanceRecord> =
        attendanceDao.getAttendanceForDate(startOfDay, endOfDay).map { it.toDomain() }

    private fun AttendanceEntity.toDomain() = AttendanceRecord(
        id = id, courseId = courseId, date = date,
        status = AttendanceStatus.fromValue(status)
    )

    private fun AttendanceRecord.toEntity() = AttendanceEntity(
        id = id, courseId = courseId, date = date, status = status.value
    )
}

@Singleton
class MaterialRepositoryImpl @Inject constructor(
    private val materialDao: MaterialDao
) : MaterialRepository {

    override fun getMaterialsForCourse(courseId: String): Flow<List<CourseMaterial>> =
        materialDao.getMaterialsForCourse(courseId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun insertMaterial(material: CourseMaterial) =
        materialDao.insertMaterial(material.toEntity())

    override suspend fun updateMaterial(material: CourseMaterial) =
        materialDao.updateMaterial(material.toEntity())

    override suspend fun deleteMaterial(materialId: String) =
        materialDao.deleteMaterialById(materialId)

    private fun MaterialEntity.toDomain() = CourseMaterial(
        id = id, courseId = courseId, title = title,
        type = MaterialType.fromValue(type), content = content, createdAt = createdAt
    )

    private fun CourseMaterial.toEntity() = MaterialEntity(
        id = id, courseId = courseId, title = title,
        type = type.value, content = content, createdAt = createdAt
    )
}

@Singleton
class PomodoroRepositoryImpl @Inject constructor(
    private val pomodoroSessionDao: PomodoroSessionDao
) : PomodoroRepository {

    override fun getAllSessions(): Flow<List<PomodoroSession>> =
        pomodoroSessionDao.getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getCompletedSessionCount(): Flow<Int> =
        pomodoroSessionDao.getCompletedSessionCount()

    override fun getTotalFocusTime(): Flow<Long?> =
        pomodoroSessionDao.getTotalFocusTime()

    override suspend fun insertSession(session: PomodoroSession) =
        pomodoroSessionDao.insertSession(session.toEntity())

    override suspend fun updateSession(session: PomodoroSession) =
        pomodoroSessionDao.updateSession(session.toEntity())

    private fun PomodoroSessionEntity.toDomain() = PomodoroSession(
        id = id, courseId = courseId, durationSeconds = durationSeconds,
        timestamp = timestamp, completed = completed
    )

    private fun PomodoroSession.toEntity() = PomodoroSessionEntity(
        id = id, courseId = courseId, durationSeconds = durationSeconds,
        timestamp = timestamp, completed = completed
    )
}

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: SettingsDao
) : SettingsRepository {

    override fun getSettings(): Flow<AppSettings?> =
        settingsDao.getSettings().map { it?.toDomain() }

    override suspend fun getSettingsOnce(): AppSettings? =
        settingsDao.getSettingsOnce()?.toDomain()

    override suspend fun insertSettings(settings: AppSettings) =
        settingsDao.insertSettings(settings.toEntity())

    override suspend fun updatePrimaryColor(colorValue: Int) =
        settingsDao.updatePrimaryColor(colorValue)

    override suspend fun updateThemeMode(mode: Int) =
        settingsDao.updateThemeMode(mode)

    override suspend fun updateNotificationEnabled(enabled: Boolean) =
        settingsDao.updateNotificationEnabled(enabled)

    override suspend fun updateUserName(userName: String) =
        settingsDao.updateUserName(userName)

    override suspend fun updateOnboardingComplete(completed: Boolean) =
        settingsDao.updateOnboardingComplete(completed)

    override suspend fun updatePomodoroSettings(focus: Int, shortBreak: Int, longBreak: Int) {
        settingsDao.updatePomodoroFocusDuration(focus)
        settingsDao.updatePomodoroShortBreak(shortBreak)
        settingsDao.updatePomodoroLongBreak(longBreak)
    }

    private fun SettingsEntity.toDomain() = AppSettings(
        primaryColorValue = primaryColorValue,
        themeMode = ThemeMode.fromValue(themeMode),
        notificationEnabled = notificationEnabled,
        userName = userName,
        onboardingComplete = onboardingComplete,
        pomodoroFocusDuration = pomodoroFocusDuration,
        pomodoroShortBreak = pomodoroShortBreak,
        pomodoroLongBreak = pomodoroLongBreak
    )

    private fun AppSettings.toEntity() = SettingsEntity(
        id = "default",
        primaryColorValue = primaryColorValue,
        themeMode = themeMode.value,
        notificationEnabled = notificationEnabled,
        userName = userName,
        onboardingComplete = onboardingComplete,
        pomodoroFocusDuration = pomodoroFocusDuration,
        pomodoroShortBreak = pomodoroShortBreak,
        pomodoroLongBreak = pomodoroLongBreak
    )
}

@Singleton
class ScreenTimeRepositoryImpl @Inject constructor(
    private val screenTimeDao: ScreenTimeDao
) : ScreenTimeRepository {

    override fun getAllScreenTime(): Flow<List<ScreenTimeLog>> =
        screenTimeDao.getAllScreenTime().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getScreenTimeForDateRange(startOfDay: Long, endOfDay: Long): Flow<List<ScreenTimeLog>> =
        screenTimeDao.getScreenTimeForDateRange(startOfDay, endOfDay).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getTotalScreenTimeForDate(date: Long): Flow<Int?> =
        screenTimeDao.getTotalScreenTimeForDate(date)

    override suspend fun insertScreenTime(log: ScreenTimeLog) =
        screenTimeDao.insertScreenTime(log.toEntity())

    private fun ScreenTimeEntity.toDomain() = ScreenTimeLog(
        id = id, appPackageName = appPackageName,
        date = date, durationMinutes = durationMinutes
    )

    private fun ScreenTimeLog.toEntity() = ScreenTimeEntity(
        id = id, appPackageName = appPackageName,
        date = date, durationMinutes = durationMinutes
    )
}
