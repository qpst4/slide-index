package com.slideindex.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.slideindex.app.R
import com.slideindex.app.settings.AppSettings
import com.slideindex.app.util.PermissionHelper
import kotlin.math.roundToInt

@Composable
fun MainScreen(
    settings: AppSettings,
    overlayGranted: Boolean,
    notificationGranted: Boolean,
    onRequestOverlay: () -> Unit,
    onRequestNotification: () -> Unit,
    onServiceEnabledChange: (Boolean) -> Unit,
    onLeftEdgeChange: (Boolean) -> Unit,
    onRightEdgeChange: (Boolean) -> Unit,
    onEdgeWidthChange: (Float) -> Unit,
    onTriggerTopChange: (Float) -> Unit,
    onTriggerHeightChange: (Float) -> Unit,
    onIndexHeightChange: (Float) -> Unit,
    onAppsPerRowChange: (Int) -> Unit,
    onPanelOpacityChange: (Float) -> Unit,
    onHapticEnabledChange: (Boolean) -> Unit,
    onHapticStrengthChange: (Int) -> Unit,
    onLayoutPreviewStart: () -> Unit,
    onLayoutPreviewStop: () -> Unit,
    onThemeColorChange: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        )

        if (!overlayGranted) {
            PermissionCard(
                title = stringResource(R.string.permission_overlay_title),
                description = stringResource(R.string.permission_overlay_desc),
                onGrant = onRequestOverlay,
            )
        }

        if (!notificationGranted) {
            PermissionCard(
                title = stringResource(R.string.permission_notification_title),
                description = stringResource(R.string.permission_notification_desc),
                onGrant = onRequestNotification,
            )
        }

        SettingSwitch(
            title = stringResource(R.string.service_enabled),
            checked = settings.serviceEnabled,
            enabled = overlayGranted && notificationGranted,
            onCheckedChange = onServiceEnabledChange,
        )

        if (settings.serviceEnabled && overlayGranted) {
            Text(
                text = stringResource(R.string.ready_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        SettingSwitch(
            title = stringResource(R.string.left_edge),
            checked = settings.leftEdgeEnabled,
            enabled = settings.serviceEnabled,
            onCheckedChange = onLeftEdgeChange,
        )

        SettingSwitch(
            title = stringResource(R.string.right_edge),
            checked = settings.rightEdgeEnabled,
            enabled = settings.serviceEnabled,
            onCheckedChange = onRightEdgeChange,
        )

        if (settings.leftEdgeEnabled || settings.rightEdgeEnabled) {
            Text(
                text = stringResource(R.string.live_preview_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        SliderSetting(
            title = stringResource(R.string.edge_width),
            value = settings.edgeTriggerWidthDp,
            valueRange = 12f..36f,
            enabled = settings.serviceEnabled,
            label = "${settings.edgeTriggerWidthDp.roundToInt()} dp",
            triggersLayoutPreview = true,
            onLayoutPreviewStart = onLayoutPreviewStart,
            onLayoutPreviewStop = onLayoutPreviewStop,
            onValueChange = onEdgeWidthChange,
        )

        SliderSetting(
            title = stringResource(R.string.trigger_top),
            value = settings.triggerTopFraction,
            valueRange = 0.05f..0.65f,
            enabled = settings.serviceEnabled,
            label = "${(settings.triggerTopFraction * 100).roundToInt()}%",
            triggersLayoutPreview = true,
            onLayoutPreviewStart = onLayoutPreviewStart,
            onLayoutPreviewStop = onLayoutPreviewStop,
            onValueChange = onTriggerTopChange,
        )

        SliderSetting(
            title = stringResource(R.string.trigger_height),
            value = settings.triggerHeightFraction,
            valueRange = 0.15f..0.55f,
            enabled = settings.serviceEnabled,
            label = "${(settings.triggerHeightFraction * 100).roundToInt()}%",
            triggersLayoutPreview = true,
            onLayoutPreviewStart = onLayoutPreviewStart,
            onLayoutPreviewStop = onLayoutPreviewStop,
            onValueChange = onTriggerHeightChange,
        )

        SliderSetting(
            title = stringResource(R.string.index_height),
            value = settings.indexHeightFraction,
            valueRange = 0.25f..0.65f,
            enabled = settings.serviceEnabled,
            label = "${(settings.indexHeightFraction * 100).roundToInt()}%",
            triggersLayoutPreview = true,
            onLayoutPreviewStart = onLayoutPreviewStart,
            onLayoutPreviewStop = onLayoutPreviewStop,
            onValueChange = onIndexHeightChange,
        )

        SliderSetting(
            title = stringResource(R.string.apps_per_row),
            value = settings.appsPerRow.toFloat(),
            valueRange = 2f..5f,
            steps = 2,
            enabled = settings.serviceEnabled,
            label = "${settings.appsPerRow} 列",
            onValueChange = { onAppsPerRowChange(it.roundToInt()) },
        )

        SliderSetting(
            title = stringResource(R.string.panel_opacity),
            value = settings.panelOpacity,
            valueRange = 0.75f..1f,
            enabled = settings.serviceEnabled,
            label = "${(settings.panelOpacity * 100).roundToInt()}%",
            onValueChange = onPanelOpacityChange,
        )

        SettingSwitch(
            title = stringResource(R.string.haptic_enabled),
            checked = settings.hapticEnabled,
            enabled = true,
            onCheckedChange = onHapticEnabledChange,
        )

        if (settings.hapticEnabled) {
            SliderSetting(
                title = stringResource(R.string.haptic_strength),
                value = settings.hapticStrengthLevel.toFloat(),
                valueRange = 0f..2f,
                steps = 1,
                enabled = true,
                label = hapticStrengthLabel(settings.hapticStrengthLevel),
                onValueChange = { onHapticStrengthChange(it.roundToInt()) },
            )
        }

        ThemeColorPicker(
            selected = settings.themeColorArgb,
            enabled = settings.serviceEnabled,
            onColorSelected = onThemeColorChange,
        )
    }
}

@Composable
private fun hapticStrengthLabel(level: Int): String {
    return when (level) {
        0 -> stringResource(R.string.haptic_strength_light)
        2 -> stringResource(R.string.haptic_strength_strong)
        else -> stringResource(R.string.haptic_strength_medium)
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    onGrant: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onGrant) {
                Text(stringResource(R.string.grant_permission))
            }
        }
    }
}

@Composable
private fun SettingSwitch(
    title: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, enabled = enabled, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SliderSetting(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    enabled: Boolean,
    label: String,
    triggersLayoutPreview: Boolean = false,
    onLayoutPreviewStart: () -> Unit = {},
    onLayoutPreviewStop: () -> Unit = {},
    onValueChange: (Float) -> Unit,
) {
    var previewActive by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(title)
            Text(label, color = MaterialTheme.colorScheme.primary)
        }
        Slider(
            value = value,
            onValueChange = {
                if (triggersLayoutPreview && !previewActive) {
                    previewActive = true
                    onLayoutPreviewStart()
                }
                onValueChange(it)
            },
            onValueChangeFinished = {
                if (triggersLayoutPreview && previewActive) {
                    previewActive = false
                    onLayoutPreviewStop()
                }
            },
            valueRange = valueRange,
            steps = steps,
            enabled = enabled,
        )
    }
}

@Composable
private fun ThemeColorPicker(
    selected: Int,
    enabled: Boolean,
    onColorSelected: (Int) -> Unit,
) {
    val colors = listOf(
        0xFF6750A4.toInt(),
        0xFF0061A4.toInt(),
        0xFF386A20.toInt(),
        0xFF984061.toInt(),
        0xFF7D5260.toInt(),
        0xFF006874.toInt(),
    )
    Column {
        Text(stringResource(R.string.theme_color))
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            colors.forEach { color ->
                val isSelected = color == selected
                OutlinedButton(
                    onClick = { onColorSelected(color) },
                    enabled = enabled,
                    modifier = Modifier.height(40.dp),
                ) {
                    Text(
                        text = "●",
                        color = androidx.compose.ui.graphics.Color(color),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}
