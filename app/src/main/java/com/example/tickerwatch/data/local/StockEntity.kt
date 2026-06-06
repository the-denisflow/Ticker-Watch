package com.example.tickerwatch.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tickerwatch.common.tickers.Ticker
import com.example.tickerwatch.domain.repository.model.StockChartView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity
data class StockEntity (
    @PrimaryKey val symbol: String,
    val longName: String,
    val shortName: String,
    val price: Double,
    val pricesJson: String
)

fun StockChartView.toEntity(): StockEntity = StockEntity(
    symbol = ticker.symbol,
    longName = longName,
    shortName = shortName,
    price = price,
    pricesJson = Gson().toJson(validPrices)
)

fun StockEntity.toDomain(ticker: Ticker): StockChartView = StockChartView(
    ticker = ticker,
    longName = longName,
    shortName = shortName,
    price = price,
    prices = Gson().fromJson(pricesJson, object : TypeToken<List<Double>>() {}.type)
)

