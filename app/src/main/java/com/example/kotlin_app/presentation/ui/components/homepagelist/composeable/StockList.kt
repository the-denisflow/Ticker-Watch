package com.example.kotlin_app.presentation.ui.components.homepagelist.composeable

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_app.common.tickers.CryptoEnum
import com.example.kotlin_app.common.tickers.Sector
import com.example.kotlin_app.common.tickers.StockMarketEnum
import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.domain.repository.model.SparkStockUiItem
import com.example.kotlin_app.presentation.ui.components.shared.StockUiItem
import com.example.kotlin_app.presentation.ui.components.stockdetaildialog.composable.StockDetailsDialog
import com.example.kotlin_app.presentation.ui.components.stockdetaildialog.state.StockState

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
                .background(color = Color.White),
        ) {
            item {
                SectorFilterChips(
                    activeFilter = activeFilter,
                    onFilterSelected = { activeFilter = it }
                )
            }
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectorFilter.entries.forEach { filter ->
            FilterChip(
                selected = filter == activeFilter,
                onClick = { onFilterSelected(filter) },
                label = { Text(text = filter.label, fontSize = 13.sp) },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0xFFF2F2F7),
                    labelColor = Color(0xFF8E8E93),
                    selectedContainerColor = Color(0xFF1C1C1E),
                    selectedLabelColor = Color.White
                ),
                border = null
            )
        }
    }
}