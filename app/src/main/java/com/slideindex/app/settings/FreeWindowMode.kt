package com.slideindex.app.settings

import android.os.Build
import com.slideindex.app.R

enum class FreeWindowMode(
    val id: Int,
    val windowingMode: Int,
    val titleRes: Int,
    val descRes: Int,
) {
    STANDARD(
        id = 0,
        windowingMode = 5,
        titleRes = R.string.free_window_mode_standard,
        descRes = R.string.free_window_mode_standard_desc,
    ),
    COLOROS(
        id = 1,
        windowingMode = 100,
        titleRes = R.string.free_window_mode_coloros,
        descRes = R.string.free_window_mode_coloros_desc,
    ),
    MAGICOS(
        id = 2,
        windowingMode = 102,
        titleRes = R.string.free_window_mode_magicos,
        descRes = R.string.free_window_mode_magicos_desc,
    ),
    ORIGINOS(
        id = 3,
        windowingMode = 5,
        titleRes = R.string.free_window_mode_originos,
        descRes = R.string.free_window_mode_originos_desc,
    ),
    FLYME(
        id = 4,
        windowingMode = 11,
        titleRes = R.string.free_window_mode_flyme,
        descRes = R.string.free_window_mode_flyme_desc,
    ),
    ;

    companion object {
        fun fromId(id: Int): FreeWindowMode =
            entries.firstOrNull { it.id == id } ?: detectDefault()

        fun detectDefault(): FreeWindowMode {
            val manufacturer = Build.MANUFACTURER.lowercase()
            val brand = Build.BRAND.lowercase()
            return when {
                manufacturer.contains("meizu") || brand.contains("meizu") -> FLYME
                manufacturer.contains("xiaomi") || brand.contains("redmi") -> STANDARD
                manufacturer.contains("oppo") || manufacturer.contains("realme") ||
                    brand.contains("oppo") || brand.contains("realme") -> COLOROS
                manufacturer.contains("vivo") || brand.contains("vivo") -> ORIGINOS
                manufacturer.contains("honor") || manufacturer.contains("huawei") ||
                    brand.contains("honor") || brand.contains("huawei") -> MAGICOS
                else -> STANDARD
            }
        }
    }
}

fun AppSettings.resolvedFreeWindowMode(): FreeWindowMode = FreeWindowMode.fromId(freeWindowModeId)
