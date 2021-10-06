package me.user.androidApp

import android.app.Application
import me.user.androidApp.di.modules.uiModule
import org.koin.android.ext.android.startKoin

class WeatherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(applicationContext, listOf(uiModule))
    }
}