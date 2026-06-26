package com.slideindex.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.slideindex.app.SlideIndexApp

class PackageChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_PACKAGE_ADDED &&
            intent?.action != Intent.ACTION_PACKAGE_REMOVED
        ) {
            return
        }
        val app = context.applicationContext as SlideIndexApp
        app.appRepository.invalidate()
    }
}
