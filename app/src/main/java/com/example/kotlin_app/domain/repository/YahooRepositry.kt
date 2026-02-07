package com.example.kotlin_app.domain.repository


import com.example.kotlin_app.common.tickers.Ticker
import com.example.kotlin_app.domain.repository.model.SparkBatchDto
import com.example.kotlin_app.domain.repository.model.SparkItemDto
import com.example.kotlin_app.domain.repository.model.YahooResultDto

interface YahooRepository {
    suspend fun getSingleChart(ticker: Ticker, range: String, interval: String): Result<YahooResultDto>
    suspend fun getBatchSpark(symbols: String, range: String = "1d", interval: String = "1d"): Result<Map<String, SparkItemDto>>
}