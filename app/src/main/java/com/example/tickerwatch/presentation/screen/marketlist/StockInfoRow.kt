package com.example.tickerwatch.presentation.screen.marketlist

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.tickerwatch.common.tickers.CryptoEnum
import com.example.tickerwatch.common.tickers.InvalidTicker
import com.example.tickerwatch.common.tickers.LogoResource
import com.example.tickerwatch.common.tickers.Sector
import com.example.tickerwatch.common.tickers.StockMarketEnum
import com.example.tickerwatch.domain.repository.model.PriceTrend
import com.example.tickerwatch.domain.repository.model.SparkStockUiItem
import com.example.tickerwatch.presentation.component.rememberShimmerTranslateAnim
import com.example.tickerwatch.presentation.component.shimmer
import com.example.tickerwatch.presentation.theme.AppColors
import com.example.tickerwatch.presentation.theme.AppDimens
import com.example.tickerwatch.presentation.theme.AppType

@Composable
fun StockInfoRow(
    stock: SparkStockUiItem,
    modifier: Modifier = Modifier,
    iconSize: Dp = AppDimens.IconStockRow
) {
    if (stock.ticker is InvalidTicker) {
        StockInfoRowShimmer(iconSize = iconSize, modifier = modifier)
    } else {
        StockInfoRowContent(stock = stock, iconSize = iconSize, modifier = modifier)
    }
}

@Composable
private fun StockInfoRowContent(
    stock: SparkStockUiItem,
    iconSize: Dp,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = when (val logo = stock.ticker.logo) {
                is LogoResource.Res -> painterResource(id = logo.resId)
                is LogoResource.Url -> rememberAsyncImagePainter(logo.url)
                else -> rememberAsyncImagePainter(null)
            },
            contentDescription = "${stock.ticker.tickerName} logo",
            modifier = Modifier
                .size(iconSize)
                .clip(CircleShape),
            tint = Color.Unspecified
        )
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = AppDimens.Space12)
        ) {
            Text(
                text = stock.ticker.tickerName,
                fontWeight = FontWeight.SemiBold,
                fontSize = AppType.BodyMedium,
                color = AppColors.Primary
            )
            Spacer(modifier = Modifier.height(AppDimens.Space2))
            Text(
                text = stock.ticker.symbol,
                fontSize = AppType.Badge,
                fontWeight = FontWeight.Normal,
                color = AppColors.Secondary
            )
            Spacer(modifier = Modifier.height(AppDimens.Space3))
            SectorChip(stock.ticker)
        }
    }
}

/**
 * A skeleton placeholder row shown while a stock's real data is still loading.
 * It mimics the shape of [StockInfoRowContent] — a circle on the left and three
 * stacked bars on the right — but filled with an animated shimmer instead of real content.
 *
 * @param iconSize The diameter of the circular placeholder on the left, matching
 *   the size of the real stock logo so the layout does not shift when data loads.
 * @param modifier Standard Compose modifier for positioning and sizing this row
 *   from the outside (e.g. padding, fillMaxWidth).
 */


@Composable
private fun SectorChip(ticker: com.example.tickerwatch.common.tickers.Ticker) {
    val sector = (ticker as? StockMarketEnum)?.sector
    val isCrypto = ticker is CryptoEnum

    val (label, bg, fg) = when {
        sector == Sector.TECHNOLOGY -> Triple("Tech", AppColors.SectorTechSurface, AppColors.SectorTech)
        sector == Sector.FINANCE    -> Triple("Finance", AppColors.SectorFinanceSurface, AppColors.SectorFinance)
        sector == Sector.HEALTHCARE -> Triple("Health", AppColors.SectorHealthSurface, AppColors.SectorHealth)
        isCrypto                    -> Triple("Crypto", AppColors.SectorCryptoSurface, AppColors.SectorCrypto)
        else                        -> return
    }

    Box(
        modifier = Modifier
            .background(bg, shape = RoundedCornerShape(AppDimens.CornerXs))
            .padding(horizontal = AppDimens.Space5, vertical = AppDimens.Space1)
    ) {
        Text(
            text = label,
            fontSize = AppType.SectorChip,
            fontWeight = FontWeight.Medium,
            color = fg
        )
    }
}

@Composable
fun MiniSparkline(
    prices: List<Double>,
    trend: PriceTrend,
    modifier: Modifier = Modifier
) {
    if (prices.size < 2) return

    val lineColor = when (trend) {
        PriceTrend.UP -> AppColors.TrendUp
        PriceTrend.DOWN -> AppColors.TrendDown
        PriceTrend.NEUTRAL -> AppColors.Secondary
    }

    Canvas(modifier = modifier) {
        val min = prices.min()
        val max = prices.max()
        val range = (max - min).takeIf { it > 0 } ?: 1.0

        val path = Path()
        prices.forEachIndexed { index, price ->
            val x = index / (prices.size - 1f) * size.width
            val y = (1f - ((price - min) / range).toFloat()) * size.height
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = AppDimens.SparklineStroke.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

@Composable
fun StockPriceInfoColum(stock: SparkStockUiItem, subLabel: String? = null) {
    val (bgColor, textColor, arrow) = when (stock.trend.progressTrend) {
        PriceTrend.UP -> Triple(AppColors.TrendUpSurface, AppColors.TrendUp, "▲")
        PriceTrend.DOWN -> Triple(AppColors.TrendDownSurface, AppColors.TrendDown, "▼")
        PriceTrend.NEUTRAL -> Triple(AppColors.SurfaceVariant, AppColors.Secondary, "–")
    }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Space4, Alignment.CenterVertically)
    ) {
        if (stock.ticker !is InvalidTicker) {
            Text(
                text = "$%.2f".format(stock.close),
                fontSize = AppType.BodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Primary
            )
            Box(
                modifier = Modifier
                    .background(bgColor, shape = RoundedCornerShape(AppDimens.CornerSm))
                    .padding(horizontal = AppDimens.Space7, vertical = AppDimens.Space3)
            ) {
                Text(
                    text = "$arrow ${stock.trend.progressPercent}",
                    fontSize = AppType.Badge,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
            }
        } else {
            val translateAnim = rememberShimmerTranslateAnim()
            repeat(2) { StockPriceShimmer(translateAnim) }

        }
    }
}

@Composable
private fun StockPriceShimmer(translateAnim: androidx.compose.runtime.State<Float>) {
    Box(
        modifier = Modifier
            .size(width = 60.dp, height = 20.dp)
            .padding(horizontal = AppDimens.Space7, vertical = AppDimens.Space3)
            .clip(RoundedCornerShape(AppDimens.CornerSm))
            .shimmer(translateAnim)
    )
}