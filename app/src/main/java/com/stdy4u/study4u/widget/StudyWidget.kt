package com.stdy4u.study4u.widget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.fontWeight
import androidx.glance.unit.ColorProvider
import androidx.glance.unit.dp
import androidx.glance.unit.sp
import com.stdy4u.study4u.MainActivity
import com.stdy4u.study4u.R

class StudyWidget : GlanceAppWidget() {

    companion object {
        val widgetClickAction = ActionParameters.Key<String>("action")
        val widgetClickRoute = ActionParameters.Key<String>("route")
    }

    override suspend fun provideContent(context: Context, id: GlanceId) {
        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .background(
                        androidx.glance.unit.ColorProvider(
                            androidx.compose.ui.graphics.Color(0xFF1B5E3B).hashCode()
                        )
                    ),
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "STUDY4U",
                    style = TextStyle(
                        color = ColorProvider(androidx.compose.ui.graphics.Color.White.hashCode()),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )

                Spacer(GlanceModifier.height(8.dp))

                Text(
                    text = "Courses Today: 3",
                    style = TextStyle(
                        color = ColorProvider(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f).hashCode()),
                        fontSize = 12.sp
                    )
                )

                Text(
                    text = "CGPA: 3.7",
                    style = TextStyle(
                        color = ColorProvider(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f).hashCode()),
                        fontSize = 12.sp
                    )
                )

                Text(
                    text = "Next: Math at 09:00",
                    style = TextStyle(
                        color = ColorProvider(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f).hashCode()),
                        fontSize = 11.sp
                    )
                )

                Spacer(GlanceModifier.height(8.dp))

                Box(
                    modifier = GlanceModifier
                        .defaultPadding()
                        .clickable(
                            actionParams = actionParametersOf(
                                widgetClickAction to "open_app",
                                widgetClickRoute to "home"
                            )
                        )
                ) {
                    Text(
                        text = "Open App",
                        style = TextStyle(
                            color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF4ADE80).hashCode()),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

class StudyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StudyWidget()
}
