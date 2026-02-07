package com.example.kotlin_app.domain.use_case

import com.example.kotlin_app.common.Logger
import com.example.kotlin_app.common.tickers.TickerRegistry
import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.domain.repository.model.StockItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import javax.inject.Inject

class GetMarketStocks @Inject constructor(
    private val getStockItem: GetStockItem,
    private val logger: Logger
) {
    companion object {
        private const val CONCURRENT_ITEMS_SIZE = 10
    }

    private val concurrencyLimit = Semaphore(CONCURRENT_ITEMS_SIZE)

    suspend operator fun invoke(
        displayRange: Range,
    ): List<StockItem> = supervisorScope {

        TickerRegistry.retrieveAllTickers().map { ticker ->
            async (Dispatchers.IO) {
                concurrencyLimit.withPermit {
                runCatching {
                    getStockItem(ticker = ticker, range = displayRange)
                }.onSuccess {
                    logger.info("${ticker.symbol} fetched")
                }.onFailure { error ->
                    logger.error("${ticker.symbol} failed: ${error.message}")
                }.getOrNull()
                }
            }
        }.awaitAll().filterNotNull()
    }
    }