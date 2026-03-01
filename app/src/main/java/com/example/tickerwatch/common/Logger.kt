package com.example.tickerwatch.common

import android.util.Log
import javax.inject.Inject

class LoggerImpl @Inject constructor() : Logger {
    override fun info(message: String) {
        Log.i("[INFO][YahooStockApp]", "[${getCallerClassName()}] $message")
    }

    override fun error(message: String) {
        Log.e("[ERROR][YahooStockApp]", message)
    }

    private fun getCallerClassName(): String {
        val stackTrace = Thread.currentThread().stackTrace

        for (element in stackTrace) {
            val className = element.className
            if (
                !className.contains("LoggerImpl") &&
                !className.contains("Logger") &&
                !className.startsWith("java.") &&
                !className.startsWith("kotlin.") &&
                !className.contains("VMStack") &&
                !className.contains("Thread")
            ) {
                return className.substringAfterLast('.').substringBefore('$')
            }
        }

        return "UnknownCaller"
    }

}

interface Logger {
    fun info( message: String)
    fun error(message: String)
}