package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CompletionResult
import com.example.data.DailyResetService
import com.example.data.GameRepository
import com.example.models.Achievement
import com.example.models.Mission
import com.example.models.UserProgress
import com.example.models.Habit
import com.example.models.Reward
import com.example.models.DailyQuest
import com.example.models.MoodEntry
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GameRepository
    private val dailyResetService: DailyResetService

    val allMissions: StateFlow<List<Mission>>
    val userProgress: StateFlow<UserProgress?>
    val allAchievements: StateFlow<List<Achievement>>
    
    val allHabits: StateFlow<List<Habit>>
    val allRewards: StateFlow<List<Reward>>
    val allDailyQuests: StateFlow<List<DailyQuest>>
    val allMoodEntries: StateFlow<List<MoodEntry>>

    val weeklyCompletionsCount: StateFlow<Int>

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = GameRepository(
            db.missionDao(),
            db.userProgressDao(),
            db.achievementDao(),
            db.habitDao(),
            db.rewardDao(),
            db.dailyQuestDao(),
            db.moodEntryDao()
        )
        dailyResetService = DailyResetService(repository, viewModelScope)

        allMissions = repository.allMissions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        weeklyCompletionsCount = allMissions
            .map { missions ->
                calculateWeeklyCompletions(missions)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )

        userProgress = repository.userProgress.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        allAchievements = repository.allAchievements.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allHabits = repository.allHabits.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allRewards = repository.allRewards.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allDailyQuests = repository.allDailyQuests.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allMoodEntries = repository.allMoodEntries.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.initDefaultAchievements()
            val progress = repository.getUserProgressDirect()
            if (progress == null) {
                repository.insertUserProgress(UserProgress())
            }
            
            // Generate daily quests if needed
            generateDailyQuestsIfNeeded()

            // Initialize default rewards if empty
            initDefaultRewardsIfNeeded()
        }
    }

    fun completeMission(missionId: Int) {
        viewModelScope.launch {
            val result = repository.completeMission(missionId)
            if (result != null) {
                if (result.alreadyCompleted) {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Already completed today!"))
                } else {
                    val mission = repository.getMissionById(missionId)
                    val xpText = mission?.let { "+${it.xpReward} XP for ${it.category}" } ?: ""
                    _uiEvent.emit(UiEvent.MissionCompleted(xpText))

                    if (result.levelUp) {
                        _uiEvent.emit(UiEvent.LevelUp)
                    }

                    result.unlockedAchievements.forEach { title ->
                        _uiEvent.emit(UiEvent.AchievementUnlocked(title))
                    }
                }
            }
        }
    }

    fun addMission(
        title: String,
        category: String,
        difficulty: String,
        repeatType: String,
        notes: String
    ) {
        viewModelScope.launch {
            val xpReward = when (difficulty) {
                "Easy" -> 20
                "Medium" -> 50
                "Hard" -> 100
                else -> 50
            }

            val mission = Mission(
                title = title,
                category = category,
                xpReward = xpReward,
                difficulty = difficulty,
                repeatType = repeatType,
                notes = notes
            )
            repository.insertMission(mission)
            _uiEvent.emit(UiEvent.ShowSnackbar("Mission \"$title\" created successfully!"))
        }
    }

    fun deleteMission(missionId: Int) {
        viewModelScope.launch {
            val mission = repository.getMissionById(missionId)
            if (mission != null) {
                repository.deleteMission(mission)
                _uiEvent.emit(UiEvent.ShowSnackbar("Mission deleted."))
            }
        }
    }

    fun updateMissionNotes(missionId: Int, notes: String) {
        viewModelScope.launch {
            val mission = repository.getMissionById(missionId)
            if (mission != null) {
                repository.updateMission(mission.copy(notes = notes))
                _uiEvent.emit(UiEvent.ShowSnackbar("Quick Note updated!"))
            }
        }
    }

    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            userProgress.value?.let { current ->
                repository.updateUserProgress(current.copy(username = newUsername))
                _uiEvent.emit(UiEvent.ShowSnackbar("Username updated to $newUsername"))
            }
        }
    }

    fun toggleTheme(darkTheme: Boolean) {
        viewModelScope.launch {
            userProgress.value?.let { current ->
                repository.updateUserProgress(current.copy(darkTheme = darkTheme))
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userProgress.value?.let { current ->
                repository.updateUserProgress(current.copy(onboardingCompleted = true))
            } ?: run {
                repository.insertUserProgress(UserProgress(onboardingCompleted = true))
            }
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
            _uiEvent.emit(UiEvent.ShowSnackbar("All progress and data reset!"))
        }
    }

    fun selectHeroClass(newClass: String) {
        viewModelScope.launch {
            userProgress.value?.let { current ->
                repository.updateUserProgress(current.copy(heroClass = newClass))
                _uiEvent.emit(UiEvent.ShowSnackbar("Hero class changed to $newClass!"))
            }
        }
    }

    fun selectTitle(newTitle: String) {
        viewModelScope.launch {
            userProgress.value?.let { current ->
                repository.updateUserProgress(current.copy(selectedTitle = newTitle))
                _uiEvent.emit(UiEvent.ShowSnackbar("Equipped Title: $newTitle!"))
            }
        }
    }

    fun buyTitle(titleName: String, cost: Int): Boolean {
        var success = false
        userProgress.value?.let { current ->
            if (current.gold >= cost) {
                success = true
                val list = current.unlockedTitles.split(",").map { it.trim() }.toMutableList()
                if (!list.contains(titleName)) {
                    list.add(titleName)
                }
                viewModelScope.launch {
                    repository.updateUserProgress(current.copy(
                        gold = current.gold - cost,
                        unlockedTitles = list.joinToString(","),
                        selectedTitle = titleName
                    ))
                    _uiEvent.emit(UiEvent.ShowSnackbar("Purchased & equipped title '$titleName'!"))
                }
            } else {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Insufficient gold! You need $cost Gold."))
                }
            }
        }
        return success
    }

    fun buyReward(rewardName: String, cost: Int): Boolean {
        var success = false
        userProgress.value?.let { current ->
            if (current.gold >= cost) {
                success = true
                viewModelScope.launch {
                    repository.updateUserProgress(current.copy(gold = current.gold - cost))
                    _uiEvent.emit(UiEvent.ShowSnackbar("Redeemed Reward: '$rewardName'! Enjoy!"))
                }
            } else {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Insufficient gold! You need $cost Gold."))
                }
            }
        }
        return success
    }

    fun updateWeeklyGoal(newGoal: Int) {
        viewModelScope.launch {
            userProgress.value?.let { current ->
                repository.updateUserProgress(current.copy(weeklyGoal = newGoal))
                _uiEvent.emit(UiEvent.ShowSnackbar("Weekly goal updated to $newGoal missions"))
            } ?: run {
                repository.insertUserProgress(UserProgress(weeklyGoal = newGoal))
            }
        }
    }

    private fun updateProgressAndCheckAchievements(updated: UserProgress) {
        viewModelScope.launch {
            repository.updateUserProgress(updated)
            val newlyUnlocked = repository.checkAndUnlockAchievements(updated)
            newlyUnlocked.forEach { title ->
                _uiEvent.emit(UiEvent.AchievementUnlocked(title))
            }
        }
    }

    fun updateUsernameAndAvatarName(username: String, avatarName: String) {
        viewModelScope.launch {
            userProgress.value?.let { current ->
                val updated = current.copy(username = username, avatarName = avatarName)
                repository.updateUserProgress(updated)
                _uiEvent.emit(UiEvent.ShowSnackbar("Profile customized: Welcome, $avatarName!"))
            }
        }
    }

    fun togglePenaltySystem(enabled: Boolean) {
        viewModelScope.launch {
            userProgress.value?.let { current ->
                val updated = current.copy(penaltyEnabled = enabled)
                repository.updateUserProgress(updated)
                val msg = if (enabled) "Penalty System Enabled! Act with honor!" else "Penalty System Suspended."
                _uiEvent.emit(UiEvent.ShowSnackbar(msg))
            }
        }
    }

    fun addHabit(title: String, category: String) {
        viewModelScope.launch {
            val habit = Habit(
                title = title,
                category = category,
                xpReward = 10,
                coinReward = 4
            )
            repository.insertHabit(habit)
            userProgress.value?.let { current ->
                val updated = current.copy(habitsCreatedCount = current.habitsCreatedCount + 1)
                updateProgressAndCheckAchievements(updated)
            }
            _uiEvent.emit(UiEvent.ShowSnackbar("Habit \"$title\" created! Repeat to grow."))
        }
    }

    fun completeHabit(habitId: Int) {
        viewModelScope.launch {
            val habitsList = allHabits.value
            val habit = habitsList.find { it.id == habitId } ?: return@launch
            val todayStr = LocalDate.now().toString()
            
            val updatedDates = if (habit.completionDates.isEmpty()) {
                todayStr
            } else {
                "${habit.completionDates},$todayStr"
            }
            val updatedHabit = habit.copy(
                completionDates = updatedDates,
                totalCompletions = habit.totalCompletions + 1
            )
            repository.updateHabit(updatedHabit)

            userProgress.value?.let { current ->
                val xpGained = habit.xpReward
                val goldGained = habit.coinReward
                
                val newTotalXP = current.totalXP + xpGained
                val newLevel = (newTotalXP / 200) + 1
                val isLevelUp = newLevel > current.level

                var xpProg = current.xpProgramming
                var xpLin = current.xpLinux
                var xpBask = current.xpBasketball
                var xpFit = current.xpFitness
                var xpSch = current.xpSchool
                var xpRead = current.xpReading
                var xpHea = current.xpHealth
                var xpLif = current.xpLife

                when (habit.category) {
                    "Programming" -> xpProg += xpGained
                    "Linux" -> xpLin += xpGained
                    "Basketball" -> xpBask += xpGained
                    "Fitness" -> xpFit += xpGained
                    "School" -> xpSch += xpGained
                    "Reading" -> xpRead += xpGained
                    "Health" -> xpHea += xpGained
                    "Life" -> xpLif += xpGained
                }

                val updatedProgress = current.copy(
                    totalXP = newTotalXP,
                    level = newLevel,
                    gold = current.gold + goldGained,
                    lifeTimeGoldEarned = current.lifeTimeGoldEarned + goldGained,
                    habitsCompletedCount = current.habitsCompletedCount + 1,
                    xpProgramming = xpProg,
                    xpLinux = xpLin,
                    xpBasketball = xpBask,
                    xpFitness = xpFit,
                    xpSchool = xpSch,
                    xpReading = xpRead,
                    xpHealth = xpHea,
                    xpLife = xpLif
                )
                
                updateProgressAndCheckAchievements(updatedProgress)
                _uiEvent.emit(UiEvent.MissionCompleted("+$xpGained XP & +$goldGained gold!"))
                if (isLevelUp) {
                    _uiEvent.emit(UiEvent.LevelUp)
                }
            }
        }
    }

    fun deleteHabit(habitId: Int) {
        viewModelScope.launch {
            val habitsList = allHabits.value
            val habit = habitsList.find { it.id == habitId } ?: return@launch
            repository.deleteHabit(habit)
            _uiEvent.emit(UiEvent.ShowSnackbar("Habit deleted."))
        }
    }

    fun addCustomReward(title: String, description: String, cost: Int) {
        viewModelScope.launch {
            val r = Reward(title = title, description = description, coinCost = cost)
            repository.insertReward(r)
            _uiEvent.emit(UiEvent.ShowSnackbar("Reward '$title' placed in shop bounties!"))
        }
    }

    fun buyCustomReward(rewardId: Int) {
        viewModelScope.launch {
            val rewardList = allRewards.value
            val reward = rewardList.find { it.id == rewardId } ?: return@launch
            userProgress.value?.let { current ->
                if (current.gold >= reward.coinCost) {
                    val updatedReward = reward.copy(purchasedCount = reward.purchasedCount + 1)
                    repository.updateReward(updatedReward)

                    val updatedProgress = current.copy(
                        gold = current.gold - reward.coinCost,
                        rewardsBoughtCount = current.rewardsBoughtCount + 1
                    )
                    updateProgressAndCheckAchievements(updatedProgress)
                    _uiEvent.emit(UiEvent.ShowSnackbar("Purchased Reward: '${reward.title}'! Clean rest well!"))
                } else {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Keep questing! You need ${reward.coinCost} Coins."))
                }
            }
        }
    }

    fun deleteReward(rewardId: Int) {
        viewModelScope.launch {
            val rewardList = allRewards.value
            val reward = rewardList.find { it.id == rewardId } ?: return@launch
            repository.deleteReward(reward)
            _uiEvent.emit(UiEvent.ShowSnackbar("Reward bounty retired."))
        }
    }

    fun generateDailyQuestsIfNeeded() {
        viewModelScope.launch {
            val todayStr = LocalDate.now().toString()
            val existing = repository.getDailyQuestsForDate(todayStr)
            if (existing.isEmpty()) {
                val dayOfWeek = LocalDate.now().dayOfWeek.value
                val easy = DailyQuest(title = "Daily Focus Check-in", description = "Log today's mood and check energy levels", xpBonus = 30, coinBonus = 10, date = todayStr, targetType = "HEALTH")
                val medium = DailyQuest(title = "Active Habit Builder", description = "Perform at least 1 habit completion today", xpBonus = 60, coinBonus = 20, date = todayStr, targetType = "HABIT")
                val hard = DailyQuest(title = "Triple Threat Sentinel", description = "Complete any 3 actions (missions or habits) today", xpBonus = 120, coinBonus = 40, date = todayStr, targetType = "COMPLETE_THREE")
                
                repository.insertDailyQuests(listOf(easy, medium, hard))
            }
        }
    }

    fun completeDailyQuest(quest: DailyQuest) {
        if (quest.isCompleted) return
        viewModelScope.launch {
            repository.updateDailyQuest(quest.copy(isCompleted = true))
            userProgress.value?.let { current ->
                val xpGained = quest.xpBonus
                val goldGained = quest.coinBonus
                val newTotalXP = current.totalXP + xpGained
                val newLevel = (newTotalXP / 200) + 1
                val isLevelUp = newLevel > current.level

                val updatedProgress = current.copy(
                    totalXP = newTotalXP,
                    level = newLevel,
                    gold = current.gold + goldGained,
                    lifeTimeGoldEarned = current.lifeTimeGoldEarned + goldGained,
                    completedQuestsCount = current.completedQuestsCount + 1
                )
                updateProgressAndCheckAchievements(updatedProgress)
                _uiEvent.emit(UiEvent.MissionCompleted("Quest Completed! +$xpGained XP & +$goldGained coins!"))
                if (isLevelUp) {
                    _uiEvent.emit(UiEvent.LevelUp)
                }
            }
        }
    }

    fun initDefaultRewardsIfNeeded() {
        viewModelScope.launch {
            val existing = repository.getAllRewardsDirect()
            if (existing.isEmpty()) {
                val defaults = listOf(
                    Reward(title = "30 mins Gaming", description = "Spend gold to play your favorite game guilt-free!", coinCost = 15),
                    Reward(title = "Guilt-free YouTube Video", description = "Spend gold to watch high quality videos!", coinCost = 10),
                    Reward(title = "Indulgent Cheat Snack/Drink", description = "Enjoy a high priority sweet treats with gold!", coinCost = 20),
                    Reward(title = "Power Nap / Deep Rest", description = "30-minute afternoon nap with fully cleared core!", coinCost = 15),
                    Reward(title = "Epic Movie Night Marathon", description = "Buy yourself a premium streaming session!", coinCost = 40)
                )
                for (r in defaults) {
                    repository.insertReward(r)
                }
            }
        }
    }

    fun logMood(mood: String, note: String = "") {
        viewModelScope.launch {
            val todayStr = LocalDate.now().toString()
            val entry = MoodEntry(date = todayStr, mood = mood, note = note)
            repository.insertMoodEntry(entry)
            userProgress.value?.let { current ->
                val energy = when (mood) {
                    "Great" -> 100
                    "Good" -> 80
                    "Okay" -> 60
                    "Tired" -> 40
                    "Bad" -> 20
                    else -> 60
                }
                val updatedProgress = current.copy(moodToday = mood, energyToday = energy)
                repository.updateUserProgress(updatedProgress)
                _uiEvent.emit(UiEvent.ShowSnackbar("Mood check-in logged: Today is a $mood day."))
            }
        }
    }

    fun exportDataAsJson(): String {
        val prog = userProgress.value ?: UserProgress()
        return """
        {
          "username": "${prog.username}",
          "avatarName": "${prog.avatarName}",
          "totalXP": ${prog.totalXP},
          "level": ${prog.level},
          "gold": ${prog.gold},
          "lastCompletedDate": "${prog.lastCompletedDate}",
          "currentStreak": ${prog.currentStreak},
          "longestStreak": ${prog.longestStreak},
          "heroClass": "${prog.heroClass}",
          "selectedTitle": "${prog.selectedTitle}",
          "unlockedTitles": "${prog.unlockedTitles}",
          "penaltyEnabled": ${prog.penaltyEnabled},
          "moodToday": "${prog.moodToday}",
          "energyToday": ${prog.energyToday},
          "lifeTimeGoldEarned": ${prog.lifeTimeGoldEarned},
          "completedMissionCount": ${prog.completedMissionCount},
          "rewardsBoughtCount": ${prog.rewardsBoughtCount},
          "completedQuestsCount": ${prog.completedQuestsCount},
          "habitsCompletedCount": ${prog.habitsCompletedCount},
          "habitsCreatedCount": ${prog.habitsCreatedCount},
          "xpProgramming": ${prog.xpProgramming},
          "xpLinux": ${prog.xpLinux},
          "xpBasketball": ${prog.xpBasketball},
          "xpFitness": ${prog.xpFitness},
          "xpSchool": ${prog.xpSchool},
          "xpReading": ${prog.xpReading},
          "xpHealth": ${prog.xpHealth},
          "xpLife": ${prog.xpLife}
        }
        """.trimIndent()
    }

    fun importDataFromJson(json: String): Boolean {
        return try {
            val clean = json.trim().removePrefix("{").removeSuffix("}").trim()
            val map = clean.split(",").associate { line ->
                val index = line.indexOf(":")
                if (index != -1) {
                    val key = line.substring(0, index).trim().removeSurrounding("\"")
                    val value = line.substring(index + 1).trim().removeSurrounding("\"")
                    key to value
                } else {
                    "" to ""
                }
            }

            val username = map["username"] ?: "Player 1"
            val avatarName = map["avatarName"] ?: "Hero"
            val totalXP = map["totalXP"]?.toIntOrNull() ?: 0
            val level = map["level"]?.toIntOrNull() ?: 1
            val gold = map["gold"]?.toIntOrNull() ?: 50
            val lastCompletedDate = map["lastCompletedDate"] ?: ""
            val currentStreak = map["currentStreak"]?.toIntOrNull() ?: 0
            val longestStreak = map["longestStreak"]?.toIntOrNull() ?: 0
            val heroClass = map["heroClass"] ?: "Warrior"
            val selectedTitle = map["selectedTitle"] ?: "Novice Adventurer"
            val unlockedTitles = map["unlockedTitles"] ?: "Novice Adventurer"
            val penaltyEnabled = map["penaltyEnabled"]?.toBooleanStrictOrNull() ?: false
            val moodToday = map["moodToday"] ?: "Good"
            val energyToday = map["energyToday"]?.toIntOrNull() ?: 100
            val lifeTimeGoldEarned = map["lifeTimeGoldEarned"]?.toIntOrNull() ?: 50
            val completedMissionCount = map["completedMissionCount"]?.toIntOrNull() ?: 0
            val rewardsBoughtCount = map["rewardsBoughtCount"]?.toIntOrNull() ?: 0
            val completedQuestsCount = map["completedQuestsCount"]?.toIntOrNull() ?: 0
            val habitsCompletedCount = map["habitsCompletedCount"]?.toIntOrNull() ?: 0
            val habitsCreatedCount = map["habitsCreatedCount"]?.toIntOrNull() ?: 0

            viewModelScope.launch {
                val current = userProgress.value ?: UserProgress()
                val updated = current.copy(
                    username = username,
                    avatarName = avatarName,
                    totalXP = totalXP,
                    level = level,
                    gold = gold,
                    lastCompletedDate = lastCompletedDate,
                    currentStreak = currentStreak,
                    longestStreak = longestStreak,
                    heroClass = heroClass,
                    selectedTitle = selectedTitle,
                    unlockedTitles = unlockedTitles,
                    penaltyEnabled = penaltyEnabled,
                    moodToday = moodToday,
                    energyToday = energyToday,
                    lifeTimeGoldEarned = lifeTimeGoldEarned,
                    completedMissionCount = completedMissionCount,
                    rewardsBoughtCount = rewardsBoughtCount,
                    completedQuestsCount = completedQuestsCount,
                    habitsCompletedCount = habitsCompletedCount,
                    habitsCreatedCount = habitsCreatedCount
                )
                repository.updateUserProgress(updated)
                _uiEvent.emit(UiEvent.ShowSnackbar("JSON Data imported successfully! Your hero is updated!"))
            }
            true
        } catch (e: Exception) {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowSnackbar("Invalid JSON format. Please try again!"))
            }
            false
        }
    }

    fun equipTitle(title: String) {
        viewModelScope.launch {
            userProgress.value?.let { current ->
                val updated = current.copy(selectedTitle = title)
                repository.updateUserProgress(updated)
                _uiEvent.emit(UiEvent.ShowSnackbar("Title equipped: '$title'!"))
            }
        }
    }

    private fun calculateWeeklyCompletions(missions: List<Mission>): Int {
        val today = LocalDate.now()
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        var count = 0
        for (mission in missions) {
            val dates = mission.completedDates.split(",")
            for (dateStr in dates) {
                if (dateStr.isNotBlank()) {
                    try {
                        val date = LocalDate.parse(dateStr)
                        if (!date.isBefore(startOfWeek) && !date.isAfter(endOfWeek)) {
                            count++
                        }
                    } catch (e: Exception) {
                        // ignore malformed dates
                    }
                }
            }
        }
        return count
    }
}

sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
    data class MissionCompleted(val xpRewardText: String) : UiEvent
    object LevelUp : UiEvent
    data class AchievementUnlocked(val achievementTitle: String) : UiEvent
}
