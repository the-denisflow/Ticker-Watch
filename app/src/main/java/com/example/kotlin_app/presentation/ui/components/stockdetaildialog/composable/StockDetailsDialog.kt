package com.example.kotlin_app.presentation.ui.components.stockdetaildialog.composable

import android.view.LayoutInflater
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.kotlin_app.R
import com.example.kotlin_app.common.tickers.CryptoEnum
import com.example.kotlin_app.common.tickers.StockMarketEnum
import com.example.kotlin_app.common.tickers.Ticker
import com.example.kotlin_app.domain.repository.model.IntervalRangeValidator
import com.example.kotlin_app.domain.repository.model.PriceTrend
import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.domain.repository.model.SparkStockUiItem
import com.example.kotlin_app.domain.repository.model.StockItem
import com.example.kotlin_app.presentation.ui.components.chart.utils.plotDiagram
import com.example.kotlin_app.presentation.ui.components.homepagelist.composeable.StockInfoRow
import com.example.kotlin_app.presentation.ui.components.homepagelist.composeable.StockPriceInfoColum
import com.example.kotlin_app.presentation.ui.components.stockdetaildialog.state.StockState
import com.github.mikephil.charting.charts.LineChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailsDialog(
    stockState: StockState,
    currentSparkItem: SparkStockUiItem?,
    onRangeChange: (Range) -> Unit,
    onDismiss: () -> Unit = {},
) {
    ModalBottomSheet(
        scrimColor = Color.Black.copy(alpha = 0.3f),
        dragHandle = { DialogHeader() },
        onDismissRequest = { onDismiss() },
    ) {
        Box(Modifier.background(Color.White)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                currentSparkItem?.let { Header(displayedItem = it) }
                Spacer(modifier = Modifier.height(8.dp))

                val validPrices = remember(stockState.item.prices) {
                    stockState.item.prices.filter { !it.isNaN() }
                }
                val periodHigh = remember(validPrices) { validPrices.maxOrNull() }
                val periodLow = remember(validPrices) { validPrices.minOrNull() }

                StockChart(
                    displayedRange = stockState.range,
                    displayedItem = stockState.item,
                    onRangeChange = onRangeChange
                )
                currentSparkItem?.let { StockMetaRow(ticker = it.ticker) }
                StockDetailsSection(
                    item = stockState.item,
                    periodLabel = stockState.range.value,
                    periodHigh = periodHigh,
                    periodLow = periodLow
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun Header(displayedItem: SparkStockUiItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StockInfoRow(displayedItem, iconSize = 48.dp)
        StockPriceInfoColum(stock = displayedItem, subLabel = "from yesterday")
    }
}

@Composable
private fun StockChart(
    displayedRange: Range,
    displayedItem: StockItem,
    onRangeChange: (Range) -> Unit
) {
    val context = LocalContext.current
    val density = context.resources.displayMetrics.density
    val screenWidth = (context.resources.displayMetrics.widthPixels / density).dp

    val validPrices = remember(displayedItem.prices) {
        displayedItem.prices.filter { !it.isNaN() }
    }
    val periodTrend = remember(validPrices) {
        if (validPrices.size >= 2) when {
            validPrices.last() > validPrices.first() -> PriceTrend.UP
            validPrices.last() < validPrices.first() -> PriceTrend.DOWN
            else -> PriceTrend.NEUTRAL
        } else PriceTrend.NEUTRAL
    }
    val periodPercent = remember(validPrices) {
        if (validPrices.size >= 2 && validPrices.first() != 0.0) {
            val pct = ((validPrices.last() - validPrices.first()) / validPrices.first()) * 100.0
            "%.2f%%".format(kotlin.math.abs(pct))
        } else null
    }
    val periodAbsoluteChange = remember(validPrices) {
        if (validPrices.size >= 2) validPrices.last() - validPrices.first() else null
    }

    if (periodPercent != null && periodAbsoluteChange != null) {
        PeriodPerformanceRow(
            price = "$" + displayedItem.price.toString(),
            trend = periodTrend,
            periodPercent = periodPercent,
            periodAbsoluteChange = periodAbsoluteChange,
        )
    }

    AndroidView(
        factory = { ctx ->
            val view = LayoutInflater.from(ctx).inflate(R.layout.frame_linechart, null, false)
            val chart = view.findViewById<LineChart>(R.id.line_chart)
            chart.description.isEnabled = false
            chart.setNoDataText("Loading…")
            view
        },
        update = { view ->
            val chart = view.findViewById<LineChart>(R.id.line_chart)
            plotDiagram(displayedItem.prices, displayedItem.timestamp, displayedRange, chart, periodTrend)
        }
    )

    val horizontalPadding = 16.dp
    val buttonCount = IntervalRangeValidator.allRanges.size
    val buttonWidth = (screenWidth - horizontalPadding * 2 - 8.dp) / buttonCount
    val selectedIndex = IntervalRangeValidator.allRanges.indexOf(displayedRange)
    val indicatorOffset by animateDpAsState(
        targetValue = buttonWidth * selectedIndex,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "period_indicator"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            IntervalRangeValidator.allRanges.forEach { range ->
                PeriodButton(
                    modifier = Modifier
                        .width(buttonWidth)
                        .height(36.dp),
                    isSelected = range == displayedRange,
                    text = range.value,
                    onClick = { onRangeChange(range) }
                )
            }
        }
        // animated blue underline
        val lineWidth = 28.dp
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = indicatorOffset + (buttonWidth - lineWidth) / 2)
                .width(lineWidth)
                .height(2.dp)
                .background(Color(0xFF007AFF), shape = RoundedCornerShape(1.dp))
        )
    }
}

@Composable
private fun PeriodPerformanceRow(
    price: String,
    trend: PriceTrend,
    periodPercent: String,
    periodAbsoluteChange: Double
) {
    val (arrow, trendColor, bg) = when (trend) {
        PriceTrend.UP -> Triple("▲", Color(0xFF2E7D32), Color(0xFFE8F5E9))
        PriceTrend.DOWN -> Triple("▼", Color(0xFFC62828), Color(0xFFFFEBEE))
        PriceTrend.NEUTRAL -> Triple("–", Color(0xFF8E8E93), Color(0xFFF2F2F7))
    }
    val sign = if (periodAbsoluteChange >= 0) "+" else "-"
    val absFormatted = "%.2f".format(kotlin.math.abs(periodAbsoluteChange))
    val formattedAmount = "${sign}$$absFormatted"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 12.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = price,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
        Text(
            text = formattedAmount,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = trendColor
        )
        Box(
            modifier = Modifier
                .background(bg, shape = RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "$arrow $periodPercent",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = trendColor
            )
        }
    }
}

private fun formatVolume(volume: Long): String = when {
    volume >= 1_000_000_000L -> "${"%.1f".format(volume / 1_000_000_000.0)}B"
    volume >= 1_000_000L     -> "${"%.1f".format(volume / 1_000_000.0)}M"
    volume >= 1_000L         -> "${"%.1f".format(volume / 1_000.0)}K"
    else                     -> volume.toString()
}

@Composable
private fun StockDetailsSection(
    item: StockItem,
    periodLabel: String,
    periodHigh: Double?,
    periodLow: Double?
) {
    val priceFormat: (Double) -> String = { "$" + "%.2f".format(it) }
    val rows = buildList {
        if (periodLow != null && periodHigh != null)
            add("$periodLabel Range" to "${priceFormat(periodLow)} – ${priceFormat(periodHigh)}")
        item.volume?.let { add("Volume" to formatVolume(it)) }
        item.previousClose?.let { add("Prev. Close" to priceFormat(it)) }
        item.exchangeName?.let { add("Exchange" to it) }
        item.currency?.let { add("Currency" to it) }
    }
    if (rows.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .background(Color(0xFFF2F2F7), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp)
    ) {
        rows.forEachIndexed { index, (label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF8E8E93)
                )
                Text(
                    text = value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1C1C1E)
                )
            }
            if (index < rows.lastIndex) {
                HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE5E5EA))
            }
        }
    }
}

@Composable
private fun StockMetaRow(ticker: Ticker) {
    val chips: List<String> = when (ticker) {
        is StockMarketEnum -> listOfNotNull(
            ticker.sector?.name?.lowercase()?.replaceFirstChar { it.titlecase() },
            ticker.country?.let { "${it.flag} ${it.displayName}" }
        )
        is CryptoEnum -> listOf("Crypto")
        else -> emptyList()
    }
    if (chips.isEmpty()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chips.forEach { label ->
            Box(
                modifier = Modifier
                    .background(Color(0xFFF2F2F7), shape = RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF3C3C43)
                )
            }
        }
    }
}

@Composable
private fun PeriodButton(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    text: String = "1D",
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = onClick
        ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = if (isSelected) Color(0xFF007AFF) else Color(0xFF8E8E93),
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 13.sp
        )
    }
}