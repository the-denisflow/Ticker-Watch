package com.example.tickerwatch.presentation.component.stockdialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.StockSummary
import com.example.tickerwatch.presentation.mapper.toDetails
import com.example.tickerwatch.presentation.model.StockChartUiState
import com.example.tickerwatch.presentation.theme.AppColors
import com.example.tickerwatch.presentation.theme.AppDimens


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailsSheet(
    stockChartUiState: StockChartUiState,
    currentSparkItem: StockSummary,
    onRangeChange: (Range) -> Unit,
    onDismiss: () -> Unit = {},
) {
    ModalBottomSheet(
        scrimColor = AppColors.Scrim,
        dragHandle = { DialogHeader() },
        onDismissRequest = { onDismiss() },
        containerColor = AppColors.Surface,
    ) {
        val detailsRow  = remember(stockChartUiState.uiItem) { stockChartUiState.uiItem.toDetails().rows }

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Header(displayedItem = currentSparkItem)

            Spacer(modifier = Modifier.height(AppDimens.Space8))

            StockChart(
                stockChartUiState = stockChartUiState,
                onRangeChange = onRangeChange
            )

            StockMetaRow(ticker = currentSparkItem.ticker)

            StockDetailsSection(
                detailsRow = detailsRow
            )

            Spacer(modifier = Modifier.height(AppDimens.Space24))
        }
    }
}