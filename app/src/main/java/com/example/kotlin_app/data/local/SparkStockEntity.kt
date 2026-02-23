package com.example.kotlin_app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.kotlin_app.common.tickers.Ticker
import com.example.kotlin_app.domain.repository.model.PriceTrend
import com.example.kotlin_app.domain.repository.model.PriceProgressTrend
import com.example.kotlin_app.domain.repository.model.SparkStockUiItem

@Entity
data class SparkStockEntity(
    @PrimaryKey val symbol: String,
    val close: Double,
    val progressTrend: String,
    val progressPercent: String
)

fun SparkStockUiItem.toEntity(): SparkStockEntity = SparkStockEntity(
    symbol = symbol,
    close = close,
    progressTrend = trend.progressTrend.name,
    progressPercent = trend.progressPercent
)

fun SparkStockEntity.toUiModel(ticker: Ticker): SparkStockUiItem = SparkStockUiItem(
    symbol = symbol,
    close = close,
    trend = PriceProgressTrend(
        progressTrend = PriceTrend.valueOf(progressTrend),
        progressPercent = progressPercent
    ),
    ticker = ticker
)