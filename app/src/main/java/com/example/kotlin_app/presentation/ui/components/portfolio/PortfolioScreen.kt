package com.example.kotlin_app.presentation.ui.components.portfolio

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.kotlin_app.domain.repository.model.PriceTrend
import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.domain.repository.model.SparkStockUiItem
import com.example.kotlin_app.presentation.ui.components.stockdetaildialog.composable.StockDetailsDialog
import com.example.kotlin_app.presentation.ui.components.stockdetaildialog.state.StockState

private const val MOCK_SHARES = 10

@Composable
fun PortfolioScreen(
    holdings: List<SparkStockUiItem>,
    stockState: StockState,
    currentSparkItem: SparkStockUiItem?,
    onSymbolSelected: (String) -> Unit,
    onRangeChange: (Range) -> Unit
) {
    if (holdings.isEmpty()) {
        PortfolioEmptyState()
    } else {
        PortfolioContent(
            holdings = holdings,
            stockState = stockState,
            currentSparkItem = currentSparkItem,
            onSymbolSelected = onSymbolSelected,
            onRangeChange = onRangeChange
        )
    }
}

@Composable
private fun PortfolioContent(
    holdings: List<SparkStockUiItem>,
    stockState: StockState,
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
                .background(Color.White)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(start = 20.dp, end = 16.dp, top = 20.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = "Portfolio",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1C1E)
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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1C1E),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
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
            item { Spacer(modifier = Modifier.height(24.dp)) }
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
private fun SummaryCard(totalValue: Double, dailyChange: Double, dailyPct: Double) {
    val isUp = dailyChange >= 0
    val arrow = if (isUp) "▲" else "▼"
    val gradientColors = if (isUp)
        listOf(Color(0xFF1A6B3C), Color(0xFF2E7D32))
    else
        listOf(Color(0xFF9B1B1B), Color(0xFFC62828))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .background(
                brush = Brush.linearGradient(gradientColors),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(24.dp)
    ) {
        Column {
            Text(
                text = "Total Value",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$%.2f".format(totalValue),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$arrow ${"%.2f".format(kotlin.math.abs(dailyPct))}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
                Text(
                    text = "%s$%.2f today".format(
                        if (isUp) "+" else "-",
                        kotlin.math.abs(dailyChange)
                    ),
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "$MOCK_SHARES shares per position · Simulated",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.5f)
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
        PriceTrend.UP -> Pair(Color(0xFF2E7D32), "▲")
        PriceTrend.DOWN -> Pair(Color(0xFFC62828), "▼")
        PriceTrend.NEUTRAL -> Pair(Color(0xFF8E8E93), "–")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = stock.ticker.logoRes?.let { painterResource(id = it) }
                        ?: rememberAsyncImagePainter(stock.ticker.urlLogo),
                    contentDescription = stock.ticker.tickerName,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    tint = Color.Unspecified
                )
                Column {
                    Text(
                        text = stock.ticker.tickerName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1C1C1E)
                    )
                    Text(
                        text = "$MOCK_SHARES shares · ${stock.ticker.symbol}",
                        fontSize = 12.sp,
                        color = Color(0xFF8E8E93)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$%.2f".format(currentValue),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1C1C1E)
                )
                Text(
                    text = "$arrow ${"%.2f".format(kotlin.math.abs(dailyChange))}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = trendColor
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp),
            thickness = 0.5.dp,
            color = Color(0xFFEEEEEE)
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
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(Color(0xFFF2F2F7), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.PieChart,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = Color(0xFFBEBEC0)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No holdings yet",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1C1C1E)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Add stocks to your watchlist to\nsee them here as portfolio holdings",
                fontSize = 14.sp,
                color = Color(0xFF8E8E93),
                textAlign = TextAlign.Center
            )
        }
    }
}