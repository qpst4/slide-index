package com.slideindex.app.settings

import com.slideindex.app.R

enum class AppLaunchPolicy(
    val id: Int,
    val titleRes: Int,
    val descRes: Int,
) {
    ALWAYS_FULLSCREEN(
        id = 0,
        titleRes = R.string.launch_policy_always_fullscreen,
        descRes = R.string.launch_policy_always_fullscreen_desc,
    ),
    ALWAYS_FREE_WINDOW(
        id = 1,
        titleRes = R.string.launch_policy_always_free_window,
        descRes = R.string.launch_policy_always_free_window_desc,
    ),
    FULLSCREEN_LONG_PRESS_FREE_WINDOW(
        id = 2,
        titleRes = R.string.launch_policy_fullscreen_long_press_free_window,
        descRes = R.string.launch_policy_fullscreen_long_press_free_window_desc,
    ),
    FREE_WINDOW_LONG_PRESS_FULLSCREEN(
        id = 3,
        titleRes = R.string.launch_policy_free_window_long_press_fullscreen,
        descRes = R.string.launch_policy_free_window_long_press_fullscreen_desc,
    ),
    ;

    fun usesLongPress(): Boolean {
        return this == FULLSCREEN_LONG_PRESS_FREE_WINDOW ||
            this == FREE_WINDOW_LONG_PRESS_FULLSCREEN
    }

    companion object {
        fun fromId(id: Int): AppLaunchPolicy =
            entries.firstOrNull { it.id == id } ?: ALWAYS_FULLSCREEN
    }
}

fun AppSettings.resolvedLaunchPolicy(): AppLaunchPolicy = AppLaunchPolicy.fromId(appLaunchPolicyId)

fun AppSettings.shouldLaunchFullscreen(longPressTriggered: Boolean): Boolean {
    if (!freeWindowEnabled) return true
    return when (resolvedLaunchPolicy()) {
        AppLaunchPolicy.ALWAYS_FULLSCREEN -> true
        AppLaunchPolicy.ALWAYS_FREE_WINDOW -> false
        AppLaunchPolicy.FULLSCREEN_LONG_PRESS_FREE_WINDOW -> !longPressTriggered
        AppLaunchPolicy.FREE_WINDOW_LONG_PRESS_FULLSCREEN -> longPressTriggered
    }
}

fun AppSettings.effectiveLongPressDurationMs(): Int =
    longPressLaunchDurationMs.coerceIn(250, 900)
