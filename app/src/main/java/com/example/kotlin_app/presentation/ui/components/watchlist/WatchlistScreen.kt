package com.example.kotlin_app.presentation.ui.components.watchlist

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.kotlin_app.domain.repository.model.PriceTrend
import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.domain.repository.model.SparkStockUiItem
import com.example.kotlin_app.presentation.ui.components.stockdetaildialog.composable.StockDetailsDialog
import com.example.kotlin_app.presentation.ui.components.stockdetaildialog.state.StockState
import com.example.kotlin_app.presentation.ui.theme.AppColors
import com.example.kotlin_app.presentation.ui.theme.AppDimens
import com.example.kotlin_app.presentation.ui.theme.AppType
import kotlin.math.roundToInt

@Composable
fun WatchlistScreen(
    items: List<SparkStockUiItem>,
    stockState: StockState,
    currentSparkItem: SparkStockUiItem?,
    onSymbolSelected: (String) -> Unit,
    onRangeChange: (Range) -> Unit,
    onToggleWatchlist: (String) -> Unit = {}
) {
    if (items.isEmpty()) {
        WatchlistEmptyState()
    } else {
        WatchlistGrid(
            items = items,
            stockState = stockState,
            currentSparkItem = currentSparkItem,
            onSymbolSelected = onSymbolSelected,
            onRangeChange = onRangeChange,
            onToggleWatchlist = onToggleWatchlist
        )
    }
}

@Composable
private fun WatchlistGrid(
    items: List<SparkStockUiItem>,
    stockState: StockState,
    currentSparkItem: SparkStockUiItem?,
    onSymbolSelected: (String) -> Unit,
    onRangeChange: (Range) -> Unit,
    onToggleWatchlist: (String) -> Unit
) {
    var watchlistItems by remember { mutableStateOf(items) }
    var itemIsSelected by remember { mutableStateOf(false) }

    LaunchedEffect(items) {
        watchlistItems = items
    }

    val gridState = rememberLazyGridState()
    val dragDropState = rememberDragDropState(gridState = gridState) { from, to ->
        watchlistItems = watchlistItems.toMutableList().apply { add(to, removeAt(from)) }
    }

    val isDragging = dragDropState.draggingIndex != null

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppDimens.Space12, vertical = AppDimens.Space8),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Space12),
            verticalArrangement = Arrangement.spacedBy(AppDimens.Space12)
        ) {
            itemsIndexed(watchlistItems, key = { _, item -> item.symbol }) { index, stock ->
                val isDraggedItem = dragDropState.draggingIndex == index
                val dragOffset = if (isDraggedItem) dragDropState.draggingOffset else Offset.Zero

                WatchlistCard(
                    stock = stock,
                    onRemove = { onToggleWatchlist(stock.symbol) },
                    modifier = Modifier
                        .pointerInput(index) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    val layoutInfo = gridState.layoutInfo.visibleItemsInfo
                                    val item = layoutInfo.firstOrNull { it.index == index }
                                    if (item != null) {
                                        val itemCenter = Offset(
                                            item.offset.x.toFloat() + item.size.width / 2f,
                                            item.offset.y.toFloat() + item.size.height / 2f
                                        )
                                        dragDropState.onDragStart(index, itemCenter)
                                    }
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    dragDropState.onDrag(dragAmount)
                                },
                                onDragEnd = { dragDropState.onDragEnd() },
                                onDragCancel = { dragDropState.onDragCancel() }
                            )
                        }
                        .pointerInput(stock.symbol) {
                            detectTapGestures(onTap = {
                                itemIsSelected = true
                                onSymbolSelected(stock.symbol)
                            })
                        }
                        .graphicsLayer {
                            if (isDraggedItem) {
                                scaleX = AppDimens.DragScale
                                scaleY = AppDimens.DragScale
                                shadowElevation = AppDimens.DragShadowElevation
                                alpha = AppDimens.DragAlpha
                            } else if (isDragging) {
                                alpha = AppDimens.NonDragAlpha
                            }
                        }
                        .then(
                            if (isDraggedItem) {
                                Modifier.offset {
                                    IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt())
                                }
                            } else Modifier
                        )
                )
            }
        }

        if (itemIsSelected) {
            StockDetailsDialog(
                stockState = stockState,
                currentSparkItem = currentSparkItem,
                onRangeChange = onRangeChange,
                onDismiss = { itemIsSelected = false }
            )
        }
    }
}

@Composable
private fun WatchlistEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(AppDimens.Space32)
        ) {
            Box(
                modifier = Modifier
                    .size(AppDimens.EmptyStateIconBox)
                    .background(AppColors.SurfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.BookmarkBorder,
                    contentDescription = null,
                    modifier = Modifier.size(AppDimens.IconLg),
                    tint = AppColors.Tertiary
                )
            }
            Spacer(modifier = Modifier.height(AppDimens.Space16))
            Text(
                text = "No stocks saved",
                fontSize = AppType.SectionTitle,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Primary
            )
            Spacer(modifier = Modifier.height(AppDimens.Space6))
            Text(
                text = "Tap the bookmark icon on any stock\nto add it to your watchlist",
                fontSize = AppType.BodyMedium,
                color = AppColors.Secondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WatchlistCard(
    stock: SparkStockUiItem,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, arrow) = when (stock.trend.progressTrend) {
        PriceTrend.UP -> Triple(AppColors.TrendUpSurface, AppColors.TrendUp, "▲")
        PriceTrend.DOWN -> Triple(AppColors.TrendDownSurface, AppColors.TrendDown, "▼")
        PriceTrend.NEUTRAL -> Triple(AppColors.SurfaceVariant, AppColors.Secondary, "–")
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(AppDimens.CardShadow, RoundedCornerShape(AppDimens.CornerWatchlistCard)),
        shape = RoundedCornerShape(AppDimens.CornerWatchlistCard),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(AppDimens.Space12)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = stock.ticker.logoRes?.let { painterResource(id = it) }
                        ?: rememberAsyncImagePainter(stock.ticker.urlLogo),
                    contentDescription = "${stock.ticker.tickerName} logo",
                    modifier = Modifier
                        .size(AppDimens.IconLg)
                        .clip(CircleShape),
                    tint = Color.Unspecified
                )
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(AppDimens.IconMd)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.BookmarkBorder,
                        contentDescription = "Remove from watchlist",
                        tint = AppColors.Tertiary,
                        modifier = Modifier.size(AppDimens.IconXs)
                    )
                }
            }
            Spacer(modifier = Modifier.height(AppDimens.Space8))
            Text(
                text = stock.ticker.tickerName,
                fontWeight = FontWeight.Bold,
                fontSize = AppType.SectionTitle,
                color = AppColors.Primary
            )
            Text(
                text = stock.ticker.symbol,
                fontSize = AppType.Caption,
                color = AppColors.Secondary
            )
            Spacer(modifier = Modifier.height(AppDimens.Space10))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$%.2f".format(stock.close),
                    fontWeight = FontWeight.Bold,
                    fontSize = AppType.CardTitle,
                    color = AppColors.Primary
                )
                Box(
                    modifier = Modifier
                        .background(bgColor, RoundedCornerShape(AppDimens.CornerSm))
                        .padding(horizontal = AppDimens.Space6, vertical = AppDimens.Space3)
                ) {
                    Text(
                        text = "$arrow ${stock.trend.progressPercent}",
                        fontSize = AppType.Badge,
                        fontWeight = FontWeight.Medium,
                        color = textColor
                    )
                }
            }
        }
    }
}