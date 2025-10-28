package com.example.nucleo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NucleoApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
    }
}
