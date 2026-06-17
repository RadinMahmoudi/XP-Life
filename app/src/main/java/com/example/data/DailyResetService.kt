package com.example.data

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

class DailyResetService(
    private val repository: GameRepository,
    private val scope: CoroutineScope
) {
    init {
        // Run immediately on app start to handle any resets needed from when the app was closed
        checkForReset()
        // Start background midnight tracking loop
        startMidnightTimer()
    }

    fun checkForReset() {
        scope.launch(Dispatchers.IO) {
            try {
                Log.d("DailyResetService", "Triggering daily reset verification flow...")
                val wasReset = repository.performDailyResetCheck()
                if (wasReset) {
                    Log.d("DailyResetService", "Daily reset successfully executed: refreshed active statuses & verified streak consistency.")
                } else {
                    Log.d("DailyResetService", "Daily verification completed. No reset was needed at this time.")
                }
            } catch (e: Exception) {
                Log.e("DailyResetService", "Failed to perform daily status check", e)
            }
        }
    }

    private fun startMidnightTimer() {
        scope.launch(Dispatchers.Default) {
            while (true) {
                try {
                    val now = LocalDateTime.now()
                    val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay()
                    val msToMidnight = Duration.between(now, nextMidnight).toMillis()

                    Log.d("DailyResetService", "Time synchronization: next automated calculation in ${msToMidnight / 1000}s.")

                    if (msToMidnight > 0) {
                        // Delay until midnight, plus 1 second cushion to ensure calendar rollover
                        delay(msToMidnight + 1000)
                    } else {
                        delay(60000) // Fall back to checking every minute
                    }

                    Log.d("DailyResetService", "Scheduled milestone reached: executing midnight rollover verification.")
                    checkForReset()
                } catch (e: Exception) {
                    Log.e("DailyResetService", "Exception in automated scheduling cycle, trying again in 5 minutes", e)
                    delay(300000) // retry in 5 minutes
                }
            }
        }
    }
}
