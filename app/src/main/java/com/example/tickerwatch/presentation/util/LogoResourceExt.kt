package com.example.tickerwatch.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.example.tickerwatch.common.tickers.LogoResource

@Composable
fun LogoResource?.toPainter(): Painter = when (this) {
    is LogoResource.Res -> painterResource(id = resId)
    is LogoResource.Url -> rememberAsyncImagePainter(url)
    null -> rememberAsyncImagePainter(null)
}
