package com.example.tickerwatch.presentation.model

import com.example.tickerwatch.domain.repository.model.PriceChangeDetails
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.StockChartState
import com.example.tickerwatch.domain.repository.model.getPriceChangedDetails
import com.example.tickerwatch.presentation.mapper.toStockSheetUiSnapshot

/**
 * Holds chart data fetched from the Spark API for a single stock
 *[StockChartState] carries detailed fields (close prices, previous close, ect.)
 * that back the chart UI.
 *
 * Combined with [StockSummary] (lighter market-list data) into [StockDialogUiState]
 * to drive the full stock detail dialog.
 *
 * @param item Spark API domain model; drives [uiItem] and [priceChangeDetails]
 * @param range currently displayed time range
 * @param isLoading true while a new range is being fetched
 *
 */
data class StockSheetUiState(
    private val item: StockChartState,
    val range: Range,
    val isLoading: Boolean = false,
) {
    val uiItem = item.toStockSheetUiSnapshot()
    val priceChangeDetails: PriceChangeDetails
    get() = getPriceChangedDetails(
            item.dataPoints.map { it.price },
            item.previousClose
        )
}
