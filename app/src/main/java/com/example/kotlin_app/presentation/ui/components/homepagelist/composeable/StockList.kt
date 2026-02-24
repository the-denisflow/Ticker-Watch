package com.example.kotlin_app.presentation.ui.components.homepagelist.composeable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.domain.repository.model.SparkStockUiItem
import com.example.kotlin_app.presentation.ui.components.shared.StockUiItem
import com.example.kotlin_app.presentation.ui.components.stockdetaildialog.composable.StockDetailsDialog
import com.example.kotlin_app.presentation.ui.components.stockdetaildialog.state.StockState

@Composable
fun StockList(
    list: List<SparkStockUiItem>,
    stockState: StockState,
    currentSparkItem: SparkStockUiItem?,
    onSymbolSelected: (String) -> Unit,
    onRangeChange: (Range) -> Unit
) {
    var itemIsSelected by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White),
        ) {
            items(list, key = { stock -> stock.symbol }) { stock ->
                StockUiItem(stock = stock,
                    onClickListener = {
                        itemIsSelected = true
                        onSymbolSelected(stock.symbol)
                    })
            }
        }
        if (itemIsSelected) {
            StockDetailsDialog(
                stockState = stockState,
                currentSparkItem = currentSparkItem,
                onRangeChange = onRangeChange,
                onDismiss = { itemIsSelected = false }
            )
        }
    }
}