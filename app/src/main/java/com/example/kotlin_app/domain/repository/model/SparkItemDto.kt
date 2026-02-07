package com.example.kotlin_app.domain.repository.model

typealias SparkBatchDto = Map<String, SparkItemDto>

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