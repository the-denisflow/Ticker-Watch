package com.example.tickerwatch.presentation.screen.stockdetail

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.tickerwatch.R
import com.example.tickerwatch.common.tickers.CryptoEnum
import com.example.tickerwatch.common.tickers.StockMarketEnum
import com.example.tickerwatch.common.tickers.Ticker
import com.example.tickerwatch.domain.repository.model.IntervalRangeValidator
import com.example.tickerwatch.domain.repository.model.PriceTrend
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.SparkStockUiItem
import com.example.tickerwatch.domain.repository.model.StockItem
import com.example.tickerwatch.presentation.chart.plotDiagram
import com.example.tickerwatch.presentation.screen.marketlist.StockInfoRow
import com.example.tickerwatch.presentation.screen.marketlist.StockPriceInfoColum
import com.example.tickerwatch.presentation.screen.stockdetail.StockState
import com.example.tickerwatch.presentation.theme.AppColors
import com.example.tickerwatch.presentation.theme.AppDimens
import com.example.tickerwatch.presentation.theme.AppType
import com.github.mikephil.charting.charts.LineChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailsDialog(
    stockState: StockState,
    currentSparkItem: SparkStockUiItem,
    onRangeChange: (Range) -> Unit,
    onDismiss: () -> Unit = {},
) {
    ModalBottomSheet(
        scrimColor = AppColors.Scrim,
        dragHandle = { DialogHeader() },
        onDismissRequest = { onDismiss() },
    ) {
        Box(Modifier.background(AppColors.Surface)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Header(displayedItem = currentSparkItem)

                    Spacer(modifier = Modifier.height(AppDimens.Space8))

                    val validPrices = remember(stockState.item.prices) {
                        stockState.item.prices.filterNotNull().filter { !it.isNaN() }
                    }

                    val periodHigh = remember(validPrices) { validPrices.maxOrNull() }
                    val periodLow = remember(validPrices) { validPrices.minOrNull() }

                    StockChart(
                        displayedRange = stockState.range,
                        displayedItem = stockState.item,
                        onRangeChange = onRangeChange
                    )

                    StockMetaRow(ticker = currentSparkItem.ticker)

                    StockDetailsSection(
                        item = stockState.item,
                        periodLabel = stockState.range.value,
                        periodHigh = periodHigh,
                        periodLow = periodLow
                    )

                    Spacer(modifier = Modifier.height(AppDimens.Space24))

                }
            }
        }
    }

@Composable
private fun Header(displayedItem: SparkStockUiItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimens.Space20, vertical = AppDimens.Space16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StockInfoRow(displayedItem, iconSize = AppDimens.IconDialog)
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
            plotDiagram(
                displayedItem.prices,
                displayedItem.timestamp,
                displayedRange,
                chart,
                periodTrend
            )
        }
    )

    val horizontalPadding = AppDimens.Space16
    val buttonCount = IntervalRangeValidator.allRanges.size
    val buttonWidth = (screenWidth - horizontalPadding * 2 - AppDimens.Space8) / buttonCount
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
            .padding(horizontal = horizontalPadding, vertical = AppDimens.Space12)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            IntervalRangeValidator.allRanges.forEach { range ->
                PeriodButton(
                    modifier = Modifier
                        .width(buttonWidth)
                        .height(AppDimens.ChartPeriodButtonHeight),
                    isSelected = range == displayedRange,
                    text = range.value,
                    onClick = { onRangeChange(range) }
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = indicatorOffset + (buttonWidth - AppDimens.ChartUnderlineWidth) / 2)
                .width(AppDimens.ChartUnderlineWidth)
                .height(AppDimens.ChartUnderlineHeight)
                .background(
                    AppColors.Accent,
                    shape = RoundedCornerShape(AppDimens.CornerChartUnderline)
                )
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
        PriceTrend.UP -> Triple("▲", AppColors.TrendUp, AppColors.TrendUpSurface)
        PriceTrend.DOWN -> Triple("▼", AppColors.TrendDown, AppColors.TrendDownSurface)
        PriceTrend.NEUTRAL -> Triple("–", AppColors.Secondary, AppColors.SurfaceVariant)
    }
    val sign = if (periodAbsoluteChange >= 0) "+" else "-"
    val absFormatted = "%.2f".format(kotlin.math.abs(periodAbsoluteChange))
    val formattedAmount = "${sign}$$absFormatted"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimens.Space24)
            .padding(top = AppDimens.Space12, bottom = AppDimens.Space4),
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Space10),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = price,
            fontSize = AppType.SectionTitle,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.Strong
        )
        Text(
            text = formattedAmount,
            fontSize = AppType.SectionTitle,
            fontWeight = FontWeight.SemiBold,
            color = trendColor
        )
        Box(
            modifier = Modifier
                .background(bg, shape = RoundedCornerShape(AppDimens.CornerSm))
                .padding(horizontal = AppDimens.Space8, vertical = AppDimens.Space4)
        ) {
            Text(
                text = "$arrow $periodPercent",
                fontSize = AppType.Body,
                fontWeight = FontWeight.SemiBold,
                color = trendColor
            )
        }
    }
}

private fun formatVolume(volume: Long): String = when {
    volume >= 1_000_000_000L -> "${"%.1f".format(volume / 1_000_000_000.0)}B"
    volume >= 1_000_000L -> "${"%.1f".format(volume / 1_000_000.0)}M"
    volume >= 1_000L -> "${"%.1f".format(volume / 1_000.0)}K"
    else -> volume.toString()
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
            .padding(horizontal = AppDimens.Space20, vertical = AppDimens.Space8)
            .background(AppColors.SurfaceVariant, shape = RoundedCornerShape(AppDimens.CornerCard))
            .padding(horizontal = AppDimens.Space16)
    ) {
        rows.forEachIndexed { index, (label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppDimens.Space12),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = AppType.BodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = AppColors.Secondary
                )
                Text(
                    text = value,
                    fontSize = AppType.BodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.Primary
                )
            }
            if (index < rows.lastIndex) {
                HorizontalDivider(
                    thickness = AppDimens.DividerThickness,
                    color = AppColors.DividerSubtle
                )
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
            .padding(horizontal = AppDimens.Space20, vertical = AppDimens.Space8),
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Space8)
    ) {
        chips.forEach { label ->
            Box(
                modifier = Modifier
                    .background(
                        AppColors.SurfaceVariant,
                        shape = RoundedCornerShape(AppDimens.CornerPill)
                    )
                    .padding(horizontal = AppDimens.Space12, vertical = AppDimens.Space6)
            ) {
                Text(
                    text = label,
                    fontSize = AppType.Caption,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.ChipContent
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
            color = if (isSelected) AppColors.Accent else AppColors.Secondary,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = AppType.Body
        )
    }
}