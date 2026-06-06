package com.example.tickerwatch.presentation.component.stockdialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.tickerwatch.domain.repository.model.StockSummary
import com.example.tickerwatch.presentation.screen.main.component.marketlist.StockInfoRow
import com.example.tickerwatch.presentation.screen.main.component.marketlist.StockPriceInfoColum
import com.example.tickerwatch.presentation.theme.AppDimens


@Composable
internal fun SheetHeader(displayedItem: StockSummary) {
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