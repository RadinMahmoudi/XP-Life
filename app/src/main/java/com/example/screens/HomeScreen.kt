package com.example.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.models.DailyQuest
import com.example.models.Habit
import com.example.models.Mission
import com.example.models.UserProgress
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel
import java.time.LocalDate

@Composable
fun HomeScreen(
    viewModel: GameViewModel,
    onNavigateToAddMission: () -> Unit,
    onNavigateToMissionDetail: (Int) -> Unit
) {
    val progressState by viewModel.userProgress.collectAsState()
    val missionsState by viewModel.allMissions.collectAsState()
    val habitsState by viewModel.allHabits.collectAsState()
    val dailyQuestsState by viewModel.allDailyQuests.collectAsState()
    val weeklyCompletions by viewModel.weeklyCompletionsCount.collectAsState()

    val progress = progressState ?: UserProgress()
    val missions = missionsState
    val habits = habitsState
    val dailyQuests = dailyQuestsState

    val todayStr = LocalDate.now().toString()

    Scaffold(
        containerColor = DeepSpaceBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddMission,
                containerColor = ElectricIndigo,
                contentColor = TextWhite,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .testTag("add_mission_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add New Mission",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // 1. Hero Avatar System Profile
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_game_dashboard_1781698724004),
                        contentDescription = "RPG HUD",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        DeepSpaceBackground.copy(alpha = 0.5f),
                                        DeepSpaceBackground
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Avatar circle display based on Hero Class
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(SlateCardBackground)
                                        .border(2.dp, ElectricIndigo, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val icon = when (progress.heroClass) {
                                        "Warrior" -> Icons.Default.Security
                                        "Mage" -> Icons.Default.AutoAwesome
                                        "Rogue" -> Icons.Default.Cyclone
                                        "Scholar" -> Icons.Default.School
                                        else -> Icons.Default.Person
                                    }
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = "Avatar icon",
                                        tint = ElectricIndigo,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = if (progress.avatarName.isBlank()) "Chosen One" else progress.avatarName,
                                        color = TextWhite,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Lvl ${progress.level}  •  ${progress.heroClass}",
                                            color = RetroCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(QuestAmber.copy(alpha = 0.2f))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = progress.selectedTitle,
                                                color = QuestAmber,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }
                                }
                            }

                            // Gold / Streak
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(QuestAmber.copy(alpha = 0.15f))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = QuestAmber, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("${progress.gold}g", color = QuestAmber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("${progress.currentStreak}d", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // 2. XP Progress Card & Mood Indicator
            item {
                val currentXPInLevel = progress.totalXP % 200
                val progressPercent = currentXPInLevel / 200.0f
                val xpLeft = 200 - currentXPInLevel

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF1A1C2C), Color(0xFF11131F))
                            )
                        )
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(24.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text("XP PROGRESS", color = ElectricIndigo, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            Text("${progress.totalXP} Total XP", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("$xpLeft XP to level ${progress.level + 1}", color = TextGray, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F172A))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progressPercent.coerceInLeast(0.03f))
                                .background(Brush.horizontalGradient(listOf(ElectricIndigo, Color(0xFFA855F7))))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Divider(color = Color(0xFF1E293B))

                    Spacer(modifier = Modifier.height(12.dp))

                    // Character Mood & Energy
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val moodIcon = when (progress.moodToday) {
                                "Great" -> Icons.Default.SentimentVerySatisfied
                                "Good" -> Icons.Default.SentimentSatisfiedAlt
                                "Okay" -> Icons.Default.SentimentSatisfied
                                "Tired" -> Icons.Default.SentimentDissatisfied
                                "Bad" -> Icons.Default.SentimentVeryDissatisfied
                                else -> Icons.Default.SentimentSatisfied
                            }
                            Icon(imageVector = moodIcon, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mood: ${progress.moodToday}", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = QuestAmber, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Energy: ${progress.energyToday}%", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 3. Daily Quests Summary
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DAILY COMMISSIONS",
                            color = RetroCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        val solvedCount = dailyQuests.count { it.isCompleted }
                        Text(
                            text = "$solvedCount/3 Cleared",
                            color = TextGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (dailyQuests.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SlateCardBackground)
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No Daily Quests suggested for today.", color = TextGray, fontSize = 12.sp)
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            dailyQuests.forEach { quest ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(SlateCardBackground)
                                        .border(
                                            1.dp,
                                            if (quest.isCompleted) EmeraldGreen.copy(alpha = 0.4f) else Color(0xFF1E293B),
                                            RoundedCornerShape(14.dp)
                                        )
                                        .clickable(enabled = !quest.isCompleted) { viewModel.completeDailyQuest(quest) }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(if (quest.isCompleted) EmeraldGreen else Color(0xFF1F2937))
                                            .border(1.dp, if (quest.isCompleted) EmeraldGreen else Color(0xFF4B5563), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = quest.title,
                                        color = if (quest.isCompleted) TextGray else TextWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text("+${quest.xpBonus} xp", color = ElectricIndigo, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 4. Repeatable Quick Habits
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "QUICK HABITS REPEATABLE",
                        color = EmeraldGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (habits.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SlateCardBackground)
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No habits configured. Create habits in Quests screen!", color = TextGray, fontSize = 12.sp)
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            habits.take(3).forEach { habit ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(SlateCardBackground)
                                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(habit.title, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("Repeated ${habit.totalCompletions} times  •  +${habit.xpReward} XP", color = TextGray, fontSize = 11.sp)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(EmeraldGreen.copy(alpha = 0.15f))
                                            .clickable { viewModel.completeHabit(habit.id) }
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("+1", color = EmeraldGreen, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Active Missions Overview
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "ACTIVE CAMPAIGN MISSIONS",
                        color = Color(0xFFA78BFA),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val activeMissions = missions.filter { !it.isCompletedToday }
                    if (activeMissions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SlateCardBackground)
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("All clear! No missions pending.", color = TextGray, fontSize = 12.sp)
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            activeMissions.take(3).forEach { m ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(SlateCardBackground)
                                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                                        .clickable { onNavigateToMissionDetail(m.id) }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF1F2937))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = m.title,
                                        color = TextWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text("+${m.xpReward} XP", color = RetroCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 6. Skill Preview Progress
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "SKILL LEVEL SYNC",
                        color = QuestAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val programmingLvl = (progress.xpProgramming / 300) + 1
                        val fitnessLvl = (progress.xpFitness / 300) + 1
                        val readingLvl = (progress.xpReading / 300) + 1

                        SkillPreviewCapsule("Programming", programmingLvl, ElectricIndigo, modifier = Modifier.weight(1f))
                        SkillPreviewCapsule("Fitness", fitnessLvl, EmeraldGreen, modifier = Modifier.weight(1f))
                        SkillPreviewCapsule("Reading", readingLvl, Color(0xFFE11D48), modifier = Modifier.weight(1f))
                    }
                }
            }

            // 7. Weekly Recap Preview Card
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(SlateCardBackground, Color(0xFF131524))
                                )
                            )
                            .border(1.dp, QuestAmber.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Timeline, contentDescription = null, tint = QuestAmber)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("WEEKLY RECAP REPORT", color = QuestAmber, fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Weekly completions total: $weeklyCompletions missions. Let's make this week legendary! Your consistency level remains impeccable.",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SkillPreviewCapsule(name: String, level: Int, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SlateCardBackground)
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(name, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text("Lvl $level", color = color, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
