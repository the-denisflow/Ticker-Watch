package com.example.kotlin_app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.kotlin_app.common.tickers.Ticker
import com.example.kotlin_app.domain.repository.model.StockItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity
data class StockEntity (
    @PrimaryKey val symbol: String,
    val longName: String,
    val shortName: String,
    val price: Double,
    val logoUrl: String?,
    val logoRes: Int?,
    val pricesJson: String
)

fun StockItem.toEntity(): StockEntity = StockEntity(
    symbol = ticker.symbol,
    longName = longName,
    shortName = shortName,
    price = price,
    logoUrl = logoUrl,
    logoRes = logoRes,
    pricesJson = Gson().toJson(prices)
)

fun StockEntity.toDomain(ticker: Ticker): StockItem = StockItem(
    ticker = ticker,
    longName = longName,
    shortName = shortName,
    price = price,
    logoUrl = logoUrl,
    logoRes = logoRes,
    prices = Gson().fromJson(pricesJson, object : TypeToken<List<Double>>() {}.type)
)

