package com.example.tickerwatch.presentation.model

/**
 * TODO: document this
 */
data class StockDetailRow(
    val label: String,
    val value: String
)

data class StockRowDetailsUi(
    val rows: List<StockDetailRow>
)