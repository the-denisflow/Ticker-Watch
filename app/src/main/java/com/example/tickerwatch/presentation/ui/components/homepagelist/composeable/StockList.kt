package com.example.tickerwatch.presentation.ui.components.homepagelist.composeable

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.tickerwatch.common.tickers.CryptoEnum
import com.example.tickerwatch.common.tickers.Sector
import com.example.tickerwatch.common.tickers.StockMarketEnum
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.SparkStockUiItem
import com.example.tickerwatch.presentation.ui.components.shared.StockUiItem
import com.example.tickerwatch.presentation.ui.components.stockdetaildialog.composable.StockDetailsDialog
import com.example.tickerwatch.presentation.ui.components.stockdetaildialog.state.StockState
import com.example.tickerwatch.presentation.ui.theme.AppColors
import com.example.tickerwatch.presentation.ui.theme.AppDimens
import com.example.tickerwatch.presentation.ui.theme.AppType

private enum class SectorFilter(val label: String) {
    ALL("All"),
    TECH("Tech"),
    FINANCE("Finance"),
    HEALTH("Health"),
    CRYPTO("Crypto"),
}

@Composable
fun StockList(
    list: List<SparkStockUiItem>,
    stockState: StockState,
    currentSparkItem: SparkStockUiItem?,
    watchlistSymbols: Set<String> = emptySet(),
    onSymbolSelected: (String) -> Unit,
    onRangeChange: (Range) -> Unit,
    onToggleWatchlist: (String) -> Unit = {}
) {
    var itemIsSelected by remember { mutableStateOf(false) }
    var activeFilter by remember { mutableStateOf(SectorFilter.ALL) }

    val filteredList = remember(list, activeFilter) {
        when (activeFilter) {
            SectorFilter.ALL -> list
            SectorFilter.TECH -> list.filter { (it.ticker as? StockMarketEnum)?.sector == Sector.TECHNOLOGY }
            SectorFilter.FINANCE -> list.filter { (it.ticker as? StockMarketEnum)?.sector == Sector.FINANCE }
            SectorFilter.HEALTH -> list.filter { (it.ticker as? StockMarketEnum)?.sector == Sector.HEALTHCARE }
            SectorFilter.CRYPTO -> list.filter { it.ticker is CryptoEnum }
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
                    onFilterSelected = { activeFilter = it }
                )
            }
            if(list.isNotEmpty()) {
                items(filteredList, key = { stock -> stock.symbol }) { stock ->
                    StockUiItem(
                        stock = stock,
                        isInWatchlist = stock.symbol in watchlistSymbols,
                        onClickListener = {
                            itemIsSelected = true
                            onSymbolSelected(stock.symbol)
                        },
                        onToggleWatchlist = { onToggleWatchlist(stock.symbol) }
                    )
                }
            } else {
                items(11) {

                }
            }
        }
        if (itemIsSelected) {
            StockDetailsDialog(
                stockState = stockState,
                currentSparkItem = currentSparkItem,
                onRangeChange = onRangeChange,
                onDismiss = { itemIsSelected = false }
            )
        }
    }
}

@Composable
private fun SectorFilterChips(
    activeFilter: SectorFilter,
    onFilterSelected: (SectorFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = AppDimens.Space16, vertical = AppDimens.Space8),
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Space8)
    ) {
        SectorFilter.entries.forEach { filter ->
            FilterChip(
                selected = filter == activeFilter,
                onClick = { onFilterSelected(filter) },
                label = { Text(text = filter.label, fontSize = AppType.Body) },
                shape = RoundedCornerShape(AppDimens.CornerPill),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = AppColors.SurfaceVariant,
                    labelColor = AppColors.Secondary,
                    selectedContainerColor = AppColors.Primary,
                    selectedLabelColor = AppColors.Surface
                ),
                border = null
            )
        }
    }
}