package com.example.tickerwatch.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tickerwatch.common.tickers.Ticker
import com.example.tickerwatch.domain.repository.model.PriceTrend
import com.example.tickerwatch.domain.repository.model.PriceProgressTrend
import com.example.tickerwatch.domain.repository.model.StockSummary
import com.example.tickerwatch.domain.repository.model.StockSymbol
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity
data class SparkStockEntity(
    @PrimaryKey val symbol: String,
    val close: Double,
    val progressTrend: String,
    val progressPercent: String,
    val pricesJson: String = "[]"
)

fun StockSummary.toEntity(): SparkStockEntity = SparkStockEntity(
    symbol = symbol.value,
    close = close,
    progressTrend = trend.progressTrend.name,
    progressPercent = trend.progressPercent,
    pricesJson = Gson().toJson(prices)
)

fun SparkStockEntity.toUiModel(ticker: Ticker): StockSummary = StockSummary(
    symbol = StockSymbol(symbol),
    close = close,
    trend = PriceProgressTrend(
        progressTrend = PriceTrend.valueOf(progressTrend),
        progressPercent = progressPercent
    ),
    ticker = ticker,
    prices = Gson().fromJson(pricesJson, object : TypeToken<List<Double>>() {}.type) ?: emptyList()
)