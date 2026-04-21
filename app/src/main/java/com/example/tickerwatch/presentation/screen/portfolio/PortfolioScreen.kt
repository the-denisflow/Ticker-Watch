package com.example.tickerwatch.presentation.screen.portfolio

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

private const val MOCK_SHARES = 10

@Composable
fun PortfolioScreen(
    holdings: List<SparkStockUiItem>,
    stockChartUiState: StockChartUiState,
    currentSparkItem: SparkStockUiItem?,
    onSymbolSelected: (String) -> Unit,
    onRangeChange: (Range) -> Unit
) {
    if (holdings.isEmpty()) {
        PortfolioEmptyState()
    } else {
        PortfolioContent(
            holdings = holdings,
            stockChartUiState = stockChartUiState,
            currentSparkItem = currentSparkItem,
            onSymbolSelected = onSymbolSelected,
            onRangeChange = onRangeChange
        )
    }
}

@Composable
private fun PortfolioContent(
    holdings: List<SparkStockUiItem>,
    stockChartUiState: StockChartUiState,
    currentSparkItem: SparkStockUiItem?,
    onSymbolSelected: (String) -> Unit,
    onRangeChange: (Range) -> Unit
) {
    var itemIsSelected by remember { mutableStateOf(false) }

    val totalValue = holdings.sumOf { it.close * MOCK_SHARES }
    val totalDailyChange = holdings.sumOf { stock ->
        val pct = stock.trend.progressPercent
            .replace("%", "").replace("+", "")
            .toDoubleOrNull() ?: 0.0
        val signedPct = if (stock.trend.progressTrend == PriceTrend.DOWN) -pct else pct
        (stock.close * MOCK_SHARES) * signedPct / 100.0
    }
    val totalDailyPct = if ((totalValue - totalDailyChange) > 0)
        totalDailyChange / (totalValue - totalDailyChange) * 100.0
    else 0.0

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Surface)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppColors.Surface)
                        .padding(start = AppDimens.Space20, end = AppDimens.Space16, top = AppDimens.Space20, bottom = AppDimens.Space4)
                ) {
                    Text(
                        text = "Portfolio",
                        fontSize = AppType.PageTitle,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Primary
                    )
                }
            }
            item {
                SummaryCard(
                    totalValue = totalValue,
                    dailyChange = totalDailyChange,
                    dailyPct = totalDailyPct
                )
            }
            item {
                Text(
                    text = "Holdings",
                    fontSize = AppType.SectionTitle,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary,
                    modifier = Modifier.padding(horizontal = AppDimens.Space20, vertical = AppDimens.Space12)
                )
            }
            items(holdings, key = { it.symbol }) { stock ->
                HoldingRow(
                    stock = stock,
                    onClick = {
                        itemIsSelected = true
                        onSymbolSelected(stock.symbol)
                    }
                )
            }
            item { Spacer(modifier = Modifier.height(AppDimens.Space24)) }
        }

        StockDetailOverlay(
            itemIsSelected = itemIsSelected,
            stockChartUiState = stockChartUiState,
            currentSparkItem = currentSparkItem,
            onRangeChange = onRangeChange,
            onDismiss = { itemIsSelected = false }
        )
        }
    }

@Composable
private fun SummaryCard(totalValue: Double, dailyChange: Double, dailyPct: Double) {
    val isUp = dailyChange >= 0
    val arrow = if (isUp) "▲" else "▼"
    val gradientColors = if (isUp)
        listOf(AppColors.PortfolioGainDark, AppColors.PortfolioGainLight)
    else
        listOf(AppColors.PortfolioLossDark, AppColors.PortfolioLossLight)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimens.Space20, vertical = AppDimens.Space8)
            .background(
                brush = Brush.linearGradient(gradientColors),
                shape = RoundedCornerShape(AppDimens.CornerPill)
            )
            .padding(AppDimens.Space24)
    ) {
        Column {
            Text(
                text = "Total Value",
                fontSize = AppType.Body,
                color = AppColors.OnGradientCaption
            )
            Spacer(modifier = Modifier.height(AppDimens.Space8))
            Text(
                text = "$%.2f".format(totalValue),
                fontSize = AppType.DisplayLarge,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnGradient
            )
            Spacer(modifier = Modifier.height(AppDimens.Space10))
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Space8),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(AppColors.OnGradientOverlay, RoundedCornerShape(AppDimens.CornerMd))
                        .padding(horizontal = AppDimens.Space10, vertical = AppDimens.Space4)
                ) {
                    Text(
                        text = "$arrow ${"%.2f".format(kotlin.math.abs(dailyPct))}%",
                        fontSize = AppType.Body,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.OnGradient
                    )
                }
                Text(
                    text = "%s$%.2f today".format(
                        if (isUp) "+" else "-",
                        kotlin.math.abs(dailyChange)
                    ),
                    fontSize = AppType.Body,
                    color = AppColors.OnGradientMuted
                )
            }
            Spacer(modifier = Modifier.height(AppDimens.Space6))
            Text(
                text = "$MOCK_SHARES shares per position · Simulated",
                fontSize = AppType.Badge,
                color = AppColors.OnGradientDisabled
            )
        }
    }
}

@Composable
private fun HoldingRow(stock: SparkStockUiItem, onClick: () -> Unit) {
    val currentValue = stock.close * MOCK_SHARES
    val pct = stock.trend.progressPercent
        .replace("%", "").replace("+", "")
        .toDoubleOrNull() ?: 0.0
    val signedPct = if (stock.trend.progressTrend == PriceTrend.DOWN) -pct else pct
    val dailyChange = currentValue * signedPct / 100.0

    val (trendColor, arrow) = when (stock.trend.progressTrend) {
        PriceTrend.UP -> Pair(AppColors.TrendUp, "▲")
        PriceTrend.DOWN -> Pair(AppColors.TrendDown, "▼")
        PriceTrend.NEUTRAL -> Pair(AppColors.Secondary, "–")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimens.Space20, vertical = AppDimens.Space14),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Space12),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = when(val logo = stock.ticker.logo ) {
                        is LogoResource.Res ->  painterResource(id = logo.resId)
                        is LogoResource.Url ->  rememberAsyncImagePainter(model = logo.url)
                        else ->  rememberAsyncImagePainter(null)
                    },
                    contentDescription = stock.ticker.tickerName,
                    modifier = Modifier
                        .size(AppDimens.IconXl)
                        .clip(CircleShape),
                    tint = Color.Unspecified
                )
                Column {
                    Text(
                        text = stock.ticker.tickerName,
                        fontSize = AppType.BodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.Primary
                    )
                    Text(
                        text = "$MOCK_SHARES shares · ${stock.ticker.symbol}",
                        fontSize = AppType.Caption,
                        color = AppColors.Secondary
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$%.2f".format(currentValue),
                    fontSize = AppType.BodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Primary
                )
                Text(
                    text = "$arrow ${"%.2f".format(kotlin.math.abs(dailyChange))}",
                    fontSize = AppType.Caption,
                    fontWeight = FontWeight.Medium,
                    color = trendColor
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = AppDimens.Space20),
            thickness = AppDimens.DividerThickness,
            color = AppColors.Divider
        )
    }
}

@Composable
private fun PortfolioEmptyState() {
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
                    imageVector = Icons.Rounded.PieChart,
                    contentDescription = null,
                    modifier = Modifier.size(AppDimens.IconLg),
                    tint = AppColors.Tertiary
                )
            }
            Spacer(modifier = Modifier.height(AppDimens.Space16))
            Text(
                text = "No holdings yet",
                fontSize = AppType.SectionTitle,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Primary
            )
            Spacer(modifier = Modifier.height(AppDimens.Space6))
            Text(
                text = "Add stocks to your watchlist to\nsee them here as portfolio holdings",
                fontSize = AppType.BodyMedium,
                color = AppColors.Secondary,
                textAlign = TextAlign.Center
            )
        }
    }
}