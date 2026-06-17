package com.example.screens

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onStartClick: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    val onboardingPages = listOf(
        OnboardingPageData(
            title = "Turn Your Life\nInto an RPG",
            description = "Complete your daily real-world activities to earn experience points, rank up, and track your self-growth like an RPG character.",
            icon = Icons.Default.SportsEsports,
            color = ElectricIndigo
        ),
        OnboardingPageData(
            title = "Missions &\nDaily Quests",
            description = "Add customizable quests with custom difficulties. High difficulty quests reward substantial XP, elevating your real skills.",
            icon = Icons.Default.CompassCalibration,
            color = RetroCyan
        ),
        OnboardingPageData(
            title = "Unlock Epic\nAchievements",
            description = "Keep streak multipliers alive, level up isolated skills, and unlock legendary achievement badges as visual milestones.",
            icon = Icons.Default.EmojiEvents,
            color = QuestAmber
        )
    )

    Scaffold(
        containerColor = DeepSpaceBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Skip Button (only if not on last page)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (pagerState.currentPage < 2) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(2)
                            }
                        }
                    ) {
                        Text(
                            text = "Skip",
                            color = TextGray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                val data = onboardingPages[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Big decorative icon layout with glowing gradient background circle
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(160.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        data.color.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                    ) {
                        Icon(
                            imageVector = data.icon,
                            contentDescription = null,
                            tint = data.color,
                            modifier = Modifier.size(84.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = data.title,
                        color = TextWhite,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        lineHeight = 40.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = data.description,
                        color = TextGray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }

            // Bottom row containing progress dots and CTA button
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    repeat(3) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = if (isSelected) 24.dp else 8.dp, height = 8.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) dataColor(pagerState.currentPage) else TextMuted)
                        )
                    }
                }

                // CTA Button
                Button(
                    onClick = {
                        if (pagerState.currentPage < 2) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onStartClick()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("onboarding_cta_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = dataColor(pagerState.currentPage)
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (pagerState.currentPage == 2) "Start Leveling Up" else "Next Mission",
                            color = DeepSpaceBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = DeepSpaceBackground
                        )
                    }
                }
            }
        }
    }
}

private fun dataColor(page: Int): Color {
    return when (page) {
        0 -> ElectricIndigo
        1 -> RetroCyan
        2 -> QuestAmber
        else -> ElectricIndigo
    }
}

private data class OnboardingPageData(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)
