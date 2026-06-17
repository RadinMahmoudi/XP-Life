package com.example.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.models.MoodEntry
import com.example.models.UserProgress
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun StatsScreen(
    viewModel: GameViewModel
) {
    val progressState by viewModel.userProgress.collectAsState()
    val missionsState by viewModel.allMissions.collectAsState()
    val moodEntriesState by viewModel.allMoodEntries.collectAsState()

    val progress = progressState ?: UserProgress()
    val missions = missionsState
    val moodEntries = moodEntriesState

    val todayStr = LocalDate.now().toString()

    // 1. Calculate XP earned today
    val xpToday = remember(missions) {
        missions.sumOf { m ->
            m.completedDates.split(",").count { it == todayStr } * m.xpReward
        }
    }

    // 2. Calculate XP earned last 7 days
    val today = LocalDate.now()
    val last7Days = remember {
        (0..6).map { today.minusDays(it.toLong()) }.reversed()
    }

    val weeklyXpData = remember(missions) {
        last7Days.map { day ->
            val xpOnDay = missions.sumOf { m ->
                m.completedDates.split(",").count { it == day.toString() } * m.xpReward
            }
            WeeklyXpPoint(day, xpOnDay)
        }
    }

    val xpThisWeek = weeklyXpData.sumOf { it.xp }

    // 3. Determine best skill category
    val skillsProgressList = listOf(
        "Programming" to progress.xpProgramming,
        "Linux" to progress.xpLinux,
        "Basketball" to progress.xpBasketball,
        "Fitness" to progress.xpFitness,
        "School" to progress.xpSchool,
        "Reading" to progress.xpReading,
        "Health" to progress.xpHealth,
        "Life" to progress.xpLife
    )
    val bestSkill = skillsProgressList.maxByOrNull { it.second } ?: ("None" to 0)

    // 4. Most completed category
    val categoriesCompletionsList = listOf(
        "Programming" to progress.countProgramming,
        "Linux" to progress.countLinux,
        "Basketball" to progress.countBasketball,
        "Fitness" to progress.countFitness,
        "School" to progress.countSchool,
        "Reading" to progress.countReading,
        "Health" to progress.countHealth,
        "Life" to progress.countLife
    )
    val mostCompletedCategory = categoriesCompletionsList.maxByOrNull { it.second } ?: ("None" to 0)

    var moodNoteText by remember { mutableStateOf("") }

    Scaffold(
        containerColor = DeepSpaceBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .testTag("stats_scroll"),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "ACCOMPLISHMENT GRAPHS",
                    color = TextGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Hero Stats Ledger",
                    color = TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 1. Weekly Recap card!
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF1E1E30), Color(0xFF11111E))
                            )
                        )
                        .border(1.dp, QuestAmber.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Timeline, contentDescription = null, tint = QuestAmber)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("WEEKLY RECAP REPORT", color = QuestAmber, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "This week you harvested +$xpThisWeek XP across your missions. Your highest standing specialty is ${bestSkill.first} at Level ${(bestSkill.second / 300) + 1}.",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Aesthetic motivation advice: Keep your streak alive to double your passive coin gains tomorrow!",
                            color = TextGray,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // 2. Daily Mood Check-In Ledger
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SlateCardBackground)
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "DAILY MOOD CHECK-IN",
                        color = EmeraldGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "How does your core system feel today? Keep logs motivational:",
                        color = TextGray,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val moods = listOf(
                        Quadruple("Great", Icons.Default.SentimentVerySatisfied, EmeraldGreen, 100),
                        Quadruple("Good", Icons.Default.SentimentSatisfiedAlt, RetroCyan, 80),
                        Quadruple("Okay", Icons.Default.SentimentSatisfied, QuestAmber, 60),
                        Quadruple("Tired", Icons.Default.SentimentDissatisfied, Color(0xFFA78BFA), 40),
                        Quadruple("Bad", Icons.Default.SentimentVeryDissatisfied, Color(0xFFFF5252), 20)
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(moods) { (label, icon, color, energy) ->
                            val isSelected = progress.moodToday == label
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) color.copy(alpha = 0.2f) else Color(0xFF1E293B))
                                    .border(1.dp, if (isSelected) color else Color(0xFF1E293B), RoundedCornerShape(12.dp))
                                    .clickable {
                                        viewModel.logMood(label, moodNoteText)
                                        moodNoteText = ""
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(label, color = if (isSelected) color else TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (moodEntries.isNotEmpty()) {
                        Text("Mood Timeline Logs:", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        moodEntries.take(3).forEach { entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("• ${entry.date}: ${entry.mood}", color = TextGray, fontSize = 11.sp)
                                if (entry.note.isNotEmpty()) {
                                    Text(entry.note, color = TextMuted, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Stat Cards Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatMetricBox(
                        title = "Player Level",
                        value = "Lvl ${progress.level}",
                        subtitle = "Total: ${progress.totalXP} XP",
                        icon = Icons.Default.MilitaryTech,
                        color = ElectricIndigo,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricBox(
                        title = "Active Streak",
                        value = "${progress.currentStreak} Days",
                        subtitle = "Best: ${progress.longestStreak} days",
                        icon = Icons.Default.LocalFireDepartment,
                        color = QuestAmber,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatMetricBox(
                        title = "Quests Finished",
                        value = "${progress.completedMissionCount}",
                        subtitle = "Cumulative Wins",
                        icon = Icons.Default.AssignmentTurnedIn,
                        color = EmeraldGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricBox(
                        title = "XP Gained Today",
                        value = "+$xpToday XP",
                        subtitle = "Weekly: +$xpThisWeek XP",
                        icon = Icons.Default.AddCircle,
                        color = RetroCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Weekly Bar Chart
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "WEEKLY XP HARVEST",
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SlateCardBackground)
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "XP Earned Daily (last 7 days)",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "total $xpThisWeek XP",
                            color = RetroCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val maxXPValue = weeklyXpData.maxOf { it.xp }.coerceAtLeast(100)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        weeklyXpData.forEach { xpPoint ->
                            val heightPercent = xpPoint.xp.toFloat() / maxXPValue

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                // XP popover
                                if (xpPoint.xp > 0) {
                                    Text(
                                        text = "${xpPoint.xp}",
                                        color = RetroCyan,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }

                                // Interactive Bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight(heightPercent.coerceAtLeast(0.04f))
                                        .width(18.dp)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    RetroCyan,
                                                    ElectricIndigo
                                                )
                                            )
                                        )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                val dayLabel = xpPoint.date.dayOfWeek.getDisplayName(
                                    TextStyle.SHORT,
                                    Locale.getDefault()
                                )
                                Text(
                                    text = dayLabel,
                                    color = TextGray,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // High skill breakdown details
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "TROPHY BREAKDOWN",
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SlateCardBackground)
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    LeaderInfoRow(
                        label = "Highest Rank Specialty",
                        value = bestSkill.first,
                        score = "${bestSkill.second} XP",
                        color = ElectricIndigo,
                        icon = Icons.Default.Star
                    )
                    Divider(color = DeepSpaceBackground, modifier = Modifier.padding(vertical = 12.dp))
                    LeaderInfoRow(
                        label = "Most Repeated Domain",
                        value = mostCompletedCategory.first,
                        score = "${mostCompletedCategory.second} Victories",
                        color = EmeraldGreen,
                        icon = Icons.Default.CheckCircle
                    )
                }
            }
        }
    }
}

data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
