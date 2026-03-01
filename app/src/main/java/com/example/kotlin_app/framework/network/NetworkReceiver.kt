package com.example.tickerwatch.framework.network

import android.net.ConnectivityManager
import android.net.Network

class NetworkReceiver(private val onConnect: () -> Unit, private val onDisconnect: () -> Unit) :
    ConnectivityManager.NetworkCallback() {
    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        onConnect.invoke()
    }

    override fun onUnavailable() {
        super.onUnavailable()
        onDisconnect.invoke()
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        onDisconnect.invoke()
    }
}