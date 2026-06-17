package com.example.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.UserProgress
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: GameViewModel
) {
    val progressState by viewModel.userProgress.collectAsState()
    val achievementsState by viewModel.allAchievements.collectAsState()
    val progress = progressState ?: UserProgress()
    val achievements = achievementsState

    var showEditIdentityDialog by remember { mutableStateOf(false) }
    var editUsername by remember { mutableStateOf("") }
    var editAvatarName by remember { mutableStateOf("") }

    var showTitleEquipDialog by remember { mutableStateOf(false) }
    var showBackupRestoreDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DeepSpaceBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "THE HERO CODEX",
                    color = TextGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Profile & Settings",
                    color = TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 1. Hero Identity Display Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(SlateCardBackground)
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(ElectricIndigo.copy(alpha = 0.15f))
                                    .border(2.dp, ElectricIndigo, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Face,
                                    contentDescription = null,
                                    tint = ElectricIndigo,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = progress.username,
                                    color = TextWhite,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Hero: ${if (progress.avatarName.isBlank()) "Chosen" else progress.avatarName}",
                                    color = TextGray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(ElectricIndigo.copy(alpha = 0.2f))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(progress.heroClass, color = ElectricIndigo, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(progress.selectedTitle, color = QuestAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        IconButton(
                            onClick = {
                                editUsername = progress.username
                                editAvatarName = progress.avatarName
                                showEditIdentityDialog = true
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E293B))
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile", tint = RetroCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Penalty system toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = if (progress.penaltyEnabled) Color(0xFFFF5252) else TextGray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Penalty System",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Gentle motivational coin deductions if daily commissions are missed.",
                                    color = TextGray,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        Switch(
                            checked = progress.penaltyEnabled,
                            onCheckedChange = { viewModel.togglePenaltySystem(it) }
                        )
                    }
                }
            }

            // 2. Titles Equipper
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTitleEquipDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCardBackground),
                    border = BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null, tint = QuestAmber)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Equip Earned Title", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Current active: ${progress.selectedTitle}", color = TextGray, fontSize = 11.sp)
                            }
                        }

                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                    }
                }
            }

            // 3. Backup & Ledger Restore Card
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showBackupRestoreDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCardBackground),
                    border = BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Backup, contentDescription = null, tint = RetroCyan)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Legacy Ledger Backup", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Export, copy or restore progress via JSON codex.", color = TextGray, fontSize = 11.sp)
                            }
                        }

                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                    }
                }
            }

            // 4. In-house Achievements (Trophies) list!
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "EARNED TROPHIES & BADGES",
                    color = QuestAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            val unlockedIds = progress.unlockedAchievementIds.split(",").map { it.trim() }

            if (achievements.isEmpty()) {
                item {
                    Text("No achievement configurations loaded.", color = TextGray, fontSize = 12.sp)
                }
            } else {
                items(achievements) { ach ->
                    val isUnlocked = unlockedIds.contains(ach.id)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SlateCardBackground)
                            .border(
                                1.dp,
                                if (isUnlocked) QuestAmber.copy(alpha = 0.3f) else Color(0xFF1E293B),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isUnlocked) QuestAmber.copy(alpha = 0.15f) else Color(0xFF1F2937)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isUnlocked) QuestAmber else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ach.title,
                                color = if (isUnlocked) TextWhite else TextGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = ach.description,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        if (isUnlocked) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(QuestAmber.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("UNLOCKED", color = QuestAmber, fontSize = 8.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }

            // 5. System Reset
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { showResetDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("HARD RESET PROGRESS", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = TextWhite)
                }
            }
        }
    }

    // IDENTITY DIALOG
    if (showEditIdentityDialog) {
        var tempUsername by remember { mutableStateOf(editUsername) }
        var tempAvatarName by remember { mutableStateOf(editAvatarName) }

        AlertDialog(
            onDismissRequest = { showEditIdentityDialog = false },
            title = { Text("Customize Identity", fontWeight = FontWeight.Black) },
            containerColor = SlateCardBackground,
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = tempUsername,
                        onValueChange = { tempUsername = it },
                        label = { Text("User/Player Name", color = TextGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricIndigo,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = tempAvatarName,
                        onValueChange = { tempAvatarName = it },
                        label = { Text("Hero Character Code (Avatar)", color = TextGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricIndigo,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempUsername.isNotBlank()) {
                            viewModel.updateUsernameAndAvatarName(tempUsername, tempAvatarName)
                            showEditIdentityDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo)
                ) {
                    Text("Save Changes", color = TextWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditIdentityDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }

    // TITLE EQUIP DIALOG
    if (showTitleEquipDialog) {
        val titlesList = progress.unlockedTitles.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        AlertDialog(
            onDismissRequest = { showTitleEquipDialog = false },
            title = { Text("Equip Title", fontWeight = FontWeight.Bold) },
            containerColor = SlateCardBackground,
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Select a title to display in your character profile:", color = TextGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    if (titlesList.isEmpty()) {
                        Text("No titles unlocked. Study or clear milestones in the shop!", color = TextWhite, fontSize = 13.sp)
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 240.dp)) {
                            items(titlesList) { tName ->
                                val isEquipped = progress.selectedTitle == tName
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isEquipped) QuestAmber.copy(alpha = 0.15f) else Color.Transparent)
                                        .clickable {
                                            viewModel.equipTitle(tName)
                                            showTitleEquipDialog = false
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(tName, color = if (isEquipped) QuestAmber else TextWhite, fontWeight = FontWeight.Bold)
                                    if (isEquipped) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = QuestAmber)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTitleEquipDialog = false }) {
                    Text("Done", color = RetroCyan)
                }
            }
        )
    }

    // RESTORE/BACKUP JSON DIALOG
    if (showBackupRestoreDialog) {
        var restoreText by remember { mutableStateOf("") }
        val generatedText = remember { viewModel.exportDataAsJson() }

        AlertDialog(
            onDismissRequest = { showBackupRestoreDialog = false },
            title = { Text("Ledger Data Backup", fontWeight = FontWeight.Black) },
            containerColor = SlateCardBackground,
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Export text (Copy or save cleanly):", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = generatedText,
                        onValueChange = {},
                        readOnly = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Restore text (Paste valid JSON data here):", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = restoreText,
                        onValueChange = { restoreText = it },
                        placeholder = { Text("Paste exact JSON string here...", color = TextGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (restoreText.isNotBlank()) {
                            if (viewModel.importDataFromJson(restoreText)) {
                                showBackupRestoreDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RetroCyan)
                ) {
                    Text("Apply Restore", color = DeepSpaceBackground)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackupRestoreDialog = false }) {
                    Text("Close", color = TextGray)
                }
            }
        )
    }

    // RESET DIALOG
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Confirm Full Reset?", color = MaterialTheme.colorScheme.error) },
            containerColor = SlateCardBackground,
            text = {
                Text("This action will completely wipe all level progress, streaks, habits, custom rewards and quest accomplishments. This cannot be undone.", color = TextGray)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Wipe Everything", color = TextWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }
}
