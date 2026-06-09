package com.example.tickerwatch.presentation.component.stockdialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.presentation.model.StockDialogUiState
import com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.ui.body.mainbody.StockDetailsSheet

@Composable
fun StockDetailsOverlay(
    stockDialogUiState: StockDialogUiState,
    onRangeChange: (Range) -> Unit,
    onDismiss: () -> Unit
) {
    if (stockDialogUiState.stockSummary != null) {
        StockDetailsSheet(
            stockDialogUiState = stockDialogUiState,
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