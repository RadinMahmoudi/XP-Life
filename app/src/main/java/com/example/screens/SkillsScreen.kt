package com.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.UserProgress
import com.example.ui.theme.*
import com.example.ui.widgets.getCategoryIcon
import com.example.viewmodel.GameViewModel

@Composable
fun SkillsScreen(
    viewModel: GameViewModel
) {
    val progressState by viewModel.userProgress.collectAsState()
    val progress = progressState ?: UserProgress()
    val missionsState by viewModel.allMissions.collectAsState()
    val missions = missionsState

    val categoryDifficultyCompletions = remember(missions) {
        val categories = listOf("Programming", "Linux", "Basketball", "Fitness", "School", "Reading", "Health", "Life")
        categories.associateWith { cat ->
            listOf("Easy", "Medium", "Hard").associateWith { diff ->
                missions.filter { m -> m.category == cat && m.difficulty == diff }.sumOf { m ->
                    if (m.completedDates.isEmpty()) 0 else m.completedDates.split(",").count { it.isNotEmpty() }
                }
            }
        }
    }

    val skillsList = listOf(
        "Programming" to (ElectricIndigo to getCategoryIcon("Programming")),
        "Linux" to (RetroCyan to getCategoryIcon("Linux")),
        "Basketball" to (QuestAmber to getCategoryIcon("Basketball")),
        "Fitness" to (EmeraldGreen to getCategoryIcon("Fitness")),
        "School" to (XPBlue to getCategoryIcon("School")),
        "Reading" to (Color(0xFFE11D48) to getCategoryIcon("Reading")),
        "Health" to (Color(0xFF0D9488) to getCategoryIcon("Health")),
        "Life" to (Color(0xFFD97706) to getCategoryIcon("Life"))
    ).map { (name, assets) ->
        val (color, icon) = assets
        val xp = when (name) {
            "Programming" -> progress.xpProgramming
            "Linux" -> progress.xpLinux
            "Basketball" -> progress.xpBasketball
            "Fitness" -> progress.xpFitness
            "School" -> progress.xpSchool
            "Reading" -> progress.xpReading
            "Health" -> progress.xpHealth
            "Life" -> progress.xpLife
            else -> 0
        }
        val completedCount = when (name) {
            "Programming" -> progress.countProgramming
            "Linux" -> progress.countLinux
            "Basketball" -> progress.countBasketball
            "Fitness" -> progress.countFitness
            "School" -> progress.countSchool
            "Reading" -> progress.countReading
            "Health" -> progress.countHealth
            "Life" -> progress.countLife
            else -> 0
        }
        val difficultyMap = categoryDifficultyCompletions[name] ?: emptyMap()
        SkillData(
            name = name,
            xp = xp,
            completedCount = completedCount,
            color = color,
            icon = icon,
            easyCompletions = difficultyMap["Easy"] ?: 0,
            mediumCompletions = difficultyMap["Medium"] ?: 0,
            hardCompletions = difficultyMap["Hard"] ?: 0
        )
    }

    Scaffold(
        containerColor = DeepSpaceBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "SKILL TREE PROGRESSION",
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
            Text(
                text = "Track your dynamic skill level as you conquer real life quests. Every 300 XP raises the respective skill level.\nEasy quests give 20 XP, Medium 50 XP, and Hard 100 XP.",
                color = TextGray,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            // Dynamic grid layout for skills trees
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("skills_grid"),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                items(skillsList) { skill ->
                    SkillProgressCard(skill = skill)
                }
            }
        }
    }
}

@Composable
fun SkillProgressCard(skill: SkillData) {
    val level = (skill.xp / 300) + 1
    val currentXPInLevel = skill.xp % 300
    val progressPercent = currentXPInLevel / 300.0f

    val rankTitle = when {
        level < 3 -> "Novice"
        level in 3..4 -> "Adept"
        level in 5..9 -> "Expert"
        else -> "Grandmaster"
    }

    val milestoneText = when {
        level < 3 -> "Next Milestone: Reach level 3 for 'Adept' class"
        level in 3..4 -> "Next Milestone: Reach level 5 for 'Expert' title"
        level in 5..9 -> "Next Milestone: Reach level 10 for 'Grandmaster' tier"
        else -> "Peak Mastery Unlocked!"
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SlateCardBackground)
            .border(
                1.dp,
                if (level >= 5) QuestAmber.copy(alpha = 0.5f) else Color(0xFF1E293B),
                RoundedCornerShape(20.dp)
            ) // Sharp tactical outline border with gold accents for Expert level+
            .padding(16.dp)
            .testTag("skill_card_${skill.name}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(skill.color.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = skill.icon,
                    contentDescription = null,
                    tint = skill.color,
                    modifier = Modifier.size(18.dp)
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(skill.color.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Lvl $level",
                    color = skill.color,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = skill.name,
            color = TextWhite,
            fontWeight = FontWeight.Black,
            fontSize = 15.sp
        )

        Text(
            text = "Title: $rankTitle",
            color = skill.color,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${skill.xp} total XP",
                color = TextGray,
                fontSize = 11.sp
            )
            Text(
                text = "$currentXPInLevel / 300",
                color = TextGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // High Density custom progress bar with gradient/borders
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(Color(0xFF0F172A).copy(alpha = 0.5f))
                .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.5f), CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progressPercent.coerceAtLeast(0.02f))
                    .clip(CircleShape)
                    .background(skill.color)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Badge Badges
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            BadgeToken(label = "NOV", color = skill.color, unlocked = true)
            BadgeToken(label = "ADP", color = skill.color, unlocked = level >= 3)
            BadgeToken(label = "EXP", color = QuestAmber, unlocked = level >= 5)
            BadgeToken(label = "GMR", color = Color(0xFFA78BFA), unlocked = level >= 10)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = milestoneText,
            color = TextGray,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 12.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${skill.completedCount} missions",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DifficultyPill(label = "E", count = skill.easyCompletions, color = EmeraldGreen)
            DifficultyPill(label = "M", count = skill.mediumCompletions, color = RetroCyan)
            DifficultyPill(label = "H", count = skill.hardCompletions, color = QuestAmber)
        }
    }
}

@Composable
fun BadgeToken(label: String, color: Color, unlocked: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (unlocked) color.copy(alpha = 0.15f) else Color(0xFF1E293B).copy(alpha = 0.3f))
            .border(
                0.5.dp,
                if (unlocked) color.copy(alpha = 0.6f) else Color(0xFF1E293B),
                RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 8.sp,
            fontWeight = FontWeight.Black,
            color = if (unlocked) color else TextMuted
        )
    }
}

@Composable
fun DifficultyPill(label: String, count: Int, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.08f))
            .border(0.5.dp, color.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 8.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "$count",
            color = TextWhite,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

data class SkillData(
    val name: String,
    val xp: Int,
    val completedCount: Int,
    val color: Color,
    val icon: ImageVector,
    val easyCompletions: Int,
    val mediumCompletions: Int,
    val hardCompletions: Int
)
