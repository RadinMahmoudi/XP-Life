package com.example.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.widgets.MissionWidget
import com.example.viewmodel.GameViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionDetailScreen(
    viewModel: GameViewModel,
    missionId: Int,
    onNavigateBack: () -> Unit
) {
    val missionsState by viewModel.allMissions.collectAsState()
    val mission = missionsState.find { it.id == missionId }

    if (mission == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepSpaceBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Quest not found or has been deleted.", color = TextWhite)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onNavigateBack) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    val diffColor = when (mission.difficulty) {
        "Easy" -> EmeraldGreen
        "Medium" -> RetroCyan
        "Hard" -> QuestAmber
        else -> ElectricIndigo
    }

    val createdDateFormatted = remember(mission.createdAt) {
        try {
            val instant = Instant.ofEpochMilli(mission.createdAt)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.systemDefault())
            formatter.format(instant)
        } catch (e: Exception) {
            "N/A"
        }
    }

    val completionsList = remember(mission.completedDates) {
        if (mission.completedDates.isBlank()) emptyList()
        else mission.completedDates.split(",").filter { it.isNotBlank() }
    }

    Scaffold(
        containerColor = DeepSpaceBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "QUEST DETAILS",
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("mission_detail_back_button")
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
            contentPadding = PaddingValues(bottom = 50.dp)
        ) {
            // Quest Header Card representation
            item {
                Spacer(modifier = Modifier.height(16.dp))
                val todayStr = remember { LocalDate.now().toString() }
                val isCompleted = mission.completedDates.split(",").contains(todayStr) || (mission.repeatType == "Once" && mission.completedDates.isNotEmpty())

                MissionWidget(
                    mission = mission,
                    isCompleted = isCompleted,
                    onCompleteToggle = { viewModel.completeMission(mission.id) }
                )
            }

            // Optional Notes Card (Interactive Quick Jnr / Notes Pad)
            item {
                Spacer(modifier = Modifier.height(24.dp))
                var noteText by remember(mission.notes) { mutableStateOf(mission.notes) }
                val isModified = noteText != mission.notes

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QUICK INTEL & SUB-TASKS",
                        color = TextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    if (isModified) {
                        Text(
                            text = "UNSAVED CHANGES",
                            color = QuestAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SlateCardBackground)
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        placeholder = { Text("Jot down quick context, strategies, sub-tasks or reminders here...", color = TextMuted, fontSize = 13.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quick_note_input"),
                        minLines = 3,
                        maxLines = 8,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = ElectricIndigo,
                            unfocusedBorderColor = Color(0xFF0F172A),
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        )
                    )

                    if (isModified) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                viewModel.updateMissionNotes(mission.id, noteText)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("save_quick_note_button"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricIndigo
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = null,
                                    tint = TextWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Save Quick Note",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Metadata info
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "METRICS",
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SlateCardBackground)
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    MetricRow(label = "Registered on", value = createdDateFormatted)
                    Divider(color = DeepSpaceBackground, modifier = Modifier.padding(vertical = 12.dp))
                    MetricRow(label = "Total Victories", value = "${completionsList.size} completions")
                }
            }

            // Completion history
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "VICTORY RECORDS (${completionsList.size})",
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (completionsList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SlateCardBackground)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No completions recorded yet for this quest.",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SlateCardBackground)
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        completionsList.reversed().forEach { date ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Success",
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = date,
                                        color = TextWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Text(
                                    text = "SUCCESS",
                                    color = EmeraldGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }

            // Trigger Buttons: Complete, and Delete
            item {
                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    onClick = {
                        viewModel.completeMission(mission.id)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("detail_complete_mission_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldGreen
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = DeepSpaceBackground
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mark as Completed (+${mission.xpReward} XP)",
                            color = DeepSpaceBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        viewModel.deleteMission(mission.id)
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("detail_delete_mission_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                            )
                        )
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Archive / Delete Quest",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailInfoBadge(
    label: String,
    value: String,
    tint: Color
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DeepSpaceBackground)
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = label.uppercase(),
            color = TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            color = tint,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MetricRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextGray,
            fontSize = 13.sp
        )
        Text(
            text = value,
            color = TextWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}
