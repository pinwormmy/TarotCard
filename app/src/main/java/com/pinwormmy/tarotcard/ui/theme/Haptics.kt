package com.pinwormmy.tarotcard.ui.theme

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

val LocalHapticsEnabled = staticCompositionLocalOf { true }

object HapticsPlayer {
    fun cardTap(
        context: Context,
        hapticFeedback: HapticFeedback
    ) {
        vibrateOneShot(context, 16L, 190)
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    fun cardFlip(
        context: Context,
        hapticFeedback: HapticFeedback
    ) {
        vibrateOneShot(context, 32L, 220)
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    fun shuffle(
        context: Context,
        hapticFeedback: HapticFeedback
    ) {
        vibrateOneShot(context, 12L, 180)
        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    fun tripleConfirm(
        context: Context,
        hapticFeedback: HapticFeedback
    ) {
        vibratePattern(
            context,
            longArrayOf(0, 55, 60, 55, 60, 65),
            intArrayOf(0, 255, 0, 255, 0, 255)
        )
        repeat(3) { hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress) }
    }

    private fun vibrateOneShot(context: Context, millis: Long, amplitude: Int): Boolean {
        return vibratePattern(context, longArrayOf(0, millis), intArrayOf(0, amplitude))
    }

    private fun vibratePattern(
        context: Context,
        timings: LongArray,
        amplitudes: IntArray
    ): Boolean {
        return runCatching {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(VibratorManager::class.java)
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            } ?: return false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(timings, -1)
            }
            true
        }.getOrElse { false }
    }
}
