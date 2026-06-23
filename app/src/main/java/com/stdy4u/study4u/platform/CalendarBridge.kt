package com.stdy4u.study4u.platform

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import java.util.*

class CalendarBridge(private val context: Context) {

    private val calendarUri = CalendarContract.Calendars.CONTENT_URI
    private val eventUri = CalendarContract.Events.CONTENT_URI

    fun createStudyEvent(title: String, description: String, startTime: Long, endTime: Long): Uri? {
        val calendarId = getPrimaryCalendarId() ?: return null

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, startTime)
            put(CalendarContract.Events.DTEND, endTime)
            put(CalendarContract.Events.TITLE, "Study: $title")
            put(CalendarContract.Events.DESCRIPTION, description)
            put(CalendarContract.Events.CALENDAR_ID, calendarId)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        return context.contentResolver.insert(eventUri, values)
    }

    fun deleteEvent(eventId: Long) {
        val deleteUri = Uri.parse("${CalendarContract.Events.CONTENT_URI}/$eventId")
        context.contentResolver.delete(deleteUri, null, null)
    }

    private fun getPrimaryCalendarId(): Long? {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.IS_PRIMARY
        )

        val cursor = context.contentResolver.query(
            calendarUri,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(0)
                val isPrimary = it.getInt(1)
                if (isPrimary == 1) return id
            }
            // Fallback to first calendar
            if (it.moveToFirst()) return it.getLong(0)
        }

        return null
    }
}
