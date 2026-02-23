package com.example.kotlin_app.presentation.ui.components.stockdetaildialog.composable

import android.view.LayoutInflater
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.kotlin_app.R
import com.example.kotlin_app.domain.repository.model.IntervalRangeValidator
import com.example.kotlin_app.domain.repository.model.PriceProgressTrend
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
                StockChart(
                    displayedRange = stockState.range,
                    displayedItem = stockState.item,
                    onDayTrend = currentSparkItem?.trend,
                    onRangeChange = onRangeChange
                )
                Spacer(modifier = Modifier.height(16.dp))
                ShareRow()
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
private fun ShareRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(Color(0xFFF2F2F7), shape = RoundedCornerShape(12.dp))
            .clickable { /* TODO: share action */ }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_share),
            contentDescription = "share",
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Share",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1C1C1E)
        )
    }
}

@Composable
private fun StockChart(
    displayedRange: Range,
    displayedItem: StockItem,
    onDayTrend: PriceProgressTrend?,
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

    if (periodPercent != null) {
        PeriodPerformanceRow(
            trend = periodTrend,
            periodLabel = displayedRange.value,
            periodPercent = periodPercent,
            onDayTrend = onDayTrend
        )
    }

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
            .background(Color(0xFFF2F2F7), shape = RoundedCornerShape(10.dp))
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(buttonWidth)
                .height(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1C1C1E))
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
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
    }
}

@Composable
private fun PeriodPerformanceRow(
    trend: PriceTrend,
    periodLabel: String,
    periodPercent: String,
    onDayTrend: PriceProgressTrend?
) {
    val (arrow, trendColor) = when (trend) {
        PriceTrend.UP -> "▲" to Color(0xFF2E7D32)
        PriceTrend.DOWN -> "▼" to Color(0xFFC62828)
        PriceTrend.NEUTRAL -> "–" to Color(0xFF8E8E93)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "$arrow $periodPercent",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = trendColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "over $periodLabel",
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF8E8E93)
            )
        }

        if (onDayTrend != null) {
            val (dayArrow, dayBg, dayFg) = when (onDayTrend.progressTrend) {
                PriceTrend.UP -> Triple("▲", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                PriceTrend.DOWN -> Triple("▼", Color(0xFFFFEBEE), Color(0xFFC62828))
                PriceTrend.NEUTRAL -> Triple("–", Color(0xFFF2F2F7), Color(0xFF8E8E93))
            }
            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .background(dayBg, shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$dayArrow ${onDayTrend.progressPercent}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = dayFg
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "from yesterday",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFAEAEB2)
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
        modifier = modifier.clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color(0xFF8E8E93),
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 13.sp
        )
    }
}