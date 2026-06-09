package com.example.tickerwatch.presentation.screen.main.component.marketlist.listitem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.tickerwatch.common.tickers.Ticker
import com.example.tickerwatch.domain.repository.model.PriceChangeDetails
import com.example.tickerwatch.domain.repository.model.PriceProgressTrend
import com.example.tickerwatch.domain.repository.model.PriceTrend
import com.example.tickerwatch.domain.repository.model.StockSummary
import com.example.tickerwatch.domain.repository.model.StockSymbol
import com.example.tickerwatch.presentation.screen.main.component.shared.rememberShimmerTranslateAnim
import com.example.tickerwatch.presentation.screen.main.component.shared.shimmer
import com.example.tickerwatch.presentation.theme.AppColors
import com.example.tickerwatch.presentation.theme.AppDimens
import com.example.tickerwatch.presentation.theme.AppType

@Composable
fun StockInfoRow(
    stock: StockSummary,
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
    stock: StockSummary,
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
            verticalArrangement = Arrangement.spacedBy(AppDimens.Space2, Alignment.CenterVertically),
            modifier = Modifier.padding(start = AppDimens.Space12, top = AppDimens.Space3, bottom = AppDimens.Space3)
        ) {
            Text(
                text = stock.ticker.tickerName,
                fontWeight = FontWeight.SemiBold,
                fontSize = AppType.BodyMedium,
                lineHeight = AppType.BodyMedium,
                color = AppColors.Primary
            )
            Text(
                text = stock.ticker.symbol,
                fontSize = AppType.Badge,
                lineHeight = AppType.Badge,
                fontWeight = FontWeight.Normal,
                color = AppColors.Secondary
            )
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
private fun SectorChip(ticker: Ticker) {
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
            .padding(horizontal = AppDimens.Space5, vertical = AppDimens.Space1),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = AppType.SectorChip,
            lineHeight = AppType.SectorChip,
            fontWeight = FontWeight.Medium,
            color = fg
        )
    }
}

@Composable
fun StockPriceInfoColum(stock: StockSummary, subLabel: String? = null) {
    if (stock.priceChangeDetails is PriceChangeDetails.Available) {
    val (bgColor, textColor, arrow) = when (stock.priceChangeDetails.changeTrend) {
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
                    text = "$arrow ${stock.priceChangeDetails.changePercent}",
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
}

@Composable
private fun StockPriceShimmer(translateAnim: State<Float>) {
    Box(
        modifier = Modifier
            .size(width = 60.dp, height = 20.dp)
            .padding(horizontal = AppDimens.Space7, vertical = AppDimens.Space3)
            .clip(RoundedCornerShape(AppDimens.CornerSm))
            .shimmer(translateAnim)
    )
}

private val previewStockTech = StockSummary(
    symbol = StockSymbol("AAPL"),
    close = 189.84,
    ticker = StockMarketEnum.APPLE,
    trend = PriceProgressTrend(PriceTrend.UP, "+1.23%"),
    prices = listOf(185.0, 187.0, 188.0, 189.84),
    chartPreviousClose = 185.0
)

private val previewStockCrypto = StockSummary(
    symbol = StockSymbol("BTC-USD"),
    close = 67_000.0,
    ticker = CryptoEnum.BITCOIN,
    trend = PriceProgressTrend(PriceTrend.DOWN, "-2.10%"),
    prices = listOf(70_000.0, 68_000.0, 67_500.0, 67_000.0),
    chartPreviousClose = 70_000.0
)

@Preview(showBackground = true)
@Composable
private fun StockInfoRowTechPreview() {
    StockInfoRow(stock = previewStockTech)
}

@Preview(showBackground = true)
@Composable
private fun StockInfoRowCryptoPreview() {
    StockInfoRow(stock = previewStockCrypto)
}

@Preview(showBackground = true)
@Composable
private fun StockPriceInfoColumPreview() {
    StockPriceInfoColum(stock = previewStockTech)
}