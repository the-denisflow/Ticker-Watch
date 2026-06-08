package com.example.tickerwatch.presentation.model

import com.example.tickerwatch.domain.repository.model.PriceChangeDetails
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.StockChartView
import com.example.tickerwatch.domain.repository.model.getPriceChangedDetails
import com.example.tickerwatch.presentation.mapper.toUi

data class StockChartViewUiState(
    private val item: StockChartView,
    val range: Range,
    val isLoading: Boolean = false,
) {
    val uiItem = item.toUi()
    val priceChangeDetails: PriceChangeDetails = getPriceChangedDetails(
            item.validPrices,
            item.previousClose
        )
}
