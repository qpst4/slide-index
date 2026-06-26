package com.slideindex.app.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import com.slideindex.app.settings.AppSettings
import com.slideindex.app.settings.HapticStrength
import com.slideindex.app.settings.resolvedHapticStrength

object HapticHelper {
    fun gestureStart(view: View, settings: AppSettings) {
        pulse(view, settings, PulseKind.GESTURE)
    }

    fun letterTick(view: View, settings: AppSettings) {
        pulse(view, settings, PulseKind.LETTER)
    }

    fun appTick(view: View, settings: AppSettings) {
        pulse(view, settings, PulseKind.APP)
    }

    fun confirmLaunch(view: View, settings: AppSettings) {
        pulse(view, settings, PulseKind.CONFIRM)
    }

    fun preview(view: View, settings: AppSettings) {
        pulse(view, settings, PulseKind.CONFIRM)
    }

    private enum class PulseKind {
        GESTURE,
        LETTER,
        APP,
        CONFIRM,
    }

    private data class PulseProfile(
        val durationMs: Long,
        val amplitude: Int,
    )

    private fun pulse(view: View, settings: AppSettings, kind: PulseKind) {
        if (!settings.hapticEnabled) return
        val profile = profileFor(kind, settings.resolvedHapticStrength())
        vibrateOneShot(view.context, profile.durationMs, profile.amplitude)
    }

    private fun profileFor(kind: PulseKind, strength: HapticStrength): PulseProfile {
        return when (strength) {
            HapticStrength.LIGHT -> when (kind) {
                PulseKind.GESTURE -> PulseProfile(8L, 36)
                PulseKind.LETTER -> PulseProfile(4L, 24)
                PulseKind.APP -> PulseProfile(6L, 32)
                PulseKind.CONFIRM -> PulseProfile(10L, 56)
            }
            HapticStrength.MEDIUM -> when (kind) {
                PulseKind.GESTURE -> PulseProfile(14L, 120)
                PulseKind.LETTER -> PulseProfile(10L, 100)
                PulseKind.APP -> PulseProfile(12L, 110)
                PulseKind.CONFIRM -> PulseProfile(18L, 170)
            }
            HapticStrength.STRONG -> when (kind) {
                PulseKind.GESTURE -> PulseProfile(22L, 235)
                PulseKind.LETTER -> PulseProfile(16L, 210)
                PulseKind.APP -> PulseProfile(18L, 220)
                PulseKind.CONFIRM -> PulseProfile(32L, 255)
            }
        }
    }

    private fun vibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private fun vibrateOneShot(context: Context, durationMs: Long, amplitude: Int) {
        val vibrator = vibrator(context) ?: return
        if (!vibrator.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val safeAmplitude = amplitude.coerceIn(1, 255)
            vibrator.vibrate(VibrationEffect.createOneShot(durationMs, safeAmplitude))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }
}
