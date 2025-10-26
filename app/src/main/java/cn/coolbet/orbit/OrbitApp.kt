package cn.coolbet.orbit

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OrbitApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}