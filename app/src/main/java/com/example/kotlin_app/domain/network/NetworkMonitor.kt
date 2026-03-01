package com.example.tickerwatch.domain.network

import kotlinx.coroutines.flow.StateFlow

interface NetworkMonitor {
    val isOnline: StateFlow<Boolean>
    fun registerNetworkCallback()
    fun unregisterNetworkCallback()
}