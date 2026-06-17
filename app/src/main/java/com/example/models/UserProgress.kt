package com.example.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Int = 1,
    val username: String = "Player 1",
    val totalXP: Int = 0,
    val level: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastCompletedDate: String = "", // YYYY-MM-DD
    val completedMissionCount: Int = 0,
    val weeklyGoal: Int = 5,
    val onboardingCompleted: Boolean = false,
    val darkTheme: Boolean = true,
    
    val gold: Int = 50,
    val heroClass: String = "Warrior", // Warrior, Mage, Rogue, Scholar, Creator
    val selectedTitle: String = "Novice Adventurer",
    val unlockedTitles: String = "Novice Adventurer",

    val avatarName: String = "Hero",
    val penaltyEnabled: Boolean = false,
    val moodToday: String = "Good", // Great, Good, Okay, Tired, Bad
    val energyToday: Int = 100,

    val rewardsBoughtCount: Int = 0,
    val lifeTimeGoldEarned: Int = 50,
    val completedQuestsCount: Int = 0,
    val habitsCompletedCount: Int = 0,
    val habitsCreatedCount: Int = 0,

    // Skill XP progress fields
    val xpProgramming: Int = 0,
    val xpLinux: Int = 0,
    val xpBasketball: Int = 0,
    val xpFitness: Int = 0,
    val xpSchool: Int = 0,
    val xpReading: Int = 0,
    val xpHealth: Int = 0,
    val xpLife: Int = 0,

    // Skill counts of completed missions
    val countProgramming: Int = 0,
    val countLinux: Int = 0,
    val countBasketball: Int = 0,
    val countFitness: Int = 0,
    val countSchool: Int = 0,
    val countReading: Int = 0,
    val countHealth: Int = 0,
    val countLife: Int = 0
)
