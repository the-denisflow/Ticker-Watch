package com.example.kotlin_app.domain.use_case

import com.example.kotlin_app.common.Logger
import com.example.kotlin_app.domain.repository.YahooRepository
import com.example.kotlin_app.domain.repository.model.SparkBatchDto
import com.example.kotlin_app.domain.repository.model.SparkItemDto
import javax.inject.Inject

class GetStocksBatch @Inject constructor(
    private val yahooRepository: YahooRepository,
    private val logger: Logger
){
    suspend operator fun invoke(
        symbols: String,
        range: String = "1d",
        interval: String = "1d"
    ): Result<Map<String, SparkItemDto>> {
        val result = yahooRepository.getBatchSpark(symbols, range, interval)
        logger.info("result: $result")
        return result
    }
}