package com.example.kotlin_app.presentation.ui.components.stockdetaildialog.composable

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data object DialogConstants {
    object Header {
        val backgroundColor: Color = Color.White
        val verticalPadding: Dp = 8.dp
    }

    object DragHandle {
        val width: Dp = 30.dp
        val height: Dp = 4.dp
        val color: Color = Color.LightGray
        val cornerRadius: Dp = 4.dp
    }
}
