package com.slideindex.app.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "slide_index_settings")

class SettingsRepository(private val context: Context) {
    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            serviceEnabled = prefs[SERVICE_ENABLED] ?: false,
            leftEdgeEnabled = prefs[LEFT_EDGE_ENABLED] ?: true,
            rightEdgeEnabled = prefs[RIGHT_EDGE_ENABLED] ?: true,
            edgeTriggerWidthDp = prefs[EDGE_TRIGGER_WIDTH] ?: 20f,
            triggerTopFraction = prefs[TRIGGER_TOP] ?: 0.30f,
            triggerHeightFraction = prefs[TRIGGER_HEIGHT] ?: 0.38f,
            indexHeightFraction = prefs[INDEX_HEIGHT] ?: 0.42f,
            appsPerRow = prefs[APPS_PER_ROW] ?: 3,
            panelOpacity = prefs[PANEL_OPACITY] ?: 0.95f,
            themeColorArgb = prefs[THEME_COLOR] ?: 0xFF6750A4.toInt(),
        )
    }

    suspend fun setServiceEnabled(enabled: Boolean) = edit { it[SERVICE_ENABLED] = enabled }
    suspend fun setLeftEdgeEnabled(enabled: Boolean) = edit { it[LEFT_EDGE_ENABLED] = enabled }
    suspend fun setRightEdgeEnabled(enabled: Boolean) = edit { it[RIGHT_EDGE_ENABLED] = enabled }
    suspend fun setEdgeTriggerWidthDp(value: Float) = edit { it[EDGE_TRIGGER_WIDTH] = value }
    suspend fun setTriggerTopFraction(value: Float) = edit { it[TRIGGER_TOP] = value }
    suspend fun setTriggerHeightFraction(value: Float) = edit { it[TRIGGER_HEIGHT] = value }
    suspend fun setIndexHeightFraction(value: Float) = edit { it[INDEX_HEIGHT] = value }
    suspend fun setAppsPerRow(value: Int) = edit { it[APPS_PER_ROW] = value.coerceIn(2, 5) }
    suspend fun setPanelOpacity(value: Float) = edit { it[PANEL_OPACITY] = value }
    suspend fun setThemeColor(argb: Int) = edit { it[THEME_COLOR] = argb }

    private suspend fun edit(block: (MutablePreferences) -> Unit) {
        context.dataStore.edit { prefs ->
            block(prefs)
        }
    }

    companion object {
        private val SERVICE_ENABLED = booleanPreferencesKey("service_enabled")
        private val LEFT_EDGE_ENABLED = booleanPreferencesKey("left_edge_enabled")
        private val RIGHT_EDGE_ENABLED = booleanPreferencesKey("right_edge_enabled")
        private val EDGE_TRIGGER_WIDTH = floatPreferencesKey("edge_trigger_width_dp")
        private val TRIGGER_TOP = floatPreferencesKey("trigger_top_fraction")
        private val TRIGGER_HEIGHT = floatPreferencesKey("trigger_height_fraction")
        private val INDEX_HEIGHT = floatPreferencesKey("index_height_fraction")
        private val APPS_PER_ROW = intPreferencesKey("apps_per_row")
        private val PANEL_OPACITY = floatPreferencesKey("panel_opacity")
        private val THEME_COLOR = intPreferencesKey("theme_color_argb")
    }
}
