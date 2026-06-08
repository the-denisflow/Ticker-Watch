package com.example.tickerwatch.data.repository

import com.example.tickerwatch.common.Logger
import com.example.tickerwatch.data.remote.api.YahooApi
import com.example.tickerwatch.domain.repository.YahooRepository
import com.example.tickerwatch.data.remote.dto.SparkItemDto
import com.example.tickerwatch.data.remote.dto.YahooResultDto
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
        val yahooResult = api.getSparkBatch(symbols = symbols, range = range, interval = interval)
        Result.success(yahooResult)
    } catch (e: Exception) {
        logger.error("Failed to fetch data for symbols: $symbols")
        Result.failure(e)
    }
}

