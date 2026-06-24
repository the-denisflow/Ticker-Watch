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
import com.example.tickerwatch.domain.repository.model.StockChartState
import com.example.tickerwatch.domain.repository.model.StockSummary
import com.example.tickerwatch.domain.repository.model.StockSymbol
import com.example.tickerwatch.domain.repository.model.getPriceChangedDetails
import com.example.tickerwatch.presentation.component.stockdialog.StockDetailsOverlay
import com.example.tickerwatch.presentation.model.StockChartUiState
import com.example.tickerwatch.presentation.model.StockDialogUiState
import com.example.tickerwatch.presentation.screen.main.component.marketlist.listitem.StockUiListItem
import com.example.tickerwatch.presentation.screen.main.component.marketlist.listitem.previewStockCrypto
import com.example.tickerwatch.presentation.screen.main.component.marketlist.listitem.previewStockTech
import com.example.tickerwatch.presentation.screen.main.component.marketlist.sectorfilter.SectorFilter
import com.example.tickerwatch.presentation.screen.main.component.marketlist.sectorfilter.SectorFilterChips
import com.example.tickerwatch.presentation.theme.AppColors

@Composable
fun StockList(
    stockList: List<StockSummary>,
    watchlistSymbols: Set<String> = emptySet(),
    onSymbolSelected: (String) -> Unit = {},
    onToggleWatchlist: (String) -> Unit = {},
    activeFilter: SectorFilter = SectorFilter.ALL,
    onFilterSelected: (SectorFilter) -> Unit = {},
) {

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = AppColors.Surface),
        ) {
            item {
                SectorFilterChips(
                    activeFilter = activeFilter,
                    onFilterSelected = { filter -> onFilterSelected(filter) })
            }
            if(stockList.isNotEmpty()) {
                items(stockList, key = { stock -> stock.symbol.value }) { stock ->
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

@Preview(showBackground = true)
@Composable
fun StockListPreview() {
    StockList(
        stockList = listOf(previewStockTech, previewStockCrypto),
        watchlistSymbols = setOf("AAPL"),
    )
}