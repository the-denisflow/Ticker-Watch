package com.example.tickerwatch.domain.repository


import com.example.tickerwatch.common.tickers.Ticker
import com.example.tickerwatch.data.remote.dto.SparkItemDto
import com.example.tickerwatch.domain.repository.model.YahooResultDto

interface YahooRepository {
    suspend fun getSingleChart(symbol: String, range: String, interval: String): Result<YahooResultDto>
    suspend fun getBatchSpark(symbols: String, range: String = "1d", interval: String = "1d"): Result<Map<String, SparkItemDto>>
}