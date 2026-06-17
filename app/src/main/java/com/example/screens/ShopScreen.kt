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
import androidx.compose.material.icons.outlined.ShoppingBag
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.Reward
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

@Composable
fun ShopScreen(viewModel: GameViewModel) {
    val progressState by viewModel.userProgress.collectAsState()
    val rewardsState by viewModel.allRewards.collectAsState()
    val progress = progressState ?: com.example.models.UserProgress()

    var activeShopTab by remember { mutableStateOf(0) } // 0: Tavern Rewards, 1: Title Merchant
    val tabs = listOf("Tavern Rewards", "Title Merchant")

    var showAddRewardDialog by remember { mutableStateOf(false) }

    val merchantTitles = listOf(
        MerchantTitle("Apprentice Mage", 30, "A seeker of high code and esoteric structures."),
        MerchantTitle("Iron Vanguard", 50, "Unflinching, disciplined, and fitness forged."),
        MerchantTitle("Stealth Rogue", 40, "Efficient, silent, executing task by task."),
        MerchantTitle("Tome Keeper", 35, "Possessor of deep academic and self-control knowledge."),
        MerchantTitle("Dungeon Sovereign", 100, "The master ruler of real life RPG systems!")
    )

    Scaffold(
        containerColor = DeepSpaceBackground,
        floatingActionButton = {
            if (activeShopTab == 0) {
                FloatingActionButton(
                    onClick = { showAddRewardDialog = true },
                    containerColor = QuestAmber,
                    contentColor = DeepSpaceBackground,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("shop_add_reward_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Reward Bounty", modifier = Modifier.size(28.dp))
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
                        text = "GOLD TAVERN & MERCHANTS",
                        color = TextGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "The Bazaar Shop",
                        color = TextWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Balance Box
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(QuestAmber.copy(alpha = 0.12f))
                        .border(1.dp, QuestAmber.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Gold Balance",
                        tint = QuestAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${progress.gold}g",
                        color = QuestAmber,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            }

            // Tab bar selector
            ScrollableTabRow(
                selectedTabIndex = activeShopTab,
                containerColor = Color.Transparent,
                contentColor = TextWhite,
                edgePadding = 24.dp,
                divider = {},
                indicator = { tabPositions ->
                    if (tabPositions.isNotEmpty()) {
                        Box(
                            Modifier
                                .tabIndicatorOffset(tabPositions[activeShopTab])
                                .height(3.dp)
                                .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                .background(QuestAmber)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = activeShopTab == index
                    Tab(
                        selected = isSelected,
                        onClick = { activeShopTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Values.extraBold else FontWeight.Values.bold,
                                color = if (isSelected) TextWhite else TextGray
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Body
            Box(modifier = Modifier.weight(1f)) {
                when (activeShopTab) {
                    0 -> TavernRewardsTabContent(
                        rewards = rewardsState,
                        onBuy = { viewModel.buyCustomReward(it.id) },
                        onDelete = { viewModel.deleteReward(it.id) }
                    )
                    1 -> TitleMerchantTabContent(
                        merchantTitles = merchantTitles,
                        unlockedTitles = progress.unlockedTitles.split(",").map { it.trim() },
                        gold = progress.gold,
                        onBuyTitle = { titleName, cost -> viewModel.buyTitle(titleName, cost) }
                    )
                }
            }
        }
    }

    // Add Reward Dialog
    if (showAddRewardDialog) {
        var rewardTitle by remember { mutableStateOf("") }
        var rewardDesc by remember { mutableStateOf("") }
        var coinCostStr by remember { mutableStateOf("15") }

        AlertDialog(
            onDismissRequest = { showAddRewardDialog = false },
            title = {
                Text(
                    "Inscribe Reward Reward",
                    fontWeight = FontWeight.Black,
                    color = TextWhite,
                    fontSize = 20.sp
                )
            },
            containerColor = SlateCardBackground,
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = rewardTitle,
                        onValueChange = { rewardTitle = it },
                        label = { Text("Reward Title", color = TextGray) },
                        placeholder = { Text("e.g., 1 Hour PS5 Play, Eat a cookie", color = TextGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = QuestAmber,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = rewardDesc,
                        onValueChange = { rewardDesc = it },
                        label = { Text("Description", color = TextGray) },
                        placeholder = { Text("What relaxation do you earn?", color = TextGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = QuestAmber,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = coinCostStr,
                        onValueChange = { coinCostStr = it },
                        label = { Text("Gold Coin Cost", color = TextGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = QuestAmber,
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
                        val cost = coinCostStr.toIntOrNull() ?: 15
                        if (rewardTitle.isNotBlank()) {
                            viewModel.addCustomReward(rewardTitle, rewardDesc, cost)
                            showAddRewardDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = QuestAmber)
                ) {
                    Text("Add Bounty", color = DeepSpaceBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddRewardDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }
}

@Composable
fun TavernRewardsTabContent(
    rewards: List<Reward>,
    onBuy: (Reward) -> Unit,
    onDelete: (Reward) -> Unit
) {
    if (rewards.isEmpty()) {
        EmptyShopPlaceholder("No rewards available. Click '+' to add a custom reward!")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(rewards, key = { it.id }) { reward ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SlateCardBackground)
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                        .clickable { onBuy(reward) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(QuestAmber.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = null, tint = QuestAmber)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = reward.title,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (reward.description.isNotEmpty()) {
                            Text(
                                text = reward.description,
                                color = TextGray,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "Redeemed: ${reward.purchasedCount} times",
                            color = Color(0xFFA78BFA),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(QuestAmber)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = DeepSpaceBackground, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${reward.coinCost}g", color = DeepSpaceBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }

                        IconButton(onClick = { onDelete(reward) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252).copy(alpha = 0.62f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TitleMerchantTabContent(
    merchantTitles: List<MerchantTitle>,
    unlockedTitles: List<String>,
    gold: Int,
    onBuyTitle: (String, Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(merchantTitles) { item ->
            val isUnlocked = unlockedTitles.contains(item.name)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SlateCardBackground)
                    .border(
                        1.dp,
                        if (isUnlocked) Color(0xFF1E293B) else QuestAmber.copy(alpha = 0.2f),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable(enabled = !isUnlocked) { onBuyTitle(item.name, item.cost) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isUnlocked) Color(0xFF1F2937) else QuestAmber.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null, tint = if (isUnlocked) TextMuted else QuestAmber)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        color = if (isUnlocked) TextGray else TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = item.description,
                        color = TextGray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isUnlocked) {
                        Text(
                            text = "EQUIPPABLE / OWNED",
                            color = EmeraldGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                if (!isUnlocked) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(QuestAmber)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = DeepSpaceBackground, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${item.cost}g", color = DeepSpaceBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

data class MerchantTitle(
    val name: String,
    val cost: Int,
    val description: String
)

@Composable
fun EmptyShopPlaceholder(msg: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.ShoppingBag,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tavern's Vault",
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
