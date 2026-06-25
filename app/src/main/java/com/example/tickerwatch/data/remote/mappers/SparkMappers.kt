package com.example.tickerwatch.data.remote.mappers

import com.example.tickerwatch.common.tickers.Ticker
import com.example.tickerwatch.data.remote.dto.SparkItemDto
import com.example.tickerwatch.domain.repository.model.PriceProgressTrend
import com.example.tickerwatch.domain.repository.model.PriceTrend
import com.example.tickerwatch.domain.repository.model.StockSummary
import com.example.tickerwatch.domain.repository.model.StockSymbol
import kotlin.math.abs

fun SparkItemDto.toUiModel(ticker: Ticker): StockSummary? {
    val symbol = symbol ?: return null
    val latestClose = close?.filterNotNull()?.lastOrNull() ?: return null
    return StockSummary(
        symbol = StockSymbol(symbol),
        close = latestClose,
        trend = this.calculateTrend(),
        ticker = ticker,
        prices = close.filterNotNull(),
        chartPreviousClose = chartPreviousClose ?: previousClose
    )
}

private fun SparkItemDto.calculateTrend(): PriceProgressTrend {
    val latestClose = close?.filterNotNull()?.lastOrNull() ?: 0.0
    val prevClose = previousClose ?: chartPreviousClose ?: 0.0
    val progressPercent = ((latestClose - prevClose) / prevClose) * 100
    val priceProgress =  "%.2f".format(abs(progressPercent)) + "%"
    return when {
        progressPercent > 0 -> PriceProgressTrend(PriceTrend.UP, priceProgress)
        progressPercent < 0 -> PriceProgressTrend(PriceTrend.DOWN, priceProgress)
        else -> PriceProgressTrend(PriceTrend.NEUTRAL, "0.0")
    }
}


