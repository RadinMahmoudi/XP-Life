package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.models.UserProgress
import com.example.screens.*
import com.example.ui.theme.*
import com.example.ui.widgets.ConfettiCelebration
import com.example.utils.VibrationHelper
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.UiEvent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: GameViewModel = viewModel()
            val progressState by viewModel.userProgress.collectAsState()
            val progress = progressState ?: UserProgress()

            MyApplicationTheme(
                darkTheme = progress.darkTheme
            ) {
                val navController = rememberNavController()
                val context = LocalContext.current
                val vibrationHelper = remember(context) { VibrationHelper(context) }
                val snackbarHostState = remember { SnackbarHostState() }

                // Celebrations States
                var showLevelUpCelebrate by remember { mutableStateOf(false) }
                var showAchievementCelebrate by remember { mutableStateOf(false) }
                var showConfettiCelebrate by remember { mutableStateOf(false) }
                var celebrationTitle by remember { mutableStateOf("") }

                LaunchedEffect(key1 = true) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is UiEvent.ShowSnackbar -> {
                                snackbarHostState.showSnackbar(event.message)
                            }
                            is UiEvent.MissionCompleted -> {
                                vibrationHelper.vibrateMissionComplete()
                                showConfettiCelebrate = true
                                Toast.makeText(context, "Mission Complete! ${event.xpRewardText}", Toast.LENGTH_SHORT).show()
                            }
                            UiEvent.LevelUp -> {
                                vibrationHelper.vibrateLevelUp()
                                showLevelUpCelebrate = true
                            }
                            is UiEvent.AchievementUnlocked -> {
                                celebrationTitle = event.achievementTitle
                                showAchievementCelebrate = true
                            }
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (progressState != null) {
                        NavHost(
                            navController = navController,
                            startDestination = if (progress.onboardingCompleted) "main" else "onboarding",
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable("onboarding") {
                                OnboardingScreen(
                                    onStartClick = {
                                        viewModel.completeOnboarding()
                                        navController.navigate("main") {
                                            popUpTo("onboarding") { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable("main") {
                                MainAppContainer(
                                    viewModel = viewModel,
                                    progress = progress,
                                    onNavigateToAddMission = {
                                        navController.navigate("add_mission")
                                    },
                                    onNavigateToMissionDetail = { id ->
                                        navController.navigate("mission_detail/$id")
                                    },
                                    snackbarHostState = snackbarHostState
                                )
                            }

                            composable("add_mission") {
                                AddMissionScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            composable("mission_detail/{missionId}") { backStackEntry ->
                                val missionId = backStackEntry.arguments?.getString("missionId")?.toIntOrNull() ?: 0
                                MissionDetailScreen(
                                    viewModel = viewModel,
                                    missionId = missionId,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                        }
                    } else {
                        // Splash Loading Indicator
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(DeepSpaceBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = ElectricIndigo)
                        }
                    }

                    // LEVEL UP CUSTOM CELEBRATION BOX OVERLAY
                    if (showLevelUpCelebrate) {
                        LevelUpOverlay(
                            level = progress.level,
                            onDismiss = { showLevelUpCelebrate = false }
                        )
                    }

                    // ACHIEVEMENT UNLOCKED CUSTOM CELEBRATION BOX OVERLAY
                    if (showAchievementCelebrate) {
                        AchievementUnlockOverlay(
                            title = celebrationTitle,
                            onDismiss = { showAchievementCelebrate = false }
                        )
                    }

                    // CONFETTI CELEBRATION PARTICLE OVERLAY FOR MISSION COMPLETION
                    ConfettiCelebration(
                        trigger = showConfettiCelebrate,
                        onAnimationEnd = { showConfettiCelebrate = false }
                    )
                }
            }
        }
    }
}

@Composable
fun MainAppContainer(
    viewModel: GameViewModel,
    progress: UserProgress,
    onNavigateToAddMission: () -> Unit,
    onNavigateToMissionDetail: (Int) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var activeTab by remember { mutableStateOf("home") }

    Scaffold(
        containerColor = DeepSpaceBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = SlateCardBackground,
                contentColor = TextWhite,
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("app_bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = activeTab == "home",
                    onClick = { activeTab = "home" },
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepSpaceBackground,
                        selectedTextColor = ElectricIndigo,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = ElectricIndigo
                    ),
                    modifier = Modifier.testTag("nav_tab_home")
                )

                NavigationBarItem(
                    selected = activeTab == "quests",
                    onClick = { activeTab = "quests" },
                    icon = { Icon(imageVector = Icons.Default.List, contentDescription = "Quests") },
                    label = { Text("Quests", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepSpaceBackground,
                        selectedTextColor = RetroCyan,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = RetroCyan
                    ),
                    modifier = Modifier.testTag("nav_tab_quests")
                )

                NavigationBarItem(
                    selected = activeTab == "skills",
                    onClick = { activeTab = "skills" },
                    icon = { Icon(imageVector = Icons.Default.MilitaryTech, contentDescription = "Skills") },
                    label = { Text("Skills", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepSpaceBackground,
                        selectedTextColor = EmeraldGreen,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = EmeraldGreen
                    ),
                    modifier = Modifier.testTag("nav_tab_skills")
                )

                NavigationBarItem(
                    selected = activeTab == "shop",
                    onClick = { activeTab = "shop" },
                    icon = { Icon(imageVector = Icons.Default.Storefront, contentDescription = "Shop") },
                    label = { Text("Shop", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepSpaceBackground,
                        selectedTextColor = QuestAmber,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = QuestAmber
                    ),
                    modifier = Modifier.testTag("nav_tab_shop")
                )

                NavigationBarItem(
                    selected = activeTab == "stats",
                    onClick = { activeTab = "stats" },
                    icon = { Icon(imageVector = Icons.Default.BarChart, contentDescription = "Stats") },
                    label = { Text("Stats", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepSpaceBackground,
                        selectedTextColor = XPBlue,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = XPBlue
                    ),
                    modifier = Modifier.testTag("nav_tab_stats")
                )

                NavigationBarItem(
                    selected = activeTab == "profile",
                    onClick = { activeTab = "profile" },
                    icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepSpaceBackground,
                        selectedTextColor = TextWhite,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = TextWhite
                    ),
                    modifier = Modifier.testTag("nav_tab_profile")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                "home" -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToAddMission = onNavigateToAddMission,
                    onNavigateToMissionDetail = onNavigateToMissionDetail
                )
                "quests" -> QuestsScreen(
                    viewModel = viewModel,
                    onNavigateToAddMission = onNavigateToAddMission,
                    onNavigateToMissionDetail = onNavigateToMissionDetail
                )
                "skills" -> SkillsScreen(
                    viewModel = viewModel
                )
                "shop" -> ShopScreen(
                    viewModel = viewModel
                )
                "stats" -> StatsScreen(
                    viewModel = viewModel
                )
                "profile" -> SettingsScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun LevelUpOverlay(
    level: Int,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f))
            .clickable(onClick = onDismiss)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(SlateCardBackground)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ElectricIndigo.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
                .padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MilitaryTech,
                contentDescription = null,
                tint = RetroCyan,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "LEVEL UP!",
                color = ElectricIndigo,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Congratulations! You have reached level $level of real life RPG. Your profile stats have been buffed.",
                color = TextWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirm Upgrade", color = DeepSpaceBackground, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AchievementUnlockOverlay(
    title: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f))
            .clickable(onClick = onDismiss)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(SlateCardBackground)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            QuestAmber.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
                .padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = QuestAmber,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "TROPHY ACQUIRED!",
                color = QuestAmber,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "You unlocked the epic trophy achievement:",
                color = TextGray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = title,
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = QuestAmber),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Claim Rewards", color = DeepSpaceBackground, fontWeight = FontWeight.Bold)
            }
        }
    }
}
