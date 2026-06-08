package com.example.tickerwatch.presentation.screen.main.component.marketlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.StockSummary
import com.example.tickerwatch.presentation.component.stockdialog.StockDetailsOverlay
import com.example.tickerwatch.presentation.model.StockChartViewUiState
import com.example.tickerwatch.presentation.screen.main.component.marketlist.listitem.StockUiListItem
import com.example.tickerwatch.presentation.screen.main.component.marketlist.sectorfilter.SectorFilter
import com.example.tickerwatch.presentation.screen.main.component.marketlist.sectorfilter.SectorFilterChips
import com.example.tickerwatch.presentation.theme.AppColors

@Composable
fun StockList(
    filteredList: List<StockSummary>,
    activeFilter: SectorFilter,
    setActiveFilter: (SectorFilter) -> Unit,
    stockDetailsOverlayIsShown: Boolean,
    toggleStockDetailsOverlay: (Boolean) -> Unit,
    stockChartViewUiState: StockChartViewUiState,
    currentSparkItem: StockSummary?,
    watchlistSymbols: Set<String> = emptySet(),
    onSymbolSelected: (String) -> Unit,
    onRangeChange: (Range) -> Unit,
    onToggleWatchlist: (String) -> Unit = {}
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
                    onFilterSelected = setActiveFilter)
            }
            if(filteredList.isNotEmpty()) {
                items(filteredList, key = { stock -> stock.symbol }) { stock ->
                    StockUiListItem(
                        stock = stock,
                        isInWatchlist = stock.symbol in watchlistSymbols,
                        onClickListener = {
                            toggleStockDetailsOverlay(true)
                            onSymbolSelected(stock.symbol)
                        },
                        onToggleWatchlist = { onToggleWatchlist(stock.symbol) }
                    )
                }
            }
        }
        StockDetailsOverlay(
            itemIsSelected = stockDetailsOverlayIsShown,
            stockChartViewUiState = stockChartViewUiState,
            currentSparkItem = currentSparkItem,
            onRangeChange = onRangeChange,
            onDismiss = { toggleStockDetailsOverlay(false) }
        )
    }
}