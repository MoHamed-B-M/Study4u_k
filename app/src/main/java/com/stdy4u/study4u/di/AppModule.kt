package com.stdy4u.study4u.di

import android.content.Context
import androidx.room.Room
import com.stdy4u.study4u.data.local.*
import com.stdy4u.study4u.data.repository.*
import com.stdy4u.study4u.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "study4u_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideCourseDao(database: AppDatabase): CourseDao = database.courseDao()

    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideAttendanceDao(database: AppDatabase): AttendanceDao = database.attendanceDao()

    @Provides
    fun provideMaterialDao(database: AppDatabase): MaterialDao = database.materialDao()

    @Provides
    fun providePomodoroSessionDao(database: AppDatabase): PomodoroSessionDao = database.pomodoroSessionDao()

    @Provides
    fun provideSettingsDao(database: AppDatabase): SettingsDao = database.settingsDao()

    @Provides
    fun provideScreenTimeDao(database: AppDatabase): ScreenTimeDao = database.screenTimeDao()

    @Provides
    @Singleton
    fun provideCourseRepository(impl: CourseRepositoryImpl): CourseRepository = impl

    @Provides
    @Singleton
    fun provideTaskRepository(impl: TaskRepositoryImpl): TaskRepository = impl

    @Provides
    @Singleton
    fun provideAttendanceRepository(impl: AttendanceRepositoryImpl): AttendanceRepository = impl

    @Provides
    @Singleton
    fun provideMaterialRepository(impl: MaterialRepositoryImpl): MaterialRepository = impl

    @Provides
    @Singleton
    fun providePomodoroRepository(impl: PomodoroRepositoryImpl): PomodoroRepository = impl

    @Provides
    @Singleton
    fun provideSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository = impl

    @Provides
    @Singleton
    fun provideScreenTimeRepository(impl: ScreenTimeRepositoryImpl): ScreenTimeRepository = impl
}
