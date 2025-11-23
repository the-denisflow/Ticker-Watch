package com.example.kotlin_app.presentation.ui.components.homepagelist.composeable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.kotlin_app.domain.repository.model.StockItem


@Composable
fun StockInfoRow(stock: StockItem,
                 iconSize : Dp = 42.dp

) {
    Row {
        Icon(
            painter = stock.logoRes?.let {
                painterResource(id = it)
            } ?: rememberAsyncImagePainter(stock.logoUrl),
            contentDescription = "Stock icon",
            modifier = Modifier
                .size(iconSize)
                .clip(CircleShape),
            tint = Color.Unspecified
        )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .height(iconSize)
                    .padding(start = 10.dp)
            ) {
                Text(text = stock.ticker.symbol, fontWeight = FontWeight.Bold)
                Text(text = stock.shortName, fontSize = 10.sp, fontWeight = FontWeight.Light)
            }
        }
    }
