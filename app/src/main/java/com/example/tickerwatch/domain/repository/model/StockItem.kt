package com.example.tickerwatch.domain.repository.model

import androidx.compose.runtime.Immutable
import com.example.tickerwatch.common.tickers.InvalidTicker
import com.example.tickerwatch.common.tickers.Ticker
import com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.util.PriceChangeDetails
import com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.util.getPriceChangedDetails


enum class PriceTrend {UP, DOWN, NEUTRAL}

@Immutable
data class PriceProgressTrend(
    val progressTrend: PriceTrend,
    val progressPercent: String
)

@Immutable
data class StockSummary(
    val symbol: String,
    val close: Double,
    val trend: PriceProgressTrend,
    val ticker: Ticker,
    val prices: List<Double> = emptyList(),
    val chartPreviousClose: Double? = null
) {
    val priceChangeDetails: PriceChangeDetails =
        getPriceChangedDetails(
            prices,
            chartPreviousClose
        )
}

private const val PLACEHOLDER_COUNT = 10

val placeholders: List<StockSummary> = (0 until PLACEHOLDER_COUNT).map { index ->
    StockSummary(
        symbol = "$index",
        close = 0.0,
        ticker = InvalidTicker.INVALIDTICKER,
        trend = PriceProgressTrend(
            progressTrend = PriceTrend.NEUTRAL,
            progressPercent = ""
        )
    )
}

data class StockChart(
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
    private var prices: List<Double?> = emptyList()) {
    val validPrices = prices.mapNotNull { it?.takeIf { !it.isNaN() } }
}

fun YahooResultDto.toStockChart(ticker: Ticker): StockChart {
    val meta = chart.result.firstOrNull()?.meta
    return StockChart(
        ticker = ticker,
        longName = meta?.longName ?: "",
        shortName = meta?.shortName ?: "",
        price = meta?.regularMarketPrice ?: 0.0,
        prices = chart.result.firstOrNull()?.indicators?.quote?.firstOrNull()?.close ?: emptyList(),
        timestamp = chart.result.firstOrNull()?.timestamp ?: emptyList(),
        previousClose = meta?.chartPreviousClose,
        volume = meta?.regularMarketVolume,
        exchangeName = meta?.fullExchangeName,
        currentRange = meta?.range,
        currency = meta?.currency
    )
}

fun createPlaceholderStockChart() = StockChart (
    ticker = InvalidTicker.INVALIDTICKER,
    longName = "",
    shortName = "",
    price = 0.0)