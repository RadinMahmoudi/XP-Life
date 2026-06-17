package com.example.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rewards")
data class Reward(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val coinCost: Int,
    val purchasedCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
