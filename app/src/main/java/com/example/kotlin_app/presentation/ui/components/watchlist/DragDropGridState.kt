package com.example.kotlin_app.presentation.ui.components.watchlist

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

class DragDropGridState(
    val gridState: LazyGridState,
    private val onMove: (from: Int, to: Int) -> Unit
) {
    var draggingIndex: Int? by mutableStateOf(null)
        private set

    var draggingOffset: Offset by mutableStateOf(Offset.Zero)
        private set

    private var dragStartPosition: Offset = Offset.Zero
    private var lastHoveredIndex: Int by mutableIntStateOf(-1)

    fun onDragStart(index: Int, startOffset: Offset) {
        draggingIndex = index
        lastHoveredIndex = index
        dragStartPosition = startOffset
        draggingOffset = Offset.Zero
    }

    fun onDrag(change: Offset) {
        draggingOffset += change
        val currentPosition = dragStartPosition + draggingOffset
        val hoveredIndex = gridState.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                val left = item.offset.x.toFloat()
                val top = item.offset.y.toFloat()
                val right = left + item.size.width.toFloat()
                val bottom = top + item.size.height.toFloat()
                currentPosition.x in left..right && currentPosition.y in top..bottom
            }?.index

        if (hoveredIndex != null && hoveredIndex != lastHoveredIndex) {
            val from = lastHoveredIndex
            lastHoveredIndex = hoveredIndex
            onMove(from, hoveredIndex)
        }
    }

    fun onDragEnd() {
        draggingIndex = null
        draggingOffset = Offset.Zero
        dragStartPosition = Offset.Zero
        lastHoveredIndex = -1
    }

    fun onDragCancel() = onDragEnd()
}

@Composable
fun rememberDragDropState(
    gridState: LazyGridState,
    onMove: (from: Int, to: Int) -> Unit
): DragDropGridState = remember(gridState) {
    DragDropGridState(gridState, onMove)
}
