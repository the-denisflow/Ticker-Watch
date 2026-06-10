package com.example.tickerwatch.domain.use_case

import com.example.tickerwatch.common.tickers.InvalidTicker
import com.example.tickerwatch.common.tickers.TickerRegistry
import com.example.tickerwatch.data.local.toEntity
import com.example.tickerwatch.data.local.toUiModel
import com.example.tickerwatch.data.repository.DbRepository
import com.example.tickerwatch.domain.repository.model.StockSummary
import com.example.tickerwatch.domain.repository.model.StockChartState
import javax.inject.Inject

class LoadStocksFromDb @Inject constructor(private val dbRepository: DbRepository) {
    suspend operator fun invoke() = dbRepository.getAllStocks()
}

class SaveStocksInDb @Inject constructor(private val dbRepository: DbRepository) {
    suspend operator fun invoke(stocks: List<StockChartState>) {
        dbRepository.saveStocks(stocks.map { it.toEntity() })
    }
}

class LoadBatchFromDb @Inject constructor(private val dbRepository: DbRepository) {
    suspend operator fun invoke(): List<StockSummary> {
        return dbRepository.getAllSparkStocks().mapNotNull { entity ->
            val ticker = TickerRegistry.replaceSymbolWithTickerEnum(entity.symbol)
            if (ticker == InvalidTicker.INVALIDTICKER) return@mapNotNull null
            entity.toUiModel(ticker)
        }
    }
}

class SaveBatchToDb @Inject constructor(private val dbRepository: DbRepository) {
    suspend operator fun invoke(stocks: List<StockSummary>) {
        dbRepository.saveSparkStocks(stocks.map { it.toEntity() })
    }
}