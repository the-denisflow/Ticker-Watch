package com.example.kotlin_app.presentation.ui.components.shared

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kotlin_app.domain.repository.model.StockItem
import com.example.kotlin_app.presentation.ui.components.homepagelist.composeable.StockInfoRow

@Composable
fun StockUiItem(
    stock: StockItem,
    onClickListener: () -> Unit = {}
    ) {
    Box(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
            .clickable { onClickListener() }
            .drawBehind {
                val strokeWidth = 0.1.dp.toPx()
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, size.height - strokeWidth / 2),
                    end = Offset(size.width, size.height - strokeWidth / 2),
                    strokeWidth = strokeWidth
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize().padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            StockInfoRow(stock = stock)
            Text(text = "$${stock.price}")
        }
    }
}
