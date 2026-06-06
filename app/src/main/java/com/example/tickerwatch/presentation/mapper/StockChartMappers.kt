package com.example.tickerwatch.presentation.mapper

import com.example.tickerwatch.domain.repository.model.StockChart
import com.example.tickerwatch.presentation.model.StockChartUi
import com.example.tickerwatch.presentation.model.StockDetailsUi

fun StockChart.toUi(): StockChartUi = StockChartUi(
    ticker = this.ticker,
    longName = this.longName,
    shortName = this.shortName,
    price = this.price,
    timestamp = this.timestamp,
    previousClose = this.previousClose,
    volume = this.volume,
    exchangeName = this.exchangeName,
    currency = this.currency,
    currentRange = this.currentRange,
    prices = this.validPrices)

fun StockChartUi.toDetails(): StockDetailsUi {
    val low = prices.minOrNull()
    val high = prices.maxOrNull()
    val rows = buildList {
        if (low != null && high != null) {
            add("$currentRange Range" to "${priceFormat(low)} – ${priceFormat(high)}")
        }
        volume?.let { add("Volume" to formatVolume(it)) }
        previousClose?.let { add("Prev. Close" to "%.2f".format(it)) }
        exchangeName?.let { add("Exchange" to it) }
        currency?.let { add("Currency" to it) }
    }

    return StockDetailsUi(
        rows
    )
}

private fun priceFormat(price: Double): String =  "$" + "%.2f".format(price)

private fun formatVolume(volume: Long): String = when {
    volume >= 1_000_000_000L -> "%.1fB".format(volume / 1_000_000_000.0)
    volume >= 1_000_000L -> "%.1fM".format(volume / 1_000_000.0)
    volume >= 1_000L -> "%.1fK".format(volume / 1_000.0)
    else -> volume.toString()
}
