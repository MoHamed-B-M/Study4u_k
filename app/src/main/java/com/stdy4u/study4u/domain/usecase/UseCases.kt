package com.stdy4u.study4u.domain.usecase

import com.stdy4u.study4u.domain.model.*
import com.stdy4u.study4u.domain.repository.*
import javax.inject.Inject

class CalculateCgpaUseCase @Inject constructor(
    private val courseRepository: CourseRepository
) {
    suspend operator fun invoke(): CgpaResult {
        val courses = courseRepository.getAllCourses().let { flow ->
            var result = emptyList<Course>()
            flow.collect { result = it; return@collect }
            result
        }

        val gradedCourses = courses.filter { it.currentGrade > 0 }
        if (gradedCourses.isEmpty()) return CgpaResult(0.0, 0.0, "N/A")

        val totalWeightedGrade = gradedCourses.sumOf { it.currentGrade * it.creditHours }
        val totalCredits = gradedCourses.sumOf { it.creditHours }
        val cgpa = if (totalCredits > 0) totalWeightedGrade / totalCredits else 0.0
        val percentage = (cgpa / 4.0) * 100.0
        val letterGrade = when {
            cgpa >= 3.7 -> "A"
            cgpa >= 3.3 -> "A-"
            cgpa >= 3.0 -> "B+"
            cgpa >= 2.7 -> "B"
            cgpa >= 2.3 -> "B-"
            cgpa >= 2.0 -> "C+"
            cgpa >= 1.7 -> "C"
            cgpa >= 1.3 -> "C-"
            cgpa >= 1.0 -> "D"
            else -> "F"
        }

        return CgpaResult(cgpa = cgpa, percentage = percentage, letterGrade = letterGrade)
    }
}

class AttendanceAnalyticsUseCase @Inject constructor() {
    operator fun invoke(records: List<AttendanceRecord>): AttendanceAnalytics {
        val present = records.count { it.status == AttendanceStatus.PRESENT }
        val absent = records.count { it.status == AttendanceStatus.ABSENT }
        val late = records.count { it.status == AttendanceStatus.LATE }
        val total = records.size
        val percentage = if (total > 0) ((present + late).toDouble() / total) * 100.0 else 0.0

        return AttendanceAnalytics(
            present = present,
            absent = absent,
            late = late,
            total = total,
            percentage = percentage,
            isBelowThreshold = percentage < 75.0
        )
    }
}

class UpNextUseCase @Inject constructor(
    private val courseRepository: CourseRepository
) {
    private val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    suspend operator fun invoke(): Course? {
        val now = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance()
        val currentDay = dayNames[calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1]
        val currentTimeMinutes = calendar.get(java.util.Calendar.HOUR_OF_DAY) * 60 +
                calendar.get(java.util.Calendar.MINUTE)

        val courses = courseRepository.getAllCourses().let { flow ->
            var result = emptyList<Course>()
            flow.collect { result = it; return@collect }
            result
        }

        val todayCourses = courses.filter { currentDay in it.weekDays }
        val upcomingCourses = todayCourses.filter { course ->
            val timeParts = course.startTime.split(":")
            val hour = timeParts[0].trim().toIntOrNull() ?: 0
            val minute = timeParts[1].trim().substringBefore(" ").toIntOrNull() ?: 0
            val isPM = course.startTime.contains("PM", ignoreCase = true)
            val courseHour = if (isPM && hour != 12) hour + 12 else if (!isPM && hour == 12) 0 else hour
            val courseMinutes = courseHour * 60 + minute
            courseMinutes > currentTimeMinutes
        }

        return upcomingCourses.minByOrNull { course ->
            val timeParts = course.startTime.split(":")
            val hour = timeParts[0].trim().toIntOrNull() ?: 0
            val minute = timeParts[1].trim().substringBefore(" ").toIntOrNull() ?: 0
            val isPM = course.startTime.contains("PM", ignoreCase = true)
            val courseHour = if (isPM && hour != 12) hour + 12 else if (!isPM && hour == 12) 0 else hour
            courseHour * 60 + minute
        }
    }
}
