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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.tickerwatch.R
import com.example.tickerwatch.domain.repository.model.IntervalRangeValidator
import com.example.tickerwatch.domain.repository.model.PriceTrend
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.StockItem
import com.example.tickerwatch.presentation.chart.plotDiagram
import com.example.tickerwatch.presentation.theme.AppColors
import com.example.tickerwatch.presentation.theme.AppDimens
import com.example.tickerwatch.presentation.theme.AppType
import com.github.mikephil.charting.charts.LineChart

@Composable
internal fun StockChart(
    displayedRange: Range,
    displayedItem: StockItem,
    onRangeChange: (Range) -> Unit
) {
    val validPrices = remember(displayedItem.prices) {
        displayedItem.prices.filterNotNull().filter { !it.isNaN() }
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

    PeriodSelector(displayedRange, onRangeChange)
}

@Composable
private fun PeriodSelector(
    displayedRange: Range,
    onRangeChange: (Range) -> Unit
) {
    val context = LocalContext.current
    val density = context.resources.displayMetrics.density
    val screenWidth = (context.resources.displayMetrics.widthPixels / density).dp

    val horizontalPadding = AppDimens.Space16
    val buttonCount = IntervalRangeValidator.allRanges.size
    val buttonWidth = (screenWidth - horizontalPadding * 2) / buttonCount
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
            IntervalRangeValidator.allRanges.forEachIndexed { index, range ->
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
                .offset(indicatorOffset + (buttonWidth - AppDimens.ChartUnderlineWidth) / 2)
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
            textAlign = TextAlign.Center,
            color = if (isSelected) AppColors.Accent else AppColors.Secondary,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = AppType.Body
        )
    }
}