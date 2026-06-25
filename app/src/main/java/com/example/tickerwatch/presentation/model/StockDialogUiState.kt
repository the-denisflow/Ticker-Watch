package com.example.tickerwatch.presentation.model

import com.example.tickerwatch.domain.repository.model.StockSummary

/**
 * UI state for the stock detail dialog/bottom sheet.
 * 
 * Combines the lightweight market-list data ([StockSummary]) with the full chart
 * data ([StockChartUiState]) so th bottom sheet has everything it needs in one place.
 * Both are nullable.
 *
 * @param chartUiState chart data fetched from the Spark API.
 * @param stockSummary market-list entry for the selected stock.
 * @param isLoaded Whether the bottom sheet is currently shown.
 */
data class StockDialogUiState(
    val chartUiState: StockSheetUiState? = null,
    val stockSummary: StockSummary? = null,
    val isLoaded: Boolean = false
) {
    /** Returns a copy with all fields cleared, hiding the dialog. */
    fun reset() = copy(
        chartUiState = null,
        stockSummary = null,
        isLoaded = false
    )
}
