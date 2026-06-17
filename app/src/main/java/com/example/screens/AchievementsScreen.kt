package com.example.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.example.models.Achievement
import com.example.models.UserProgress
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AchievementsScreen(
    viewModel: GameViewModel
) {
    val achievementsState by viewModel.allAchievements.collectAsState()
    val progressState by viewModel.userProgress.collectAsState()

    val achievements = achievementsState
    val progress = progressState ?: UserProgress()

    val unlockedCount = achievements.count { it.isUnlocked }
    var activeSubTab by remember { mutableStateOf("trophies") } // "trophies" or "shop"
    val context = LocalContext.current

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

            // TOP NAVIGATION CHIPS FOR SUB-TABS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SlateCardBackground)
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "trophies" to "🏆  Trophies",
                    "shop" to "🪙  Merchant Shop"
                ).forEach { (tabId, label) ->
                    val isSelected = activeSubTab == tabId
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) ElectricIndigo else Color.Transparent)
                            .clickable { activeSubTab = tabId }
                            .testTag("ach_subtab_$tabId")
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) TextWhite else TextGray,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (activeSubTab == "trophies") {
                // TROPHIES / ACHIEVEMENTS LIST
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ACHIEVEMENTS",
                            color = TextWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Unlock status and game history trophies.",
                            color = TextGray,
                            fontSize = 12.sp
                        )
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(QuestAmber.copy(alpha = 0.15f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$unlockedCount / ${achievements.size} Unlocked",
                            color = QuestAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Completion status bar
                val progressPercent = if (achievements.isEmpty()) 0f else unlockedCount.toFloat() / achievements.size
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F172A).copy(alpha = 0.5f))
                        .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.5f), CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progressPercent.coerceAtLeast(0.02f))
                            .clip(CircleShape)
                            .background(QuestAmber)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (achievements.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ElectricIndigo)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("achievements_list"),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 96.dp)
                    ) {
                        items(achievements) { ach ->
                            AchievementItem(achievement = ach)
                        }
                    }
                }
            } else {
                // GAMIFIED RPG TAVERN SHOP (Class Customize, Titles, Custom Gold Bounties)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 96.dp)
                ) {
                    // GOLD BALANCE CARD
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, QuestAmber.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = SlateCardBackground
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF1E1A11), SlateCardBackground)
                                    )
                                )
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "GOLD COIN BALANCE",
                                    color = TextGray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = "Gold Coin",
                                        tint = QuestAmber,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${progress.gold} Gold",
                                        color = QuestAmber,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            // Active custom badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ElectricIndigo.copy(alpha = 0.2f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = progress.selectedTitle,
                                    color = ElectricIndigo,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // HERO CLASSES CARD
                    Text(
                        text = "CHOOSE YOUR RPG HERO CLASS",
                        color = TextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val heroClasses = listOf(
                        Triple("Warrior", "⚔️ Warrior Path", "Increases stamina, physical strength & active exercise goals."),
                        Triple("Mage", "🔮 Mage Path", "Focuses on computer science, logic, Linux & mental algorithms."),
                        Triple("Rogue", "🗡️ Rogue Path", "Agile habits completion, speed chores organizer & quick list tasks."),
                        Triple("Scholar", "📚 Scholar Path", "Academics, reading literature, research & knowledge collection."),
                        Triple("Creator", "🎨 Creator Path", "Design arts, audio engineering, projects building & visual crafts.")
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(SlateCardBackground)
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        heroClasses.forEach { (clsName, label, description) ->
                            val isSelected = progress.heroClass == clsName
                            val classColor = when (clsName) {
                                "Warrior" -> Color(0xFFFF4E4E)
                                "Mage" -> ElectricIndigo
                                "Rogue" -> EmeraldGreen
                                "Scholar" -> RetroCyan
                                "Creator" -> QuestAmber
                                else -> TextWhite
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) classColor.copy(alpha = 0.12f) else Color.Transparent)
                                    .border(
                                        1.dp,
                                        if (isSelected) classColor.copy(alpha = 0.4f) else Color.Transparent,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.selectHeroClass(clsName) }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(classColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Star,
                                        contentDescription = null,
                                        tint = classColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) classColor else TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = description,
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // HERO TITLE MERCHANT (SPEND GOLD TO EQUIP)
                    Text(
                        text = "TITLE MERCHANT MERCHANT (Spend Gold)",
                        color = TextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val merchantTitles = listOf(
                        Pair("Novice Adventurer", 0),
                        Pair("Adept Sentinel", 30),
                        Pair("Logic Weaver", 50),
                        Pair("Chronicle Keeper", 80),
                        Pair("Cyber Artisan", 120),
                        Pair("Apex Sovereign", 200)
                    )

                    val unlockedList = progress.unlockedTitles.split(",").map { it.trim() }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(SlateCardBackground)
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        merchantTitles.forEach { (titleName, cost) ->
                            val isUnlocked = unlockedList.contains(titleName) || cost == 0
                            val isEquipped = progress.selectedTitle == titleName

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = titleName,
                                        color = if (isEquipped) RetroCyan else TextWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (cost == 0) "Free Base Title" else "Requirement: $cost Gold Coins",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                when {
                                    isEquipped -> {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(RetroCyan.copy(alpha = 0.15f))
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "EQUIPPED",
                                                color = RetroCyan,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                    isUnlocked -> {
                                        Button(
                                            onClick = { viewModel.selectTitle(titleName) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF1E293B),
                                                contentColor = TextWhite
                                            ),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("EQUIP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    else -> {
                                        Button(
                                            onClick = {
                                                viewModel.buyTitle(titleName, cost)
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = QuestAmber,
                                                contentColor = Color.Black
                                            ),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.MonetizationOn,
                                                    contentDescription = null,
                                                    tint = Color.Black,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("BUY $cost", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                            }
                                        }
                                    }
                                }
                            }
                            if (cost != 200) {
                                Divider(color = DeepSpaceBackground, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // HABITICA STYLE REWARD BOUNTIES (offline self-defined rewards)
                    Text(
                        text = "TAVERN SELF REWARDS (Redeem with Gold)",
                        color = TextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Spend gold to earn rights to enjoy these guilt-free offline real-world bounties!",
                        color = TextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                    )

                    val bounties = listOf(
                        Triple("Midday Power Nap", "Allows a 30-min power nap during high productivity blocks.", 15),
                        Triple("Satisfying Snack Treatment", "Redeem rights for a treat, sweet snack or soft cheat meal.", 20),
                        Triple("Binge Watch 1 Video Episode", "Redeem rights to binge 1 episode of your current video series.", 30),
                        Triple("Agile Gaming hour", "Allows 1 hour of guilt-free video games or mobile sessions.", 45),
                        Triple("Special Premium Purchase", "Redeem rights for a cute minor impulse purchase/treat.", 150)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(SlateCardBackground)
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        bounties.forEach { (bname, bdesc, bcost) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = bname,
                                        color = TextWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = bdesc,
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Button(
                                    onClick = {
                                        viewModel.buyReward(bname, bcost)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EmeraldGreen,
                                        contentColor = TextWhite
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = TextWhite,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("$bcost g", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            if (bcost != 150) {
                                Divider(color = DeepSpaceBackground)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementItem(achievement: Achievement) {
    val unlockedDate = remember(achievement.unlockedAt) {
        if (achievement.unlockedAt == 0L) ""
        else {
            try {
                val instant = Instant.ofEpochMilli(achievement.unlockedAt)
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    .withZone(ZoneId.systemDefault())
                formatter.format(instant)
            } catch (e: Exception) {
                ""
            }
        }
    }

    val icon = when (achievement.iconName) {
        "emoji_events" -> Icons.Default.EmojiEvents
        "star" -> Icons.Default.Star
        "military_tech" -> Icons.Default.MilitaryTech
        "workspace_premium" -> Icons.Default.WorkspacePremium
        "local_fire_department" -> Icons.Default.LocalFireDepartment
        "whatshot" -> Icons.Default.Whatshot
        "terminal" -> Icons.Default.Terminal
        "code" -> Icons.Default.Code
        "sports_basketball" -> Icons.Default.SportsBasketball
        "fitness_center" -> Icons.Default.FitnessCenter
        "menu_book" -> Icons.Default.MenuBook
        else -> Icons.Default.MilitaryTech
    }

    val cardBorder = if (achievement.isUnlocked) {
        BorderStroke(1.dp, QuestAmber.copy(alpha = 0.5f))
    } else {
        BorderStroke(1.dp, Color(0xFF1E293B))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("achievement_item_${achievement.id}"),
        colors = CardDefaults.cardColors(
            containerColor = SlateCardBackground
        ),
        shape = RoundedCornerShape(16.dp),
        border = cardBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (achievement.isUnlocked) QuestAmber.copy(alpha = 0.15f)
                        else Color(0xFF1C273C).copy(alpha = 0.2f)
                    )
            ) {
                Icon(
                    imageVector = if (achievement.isUnlocked) icon else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (achievement.isUnlocked) QuestAmber else TextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = achievement.title,
                    color = if (achievement.isUnlocked) TextWhite else TextGray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = achievement.description,
                    color = if (achievement.isUnlocked) TextGray else TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                if (achievement.isUnlocked && unlockedDate.isNotBlank()) {
                    Text(
                        text = "Unlocked: $unlockedDate",
                        color = QuestAmber.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            if (achievement.isUnlocked) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Unlocked",
                    tint = QuestAmber,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
