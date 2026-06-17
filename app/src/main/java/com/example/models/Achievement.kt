package com.example.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String = "XP", // "XP", "Streak", "Skill", "Mission", "Shop", "Quest", "Habit"
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0L,
    val iconName: String
)
