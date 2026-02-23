package com.example.kotlin_app.presentation.ui.components.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kotlin_app.domain.repository.model.SparkStockUiItem
import com.example.kotlin_app.presentation.ui.components.homepagelist.composeable.MiniSparkline
import com.example.kotlin_app.presentation.ui.components.homepagelist.composeable.StockInfoRow
import com.example.kotlin_app.presentation.ui.components.homepagelist.composeable.StockPriceInfoColum

@Composable
fun StockUiItem(
    stock: SparkStockUiItem,
    onClickListener: () -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clickable { onClickListener() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            StockInfoRow(stock = stock, modifier = Modifier.weight(1f))
            MiniSparkline(
                prices = stock.prices,
                trend = stock.trend.progressTrend,
                modifier = Modifier
                    .width(72.dp)
                    .height(32.dp)
                    .padding(horizontal = 4.dp)
            )
            StockPriceInfoColum(stock = stock)
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 0.5.dp,
            color = Color(0xFFEEEEEE)
        )
    }
}