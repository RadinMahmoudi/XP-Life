package com.example.screens

import androidx.compose.animation.*
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
import androidx.compose.material.icons.outlined.AddTask
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.DailyQuest
import com.example.models.Habit
import com.example.models.Mission
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

@Composable
fun QuestsScreen(
    viewModel: GameViewModel,
    onNavigateToAddMission: () -> Unit,
    onNavigateToMissionDetail: (Int) -> Unit
) {
    val missionsState by viewModel.allMissions.collectAsState()
    val dailyQuestsState by viewModel.allDailyQuests.collectAsState()
    val habitsState by viewModel.allHabits.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Daily Quests, 1: Missions, 2: Habits
    val tabs = listOf("Daily Quests", "Missions", "Habits")

    var showAddHabitDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DeepSpaceBackground,
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = onNavigateToAddMission,
                    containerColor = RetroCyan,
                    contentColor = DeepSpaceBackground,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("quests_add_mission_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Mission", modifier = Modifier.size(28.dp))
                }
            } else if (selectedTab == 2) {
                FloatingActionButton(
                    onClick = { showAddHabitDialog = true },
                    containerColor = EmeraldGreen,
                    contentColor = DeepSpaceBackground,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("quests_add_habit_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Habit", modifier = Modifier.size(28.dp))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "JOURNEY LOG",
                        color = TextGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Missions & Quests",
                        color = TextWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SlateCardBackground)
                        .clickable { viewModel.generateDailyQuestsIfNeeded() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sync Daily Quests",
                        tint = RetroCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Scrollable Tab Row Selection
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = TextWhite,
                edgePadding = 24.dp,
                divider = {},
                indicator = { tabPositions ->
                    if (tabPositions.isNotEmpty()) {
                        Box(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .height(3.dp)
                                .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                .background(
                                    when (selectedTab) {
                                        0 -> ElectricIndigo
                                        1 -> RetroCyan
                                        else -> EmeraldGreen
                                    }
                                )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                color = if (isSelected) TextWhite else TextGray
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Content
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> DailyQuestsTabContent(
                        quests = dailyQuestsState,
                        onComplete = { viewModel.completeDailyQuest(it) }
                    )
                    1 -> MissionsTabContent(
                        missions = missionsState,
                        onComplete = { viewModel.completeMission(it.id) },
                        onDelete = { viewModel.deleteMission(it.id) },
                        onEdit = { onNavigateToMissionDetail(it.id) }
                    )
                    2 -> HabitsTabContent(
                        habits = habitsState,
                        onComplete = { viewModel.completeHabit(it.id) },
                        onDelete = { viewModel.deleteHabit(it.id) }
                    )
                }
            }
        }
    }

    // Add Habit Dialog
    if (showAddHabitDialog) {
        var habitTitle by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf("Life") }
        val categories = listOf("Programming", "Linux", "Basketball", "Fitness", "School", "Reading", "Health", "Life")

        AlertDialog(
            onDismissRequest = { showAddHabitDialog = false },
            title = {
                Text(
                    "Enlist Repeatable Habit",
                    fontWeight = FontWeight.Black,
                    color = TextWhite,
                    fontSize = 20.sp
                )
            },
            containerColor = SlateCardBackground,
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = habitTitle,
                        onValueChange = { habitTitle = it },
                        label = { Text("What activity will you repeat?", color = TextGray) },
                        placeholder = { Text("e.g., Code review, Drink water, Stretching", color = TextGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Select Discipline/Category:", fontWeight = FontWeight.Bold, color = TextGray, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Column {
                        categories.chunked(3).forEach { rowList ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowList.forEach { cat ->
                                    val isCatSelected = selectedCategory == cat
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isCatSelected) EmeraldGreen.copy(alpha = 0.2f) else SlateCardBackground)
                                            .border(1.dp, if (isCatSelected) EmeraldGreen else Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                            .clickable { selectedCategory = cat }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cat,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCatSelected) EmeraldGreen else TextGray
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (habitTitle.isNotBlank()) {
                            viewModel.addHabit(habitTitle, selectedCategory)
                            showAddHabitDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    Text("Add Habit", color = DeepSpaceBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddHabitDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }
}

@Composable
fun DailyQuestsTabContent(
    quests: List<DailyQuest>,
    onComplete: (DailyQuest) -> Unit
) {
    if (quests.isEmpty()) {
        EmptyQuestsPlaceholder("No daily quests loaded. Click sync in top right.")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(ElectricIndigo.copy(alpha = 0.15f), Color.Transparent)
                            )
                        )
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = null,
                            tint = ElectricIndigo,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Standard Commission", color = ElectricIndigo, fontWeight = FontWeight.Black, fontSize = 14.sp)
                            Text("Perform these actions today to earn massive bonus XP and coins!", color = TextGray, fontSize = 11.sp)
                        }
                    }
                }
            }

            items(quests) { quest ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SlateCardBackground)
                        .border(
                            1.dp,
                            if (quest.isCompleted) EmeraldGreen.copy(alpha = 0.5f) else Color(0xFF1E293B),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable(enabled = !quest.isCompleted) { onComplete(quest) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (quest.isCompleted) EmeraldGreen else Color(0xFF1F2937))
                            .border(1.dp, if (quest.isCompleted) EmeraldGreen else Color(0xFF4B5563), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (quest.isCompleted) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Done", tint = DeepSpaceBackground, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = quest.title,
                            color = if (quest.isCompleted) TextGray else TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textDecoration = if (quest.isCompleted) TextDecoration.LineThrough else null
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = quest.description,
                            color = TextGray,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("+${quest.xpBonus} XP", color = ElectricIndigo, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        Text("+${quest.coinBonus}g", color = QuestAmber, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MissionsTabContent(
    missions: List<Mission>,
    onComplete: (Mission) -> Unit,
    onDelete: (Mission) -> Unit,
    onEdit: (Mission) -> Unit
) {
    if (missions.isEmpty()) {
        EmptyQuestsPlaceholder("No Active Missions catalogued yet! Click '+' in the bottom right to record one!")
    } else {
        val incomplete = missions.filter { !it.isCompletedToday }
        val completed = missions.filter { it.isCompletedToday }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (incomplete.isNotEmpty()) {
                item {
                    Text("ACTIVE CAMPAIGNS", color = RetroCyan, fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 0.5.sp)
                }

                items(incomplete, key = { it.id }) { mission ->
                    MissionItemCard(
                        mission = mission,
                        onComplete = { onComplete(mission) },
                        onDelete = { onDelete(mission) },
                        onEdit = { onEdit(mission) }
                    )
                }
            }

            if (completed.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("COMPLETED TODAY", color = TextGray, fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 0.5.sp)
                }

                items(completed, key = { it.id }) { mission ->
                    MissionItemCard(
                        mission = mission,
                        onComplete = {},
                        onDelete = { onDelete(mission) },
                        onEdit = { onEdit(mission) }
                    )
                }
            }
        }
    }
}

@Composable
fun MissionItemCard(
    mission: Mission,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SlateCardBackground)
            .border(
                1.dp,
                if (mission.isCompletedToday) EmeraldGreen.copy(alpha = 0.4f) else Color(0xFF1E293B),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Complete Checker
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (mission.isCompletedToday) EmeraldGreen else Color(0xFF1F2937))
                .border(1.dp, if (mission.isCompletedToday) EmeraldGreen else Color(0xFF4B5563), CircleShape)
                .clickable { if (!mission.isCompletedToday) onComplete() },
            contentAlignment = Alignment.Center
        ) {
            if (mission.isCompletedToday) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Done", tint = DeepSpaceBackground, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onEdit() }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val difficultyColor = when (mission.difficulty) {
                    "Easy" -> EmeraldGreen
                    "Medium" -> QuestAmber
                    "Hard" -> Color(0xFFFF5252)
                    else -> TextGray
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(difficultyColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = mission.difficulty,
                        color = difficultyColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = mission.category,
                    color = TextGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = mission.title,
                color = if (mission.isCompletedToday) TextGray else TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                textDecoration = if (mission.isCompletedToday) TextDecoration.LineThrough else null,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text("+${mission.xpReward} XP", color = RetroCyan, fontWeight = FontWeight.Black, fontSize = 12.sp)
                val coinRewardAmt = when (mission.difficulty) {
                    "Easy" -> 5
                    "Medium" -> 15
                    "Hard" -> 30
                    else -> 10
                }
                Text("+${coinRewardAmt}g", color = QuestAmber, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252).copy(alpha = 0.62f))
            }
        }
    }
}

@Composable
fun HabitsTabContent(
    habits: List<Habit>,
    onComplete: (Habit) -> Unit,
    onDelete: (Habit) -> Unit
) {
    if (habits.isEmpty()) {
        EmptyQuestsPlaceholder("No repeatable Habits inscribed! Click '+' below to enlist a good habit loop.")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(habits) { habit ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SlateCardBackground)
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(EmeraldGreen.copy(alpha = 0.15f))
                            .clickable { onComplete(habit) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.PlusOne, contentDescription = "Complete Habit Loop", tint = EmeraldGreen)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = null,
                                tint = EmeraldGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = habit.category,
                                color = TextGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = habit.title,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Repeated: ${habit.totalCompletions} times",
                            color = TextGray,
                            fontSize = 11.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("+${habit.xpReward} XP", color = EmeraldGreen, fontWeight = FontWeight.Black, fontSize = 12.sp)
                            Text("+${habit.coinReward}g", color = QuestAmber, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        IconButton(onClick = { onDelete(habit) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252).copy(alpha = 0.62f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyQuestsPlaceholder(msg: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.MilitaryTech,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Journeyman's Ledger",
            color = TextWhite,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = msg,
            color = TextGray,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}
