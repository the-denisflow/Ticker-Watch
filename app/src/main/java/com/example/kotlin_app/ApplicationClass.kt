package com.example.tickerwatch

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.tickerwatch.domain.network.NetworkMonitor
import com.example.tickerwatch.framework.network.NetworkMonitorImpl
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ApplicationClass: Application() {
    @Inject
    lateinit var networkMonitor: NetworkMonitorImpl

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(networkMonitor)
    }
}