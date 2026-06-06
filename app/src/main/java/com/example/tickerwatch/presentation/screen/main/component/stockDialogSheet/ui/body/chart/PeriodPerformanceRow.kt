package com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.ui.body.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.tickerwatch.domain.repository.model.PriceTrend
import com.example.tickerwatch.presentation.theme.AppColors
import com.example.tickerwatch.presentation.theme.AppDimens
import com.example.tickerwatch.presentation.theme.AppType
import kotlin.math.abs

@Composable
internal fun PeriodPerformanceRow(
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
    val absFormatted = "%.2f".format(abs(periodAbsoluteChange))
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

@Preview
@Composable
private fun PeriodPerformanceRowPreview() {
    Box(modifier = Modifier.background(AppColors.Surface)) {
        PeriodPerformanceRow(
            price = "$150.00",
            trend = PriceTrend.UP,
            periodPercent = "2.5%",
            periodAbsoluteChange = 3.75
        )
    }
}

