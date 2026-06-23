package com.grinch.rivo4.view.onboarding

import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph

data class MorphingPage(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val shapeCornerPercent: Int,
    val rotation: Float,
    val scale: Float
)

// ✅ قائمة الصفحات المحدثة لتناسب تطبيق Pdialer
private val pages = listOf(
    MorphingPage(
        icon = Icons.Default.Contacts,
        title = "Smart Contacts",
        description = "Manage your contacts with a clean and expressive Material 3 interface",
        shapeCornerPercent = 50,
        rotation = 0f,
        scale = 1f
    ),
    MorphingPage(
        icon = Icons.Default.AutoAwesome,
        title = "Visual Experience",
        description = "Enjoy immersive Blur and Glow effects optimized for your device",
        shapeCornerPercent = 30,
        rotation = 45f,
        scale = 1.2f
    ),
    MorphingPage(
        icon = Icons.Default.FlashOn,
        title = "Fast & Optimized",
        description = "A lightweight experience built for speed and smooth bouncy animations",
        shapeCornerPercent = 10,
        rotation = 0f,
        scale = 1f
    )
)

@Destination // ✅ إضافة الوجهة لنظام التنقل
@Composable
fun MorphingOnboardingScreen(
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    var currentPage by remember { mutableIntStateOf(0) }

    // ✅ دالة الإنهاء وحفظ الحالة لعدم الظهور مجدداً
    val onFinished = {
        val sharedPref = context.getSharedPreferences("pdialer_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("is_first_launch", false).apply()

        // الانتقال للواجهة الرئيسية (تأكد من اسم الوجهة لديك)
        // navigator.navigate(DialPadScreenDestination)
    }

    val cornerPercent by animateFloatAsState(
        targetValue = pages[currentPage].shapeCornerPercent.toFloat(),
        animationSpec = tween(500), label = ""
    )
    val rotation by animateFloatAsState(
        targetValue = pages[currentPage].rotation,
        animationSpec = tween(500), label = ""
    )
    val shapeSize by animateDpAsState(
        targetValue = (140 * pages[currentPage].scale).dp,
        animationSpec = tween(500), label = ""
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background morphing shapes
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .offset(x = (-50).dp, y = 100.dp)
                    .rotate(rotation * 2)
                    .clip(RoundedCornerShape(cornerPercent.toInt()))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            )

            // Skip button
            TextButton(
                onClick = onFinished,
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Text("Skip")
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                // Main morphing shape
                Box(
                    modifier = Modifier
                        .size(shapeSize)
                        .rotate(rotation)
                        .clip(RoundedCornerShape(cornerPercent.toInt()))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = pages[currentPage].icon,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp).rotate(-rotation),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = pages[currentPage].title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = pages[currentPage].description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Indicators
                Row(horizontalArrangement = Arrangement.Center) {
                    repeat(pages.size) { index ->
                        val isSelected = index == currentPage
                        val indicatorWidth by animateDpAsState(if (isSelected) 24.dp else 8.dp, label = "")
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(indicatorWidth, 8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentPage > 0) {
                        TextButton(onClick = { currentPage-- }) { Text("Back") }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Button(
                        onClick = {
                            if (currentPage < pages.size - 1) {
                                currentPage++
                            } else {
                                onFinished()
                            }
                        },
                        shape = RoundedCornerShape(cornerPercent.toInt().coerceIn(10, 50))
                    ) {
                        Text(if (currentPage == pages.size - 1) "Get Started" else "Next")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
