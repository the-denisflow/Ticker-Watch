package com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.ui.body.chart

import android.view.LayoutInflater
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.tickerwatch.R
import com.example.tickerwatch.common.tickers.StockMarketEnum
import com.example.tickerwatch.domain.repository.model.PriceChangeDetails
import com.example.tickerwatch.domain.repository.model.PriceTrend
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.StockChartState
import com.example.tickerwatch.presentation.androidview.chart.plotDiagram
import com.example.tickerwatch.presentation.model.StockChartUiState
import com.example.tickerwatch.presentation.theme.AppColors
import com.github.mikephil.charting.charts.LineChart

@Composable
internal fun StockChartState(
    StockChartUiState: StockChartUiState,
    onRangeChange: (Range) -> Unit
) {
    val details = StockChartUiState.priceChangeDetails
    var chartTrend: PriceTrend

    if (details is PriceChangeDetails.Available) {

        chartTrend = details.changeTrend

        Column {
            PeriodPerformanceRow(
                price = "$" + StockChartUiState.uiItem.price.toString(),
                trend = details.changeTrend,
                periodPercent = details.changePercent,
                periodAbsoluteChange = details.changeAbsolut,
            )

            AndroidView(
                factory = { ctx ->
                    val view = LayoutInflater.from(ctx).inflate(R.layout.frame_linechart, null, false)
                    val chart = view.findViewById<LineChart>(R.id.line_chart)
                    chart.description.isEnabled = false
                    chart.setNoDataText("Loading…")
                    view
                },
                update = { view ->
                    val chart = view.findViewById<LineChart>(R.id.line_chart)
                    plotDiagram(
                        StockChartUiState.uiItem.prices,
                        StockChartUiState.uiItem.timestamp,
                        StockChartUiState.range,
                        chart,
                        chartTrend
                    )
                }
            )

            PeriodSelector(StockChartUiState.range, onRangeChange)
        }
    }
}

@Preview
@Composable
private fun StockChartStatePreview() {
    val mockStockChartState = StockChartState(
        ticker = StockMarketEnum.entries.first(),
        longName = "Apple Inc.",
        shortName = "Apple",
        price = 189.45,
        timestamp = listOf(
            1717600000, 1717603600, 1717607200,
            1717610800, 1717614400, 1717618000
        ),
        previousClose = 187.32,
        volume = 52_340_000,
        exchangeName = "NASDAQ",
        currency = "USD",
        currentRange = "1D",
        prices = listOf(
            187.5, 188.2, 189.0,
            188.7, 189.3, 189.45
        )
    )
    val uiState = StockChartUiState(
        item = mockStockChartState,
        range = Range.ONE_DAY,
        isLoading = false
    )
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(AppColors.Surface)) {
        StockChartState(StockChartUiState = uiState, onRangeChange = {})
    }
}
