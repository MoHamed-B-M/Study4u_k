package com.stdy4u.study4u.presentation.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.ExperimentalFoundationApi
import com.stdy4u.study4u.presentation.components.SquishButton
import com.stdy4u.study4u.presentation.theme.SplashBackground
import com.stdy4u.study4u.presentation.theme.SplashGreenGlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class FeaturePage(
    val icon: String,
    val title: String,
    val description: String
)

val featurePages = listOf(
    FeaturePage(
        icon = "\uD83C\uDF1F",
        title = "Focus Timer",
        description = "Stay productive with Pomodoro study sessions. Track your focus time and build better study habits."
    ),
    FeaturePage(
        icon = "\uD83D\uDCC5",
        title = "Smart Schedule",
        description = "Manage your courses, track attendance, and never miss a class with intelligent reminders."
    ),
    FeaturePage(
        icon = "\uD83D\uDCDD",
        title = "Task & Notes",
        description = "Organize assignments, take notes, and calculate your CGPA effortlessly."
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeaturePreviewScreen(
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { featurePages.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            delay(6000)
            val nextPage = (pagerState.currentPage + 1) % featurePages.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "stdy4u",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "STUDY SMARTER",
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                color = SplashGreenGlow.copy(alpha = 0.8f),
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) { page ->
                val feature = featurePages[page]
                val alpha by animateFloatAsState(
                    targetValue = if (pagerState.currentPage == page) 1f else 0.3f,
                    animationSpec = tween(300),
                    label = "alpha"
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = feature.icon,
                        fontSize = 72.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = feature.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = alpha),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = feature.description,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = alpha * 0.7f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(featurePages.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == index) 24.dp else 8.dp, 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (pagerState.currentPage == index) SplashGreenGlow
                                else Color.White.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .padding(24.dp)
            ) {
                SquishButton(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "START APPLICATION",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
