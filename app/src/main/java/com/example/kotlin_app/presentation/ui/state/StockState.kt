package com.example.kotlin_app.presentation.ui.state

import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.domain.repository.model.StockItem

data class StockState(
    val item: StockItem,
    val range: Range
)
