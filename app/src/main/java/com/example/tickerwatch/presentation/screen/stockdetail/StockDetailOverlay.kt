package com.example.tickerwatch.presentation.screen.stockdetail

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.StockSummary

@Composable
fun StockDetailOverlay(
    itemIsSelected: Boolean,
    stockChartUiState: StockChartUiState,
    currentSparkItem: StockSummary?,
    onRangeChange: (Range) -> Unit,
    onDismiss: () -> Unit
) {
    if (!itemIsSelected) return

    if (currentSparkItem != null) {
        StockDetailsDialog(
            stockChartUiState = stockChartUiState,
            currentSparkItem = currentSparkItem,
            onRangeChange = onRangeChange,
            onDismiss = onDismiss
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Unable to load data") },
            text = { Text("An API error occurred while fetching stock data. Please try again.") },
            confirmButton = {
                TextButton(onClick = onDismiss) { Text("OK") }
            }
        )
    }
}