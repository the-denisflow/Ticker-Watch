package com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.ui.body.metadatasection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.tickerwatch.common.tickers.CryptoEnum
import com.example.tickerwatch.common.tickers.StockMarketEnum
import com.example.tickerwatch.common.tickers.Ticker
import com.example.tickerwatch.common.tickers.getTagsFromTicker
import com.example.tickerwatch.presentation.theme.AppColors
import com.example.tickerwatch.presentation.theme.AppDimens
import com.example.tickerwatch.presentation.theme.AppType

@Composable
internal fun TickerTagsRow(chips: List<String>) {
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

@Preview
@Composable
private fun TickerTagsRowCryptoPreview() {
    TickerTagsRow(chips = CryptoEnum.ETHEREUM.getTagsFromTicker())
}

@Preview
@Composable
private fun TickerTagsRowPreview() {
    TickerTagsRow(chips = StockMarketEnum.META.getTagsFromTicker())
}