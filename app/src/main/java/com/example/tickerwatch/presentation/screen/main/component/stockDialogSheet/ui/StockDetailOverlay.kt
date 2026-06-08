package com.example.tickerwatch.presentation.component.stockdialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.StockSummary
import com.example.tickerwatch.presentation.model.StockChartViewUiState

@Composable
fun StockDetailsOverlay(
    itemIsSelected: Boolean,
    stockChartViewUiState: StockChartViewUiState,
    currentSparkItem: StockSummary?,
    onRangeChange: (Range) -> Unit,
    onDismiss: () -> Unit
) {
    if (!itemIsSelected) return

    if (currentSparkItem != null) {
        StockDetailsSheet(
            StockChartViewUiState = stockChartViewUiState,
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