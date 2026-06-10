package com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.ui.body.mainbody

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
import com.example.tickerwatch.presentation.component.stockdialog.SheetHeader
import com.example.tickerwatch.presentation.mapper.toDetails
import com.example.tickerwatch.presentation.model.StockChartUiState
import com.example.tickerwatch.presentation.model.StockDialogUiState
import com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.ui.body.chart.StockChartState
import com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.ui.body.metadatasection.StockMetaRow
import com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.ui.body.moredetailssection.StockDetailsSection
import com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.ui.header.ModalHeader
import com.example.tickerwatch.presentation.theme.AppColors
import com.example.tickerwatch.presentation.theme.AppDimens


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailsSheet(
    stockDialogUiState: StockDialogUiState,
    onRangeChange: (Range) -> Unit,
    onDismiss: () -> Unit = {},
) {


    //             StockChartUiState = StockChartUiState,
    //            dialogStock = dialogStock,




    ModalBottomSheet(
        scrimColor = AppColors.Scrim,
        dragHandle = { ModalHeader() },
        onDismissRequest = { onDismiss() },
        containerColor = AppColors.Surface,
    ) {
        val detailsRow  = remember(stockDialogUiState.chartUiState!!.uiItem) { stockDialogUiState.chartUiState.uiItem.toDetails().rows }

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            SheetHeader(displayedItem = stockDialogUiState.stockSummary!!)

            Spacer(modifier = Modifier.height(AppDimens.Space8))

            StockChartState(
                StockChartUiState = stockDialogUiState.chartUiState,
                onRangeChange = onRangeChange
            )

            StockMetaRow(ticker = stockDialogUiState.stockSummary.ticker)

            StockDetailsSection(
                detailsRow = detailsRow
            )

            Spacer(modifier = Modifier.height(AppDimens.Space24))
        }
    }
}