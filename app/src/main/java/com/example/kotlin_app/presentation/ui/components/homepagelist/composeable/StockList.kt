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
import com.example.kotlin_app.domain.repository.model.SparkStockUiItem
import com.example.kotlin_app.domain.repository.model.StockItem
import com.example.kotlin_app.domain.repository.model.createPlaceholderStockItem
import com.example.kotlin_app.presentation.ui.components.shared.StockUiItem
import com.example.kotlin_app.presentation.ui.components.stockdetaildialog.composable.StockDetailsDialog
import com.example.kotlin_app.presentation.viewmodel.MarketViewModel

@Composable
fun StockList(
    list: List<SparkStockUiItem>,
    marketViewModel: MarketViewModel
) {
    var itemIsSelected by remember { mutableStateOf<Boolean>(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White),
        ) {
            items(list) { stock ->
                StockUiItem(stock = stock,
                    onClickListener = {
                        itemIsSelected = true
                        marketViewModel.updateCurrentSymbol(stock.symbol)
                    })
            }
        }
        if (itemIsSelected) {
            StockDetailsDialog(
                marketViewModel = marketViewModel,
                onDismiss = { itemIsSelected = false }
            )
        }
    }
}