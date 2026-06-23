package com.stdy4u.study4u.widget

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri

class StudyWidgetContentProvider : ContentProvider() {

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor = MatrixCursor(
            arrayOf("courses_count", "cgpa", "next_course", "next_time")
        )

        // Widget data would normally come from repository queries
        cursor.newRow().apply {
            add("courses_count", "3")
            add("cgpa", "3.7")
            add("next_course", "Math 201")
            add("next_time", "09:00 AM")
        }

        return cursor
    }

    override fun getType(uri: Uri): String = "vnd.android.cursor.dir/widget_data"

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}
