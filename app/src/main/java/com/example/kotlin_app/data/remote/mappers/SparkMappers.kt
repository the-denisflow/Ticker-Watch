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
    val prevClose = previousClose ?: chartPreviousClose ?: return null

    val progressPercent = ((latestClose - prevClose) / prevClose) * 100

    val trend = when {
        progressPercent > 0 -> PriceProgressTrend(PriceTrend.UP, abs(progressPercent))
        progressPercent < 0 -> PriceProgressTrend(PriceTrend.DOWN, abs(progressPercent))
        else -> PriceProgressTrend(PriceTrend.NEUTRAL, 0.0)
    }

    return SparkStockUiItem(
        symbol = symbol,
        close = latestClose,
        trend = trend,
        ticker = ticker
    )
}
