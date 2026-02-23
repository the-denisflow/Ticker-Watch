package com.example.kotlin_app.presentation.ui.components.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.domain.repository.model.SparkStockUiItem
import com.example.kotlin_app.presentation.ui.components.homepagelist.composeable.LoadingState
import com.example.kotlin_app.presentation.ui.components.homepagelist.composeable.StockList
import com.example.kotlin_app.presentation.ui.components.stockdetaildialog.state.StockState
import com.example.kotlin_app.presentation.ui.uimodels.TopIconData
import com.example.kotlin_app.presentation.ui.utils.MainPageData.topIconAdd
import com.example.kotlin_app.presentation.ui.utils.MainPageData.topIconSort
import com.example.kotlin_app.presentation.ui.utils.MainPageDimens.headerHeight
import com.example.kotlin_app.presentation.ui.utils.MainPageDimens.headerHorizontalMargin
import com.example.kotlin_app.presentation.ui.utils.MainPageDimens.headerTopMargin
import com.example.kotlin_app.presentation.ui.utils.MainPageDimens.iconSize

private enum class BottomTab(val label: String, val icon: ImageVector) {
    MARKETS("Markets", Icons.Rounded.BarChart),
    WATCHLIST("Watchlist", Icons.Rounded.BookmarkBorder),
    PORTFOLIO("Portfolio", Icons.Rounded.PieChart),
    SETTINGS("Settings", Icons.Rounded.Settings),
}

@Composable
fun MainPage(
    stockList: List<SparkStockUiItem>,
    stockState: StockState,
    currentSparkItem: SparkStockUiItem?,
    onSymbolSelected: (String) -> Unit,
    onRangeChange: (Range) -> Unit
) {
    var selectedTab by remember { mutableStateOf(BottomTab.MARKETS) }

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
                            MainPageHeader()
                            StockList(
                                list = stockList,
                                stockState = stockState,
                                currentSparkItem = currentSparkItem,
                                onSymbolSelected = onSymbolSelected,
                                onRangeChange = onRangeChange
                            )
                        }
                    }
                }
                else -> PlaceholderScreen(label = selectedTab.label)
            }
        }
    }
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

@Composable
private fun PlaceholderScreen(label: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$label coming soon",
            fontSize = 16.sp,
            color = Color(0xFF8E8E93)
        )
    }
}

@Preview
@Composable
fun MainPageHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .height(headerHeight)
            .padding(top = headerTopMargin)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = headerHorizontalMargin),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TopIcon(data = topIconSort)
            TopIcon(data = topIconAdd)
        }
    }
}

@Composable
fun TopIcon(data: TopIconData) {
    Icon(
        modifier = Modifier.size(iconSize),
        imageVector = data.imageVector,
        contentDescription = data.contentDescription,
        tint = Color.Unspecified
    )
}