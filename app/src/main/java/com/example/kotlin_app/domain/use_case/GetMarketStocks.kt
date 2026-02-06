package com.example.kotlin_app.domain.use_case

import com.example.kotlin_app.common.Logger
import com.example.kotlin_app.common.tickers.StockTicker
import com.example.kotlin_app.common.tickers.StockTicker.Companion.allTickers
import com.example.kotlin_app.domain.repository.YahooRepository
import com.example.kotlin_app.domain.repository.model.IntervalRangeValidator.getValidIntervalsFor
import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.domain.repository.model.StockItem
import com.example.kotlin_app.domain.repository.model.createPlaceholderStockItem
import com.example.kotlin_app.domain.repository.model.toStockItem
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
            allTickers.map { ticker ->
                async {
                    runCatching {
                        getStockItem(ticker = ticker, range = displayRange)
                    }
                        .onFailure { error ->
                            logger.error(
                                "Failed to fetch ${ticker.symbol}: ${error.message}"
                            )
                        }
                        .getOrNull()
                        ?.also {
                            logger.info("Fetched stock item for ${ticker.symbol}")
                        }
                        ?: createPlaceholderStockItem()
                }
            }.awaitAll()
        }
}