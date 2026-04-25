package com.example.tickerwatch.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.example.tickerwatch.common.tickers.InvalidTicker
import com.example.tickerwatch.domain.repository.model.StockSummary
import com.example.tickerwatch.presentation.screen.marketlist.StockInfoRow
import com.example.tickerwatch.presentation.screen.marketlist.StockPriceInfoColum
import com.example.tickerwatch.presentation.theme.AppColors
import com.example.tickerwatch.presentation.theme.AppDimens

@Composable
fun StockUiListItem(
    stock: StockSummary,
    isInWatchlist: Boolean = false,
    onClickListener: () -> Unit = {},
    onToggleWatchlist: () -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppDimens.StockRowHeight)
                .clickable { onClickListener() }
                .padding(start = AppDimens.Space16, end = AppDimens.Space4),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            StockInfoRow(stock = stock, modifier = Modifier.weight(1f))

            StockPriceInfoColum(stock = stock)

            WatchlistButton(
                isInWatchlist = isInWatchlist,
                enabled = stock.ticker !is InvalidTicker,
                onToggleWatchlist = onToggleWatchlist
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = AppDimens.Space16),
            thickness = AppDimens.DividerThickness,
            color = AppColors.Divider
        )
    }
}

@Composable
private fun WatchlistButton(
    isInWatchlist: Boolean,
    enabled: Boolean,
    onToggleWatchlist: () -> Unit
) {
    IconButton(
        onClick = onToggleWatchlist,
        enabled = enabled,
        modifier = Modifier
            .size(AppDimens.IconXl)
            .alpha(if (enabled) 1f else 0.5f)
    ) {
        Icon(
            imageVector = if (isInWatchlist) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
            contentDescription = if (isInWatchlist) "Remove from watchlist" else "Add to watchlist",
            tint = if (isInWatchlist) AppColors.Primary else AppColors.Tertiary,
            modifier = Modifier.size(AppDimens.IconSm)
        )
    }
}