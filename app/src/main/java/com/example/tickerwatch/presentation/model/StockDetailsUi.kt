package com.example.tickerwatch.presentation.model

/**
 * A single labeled data point displayed in the stock details section
 *
 * Each row appears as a left-aligned [label] and right-aligned [value] inside
 *
 * Example:
 *
 * StockDetailRow(label = "volume", value = "25,345.000")
 * StockDetailRow(label = "Exchange", value "NASDAQ")
 *
 */
data class StockDetailRow(
    val label: String,
    val value: String
)

data class StockRowDetailsUi(
    val rows: List<StockDetailRow>
)