package com.slideindex.app

import android.app.Application
import com.slideindex.app.data.AppRepository
import com.slideindex.app.settings.SettingsRepository

class SlideIndexApp : Application() {
    lateinit var appRepository: AppRepository
        private set
    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        appRepository = AppRepository(this)
        settingsRepository = SettingsRepository(this)
    }

    companion object {
        lateinit var instance: SlideIndexApp
            private set
    }
}
