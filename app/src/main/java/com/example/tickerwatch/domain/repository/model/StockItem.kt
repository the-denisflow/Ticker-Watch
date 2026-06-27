package com.example.tickerwatch.domain.repository.model

import androidx.compose.runtime.Immutable
import com.example.tickerwatch.common.tickers.InvalidTicker
import com.example.tickerwatch.common.tickers.Ticker
import com.example.tickerwatch.data.remote.dto.YahooResultDto


enum class PriceTrend {UP, DOWN, NEUTRAL}

@Immutable
data class PriceProgressTrend(
    val progressTrend: PriceTrend,
    val progressPercent: String
)

@Immutable
data class StockSummary(
    val symbol: StockSymbol,
    val close: Double,
    val trend: PriceProgressTrend,
    val ticker: Ticker,
    val prices: List<Double> = emptyList(),
    val chartPreviousClose: Double? = null
) {
    val priceChangeDetails: PriceChangeDetails = getPriceChangedDetails(
            prices,
            chartPreviousClose
        )
}

private const val PLACEHOLDER_COUNT = 10

val placeholders: List<StockSummary> = (0 until PLACEHOLDER_COUNT).map { index ->
    StockSummary(
        symbol = StockSymbol("mock_$index"),
        close = 0.0,
        ticker = InvalidTicker.INVALIDTICKER,
        trend = PriceProgressTrend(
            progressTrend = PriceTrend.NEUTRAL,
            progressPercent = ""
        )
    )
}

data class StockChartDataPoint(
    val timestamp: Int,
    val price: Double
)

data class StockChartState(
    val ticker: Ticker,
    val longName: String,
    val shortName: String,
    var price: Double,
    val dataPoints: List<StockChartDataPoint> = emptyList(),
    val previousClose: Double? = null,
    val volume: Long? = null,
    val exchangeName: String? = null,
    val currency: String? = null,
    val currentRange: String? = null,
    )

fun YahooResultDto.toStockChartState(ticker: Ticker): StockChartState {
    val meta = chart.result.firstOrNull()?.meta
    val prices = chart.result.firstOrNull()?.indicators?.quote?.firstOrNull()?.close ?: emptyList()
    val timestamps = chart.result.firstOrNull()?.timestamp ?: emptyList()
    return StockChartState(
        ticker = ticker,
        longName = meta?.longName ?: "",
        shortName = meta?.shortName ?: "",
        price = meta?.regularMarketPrice ?: 0.0,
        dataPoints = timestamps.zip(prices).mapNotNull { (timestamp, price) ->
            price?.takeIf { !it.isNaN() }?.let { StockChartDataPoint(timestamp, it) }
        }
        ,
        previousClose = meta?.chartPreviousClose,
        volume = meta?.regularMarketVolume,
        exchangeName = meta?.fullExchangeName,
        currentRange = meta?.range,
        currency = meta?.currency
    )
}

fun createPlaceholderStockChartState() = StockChartState (
    ticker = InvalidTicker.INVALIDTICKER,
    longName = "",
    shortName = "",
    price = 0.0)