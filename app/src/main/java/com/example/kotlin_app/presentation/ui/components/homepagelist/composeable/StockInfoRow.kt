package com.example.kotlin_app.presentation.ui.components.homepagelist.composeable

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import coil.compose.rememberAsyncImagePainter
import com.example.kotlin_app.common.tickers.CryptoEnum
import com.example.kotlin_app.common.tickers.Sector
import com.example.kotlin_app.common.tickers.StockMarketEnum
import com.example.kotlin_app.domain.repository.model.PriceTrend
import com.example.kotlin_app.domain.repository.model.SparkStockUiItem
import com.example.kotlin_app.presentation.ui.theme.AppColors
import com.example.kotlin_app.presentation.ui.theme.AppDimens
import com.example.kotlin_app.presentation.ui.theme.AppType

@Composable
fun StockInfoRow(stock: SparkStockUiItem, iconSize: Dp = AppDimens.IconStockRow, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = stock.ticker.logoRes?.let {
                painterResource(id = it)
            } ?: rememberAsyncImagePainter(stock.ticker.urlLogo),
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

@Composable
private fun SectorChip(ticker: com.example.kotlin_app.common.tickers.Ticker) {
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
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = "$%.2f".format(stock.close),
            fontSize = AppType.BodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.Primary
        )
        Spacer(modifier = Modifier.height(AppDimens.Space4))
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
        if (subLabel != null) {
            Spacer(modifier = Modifier.height(AppDimens.Space3))
            Text(
                text = subLabel,
                fontSize = AppType.NavLabel,
                fontWeight = FontWeight.Normal,
                color = AppColors.Quaternary
            )
        }
    }
}