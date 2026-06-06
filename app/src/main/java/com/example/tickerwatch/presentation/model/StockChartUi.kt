package com.example.tickerwatch.presentation.model

import com.example.tickerwatch.common.tickers.Ticker

data class StockChartViewUi(
    val ticker: Ticker,
    val longName: String,
    val shortName: String,
    var price: Double,
    var timestamp: List<Int> = emptyList(),
    val previousClose: Double? = null,
    val volume: Long? = null,
    val exchangeName: String? = null,
    val currency: String? = null,
    val currentRange: String? = null,
    val prices: List<Double>
)
