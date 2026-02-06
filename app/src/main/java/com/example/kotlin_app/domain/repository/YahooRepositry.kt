package com.example.kotlin_app.domain.repository


import com.example.kotlin_app.common.tickers.Ticker
import com.example.kotlin_app.domain.repository.model.YahooResultDto

interface YahooRepository {
    suspend fun getChart(ticker: Ticker, range: String, interval: String): Result<YahooResultDto>
}