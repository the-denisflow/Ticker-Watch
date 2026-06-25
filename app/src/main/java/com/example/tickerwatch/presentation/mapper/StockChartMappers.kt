package com.example.tickerwatch.presentation.mapper

import com.example.tickerwatch.common.tickers.getTagsFromTicker
import com.example.tickerwatch.domain.repository.model.StockChartState
import com.example.tickerwatch.presentation.model.StockDetailRow
import com.example.tickerwatch.presentation.model.StockRowDetailsUi
import com.example.tickerwatch.presentation.model.StockSheetUiSnapshot

fun StockChartState.toStockSheetUiSnapshot(): StockSheetUiSnapshot = StockSheetUiSnapshot(
    ticker = this.ticker,
    longName = this.longName,
    shortName = this.shortName,
    price = this.price,
    timestamps = this.timestamp,
    previousClose = this.previousClose,
    volume = this.volume,
    exchangeName = this.exchangeName,
    currency = this.currency,
    currentRange = this.currentRange,
    prices = this.validPrices,
    tags = ticker.getTagsFromTicker()
    )

fun StockSheetUiSnapshot.toDetails(): StockRowDetailsUi {
    val low = prices.minOrNull()
    val high = prices.maxOrNull()
    val rows = buildList {
        if (low != null && high != null) {
            add(StockDetailRow("$currentRange Range", "${priceFormat(low)} – ${priceFormat(high)}"))
        }
        volume?.let { add(StockDetailRow("Volume", formatVolume(it))) }
        previousClose?.let { add(StockDetailRow("Prev. Close", "%.2f".format(it))) }
        exchangeName?.let { add(StockDetailRow("Exchange", it)) }
        currency?.let { add(StockDetailRow("Currency", it)) }
    }

    return StockRowDetailsUi(
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
