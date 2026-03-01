package com.example.kotlin_app.presentation.ui.components.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.SortByAlpha
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.domain.repository.model.SparkStockUiItem
import com.example.kotlin_app.presentation.ui.components.homepagelist.composeable.LoadingState
import com.example.kotlin_app.presentation.ui.components.homepagelist.composeable.StockList
import com.example.kotlin_app.presentation.ui.components.portfolio.PortfolioScreen
import com.example.kotlin_app.presentation.ui.components.stockdetaildialog.state.StockState
import com.example.kotlin_app.presentation.ui.components.watchlist.WatchlistScreen
import com.example.kotlin_app.presentation.viewmodel.SortOption
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class BottomTab(val label: String, val icon: ImageVector) {
    MARKETS("Markets", Icons.Rounded.BarChart),
    WATCHLIST("Watchlist", Icons.Rounded.BookmarkBorder),
    PORTFOLIO("Portfolio", Icons.Rounded.PieChart),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    stockList: List<SparkStockUiItem>,
    stockState: StockState,
    currentSparkItem: SparkStockUiItem?,
    sortOption: SortOption,
    watchlistSymbols: Set<String>,
    onSymbolSelected: (String) -> Unit,
    onRangeChange: (Range) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onToggleWatchlist: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(BottomTab.MARKETS) }
    var showSortSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            MainBottomBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                BottomTab.MARKETS -> {
                    if (stockList.isEmpty()) {
                        LoadingState()
                    } else {
                        Column {
                            MarketsHeader(
                                stockCount = stockList.size,
                                sortOption = sortOption,
                                onSortClick = { showSortSheet = true }
                            )
                            StockList(
                                list = stockList,
                                stockState = stockState,
                                currentSparkItem = currentSparkItem,
                                watchlistSymbols = watchlistSymbols,
                                onSymbolSelected = onSymbolSelected,
                                onRangeChange = onRangeChange,
                                onToggleWatchlist = onToggleWatchlist
                            )
                        }
                    }
                }
                BottomTab.WATCHLIST -> WatchlistScreen(
                    items = stockList.filter { it.symbol in watchlistSymbols },
                    stockState = stockState,
                    currentSparkItem = currentSparkItem,
                    onSymbolSelected = onSymbolSelected,
                    onRangeChange = onRangeChange,
                    onToggleWatchlist = onToggleWatchlist
                )
                BottomTab.PORTFOLIO -> PortfolioScreen(
                    holdings = stockList.filter { it.symbol in watchlistSymbols },
                    stockState = stockState,
                    currentSparkItem = currentSparkItem,
                    onSymbolSelected = onSymbolSelected,
                    onRangeChange = onRangeChange
                )
            }
        }
    }

    if (showSortSheet) {
        SortBottomSheet(
            currentOption = sortOption,
            onOptionSelected = { option ->
                onSortChange(option)
                showSortSheet = false
            },
            onDismiss = { showSortSheet = false }
        )
    }
}

@Composable
private fun MarketsHeader(
    stockCount: Int,
    sortOption: SortOption,
    onSortClick: () -> Unit
) {
    val today = remember {
        SimpleDateFormat("EEE, MMM yyyy", Locale.getDefault()).format(Date())
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(start = 20.dp, end = 16.dp, top = 20.dp, bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Markets",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1C1E)
            )
            SortButton(
                label = if (sortOption == SortOption.DEFAULT) "Sort" else sortOption.label,
                onClick = onSortClick
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$stockCount assets · $today",
            fontSize = 13.sp,
            color = Color(0xFF8E8E93)
        )
    }
}

@Composable
private fun SortButton(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(Color(0xFFF2F2F7), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.FilterList,
            contentDescription = "Sort",
            modifier = Modifier.size(16.dp),
            tint = Color(0xFF1C1C1E)
        )
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1C1C1E)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortBottomSheet(
    currentOption: SortOption,
    onOptionSelected: (SortOption) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Sort by",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1C1E),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            SortOption.entries.forEach { option ->
                SortOptionRow(
                    option = option,
                    isSelected = option == currentOption,
                    onClick = { onOptionSelected(option) }
                )
            }
        }
    }
}

@Composable
private fun SortOptionRow(
    option: SortOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (icon, description) = when (option) {
        SortOption.DEFAULT -> Pair(Icons.Rounded.FilterList, "Original order")
        SortOption.NAME_ASC -> Pair(Icons.Rounded.SortByAlpha, "Alphabetical order")
        SortOption.PRICE_DESC -> Pair(Icons.Rounded.ArrowDownward, "Highest price first")
        SortOption.CHANGE_DESC -> Pair(Icons.Rounded.Timeline, "Biggest movers first")
        SortOption.SECTOR -> Pair(Icons.Rounded.BarChart, "Group by sector")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isSelected) Color(0xFF1C1C1E) else Color(0xFFF2F2F7),
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (isSelected) Color.White else Color(0xFF8E8E93)
                )
            }
            Column {
                Text(
                    text = option.label,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1C1C1E)
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color(0xFF8E8E93)
                )
            }
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = Color(0xFF1C1C1E),
                modifier = Modifier.size(22.dp)
            )
        }
    }
    HorizontalDivider(color = Color(0xFFF2F2F7), thickness = 0.5.dp)
}

@Composable
private fun MainBottomBar(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        BottomTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(imageVector = tab.icon, contentDescription = tab.label)
                },
                label = {
                    Text(text = tab.label, fontSize = 10.sp)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF1C1C1E),
                    selectedTextColor = Color(0xFF1C1C1E),
                    unselectedIconColor = Color(0xFF8E8E93),
                    unselectedTextColor = Color(0xFF8E8E93),
                    indicatorColor = Color(0xFFF2F2F7)
                )
            )
        }
    }
}