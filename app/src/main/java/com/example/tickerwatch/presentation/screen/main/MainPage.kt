package com.example.tickerwatch.presentation.screen.main

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.SparkStockUiItem
import com.example.tickerwatch.presentation.screen.marketlist.StockList
import com.example.tickerwatch.presentation.screen.portfolio.PortfolioScreen
import com.example.tickerwatch.presentation.screen.stockdetail.StockState
import com.example.tickerwatch.presentation.screen.watchlist.WatchlistScreen
import com.example.tickerwatch.presentation.theme.AppColors
import com.example.tickerwatch.presentation.theme.AppDimens
import com.example.tickerwatch.presentation.theme.AppType
import com.example.tickerwatch.presentation.viewmodel.SortOption
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
        containerColor = AppColors.Surface,
        bottomBar = {
            MainBottomBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                BottomTab.MARKETS -> {
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
            .background(AppColors.Surface)
            .padding(start = AppDimens.Space20, end = AppDimens.Space16, top = AppDimens.Space20, bottom = AppDimens.Space12)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Markets",
                fontSize = AppType.PageTitle,
                fontWeight = FontWeight.Bold,
                color = AppColors.Primary
            )
            SortButton(
                label = if (sortOption == SortOption.DEFAULT) "Sort" else sortOption.label,
                onClick = onSortClick
            )
        }
        Spacer(modifier = Modifier.height(AppDimens.Space4))
        Text(
            text = "$stockCount assets · $today",
            fontSize = AppType.Body,
            color = AppColors.Secondary
        )
    }
}

@Composable
private fun SortButton(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(AppColors.SurfaceVariant, RoundedCornerShape(AppDimens.CornerPill))
            .clickable { onClick() }
            .padding(horizontal = AppDimens.Space12, vertical = AppDimens.Space7),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Space4)
    ) {
        Icon(
            imageVector = Icons.Rounded.FilterList,
            contentDescription = "Sort",
            modifier = Modifier.size(AppDimens.IconXs),
            tint = AppColors.Primary
        )
        Text(
            text = label,
            fontSize = AppType.Body,
            fontWeight = FontWeight.Medium,
            color = AppColors.Primary
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
        containerColor = AppColors.Surface,
        shape = RoundedCornerShape(topStart = AppDimens.CornerModal, topEnd = AppDimens.CornerModal)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimens.Space20)
                .padding(bottom = AppDimens.Space32)
        ) {
            Text(
                text = "Sort by",
                fontSize = AppType.SectionTitle,
                fontWeight = FontWeight.Bold,
                color = AppColors.Primary,
                modifier = Modifier.padding(bottom = AppDimens.Space8)
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
            .padding(vertical = AppDimens.Space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Space12)
        ) {
            Box(
                modifier = Modifier
                    .size(AppDimens.IconXl)
                    .background(
                        if (isSelected) AppColors.Primary else AppColors.SurfaceVariant,
                        RoundedCornerShape(AppDimens.CornerLg)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(AppDimens.IconSm),
                    tint = if (isSelected) AppColors.Surface else AppColors.Secondary
                )
            }
            Column {
                Text(
                    text = option.label,
                    fontSize = AppType.BodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Primary
                )
                Text(
                    text = description,
                    fontSize = AppType.Caption,
                    color = AppColors.Secondary
                )
            }
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = AppColors.Primary,
                modifier = Modifier.size(AppDimens.IconCheck)
            )
        }
    }
    HorizontalDivider(color = AppColors.SurfaceVariant, thickness = AppDimens.DividerThickness)
}

@Composable
private fun MainBottomBar(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit
) {
    NavigationBar(
        containerColor = AppColors.Surface,
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
                    Text(text = tab.label, fontSize = AppType.NavLabel)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AppColors.Primary,
                    selectedTextColor = AppColors.Primary,
                    unselectedIconColor = AppColors.Secondary,
                    unselectedTextColor = AppColors.Secondary,
                    indicatorColor = AppColors.SurfaceVariant
                )
            )
        }
    }
}