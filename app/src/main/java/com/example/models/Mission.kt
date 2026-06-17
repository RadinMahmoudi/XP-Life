package com.example.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "missions")
data class Mission(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // "Programming", "Linux", "Basketball", "Fitness", "School", "Reading", "Health", "Life"
    val xpReward: Int,
    val coinReward: Int = 10,
    val difficulty: String, // "Easy", "Medium", "Hard"
    val repeatType: String, // "Once", "Daily", "Weekly"
    val notes: String = "",
    val isCompletedToday: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedDates: String = "" // Comma-separated YYYY-MM-DD strings
)
