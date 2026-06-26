package com.slideindex.app.overlay

import android.content.Context
import android.view.WindowManager
import com.slideindex.app.data.AppRepository
import com.slideindex.app.settings.AppSettings
import kotlinx.coroutines.CoroutineScope

class OverlayManager(
    private val context: Context,
    private val appRepository: AppRepository,
    private val scope: CoroutineScope,
) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var leftController: SideOverlayController? = null
    private var rightController: SideOverlayController? = null
    private var currentSettings: AppSettings = AppSettings()
    private var previewMode = false

    fun applySettings(settings: AppSettings) {
        currentSettings = settings
        val screenWidth = context.resources.displayMetrics.widthPixels

        if (!settings.serviceEnabled) {
            leftController?.destroy()
            rightController?.destroy()
            leftController = null
            rightController = null
            return
        }

        if (settings.leftEdgeEnabled) {
            if (leftController == null) {
                leftController = SideOverlayController(
                    context = context,
                    side = PanelSide.LEFT,
                    windowManager = windowManager,
                    appRepository = appRepository,
                    scope = scope,
                )
            }
            leftController?.updateSettings(settings, screenWidth)
            leftController?.showEdge()
        } else {
            leftController?.destroy()
            leftController = null
        }

        if (settings.rightEdgeEnabled) {
            if (rightController == null) {
                rightController = SideOverlayController(
                    context = context,
                    side = PanelSide.RIGHT,
                    windowManager = windowManager,
                    appRepository = appRepository,
                    scope = scope,
                )
            }
            rightController?.updateSettings(settings, screenWidth)
            rightController?.showEdge()
        } else {
            rightController?.destroy()
            rightController = null
        }

        applyPreviewToControllers()
    }

    fun setPreviewMode(enabled: Boolean) {
        previewMode = enabled
        applyPreviewToControllers()
    }

    private fun applyPreviewToControllers() {
        if (!currentSettings.serviceEnabled) return
        leftController?.setPreviewMode(previewMode && currentSettings.leftEdgeEnabled)
        rightController?.setPreviewMode(previewMode && currentSettings.rightEdgeEnabled)
    }

    fun reloadApps() {
        leftController?.reloadApps()
        rightController?.reloadApps()
    }

    fun destroy() {
        leftController?.destroy()
        rightController?.destroy()
        leftController = null
        rightController = null
    }
}
