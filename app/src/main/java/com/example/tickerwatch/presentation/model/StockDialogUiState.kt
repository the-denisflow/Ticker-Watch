package com.example.tickerwatch.presentation.model

import com.example.tickerwatch.domain.repository.model.StockSummary

data class StockDialogUiState(
    val chartView: StockChartViewUiState? = null,
    val stockSummary: StockSummary? = null,
    val isVisible: Boolean = false
) {
    fun reset() = copy(
        chartView = null,
        stockSummary = null,
        isVisible = false
    )
}
