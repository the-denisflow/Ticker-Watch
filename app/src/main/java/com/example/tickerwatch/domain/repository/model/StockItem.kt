package com.example.tickerwatch.domain.repository.model

import androidx.compose.runtime.Immutable
import com.example.tickerwatch.common.tickers.InvalidTicker
import com.example.tickerwatch.common.tickers.Ticker

enum class PriceTrend {UP, DOWN, NEUTRAL}

@Immutable
data class PriceProgressTrend(
    val progressTrend: PriceTrend,
    val progressPercent: String
)

@Immutable
data class SparkStockUiItem(
    val symbol: String,
    val close: Double,
    val trend: PriceProgressTrend,
    val ticker: Ticker,
    val prices: List<Double> = emptyList()
)

private const val PLACEHOLDER_COUNT = 10

val placeholders: List<SparkStockUiItem> = (0 until PLACEHOLDER_COUNT).map { index ->
    SparkStockUiItem(
        symbol = "$index",
        close = 0.0,
        ticker = InvalidTicker.INVALIDTICKER,
        trend = PriceProgressTrend(
            progressTrend = PriceTrend.NEUTRAL,
            progressPercent = ""
        )
    )
}

data class StockItem (val ticker: Ticker,
                      val longName: String,
                      val shortName: String,
                      var price: Double,
                      var prices: List<Double> = emptyList(),
                      var timestamp: List<Int> = emptyList(),
                      val previousClose: Double? = null,
                      val volume: Long? = null,
                      val exchangeName: String? = null,
                      val currency: String? = null)

fun YahooResultDto.toStockItem(ticker: Ticker): StockItem {
    val meta = chart.result.firstOrNull()?.meta
    return StockItem(
        ticker = ticker,
        longName = meta?.longName ?: "",
        shortName = meta?.shortName ?: "",
        price = meta?.regularMarketPrice ?: 0.0,
        prices = chart.result.firstOrNull()?.indicators?.quote?.firstOrNull()?.close ?: emptyList(),
        timestamp = chart.result.firstOrNull()?.timestamp ?: emptyList(),
        previousClose = meta?.chartPreviousClose,
        volume = meta?.regularMarketVolume,
        exchangeName = meta?.fullExchangeName,
        currency = meta?.currency
    )
}

fun createPlaceholderStockItem() = StockItem (
    ticker = InvalidTicker.INVALIDTICKER,
    longName = "",
    shortName = "",
    price = 0.0)