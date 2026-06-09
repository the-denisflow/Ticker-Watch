package com.example.tickerwatch.presentation.screen.main.component.marketlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.tickerwatch.common.tickers.CryptoEnum
import com.example.tickerwatch.common.tickers.Sector
import com.example.tickerwatch.common.tickers.StockMarketEnum
import com.example.tickerwatch.common.tickers.Ticker
import com.example.tickerwatch.domain.repository.model.PriceChangeDetails
import com.example.tickerwatch.domain.repository.model.PriceProgressTrend
import com.example.tickerwatch.domain.repository.model.PriceTrend
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.StockChartView
import com.example.tickerwatch.domain.repository.model.StockSummary
import com.example.tickerwatch.domain.repository.model.StockSymbol
import com.example.tickerwatch.domain.repository.model.getPriceChangedDetails
import com.example.tickerwatch.presentation.component.stockdialog.StockDetailsOverlay
import com.example.tickerwatch.presentation.model.StockChartViewUiState
import com.example.tickerwatch.presentation.model.StockDialogUiState
import com.example.tickerwatch.presentation.screen.main.component.marketlist.listitem.StockUiListItem
import com.example.tickerwatch.presentation.screen.main.component.marketlist.sectorfilter.SectorFilter
import com.example.tickerwatch.presentation.screen.main.component.marketlist.sectorfilter.SectorFilterChips
import com.example.tickerwatch.presentation.theme.AppColors

@Composable
fun StockList(
    stockList: List<StockSummary>,
    stockDialogUiState: StockDialogUiState,
    watchlistSymbols: Set<String> = emptySet(),
    onSymbolSelected: (String) -> Unit = {},
    onToggleWatchlist: (String) -> Unit = {},

) {
    var activeFilter by remember { mutableStateOf(SectorFilter.ALL) }

    val filteredList = remember(stockList, activeFilter) {
        when (activeFilter) {
            SectorFilter.ALL -> stockList
            SectorFilter.TECH -> stockList.filter { (it.ticker as? StockMarketEnum)?.sector == Sector.TECHNOLOGY }
            SectorFilter.FINANCE -> stockList.filter { (it.ticker as? StockMarketEnum)?.sector == Sector.FINANCE }
            SectorFilter.HEALTH -> stockList.filter { (it.ticker as? StockMarketEnum)?.sector == Sector.HEALTHCARE }
            SectorFilter.CRYPTO -> stockList.filter { it.ticker is CryptoEnum }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = AppColors.Surface),
        ) {
            item {
                SectorFilterChips(
                    activeFilter = activeFilter,
                    onFilterSelected = { filter -> activeFilter = filter })
            }
            if(filteredList.isNotEmpty()) {
                items(filteredList, key = { stock -> stock.symbol.value }) { stock ->
                    StockUiListItem(
                        stock = stock,
                        isInWatchlist = stock.symbol.value in watchlistSymbols,
                        onClickListener = {
                            onSymbolSelected(stock.symbol.value)
                        },
                        onToggleWatchlist = { onToggleWatchlist(stock.symbol.value) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun StockListPreview() {
    val stockList = listOf(
        StockSummary(
            symbol = StockSymbol("AAPL"),
            close = 150.0,
            trend = PriceProgressTrend(
                progressPercent = "1.5%",
                progressTrend = PriceTrend.UP
            ),
            ticker = StockMarketEnum.valueOf("AAPL"),
            prices = listOf(140.0, 145.0, 148.0, 150.0),
            chartPreviousClose = 148.0
        ),
        StockSummary(
            symbol = StockSymbol("GOOGL"),
            close = 2800.0,
            trend = PriceProgressTrend(
                progressPercent = "2.5%",
                progressTrend = PriceTrend.DOWN
            ),
            ticker = StockMarketEnum.valueOf("GOOGL"),
            prices = listOf(2850.0, 2825.0, 2810.0, 2800.0),
            chartPreviousClose = 2810.0
        ),
        StockSummary(
            symbol = StockSymbol("META"),
            close = 2800.0,
            trend = PriceProgressTrend(
                progressPercent = "1.5%",
                progressTrend = PriceTrend.UP
            ),
            ticker = StockMarketEnum.valueOf("META"),
            prices = listOf(2850.0, 2825.0, 2810.0, 2800.0),
            chartPreviousClose = 2810.0
        ),
        StockSummary(
            symbol = StockSymbol("IBM"),
            close = 2800.0,
            trend = PriceProgressTrend(
                progressPercent = "0.5%",
                progressTrend = PriceTrend.UP
            ),
            ticker = StockMarketEnum.valueOf("IBM"),
            prices = listOf(2850.0, 2825.0, 2810.0, 2800.0),
            chartPreviousClose = 2810.0
        ),
    )

    val mockStockChartView = StockChartView(
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
    val uiState = StockChartViewUiState(
        item = mockStockChartView,
        range = Range.ONE_DAY,
        isLoading = false
    )

//    @Immutable
//    data class StockSummary(
//        val symbol: String,
//        val close: Double,
//        val trend: PriceProgressTrend,
//        val ticker: Ticker,
//        val prices: List<Double> = emptyList(),
//        val chartPreviousClose: Double? = null
//    )

}