package com.example.kotlin_app.domain.repository.model

import com.example.kotlin_app.common.tickers.InvalidTicker
import com.example.kotlin_app.common.tickers.Ticker

data class StockItem (val ticker: Ticker,
                      val longName: String,
                      val shortName: String,
                      var price: Double,
                      val logoUrl: String?,
                      val logoRes: Int?,
                      var prices: List<Double> = emptyList<Double>(),
                      var timestamp: List<Int> = emptyList<Int>(),
    )

fun YahooResultDto.toStockItem(ticker: Ticker, logoUrl: String? = null, logoRes: Int? = null): StockItem {
    return StockItem(
        ticker = ticker,
        logoUrl = logoUrl,
        logoRes = logoRes,
        longName = chart.result.first().meta.longName,
        shortName =  chart.result.first().meta.shortName,
        price = chart.result.firstOrNull()?.meta?.regularMarketPrice ?: 0.0,
        prices = chart.result.firstOrNull()?.indicators?.quote?.firstOrNull()?.close ?: emptyList(),
        timestamp = chart.result.firstOrNull()?.timestamp ?: emptyList()
    )
}

fun createPlaceholderStockItem() = StockItem (
    ticker = InvalidTicker.INVALIDTICKER,
    longName = "",
    shortName = "",
    price = 0.0,
    logoUrl = null,
    logoRes = null )