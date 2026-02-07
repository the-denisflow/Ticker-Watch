package com.example.kotlin_app.data.repository

import com.example.kotlin_app.common.Logger
import com.example.kotlin_app.common.tickers.Ticker
import com.example.kotlin_app.data.remote.YahooApi
import com.example.kotlin_app.domain.repository.YahooRepository
import com.example.kotlin_app.domain.repository.model.SparkBatchDto
import com.example.kotlin_app.domain.repository.model.SparkItemDto
import com.example.kotlin_app.domain.repository.model.YahooResultDto
import javax.inject.Inject

class YahooRepositoryImpl @Inject constructor(
    private val api: YahooApi,
    private val logger: Logger
) : YahooRepository {
    override suspend fun getSingleChart(ticker: Ticker, range: String, interval: String): Result<YahooResultDto> {
      return try {
          val yahooResult = api.getSingleChart(ticker.symbol, range = range, interval = interval)
          Result.success(yahooResult)
      } catch (e: Exception) {
          logger.error("Failed to fetch chart for ${ticker.symbol}: ${e.message}")
          Result.failure(e)
      }
    }

    override suspend fun getBatchSpark(
        symbols: String,
        range: String,
        interval: String
    ): Result<Map<String, SparkItemDto>>  =   try {
        val yahooResult = api.getSparkBatch(symbols = symbols, range = range, interval = range)
        Result.success(yahooResult)
    } catch (e: Exception) {
        logger.error("Failed to fetch data for symbols: $symbols")
        Result.failure(e)
    }
}

