package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.models.Achievement
import com.example.models.Mission
import com.example.models.UserProgress
import com.example.models.Habit
import com.example.models.Reward
import com.example.models.DailyQuest
import com.example.models.MoodEntry

@Database(
    entities = [
        Mission::class,
        UserProgress::class,
        Achievement::class,
        Habit::class,
        Reward::class,
        DailyQuest::class,
        MoodEntry::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun missionDao(): MissionDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun achievementDao(): AchievementDao
    abstract fun habitDao(): HabitDao
    abstract fun rewardDao(): RewardDao
    abstract fun dailyQuestDao(): DailyQuestDao
    abstract fun moodEntryDao(): MoodEntryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "xplife_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
