package com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.util

import com.example.tickerwatch.domain.repository.model.PriceTrend
import kotlin.math.abs

sealed class PriceChangeDetails {
    data class Available(
        val changeTrend: PriceTrend,
        val changePercent: String,
        val changeAbsolut: Double
    ) : PriceChangeDetails()

    data object Unavailable : PriceChangeDetails()
}

fun getPriceChangedDetails(prices: List<Double?>, previousClose: Double? = null): PriceChangeDetails {
    val validPrices = prices.filterNotNull().filter { !it.isNaN() }
    if (validPrices.isEmpty()) return PriceChangeDetails.Unavailable

    val baseline = previousClose?.takeIf { it != 0.0 }
        ?: validPrices.firstOrNull()?.takeIf { it != 0.0 && validPrices.size >= 2 }
        ?: return PriceChangeDetails.Unavailable

    val lastPrice = validPrices.last()
    val trend = when {
        lastPrice > baseline -> PriceTrend.UP
        lastPrice < baseline -> PriceTrend.DOWN
        else -> PriceTrend.NEUTRAL
    }
    val pct = ((lastPrice - baseline) / baseline) * 100.0

    return PriceChangeDetails.Available(
        changeTrend = trend,
        changePercent = "%.2f%%".format(abs(pct)),
        changeAbsolut = lastPrice - baseline
    )
}
