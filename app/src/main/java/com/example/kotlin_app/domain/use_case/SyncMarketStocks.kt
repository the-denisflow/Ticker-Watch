package com.example.kotlin_app.domain.use_case

import com.example.kotlin_app.common.Logger
import com.example.kotlin_app.common.tickers.StockTicker
import com.example.kotlin_app.common.tickers.StockTicker.Companion.toStockTicker
import com.example.kotlin_app.data.local.toDomain
import com.example.kotlin_app.domain.network.NetworkMonitor
import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.domain.repository.model.StockItem
import javax.inject.Inject

class SyncMarketStocks @Inject constructor(
    private val getMarketStocks: GetMarketStocks,
    private val saveStocksInDb: SaveStocksInDb,
    private val loadStocksFromDb: LoadStocksFromDb,
    private val networkMonitor: NetworkMonitor,
    private val logger: Logger
) {
    suspend operator fun invoke(range: Range): List<StockItem> {
        return (if (networkMonitor.isOnline.value) {
            logger.info("Device is online")
            val stocks = getMarketStocks(range)
            saveStocksInDb(stocks)
            logger.info("Fetched ${stocks.size} stocks")
            stocks
        } else {
            logger.info("Device is offline")
            loadStocksFromDb().map { it.toDomain(toStockTicker(it.symbol)) }
                .filter { it.ticker != StockTicker.IVALIDTICKER }

        })
    }
    }
