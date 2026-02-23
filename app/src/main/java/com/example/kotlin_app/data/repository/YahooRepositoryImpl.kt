package com.example.kotlin_app.data.repository

import com.example.kotlin_app.common.Logger
import com.example.kotlin_app.common.tickers.Ticker
import com.example.kotlin_app.data.remote.api.YahooApi
import com.example.kotlin_app.domain.repository.YahooRepository
import com.example.kotlin_app.data.remote.dto.SparkItemDto
import com.example.kotlin_app.domain.repository.model.YahooResultDto
import javax.inject.Inject

class YahooRepositoryImpl @Inject constructor(
    private val api: YahooApi,
    private val logger: Logger
) : YahooRepository {
    override suspend fun getSingleChart(symbol: String, range: String, interval: String): Result<YahooResultDto> {
      return try {
          val yahooResult = api.getSingleChart(symbol, range = range, interval = interval)
          Result.success(yahooResult)
      } catch (e: Exception) {
          logger.error("Failed to fetch chart for ${symbol}: ${e.message}")
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

