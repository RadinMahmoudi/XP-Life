package com.example.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_quests")
data class DailyQuest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val xpBonus: Int,
    val coinBonus: Int,
    val isCompleted: Boolean = false,
    val date: String = "", // YYYY-MM-DD
    val targetType: String = "" // "EXERCISE", "LINUX_PROG", "COMPLETE_THREE", "HEALTH", "GENERIC"
)
