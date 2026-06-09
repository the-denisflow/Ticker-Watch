package com.example.tickerwatch.domain.repository.model

@JvmInline
value class StockSymbol(val value: String) {
    companion object {
        val Invalid = StockSymbol("")
    }

    val isValid: Boolean
        get() = this != Invalid
}