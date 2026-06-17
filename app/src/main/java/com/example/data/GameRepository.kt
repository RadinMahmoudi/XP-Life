package com.example.data

import com.example.models.Achievement
import com.example.models.Mission
import com.example.models.UserProgress
import com.example.models.Habit
import com.example.models.Reward
import com.example.models.DailyQuest
import com.example.models.MoodEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GameRepository(
    private val missionDao: MissionDao,
    private val userProgressDao: UserProgressDao,
    private val achievementDao: AchievementDao,
    private val habitDao: HabitDao,
    private val rewardDao: RewardDao,
    private val dailyQuestDao: DailyQuestDao,
    private val moodEntryDao: MoodEntryDao
) {
    val allMissions: Flow<List<Mission>> = missionDao.getAllMissions()
    val userProgress: Flow<UserProgress?> = userProgressDao.getUserProgress()
    val allAchievements: Flow<List<Achievement>> = achievementDao.getAllAchievements()
    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()
    val allRewards: Flow<List<Reward>> = rewardDao.getAllRewards()
    val allDailyQuests: Flow<List<DailyQuest>> = dailyQuestDao.getAllDailyQuests()
    val allMoodEntries: Flow<List<MoodEntry>> = moodEntryDao.getAllMoodEntries()

    suspend fun getMissionById(id: Int): Mission? = missionDao.getMissionById(id)

    suspend fun insertMission(mission: Mission) = missionDao.insertMission(mission)

    suspend fun updateMission(mission: Mission) = missionDao.updateMission(mission)

    suspend fun deleteMission(mission: Mission) = missionDao.deleteMission(mission)

    suspend fun insertHabit(habit: Habit) = habitDao.insertHabit(habit)
    suspend fun updateHabit(habit: Habit) = habitDao.updateHabit(habit)
    suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)
    suspend fun getAllHabitsDirect() = habitDao.getAllHabitsDirect()

    suspend fun insertReward(reward: Reward) = rewardDao.insertReward(reward)
    suspend fun updateReward(reward: Reward) = rewardDao.updateReward(reward)
    suspend fun deleteReward(reward: Reward) = rewardDao.deleteReward(reward)
    suspend fun getAllRewardsDirect() = rewardDao.getAllRewardsDirect()

    suspend fun insertDailyQuests(quests: List<DailyQuest>) = dailyQuestDao.insertDailyQuests(quests)
    suspend fun updateDailyQuest(quest: DailyQuest) = dailyQuestDao.updateDailyQuest(quest)
    suspend fun getDailyQuestsForDate(date: String) = dailyQuestDao.getDailyQuestsForDate(date)

    suspend fun insertMoodEntry(entry: MoodEntry) = moodEntryDao.insertMoodEntry(entry)
    suspend fun getMoodEntryForDate(date: String) = moodEntryDao.getMoodEntryForDate(date)

    suspend fun getUserProgressDirect(): UserProgress? = userProgressDao.getUserProgressDirect()

    suspend fun insertUserProgress(userProgress: UserProgress) = userProgressDao.insertUserProgress(userProgress)

    suspend fun updateUserProgress(userProgress: UserProgress) = userProgressDao.updateUserProgress(userProgress)

    // Check if the achievements need initialization and insert default list if empty
    suspend fun initDefaultAchievements() {
        val existing = achievementDao.getAllAchievementsDirect()
        if (existing.isEmpty() || existing.size < 15) {
            val defaults = listOf(
                Achievement("first_mission", "First Mission", "Complete your first mission", category = "Mission", iconName = "emoji_events"),
                Achievement("getting_started", "Getting Started", "Earn 100 XP", category = "XP", iconName = "star"),
                Achievement("level_5", "Level 5", "Reach level 5", category = "XP", iconName = "military_tech"),
                Achievement("level_10", "Level 10", "Reach level 10", category = "XP", iconName = "workspace_premium"),
                Achievement("streak_3", "3-Day Streak", "Complete a mission 3 days in a row", category = "Streak", iconName = "local_fire_department"),
                Achievement("streak_7", "7-Day Streak", "Complete a mission 7 days in a row", category = "Streak", iconName = "whatshot"),
                Achievement("linux_beginner", "Linux Beginner", "Earn 300 XP in Linux", category = "Skill", iconName = "terminal"),
                Achievement("code_warrior", "Code Warrior", "Earn 500 XP in Programming", category = "Skill", iconName = "code"),
                Achievement("basketball_beast", "Basketball Beast", "Earn 500 XP in Basketball", category = "Skill", iconName = "sports_basketball"),
                Achievement("fitness_starter", "Fitness Starter", "Complete 5 fitness missions", category = "Skill", iconName = "fitness_center"),
                Achievement("book_mode", "Book Mode", "Complete 5 reading missions", category = "Skill", iconName = "menu_book"),
                
                // New achievements
                Achievement("first_reward", "First Reward Bought", "Buy a custom reward from the tavern shop", category = "Shop", iconName = "workspace_premium"),
                Achievement("coins_100", "100 Coins Earned", "Earn 100 gold coins over your lifetime", category = "Shop", iconName = "star"),
                Achievement("missions_10", "Adept Quester", "Complete 10 total quests/missions", category = "Mission", iconName = "emoji_events"),
                Achievement("missions_50", "Elite Sentinel", "Complete 50 total quests/missions", category = "Mission", iconName = "workspace_premium"),
                Achievement("quest_1", "First Daily Quest", "Complete your first dynamic Daily Quest", category = "Quest", iconName = "local_fire_department"),
                Achievement("quest_7", "Quest Crusader", "Complete 7 dynamic Daily Quests", category = "Quest", iconName = "whatshot"),
                Achievement("habit_1", "First Habit Creator", "Create your first repeatable habit", category = "Habit", iconName = "terminal"),
                Achievement("habit_10", "Habitual Master", "Complete habits 10 times", category = "Habit", iconName = "code")
            )
            // Clear and insert to ensure update of categories
            achievementDao.insertAchievements(defaults)
        }
    }

    suspend fun checkAndUnlockAchievements(progress: UserProgress): List<String> {
        val achievements = achievementDao.getAllAchievementsDirect()
        val unlockedTitles = mutableListOf<String>()

        for (a in achievements) {
            if (a.isUnlocked) continue

            val shouldUnlock = when (a.id) {
                "first_mission" -> progress.completedMissionCount >= 1
                "getting_started" -> progress.totalXP >= 100
                "level_5" -> progress.level >= 5
                "level_10" -> progress.level >= 10
                "streak_3" -> progress.currentStreak >= 3
                "streak_7" -> progress.currentStreak >= 7
                "linux_beginner" -> progress.xpLinux >= 300
                "code_warrior" -> progress.xpProgramming >= 500
                "basketball_beast" -> progress.xpBasketball >= 500
                "fitness_starter" -> progress.countFitness >= 5
                "book_mode" -> progress.countReading >= 5
                
                // New achievements
                "first_reward" -> progress.rewardsBoughtCount >= 1
                "coins_100" -> progress.lifeTimeGoldEarned >= 100
                "missions_10" -> progress.completedMissionCount >= 10
                "missions_50" -> progress.completedMissionCount >= 50
                "quest_1" -> progress.completedQuestsCount >= 1
                "quest_7" -> progress.completedQuestsCount >= 7
                "habit_1" -> progress.habitsCreatedCount >= 1
                "habit_10" -> progress.habitsCompletedCount >= 10
                else -> false
            }

            if (shouldUnlock) {
                achievementDao.updateAchievement(
                    a.copy(isUnlocked = true, unlockedAt = System.currentTimeMillis())
                )
                unlockedTitles.add(a.title)
            }
        }
        return unlockedTitles
    }

    // Complete a mission
    suspend fun completeMission(missionId: Int): CompletionResult? {
        val mission = missionDao.getMissionById(missionId) ?: return null
        val todayStr = LocalDate.now().toString()

        // 1. Check if already completed today
        val isCompletedToday = mission.completedDates.split(",").contains(todayStr)
        if (isCompletedToday) return CompletionResult(levelUp = false, unlockedAchievements = emptyList(), alreadyCompleted = true)

        if (mission.repeatType == "Once" && mission.completedDates.isNotEmpty()) {
            return CompletionResult(levelUp = false, unlockedAchievements = emptyList(), alreadyCompleted = true)
        }

        // 2. Add today's date to history
        val updatedDates = if (mission.completedDates.isEmpty()) {
            todayStr
        } else {
            "${mission.completedDates},$todayStr"
        }

        val updatedMission = mission.copy(
            isCompletedToday = true,
            completedDates = updatedDates
        )
        missionDao.updateMission(updatedMission)

        // 3. fetch or create UserProgress
        var progress = userProgressDao.getUserProgressDirect()
        if (progress == null) {
            progress = UserProgress()
            userProgressDao.insertUserProgress(progress)
            progress = userProgressDao.getUserProgressDirect()!!
        }

        val xpGained = updatedMission.xpReward
        val newTotalXP = progress.totalXP + xpGained

        // "Level system: currentLevel = floor(totalXP / 200) + 1"
        val newLevel = (newTotalXP / 200) + 1
        val isLevelUp = newLevel > progress.level

        // Streak logic
        val yesterdayStr = LocalDate.now().minusDays(1).toString()
        var newStreak = progress.currentStreak

        if (progress.lastCompletedDate == yesterdayStr) {
            newStreak += 1
        } else if (progress.lastCompletedDate == todayStr) {
            // Already completed another mission today, keep current streak
        } else {
            // Broke streak or clean start
            newStreak = 1
        }

        val newLongestStreak = maxOf(progress.longestStreak, newStreak)

        // Update Skill XP and completions
        var xpProg = progress.xpProgramming
        var xpLin = progress.xpLinux
        var xpBask = progress.xpBasketball
        var xpFit = progress.xpFitness
        var xpSch = progress.xpSchool
        var xpRead = progress.xpReading
        var xpHea = progress.xpHealth
        var xpLif = progress.xpLife

        var countProg = progress.countProgramming
        var countLin = progress.countLinux
        var countBask = progress.countBasketball
        var countFit = progress.countFitness
        var countSch = progress.countSchool
        var countRead = progress.countReading
        var countHea = progress.countHealth
        var countLif = progress.countLife

        when (updatedMission.category) {
            "Programming" -> { xpProg += xpGained; countProg += 1 }
            "Linux" -> { xpLin += xpGained; countLin += 1 }
            "Basketball" -> { xpBask += xpGained; countBask += 1 }
            "Fitness" -> { xpFit += xpGained; countFit += 1 }
            "School" -> { xpSch += xpGained; countSch += 1 }
            "Reading" -> { xpRead += xpGained; countRead += 1 }
            "Health" -> { xpHea += xpGained; countHea += 1 }
            "Life" -> { xpLif += xpGained; countLif += 1 }
        }

        val goldGained = xpGained / 2
        val updatedProgress = progress.copy(
            totalXP = newTotalXP,
            level = newLevel,
            currentStreak = newStreak,
            longestStreak = newLongestStreak,
            lastCompletedDate = todayStr,
            completedMissionCount = progress.completedMissionCount + 1,
            gold = progress.gold + goldGained,
            xpProgramming = xpProg,
            xpLinux = xpLin,
            xpBasketball = xpBask,
            xpFitness = xpFit,
            xpSchool = xpSch,
            xpReading = xpRead,
            xpHealth = xpHea,
            xpLife = xpLif,
            countProgramming = countProg,
            countLinux = countLin,
            countBasketball = countBask,
            countFitness = countFit,
            countSchool = countSch,
            countReading = countRead,
            countHealth = countHea,
            countLife = countLif
        )

        userProgressDao.updateUserProgress(updatedProgress)

        // 4. check achievements
        val newlyUnlocked = checkAndUnlockAchievements(updatedProgress)

        return CompletionResult(
            levelUp = isLevelUp,
            unlockedAchievements = newlyUnlocked,
            alreadyCompleted = false
        )
    }

    suspend fun resetAllData() {
        missionDao.deleteAllMissions()
        userProgressDao.deleteUserProgress()
        achievementDao.lockAllAchievements()

        // Insert initial progress
        val defaultProgress = UserProgress(
            username = "Player 1",
            totalXP = 0,
            level = 1,
            currentStreak = 0,
            longestStreak = 0,
            lastCompletedDate = "",
            completedMissionCount = 0,
            onboardingCompleted = false,
            darkTheme = true
        )
        userProgressDao.insertUserProgress(defaultProgress)
        
        // Also insert default mission presets for a beautiful empty state / demo
        val demoMissions = listOf(
            Mission(title = "Study Linux Terminal Commands", category = "Linux", xpReward = 50, difficulty = "Medium", repeatType = "Daily", notes = "Review file manipulation and navigation."),
            Mission(title = "Coding for 1 hour", category = "Programming", xpReward = 100, difficulty = "Hard", repeatType = "Daily", notes = "Build out Compose screens for XP Life."),
            Mission(title = "Practice Basketball Shots", category = "Basketball", xpReward = 50, difficulty = "Medium", repeatType = "Daily", notes = "Take 50 free throws and mid-range shots."),
            Mission(title = "Read a book chapter", category = "Reading", xpReward = 20, difficulty = "Easy", repeatType = "Daily", notes = "Currently reading Clean Code."),
            Mission(title = "Complete high intensity workout", category = "Fitness", xpReward = 50, difficulty = "Medium", repeatType = "Daily", notes = "30 mins HIIT or bodyweight home exercise.")
        )
        for (m in demoMissions) {
            missionDao.insertMission(m)
        }
    }

    suspend fun performDailyResetCheck(): Boolean {
        val todayStr = LocalDate.now().toString()
        val yesterdayStr = LocalDate.now().minusDays(1).toString()
        var updatedAny = false

        // 1. Reset check for recurring missions
        val missions = missionDao.getAllMissionsDirect()
        for (m in missions) {
            if (m.isCompletedToday && m.repeatType != "Once") {
                val lastDate = m.completedDates.split(",").lastOrNull { mDate -> mDate.isNotEmpty() }
                if (lastDate != todayStr) {
                    missionDao.updateMission(m.copy(isCompletedToday = false))
                    updatedAny = true
                }
            }
        }

        // 2. Reset streak if broken
        val progress = userProgressDao.getUserProgressDirect()
        if (progress != null) {
            if (progress.lastCompletedDate.isNotEmpty() &&
                progress.lastCompletedDate != todayStr &&
                progress.lastCompletedDate != yesterdayStr &&
                progress.currentStreak > 0
            ) {
                userProgressDao.updateUserProgress(progress.copy(currentStreak = 0))
                updatedAny = true
            }
        }
        return updatedAny
    }
}

data class CompletionResult(
    val levelUp: Boolean,
    val unlockedAchievements: List<String>,
    val alreadyCompleted: Boolean
)
