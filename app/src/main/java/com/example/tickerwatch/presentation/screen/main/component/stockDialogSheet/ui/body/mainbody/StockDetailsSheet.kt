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
import com.example.tickerwatch.presentation.model.StockChartViewUiState
import com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.ui.body.chart.StockChartView
import com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.ui.body.metadatasection.StockMetaRow
import com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.ui.body.moredetailssection.StockDetailsSection
import com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.ui.header.ModalHeader
import com.example.tickerwatch.presentation.theme.AppColors
import com.example.tickerwatch.presentation.theme.AppDimens


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailsSheet(
    StockChartViewUiState: StockChartViewUiState,
    currentSparkItem: StockSummary,
    onRangeChange: (Range) -> Unit,
    onDismiss: () -> Unit = {},
) {
    ModalBottomSheet(
        scrimColor = AppColors.Scrim,
        dragHandle = { ModalHeader() },
        onDismissRequest = { onDismiss() },
        containerColor = AppColors.Surface,
    ) {
        val detailsRow  = remember(StockChartViewUiState.uiItem) { StockChartViewUiState.uiItem.toDetails().rows }

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            SheetHeader(displayedItem = currentSparkItem)

            Spacer(modifier = Modifier.height(AppDimens.Space8))

            StockChartView(
                stockChartViewUiState = StockChartViewUiState,
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