package com.example.tickerwatch.domain.use_case

import com.example.tickerwatch.common.Logger
import com.example.tickerwatch.common.tickers.Ticker
import com.example.tickerwatch.domain.repository.YahooRepository
import com.example.tickerwatch.data.remote.mappers.toUiModel
import com.example.tickerwatch.domain.repository.model.SparkStockUiItem
import javax.inject.Inject

class GetStocksBatch @Inject constructor(
    private val yahooRepository: YahooRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(
        symbols: String,
        range: String = "1d",
        interval: String = "1d",
        tickers: List<Ticker>
    ): List<SparkStockUiItem> {
        val result = yahooRepository.getBatchSpark(symbols, range, interval)
        if (result.isFailure) {
            logger.error("Failed to fetch stocks batch: ${result.exceptionOrNull()?.message}")
        } else {
            logger.info("Fetched stocks batch successfully")
        }
        return result.getOrNull()
            ?.mapNotNull { (symbol, dto) ->
                val ticker = tickers.find { it.symbol == symbol } ?: return@mapNotNull null
                dto.toUiModel(ticker)
            } ?: emptyList()
    }
}