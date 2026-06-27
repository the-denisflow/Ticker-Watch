package com.example.tickerwatch.presentation.model
import com.example.tickerwatch.common.tickers.Ticker
import com.example.tickerwatch.domain.repository.model.StockChartDataPoint

/**
 * Point-in-time UI model for the stock details bottom sheet.
 *
 * Produces by [StockChartState.toStockSheetUiSnapshot] and consumed by the sheet composables
 *
 * @param ticker Identifies the stock/crypto; drives [tags] and icon rendering.
 * @param longName Full display name.
 * @param shortName Abbreviated name shown in compact contexts.
 * @param price Latest price from the Spark API response.
 * @param timestamps Unix timestamps corresponding to each entry in [prices].
 * @param previousClose close price from the previous session.
 * @param volume Trading value for the current session.
 * @param exchangeName Exchange the ticker is listed on.
 * @param currency Currency the ticker is traded in.
 * @param currentRange Human readable label for the selected time range (e.g. "1D")
 * @param prices Close prices over the selected range.
 * @param tags Derived display chips(e.g. sector, country).
 */

data class StockSheetUiSnapshot(
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
    val tags: List<String>
)
