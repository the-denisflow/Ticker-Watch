package com.example.kotlin_app.domain.use_case

import com.example.kotlin_app.common.Logger
import com.example.kotlin_app.common.tickers.TickerRegistry
import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.domain.repository.model.StockItem
import com.example.kotlin_app.domain.repository.model.createPlaceholderStockItem
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

class GetMarketStocks @Inject constructor(
    private val getStockItem: GetStockItem,
    private val logger: Logger
){
    suspend operator fun invoke(displayRange: Range): List<StockItem> =
        supervisorScope {
            TickerRegistry.retrieveAllTickers().map { ticker ->
                async {
                    runCatching {
                        getStockItem(ticker = ticker, range = displayRange)
                    }
                        .onFailure { error ->
                            logger.error("Failed to fetch stock item for ${ticker.symbol}: ${error.message}")
                        }
                        .getOrNull()
                        ?: createPlaceholderStockItem()
                }
            }.awaitAll()
        }
}