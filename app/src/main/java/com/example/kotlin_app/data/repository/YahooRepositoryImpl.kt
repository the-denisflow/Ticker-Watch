package com.example.kotlin_app.data.repository

import com.example.kotlin_app.common.tickers.StockTicker
import com.example.kotlin_app.data.remote.YahooApi
import com.example.kotlin_app.domain.repository.YahooRepository
import com.example.kotlin_app.domain.repository.model.Interval
import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.domain.repository.model.YahooResult
import javax.inject.Inject

class YahooRepositoryImpl @Inject constructor(
    private val api: YahooApi
) : YahooRepository {
    override suspend fun getChart(ticker: StockTicker, range: String, interval: String): Result<YahooResult> {
      return try {
          val yahooResult = api.getChart(ticker.symbol, range = range, interval = interval)
          Result.success(yahooResult)
      } catch (e: Exception) {
          Result.failure(e)
      }
    }
}