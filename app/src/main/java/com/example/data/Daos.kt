package com.example.data

import androidx.room.*
import com.example.models.Achievement
import com.example.models.Mission
import com.example.models.UserProgress
import com.example.models.Habit
import com.example.models.Reward
import com.example.models.DailyQuest
import com.example.models.MoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionDao {
    @Query("SELECT * FROM missions ORDER BY id DESC")
    fun getAllMissions(): Flow<List<Mission>>

    @Query("SELECT * FROM missions")
    suspend fun getAllMissionsDirect(): List<Mission>

    @Query("SELECT * FROM missions WHERE id = :id LIMIT 1")
    suspend fun getMissionById(id: Int): Mission?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: Mission): Long

    @Update
    suspend fun updateMission(mission: Mission)

    @Delete
    suspend fun deleteMission(mission: Mission)

    @Query("DELETE FROM missions")
    suspend fun deleteAllMissions()
}

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    fun getUserProgress(): Flow<UserProgress?>

    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    suspend fun getUserProgressDirect(): UserProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProgress(userProgress: UserProgress)

    @Update
    suspend fun updateUserProgress(userProgress: UserProgress)

    @Query("DELETE FROM user_progress")
    suspend fun deleteUserProgress()
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements")
    suspend fun getAllAchievementsDirect(): List<Achievement>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<Achievement>)

    @Update
    suspend fun updateAchievement(achievement: Achievement)

    @Query("UPDATE achievements SET isUnlocked = 0, unlockedAt = 0")
    suspend fun lockAllAchievements()
}

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY id DESC")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits")
    suspend fun getAllHabitsDirect(): List<Habit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits()
}

@Dao
interface RewardDao {
    @Query("SELECT * FROM rewards ORDER BY id DESC")
    fun getAllRewards(): Flow<List<Reward>>

    @Query("SELECT * FROM rewards")
    suspend fun getAllRewardsDirect(): List<Reward>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReward(reward: Reward): Long

    @Update
    suspend fun updateReward(reward: Reward)

    @Delete
    suspend fun deleteReward(reward: Reward)

    @Query("DELETE FROM rewards")
    suspend fun deleteAllRewards()
}

@Dao
interface DailyQuestDao {
    @Query("SELECT * FROM daily_quests ORDER BY id DESC")
    fun getAllDailyQuests(): Flow<List<DailyQuest>>

    @Query("SELECT * FROM daily_quests WHERE date = :date")
    suspend fun getDailyQuestsForDate(date: String): List<DailyQuest>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyQuests(quests: List<DailyQuest>)

    @Update
    suspend fun updateDailyQuest(quest: DailyQuest)

    @Query("DELETE FROM daily_quests")
    suspend fun deleteAllDailyQuests()
}

@Dao
interface MoodEntryDao {
    @Query("SELECT * FROM mood_entries ORDER BY date DESC")
    fun getAllMoodEntries(): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE date = :date LIMIT 1")
    suspend fun getMoodEntryForDate(date: String): MoodEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntry(entry: MoodEntry)

    @Query("DELETE FROM mood_entries")
    suspend fun deleteAllMoodEntries()
}
