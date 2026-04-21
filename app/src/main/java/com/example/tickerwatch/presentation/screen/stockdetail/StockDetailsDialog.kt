package com.example.tickerwatch.presentation.screen.stockdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.tickerwatch.common.tickers.CryptoEnum
import com.example.tickerwatch.common.tickers.StockMarketEnum
import com.example.tickerwatch.common.tickers.Ticker
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.SparkStockUiItem
import com.example.tickerwatch.domain.repository.model.StockItem
import com.example.tickerwatch.presentation.screen.marketlist.StockInfoRow
import com.example.tickerwatch.presentation.screen.marketlist.StockPriceInfoColum
import com.example.tickerwatch.presentation.theme.AppColors
import com.example.tickerwatch.presentation.theme.AppDimens
import com.example.tickerwatch.presentation.theme.AppType

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