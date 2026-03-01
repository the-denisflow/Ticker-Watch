package com.example.tickerwatch.data.remote.dto

data class SparkItemDto(
    val timestamp: List<Long>?,
    val symbol: String?,
    val end: Long?,
    val dataGranularity: Int?,
    val start: Long?,
    val close: List<Double?>?,
    val previousClose: Double?,
    val chartPreviousClose: Double?
)