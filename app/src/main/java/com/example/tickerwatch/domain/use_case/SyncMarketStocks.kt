package com.example.tickerwatch.domain.use_case

import com.example.tickerwatch.common.Logger
import com.example.tickerwatch.common.tickers.TickerRegistry
import com.example.tickerwatch.domain.network.NetworkMonitor
import com.example.tickerwatch.domain.repository.model.SparkStockUiItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SyncMarketStocks @Inject constructor(
    private val getStocksBatch: GetStocksBatch,
    private val saveBatchToDb: SaveBatchToDb,
    private val loadBatchFromDb: LoadBatchFromDb,
    private val networkMonitor: NetworkMonitor,
    private val logger: Logger
) {
    operator fun invoke(): Flow<List<SparkStockUiItem>> = flow {
        val cached = loadBatchFromDb()
        if (cached.isNotEmpty()) {
            logger.info("Emitting cached data")
            emit(cached)
        }

        if (networkMonitor.isOnline.value) {
            logger.info("Device is online, fetching fresh data")
            val allTickers = TickerRegistry.retrieveAllTickers()
            val symbols = allTickers.joinToString(",") { it.symbol }
            val fresh = getStocksBatch(
                symbols = symbols,
                tickers = allTickers,
                interval = "60m",
                range = "1d"
            )
            if (fresh.isNotEmpty()) {
                saveBatchToDb(fresh)
                emit(fresh)
            }
        } else {
            logger.info("Device is offline, serving cached data only")
        }
    }
}