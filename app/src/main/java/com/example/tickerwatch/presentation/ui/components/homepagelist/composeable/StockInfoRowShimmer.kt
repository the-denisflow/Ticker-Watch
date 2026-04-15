package com.example.tickerwatch.presentation.ui.components.homepagelist.composeable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import com.example.tickerwatch.presentation.ui.components.common.rememberShimmerTranslateAnim
import com.example.tickerwatch.presentation.ui.components.common.shimmer
import com.example.tickerwatch.presentation.ui.theme.AppDimens

@Composable
internal fun StockInfoRowShimmer(
    iconSize: Dp,
    modifier: Modifier = Modifier
) {
    val translateAnim = rememberShimmerTranslateAnim()

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(CircleShape)
                .shimmer(translateAnim)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(AppDimens.Space5, Alignment.CenterVertically),
            modifier = Modifier.padding(start = AppDimens.Space12)
        ) {
            Box(
                modifier = Modifier
                    .width(AppDimens.SparklineWidth)
                    .height(AppDimens.Space12)
                    .clip(RoundedCornerShape(AppDimens.CornerXs))
                    .shimmer(translateAnim)
            )
            Box(
                modifier = Modifier
                    .width(AppDimens.Space32)
                    .height(AppDimens.Space10)
                    .clip(RoundedCornerShape(AppDimens.CornerXs))
                    .shimmer(translateAnim)
            )
            Box(
                modifier = Modifier
                    .width(AppDimens.Space24)
                    .height(AppDimens.Space12)
                    .clip(RoundedCornerShape(AppDimens.CornerXs))
                    .shimmer(translateAnim)
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 400)
@Composable
private fun PreviewTest() {
    StockInfoRowShimmer(modifier = Modifier.fillMaxSize(), iconSize = AppDimens.IconStockRow )
}