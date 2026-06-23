package com.stdy4u.study4u.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        CourseEntity::class,
        TaskEntity::class,
        AttendanceEntity::class,
        MaterialEntity::class,
        PomodoroSessionEntity::class,
        SettingsEntity::class,
        ScreenTimeEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun courseDao(): CourseDao
    abstract fun taskDao(): TaskDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun materialDao(): MaterialDao
    abstract fun pomodoroSessionDao(): PomodoroSessionDao
    abstract fun settingsDao(): SettingsDao
    abstract fun screenTimeDao(): ScreenTimeDao
}
