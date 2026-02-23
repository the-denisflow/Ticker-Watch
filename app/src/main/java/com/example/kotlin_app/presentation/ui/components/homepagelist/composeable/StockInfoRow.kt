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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.kotlin_app.common.tickers.CryptoEnum
import com.example.kotlin_app.common.tickers.Sector
import com.example.kotlin_app.common.tickers.StockMarketEnum
import com.example.kotlin_app.domain.repository.model.PriceTrend
import com.example.kotlin_app.domain.repository.model.SparkStockUiItem

@Composable
fun StockInfoRow(stock: SparkStockUiItem, iconSize: Dp = 42.dp, modifier: Modifier = Modifier) {
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
            modifier = Modifier.padding(start = 12.dp)
        ) {
            Text(
                text = stock.ticker.tickerName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color(0xFF1C1C1E)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stock.ticker.symbol,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF8E8E93)
            )
            Spacer(modifier = Modifier.height(3.dp))
            SectorChip(stock.ticker)
        }
    }
}

@Composable
private fun SectorChip(ticker: com.example.kotlin_app.common.tickers.Ticker) {
    val sector = (ticker as? StockMarketEnum)?.sector
    val isCrypto = ticker is CryptoEnum

    val (label, bg, fg) = when {
        sector == Sector.TECHNOLOGY -> Triple("Tech", Color(0xFFE3F2FD), Color(0xFF1565C0))
        sector == Sector.FINANCE    -> Triple("Finance", Color(0xFFE8F5E9), Color(0xFF2E7D32))
        sector == Sector.HEALTHCARE -> Triple("Health", Color(0xFFFFF3E0), Color(0xFFE65100))
        isCrypto                    -> Triple("Crypto", Color(0xFFF3E5F5), Color(0xFF6A1B9A))
        else                        -> return
    }

    Box(
        modifier = Modifier
            .background(bg, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 5.dp, vertical = 1.dp)
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
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
        PriceTrend.UP -> Color(0xFF2E7D32)
        PriceTrend.DOWN -> Color(0xFFC62828)
        PriceTrend.NEUTRAL -> Color(0xFF8E8E93)
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
                width = 1.5.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

@Composable
fun StockPriceInfoColum(stock: SparkStockUiItem, subLabel: String? = null) {
    val (bgColor, textColor, arrow) = when (stock.trend.progressTrend) {
        PriceTrend.UP -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "▲")
        PriceTrend.DOWN -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "▼")
        PriceTrend.NEUTRAL -> Triple(Color(0xFFF2F2F7), Color(0xFF8E8E93), "–")
    }

    Column(
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = "$%.2f".format(stock.close),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1C1C1E)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .background(bgColor, shape = RoundedCornerShape(6.dp))
                .padding(horizontal = 7.dp, vertical = 3.dp)
        ) {
            Text(
                text = "$arrow ${stock.trend.progressPercent}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
        if (subLabel != null) {
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = subLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFAEAEB2)
            )
        }
    }
}
