package com.example.tickerwatch.presentation.ui.components.stockdetaildialog.state

import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.StockItem

data class StockState(
    val item: StockItem,
    val range: Range
)
