package com.example.tickerwatch.presentation.model

import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.StockChart
import com.example.tickerwatch.presentation.mapper.toUi
import com.example.tickerwatch.presentation.component.stockdialog.PriceChangeDetails
import com.example.tickerwatch.presentation.component.stockdialog.getPriceChangedDetails

data class StockChartUiState(
    private val item: StockChart,
    val range: Range,
    val isLoading: Boolean = false,
) {
    val uiItem = item.toUi()
    val priceChangeDetails: PriceChangeDetails = getPriceChangedDetails(item.validPrices, item.previousClose)
}
