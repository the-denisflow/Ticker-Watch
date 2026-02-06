package com.example.kotlin_app.data.repository

import com.example.kotlin_app.common.Logger
import com.example.kotlin_app.common.tickers.Ticker
import com.example.kotlin_app.data.remote.YahooApi
import com.example.kotlin_app.domain.repository.YahooRepository
import com.example.kotlin_app.domain.repository.model.YahooResultDto
import javax.inject.Inject

class YahooRepositoryImpl @Inject constructor(
    private val api: YahooApi,
    private val logger: Logger
) : YahooRepository {
    override suspend fun getChart(ticker: Ticker, range: String, interval: String): Result<YahooResultDto> {
      return try {
          val yahooResult = api.getChart(ticker.symbol, range = range, interval = interval)
          Result.success(yahooResult)
      } catch (e: Exception) {
          logger.error("Failed to fetch chart for ${ticker.symbol}: ${e.message}")
          Result.failure(e)
      }
    }
}