package com.example.kotlin_app.domain.repository


import com.example.kotlin_app.common.tickers.Ticker
import com.example.kotlin_app.data.remote.dto.SparkItemDto
import com.example.kotlin_app.domain.repository.model.YahooResultDto

interface YahooRepository {
    suspend fun getSingleChart(symbol: String, range: String, interval: String): Result<YahooResultDto>
    suspend fun getBatchSpark(symbols: String, range: String = "1d", interval: String = "1d"): Result<Map<String, SparkItemDto>>
}