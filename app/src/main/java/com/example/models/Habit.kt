package com.example.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String = "Life", // "Programming", "Linux", "Basketball", "Fitness", "School", "Reading", "Health", "Life"
    val xpReward: Int = 5,
    val coinReward: Int = 2,
    val completionDates: String = "", // Comma-separated YYYY-MM-DD strings
    val totalCompletions: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
