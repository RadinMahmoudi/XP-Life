package com.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.widgets.getCategoryIcon
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMissionScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Programming") }
    var selectedDifficulty by remember { mutableStateOf("Medium") }
    var selectedRepeatType by remember { mutableStateOf("Daily") }
    var notes by remember { mutableStateOf("") }

    val categories = listOf("Programming", "Linux", "Basketball", "Fitness", "School", "Reading", "Health", "Life")
    val difficulties = listOf("Easy", "Medium", "Hard")
    val repeatTypes = listOf("Once", "Daily", "Weekly")

    val xpReward = when (selectedDifficulty) {
        "Easy" -> 20
        "Medium" -> 50
        "Hard" -> 100
        else -> 50
    }

    Scaffold(
        containerColor = DeepSpaceBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CREATE QUEST",
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("add_mission_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepSpaceBackground,
                    titleContentColor = TextWhite
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Mission Title Input
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "QUEST NAME",
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("e.g. Study Arch Linux", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mission_title_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = ElectricIndigo,
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedContainerColor = SlateCardBackground,
                        unfocusedContainerColor = SlateCardBackground
                    )
                )
            }

            // Category Selection (Horizontal Row list of dynamic badges)
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "SKILL CATEGORY",
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        val isSelected = category == selectedCategory
                        val color = when (category) {
                            "Programming" -> ElectricIndigo
                            "Linux" -> RetroCyan
                            "Basketball" -> QuestAmber
                            "Fitness" -> EmeraldGreen
                            else -> XPBlue
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) color else SlateCardBackground)
                                .border(1.dp, if (isSelected) color else Color(0xFF1E293B), RoundedCornerShape(12.dp))
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                .testTag("category_chip_$category")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = getCategoryIcon(category),
                                    contentDescription = null,
                                    tint = if (isSelected) DeepSpaceBackground else color,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = category,
                                    color = if (isSelected) DeepSpaceBackground else TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Difficulty Grids (Card level and rewards representation)
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "DIFFICULTY & XP REWARD",
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    difficulties.forEach { diff ->
                        val isSelected = diff == selectedDifficulty
                        val color = when (diff) {
                            "Easy" -> EmeraldGreen
                            "Medium" -> RetroCyan
                            "Hard" -> QuestAmber
                            else -> ElectricIndigo
                        }

                        val xp = when (diff) {
                            "Easy" -> 20
                            "Medium" -> 50
                            "Hard" -> 100
                            else -> 50
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedDifficulty = diff }
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) color else Color(0xFF1E293B),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .testTag("difficulty_card_$diff"),
                            colors = CardDefaults.cardColors(
                                containerColor = SlateCardBackground
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = diff,
                                    color = color,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = null,
                                    tint = XPBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "+$xp XP",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Repeat Options (Once, Daily, Weekly)
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "REPEAT SCHEDULE",
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeatTypes.forEach { type ->
                        val isSelected = type == selectedRepeatType
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) ElectricIndigo else SlateCardBackground)
                                .border(1.dp, if (isSelected) ElectricIndigo else Color(0xFF1E293B), RoundedCornerShape(12.dp))
                                .clickable { selectedRepeatType = type }
                                .padding(vertical = 12.dp)
                                .testTag("repeat_chip_$type")
                        ) {
                            Text(
                                text = type,
                                color = if (isSelected) DeepSpaceBackground else TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Optional Notes Field
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "NOTES / SUBTREES (OPTIONAL)",
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Enter detailed steps, resources or checklists...", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("mission_notes_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = ElectricIndigo,
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedContainerColor = SlateCardBackground,
                        unfocusedContainerColor = SlateCardBackground
                    )
                )
            }

            // Save CTA Button
            item {
                Spacer(modifier = Modifier.height(36.dp))
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addMission(
                                title = title,
                                category = selectedCategory,
                                difficulty = selectedDifficulty,
                                repeatType = selectedRepeatType,
                                notes = notes
                            )
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("save_mission_button"),
                    enabled = title.isNotBlank(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricIndigo,
                        disabledContainerColor = SlateCardBackground
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = if (title.isNotBlank()) DeepSpaceBackground else TextMuted
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add to Mission Journal (+${xpReward} XP)",
                            color = if (title.isNotBlank()) DeepSpaceBackground else TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
