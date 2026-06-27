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
import androidx.compose.ui.tooling.preview.Preview
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.StockChartDataPoint
import com.example.tickerwatch.domain.repository.model.StockChartState as StockChartStateDomain
import com.example.tickerwatch.domain.repository.model.StockSummary
import com.example.tickerwatch.presentation.component.stockdialog.SheetHeader
import com.example.tickerwatch.presentation.mapper.toDetails
import com.example.tickerwatch.presentation.model.StockDialogUiState
import com.example.tickerwatch.presentation.model.StockSheetUiState
import com.example.tickerwatch.presentation.screen.main.component.marketlist.listitem.previewStockTech
import com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.ui.body.chart.StockChartState
import com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.ui.body.metadatasection.TickerTagsRow
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
    ModalBottomSheet(
        scrimColor = AppColors.Scrim,
        dragHandle = { ModalHeader() },
        onDismissRequest = { onDismiss() },
        containerColor = AppColors.Surface,
    ) {
        StockDetailsSheetContent(
            stockSummary = stockDialogUiState.stockSummary!!,
            chartUiState = stockDialogUiState.chartUiState!!,
            onRangeChange = onRangeChange
        )
    }
}

@Composable
fun StockDetailsSheetContent(
    stockSummary: StockSummary,
    chartUiState: StockSheetUiState,
    onRangeChange: (Range) -> Unit,
) {
    val detailsRow = remember(chartUiState.uiItem) {
        chartUiState.uiItem.toDetails().rows
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        SheetHeader(displayedItem = stockSummary)

        Spacer(modifier = Modifier.height(AppDimens.Space8))

        StockChartState(
            stockChartUiState = chartUiState,
            onRangeChange = onRangeChange
        )

        TickerTagsRow(chips = chartUiState.uiItem.tags)

        StockDetailsSection(detailsRow = detailsRow)

        Spacer(modifier = Modifier.height(AppDimens.Space24))
    }
}

private val previewDialogUiState = StockDialogUiState(
    stockSummary = previewStockTech,
    chartUiState = StockSheetUiState(
        item = StockChartStateDomain(
            ticker = previewStockTech.ticker,
            longName = "Apple Inc.",
            shortName = "Apple",
            price = 189.84,
            dataPoints = listOf(
                StockChartDataPoint(1717600000, 187.5),
                StockChartDataPoint(1717603600, 188.2),
                StockChartDataPoint(1717607200, 189.0),
                StockChartDataPoint(1717610800, 188.7),
                StockChartDataPoint(1717614400, 189.3),
                StockChartDataPoint(1717618000, 189.45),
            ),
            previousClose = 185.0,
            volume = 52_340_000,
            exchangeName = "NASDAQ",
            currency = "USD",
            currentRange = "1D",
        ),
        range = Range.ONE_DAY,
        isLoading = false
    ),
    isLoaded = true
)

@Preview(showBackground = true)
@Composable
private fun StockDetailsSheetPreview() {
    StockDetailsSheetContent(
        stockSummary = previewDialogUiState.stockSummary!!,
        chartUiState = previewDialogUiState.chartUiState!!,
        onRangeChange = {}
    )
}