package com.example.kotlin_app.data.remote.mappers

import com.example.kotlin_app.common.tickers.Ticker
import com.example.kotlin_app.data.remote.dto.SparkItemDto
import com.example.kotlin_app.domain.repository.model.PriceProgressTrend
import com.example.kotlin_app.domain.repository.model.PriceTrend
import com.example.kotlin_app.domain.repository.model.SparkStockUiItem
import kotlin.math.abs

fun SparkItemDto.toUiModel(ticker: Ticker): SparkStockUiItem? {
    val symbol = symbol ?: return null
    val latestClose = close?.filterNotNull()?.lastOrNull() ?: return null
    return SparkStockUiItem(
        symbol = symbol,
        close = latestClose,
        trend = this.calculateTrend(),
        ticker = ticker
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


