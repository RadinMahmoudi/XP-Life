package com.example.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

class VibrationHelper(private val context: Context) {

    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            Log.e("VibrationHelper", "Failed to obtain Vibrator service", e)
            null
        }
    }

    /**
     * Triggers a light tactile haptic sensation for standard completed clicks/actions.
     */
    fun vibrateClick() {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(45)
            }
        } catch (e: Exception) {
            Log.e("VibrationHelper", "Vibration failed", e)
        }
    }

    /**
     * Triggers a double-pulse tactile feel for a standard quest or mission completion.
     */
    fun vibrateMissionComplete() {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        try {
            val timings = longArrayOf(0, 60, 50, 60)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // For waveform with timings, -1 means no repeat
                v.vibrate(VibrationEffect.createWaveform(timings, -1))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(timings, -1)
            }
        } catch (e: Exception) {
            Log.e("VibrationHelper", "Vibration failed", e)
        }
    }

    /**
     * Triggers a grandiose, triumphant haptic cadence for leveling up.
     */
    fun vibrateLevelUp() {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        try {
            val timings = longArrayOf(0, 120, 80, 120, 80, 250)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createWaveform(timings, -1))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(timings, -1)
            }
        } catch (e: Exception) {
            Log.e("VibrationHelper", "Vibration failed", e)
        }
    }
}
