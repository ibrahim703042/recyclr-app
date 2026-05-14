package com.gdsc.recyclr

import android.app.Application
import com.gdsc.recyclr.util.AppLogger
import dagger.hilt.android.HiltAndroidApp

import com.gdsc.recyclr.push.RecyclrNotificationChannels

@HiltAndroidApp
class RecyclrApp : Application() {
    override fun onCreate() {
        super.onCreate()
        RecyclrNotificationChannels.ensureCreated(this)
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            AppLogger.e("Crash non géré — voir stack ci-dessous", throwable)
            previous?.uncaughtException(thread, throwable)
        }
    }
}
