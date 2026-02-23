package com.example.kotlin_app.domain.use_case

import com.example.kotlin_app.common.tickers.InvalidTicker
import com.example.kotlin_app.common.tickers.TickerRegistry
import com.example.kotlin_app.data.local.toEntity
import com.example.kotlin_app.data.local.toUiModel
import com.example.kotlin_app.data.repository.DbRepository
import com.example.kotlin_app.domain.repository.model.SparkStockUiItem
import com.example.kotlin_app.domain.repository.model.StockItem
import javax.inject.Inject

class LoadStocksFromDb @Inject constructor(private val dbRepository: DbRepository) {
    suspend operator fun invoke() = dbRepository.getAllStocks()
}

class SaveStocksInDb @Inject constructor(private val dbRepository: DbRepository) {
    suspend operator fun invoke(stocks: List<StockItem>) {
        dbRepository.saveStocks(stocks.map { it.toEntity() })
    }
}

class LoadBatchFromDb @Inject constructor(private val dbRepository: DbRepository) {
    suspend operator fun invoke(): List<SparkStockUiItem> {
        return dbRepository.getAllSparkStocks().mapNotNull { entity ->
            val ticker = TickerRegistry.replaceSymbolWithTickerEnum(entity.symbol)
            if (ticker == InvalidTicker.INVALIDTICKER) return@mapNotNull null
            entity.toUiModel(ticker)
        }
    }
}

class SaveBatchToDb @Inject constructor(private val dbRepository: DbRepository) {
    suspend operator fun invoke(stocks: List<SparkStockUiItem>) {
        dbRepository.saveSparkStocks(stocks.map { it.toEntity() })
    }
}