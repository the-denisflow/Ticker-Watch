package com.example.tickerwatch.presentation.screen.watchlist

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.tickerwatch.common.tickers.LogoResource
import com.example.tickerwatch.domain.repository.model.PriceTrend
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.SparkStockUiItem
import com.example.tickerwatch.presentation.screen.stockdetail.StockDetailOverlay
import com.example.tickerwatch.presentation.screen.stockdetail.StockChartUiState
import com.example.tickerwatch.presentation.theme.AppColors
import com.example.tickerwatch.presentation.theme.AppDimens
import com.example.tickerwatch.presentation.theme.AppType

@Composable
fun WatchlistScreen(
    items: List<SparkStockUiItem>,
    stockChartUiState: StockChartUiState,
    currentSparkItem: SparkStockUiItem?,
    onSymbolSelected: (String) -> Unit,
    onRangeChange: (Range) -> Unit,
    onToggleWatchlist: (String) -> Unit = {}
) {
    if (items.isEmpty()) {
        WatchlistEmptyState()
    } else {
        WatchlistGrid(
            items = items,
            StockChartUiState = stockChartUiState,
            currentSparkItem = currentSparkItem,
            onSymbolSelected = onSymbolSelected,
            onRangeChange = onRangeChange,
            onToggleWatchlist = onToggleWatchlist
        )
    }
}

@Composable
private fun WatchlistGrid(
    items: List<SparkStockUiItem>,
    StockChartUiState: StockChartUiState,
    currentSparkItem: SparkStockUiItem?,
    onSymbolSelected: (String) -> Unit,
    onRangeChange: (Range) -> Unit,
    onToggleWatchlist: (String) -> Unit
) {
    var itemIsSelected by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppDimens.Space12, vertical = AppDimens.Space8),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Space12),
            verticalArrangement = Arrangement.spacedBy(AppDimens.Space12)
        ) {
            items(items, key = { it.symbol }) { stock ->
                WatchlistCard(
                    stock = stock,
                    onRemove = { onToggleWatchlist(stock.symbol) },
                    modifier = Modifier.clickable {
                        itemIsSelected = true
                        onSymbolSelected(stock.symbol)
                    }
                )
            }
        }

        StockDetailOverlay(
            itemIsSelected = itemIsSelected,
            stockChartUiState = StockChartUiState,
            currentSparkItem = currentSparkItem,
            onRangeChange = onRangeChange,
            onDismiss = { itemIsSelected = false }
        )
    }
}

@Composable
private fun WatchlistEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(AppDimens.Space32)
        ) {
            Box(
                modifier = Modifier
                    .size(AppDimens.EmptyStateIconBox)
                    .background(AppColors.SurfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.BookmarkBorder,
                    contentDescription = null,
                    modifier = Modifier.size(AppDimens.IconLg),
                    tint = AppColors.Tertiary
                )
            }
            Spacer(modifier = Modifier.height(AppDimens.Space16))
            Text(
                text = "No stocks saved",
                fontSize = AppType.SectionTitle,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Primary
            )
            Spacer(modifier = Modifier.height(AppDimens.Space6))
            Text(
                text = "Tap the bookmark icon on any stock\nto add it to your watchlist",
                fontSize = AppType.BodyMedium,
                color = AppColors.Secondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WatchlistCard(
    stock: SparkStockUiItem,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, arrow) = when (stock.trend.progressTrend) {
        PriceTrend.UP -> Triple(AppColors.TrendUpSurface, AppColors.TrendUp, "▲")
        PriceTrend.DOWN -> Triple(AppColors.TrendDownSurface, AppColors.TrendDown, "▼")
        PriceTrend.NEUTRAL -> Triple(AppColors.SurfaceVariant, AppColors.Secondary, "–")
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(AppDimens.CardShadow, RoundedCornerShape(AppDimens.CornerWatchlistCard)),
        shape = RoundedCornerShape(AppDimens.CornerWatchlistCard),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(AppDimens.Space12)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = when (val logo = stock.ticker.logo) {
                        is LogoResource.Res -> painterResource(id = logo.resId)
                        is LogoResource.Url -> rememberAsyncImagePainter(logo.url)
                        null -> rememberAsyncImagePainter(null)
                    },
                    contentDescription = "${stock.ticker.tickerName} logo",
                    modifier = Modifier
                        .size(AppDimens.IconLg)
                        .clip(CircleShape),
                    tint = Color.Unspecified
                )
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(AppDimens.IconMd)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.BookmarkBorder,
                        contentDescription = "Remove from watchlist",
                        tint = AppColors.Tertiary,
                        modifier = Modifier.size(AppDimens.IconXs)
                    )
                }
            }
            Spacer(modifier = Modifier.height(AppDimens.Space8))
            Text(
                text = stock.ticker.tickerName,
                fontWeight = FontWeight.Bold,
                fontSize = AppType.SectionTitle,
                color = AppColors.Primary
            )
            Text(
                text = stock.ticker.symbol,
                fontSize = AppType.Caption,
                color = AppColors.Secondary
            )
            Spacer(modifier = Modifier.height(AppDimens.Space10))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$%.2f".format(stock.close),
                    fontWeight = FontWeight.Bold,
                    fontSize = AppType.CardTitle,
                    color = AppColors.Primary
                )
                Box(
                    modifier = Modifier
                        .background(bgColor, RoundedCornerShape(AppDimens.CornerSm))
                        .padding(horizontal = AppDimens.Space6, vertical = AppDimens.Space3)
                ) {
                    Text(
                        text = "$arrow ${stock.trend.progressPercent}",
                        fontSize = AppType.Badge,
                        fontWeight = FontWeight.Medium,
                        color = textColor
                    )
                }
            }
        }
    }
}