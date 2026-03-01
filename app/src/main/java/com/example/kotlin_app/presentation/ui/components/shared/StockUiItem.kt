package com.example.kotlin_app.presentation.ui.components.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.kotlin_app.domain.repository.model.SparkStockUiItem
import com.example.kotlin_app.presentation.ui.components.homepagelist.composeable.MiniSparkline
import com.example.kotlin_app.presentation.ui.components.homepagelist.composeable.StockInfoRow
import com.example.kotlin_app.presentation.ui.components.homepagelist.composeable.StockPriceInfoColum
import com.example.kotlin_app.presentation.ui.theme.AppColors
import com.example.kotlin_app.presentation.ui.theme.AppDimens

@Composable
fun StockUiItem(
    stock: SparkStockUiItem,
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
            MiniSparkline(
                prices = stock.prices,
                trend = stock.trend.progressTrend,
                modifier = Modifier
                    .width(AppDimens.SparklineWidth)
                    .height(AppDimens.SparklineHeight)
                    .padding(horizontal = AppDimens.Space4)
            )
            StockPriceInfoColum(stock = stock)
            IconButton(
                onClick = onToggleWatchlist,
                modifier = Modifier.size(AppDimens.IconXl)
            ) {
                Icon(
                    imageVector = if (isInWatchlist) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                    contentDescription = if (isInWatchlist) "Remove from watchlist" else "Add to watchlist",
                    tint = if (isInWatchlist) AppColors.Primary else AppColors.Tertiary,
                    modifier = Modifier.size(AppDimens.IconSm)
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = AppDimens.Space16),
            thickness = AppDimens.DividerThickness,
            color = AppColors.Divider
        )
    }
}