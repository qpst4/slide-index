package com.slideindex.app.settings

data class AppSettings(
    val serviceEnabled: Boolean = false,
    val leftEdgeEnabled: Boolean = true,
    val rightEdgeEnabled: Boolean = true,
    val edgeTriggerWidthDp: Float = 20f,
    val triggerTopFraction: Float = 0.30f,
    val triggerHeightFraction: Float = 0.38f,
    val indexHeightFraction: Float = 0.42f,
    val appsPerRow: Int = 3,
    val panelOpacity: Float = 0.95f,
    val themeColorArgb: Int = 0xFF6750A4.toInt(),
)
