package com.slideindex.app

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.slideindex.app.service.OverlayService
import com.slideindex.app.settings.AppSettings
import com.slideindex.app.ui.MainScreen
import com.slideindex.app.ui.theme.SlideIndexTheme
import com.slideindex.app.util.PermissionHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var overlayGranted by mutableStateOf(false)
    private var notificationGranted by mutableStateOf(true)

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        notificationGranted = granted || PermissionHelper.hasNotificationPermission(this)
        refreshServiceState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        refreshPermissionState()

        val app = application as SlideIndexApp
        setContent {
            val settings by app.settingsRepository.settings.collectAsStateWithLifecycle(
                initialValue = AppSettings(),
            )
            SlideIndexTheme(
                seedColor = androidx.compose.ui.graphics.Color(settings.themeColorArgb),
            ) {
                MainScreen(
                    settings = settings,
                    overlayGranted = overlayGranted,
                    notificationGranted = notificationGranted,
                    onRequestOverlay = {
                        startActivity(PermissionHelper.overlaySettingsIntent(this))
                    },
                    onRequestNotification = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    },
                    onServiceEnabledChange = { enabled ->
                        lifecycleScope.launch {
                            app.settingsRepository.setServiceEnabled(enabled)
                            refreshServiceState()
                        }
                    },
                    onLeftEdgeChange = { enabled ->
                        lifecycleScope.launch { app.settingsRepository.setLeftEdgeEnabled(enabled) }
                    },
                    onRightEdgeChange = { enabled ->
                        lifecycleScope.launch { app.settingsRepository.setRightEdgeEnabled(enabled) }
                    },
                    onEdgeWidthChange = { value ->
                        lifecycleScope.launch { app.settingsRepository.setEdgeTriggerWidthDp(value) }
                    },
                    onTriggerTopChange = { value ->
                        lifecycleScope.launch { app.settingsRepository.setTriggerTopFraction(value) }
                    },
                    onTriggerHeightChange = { value ->
                        lifecycleScope.launch { app.settingsRepository.setTriggerHeightFraction(value) }
                    },
                    onIndexHeightChange = { value ->
                        lifecycleScope.launch { app.settingsRepository.setIndexHeightFraction(value) }
                    },
                    onAppsPerRowChange = { value ->
                        lifecycleScope.launch { app.settingsRepository.setAppsPerRow(value) }
                    },
                    onPanelOpacityChange = { value ->
                        lifecycleScope.launch { app.settingsRepository.setPanelOpacity(value) }
                    },
                    onThemeColorChange = { color ->
                        lifecycleScope.launch { app.settingsRepository.setThemeColor(color) }
                    },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshPermissionState()
        refreshServiceState(startPreviewAfter = true)
    }

    override fun onPause() {
        sendOverlayPreviewIntent(OverlayService.ACTION_PREVIEW_STOP)
        super.onPause()
    }

    private fun sendOverlayPreviewIntent(action: String) {
        if (!overlayGranted) return
        val intent = Intent(this, OverlayService::class.java).setAction(action)
        startService(intent)
    }

    private fun refreshPermissionState() {
        overlayGranted = PermissionHelper.canDrawOverlays(this)
        notificationGranted = PermissionHelper.hasNotificationPermission(this)
    }

    private fun refreshServiceState(startPreviewAfter: Boolean = false) {
        lifecycleScope.launch {
            val app = application as SlideIndexApp
            val settings = app.settingsRepository.settings.first()
            val shouldRun = settings.serviceEnabled && overlayGranted && notificationGranted
            val serviceIntent = Intent(this@MainActivity, OverlayService::class.java)
            if (shouldRun) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent)
                } else {
                    startService(serviceIntent)
                }
                if (startPreviewAfter) {
                    sendOverlayPreviewIntent(OverlayService.ACTION_PREVIEW_START)
                }
            } else {
                stopService(serviceIntent)
            }
        }
    }
}
