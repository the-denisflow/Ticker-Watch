package com.example.kotlin_app.presentation.ui.components.stockdetaildialog.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun DialogHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DialogConstants.Header.backgroundColor)
            .padding(vertical = DialogConstants.Header.verticalPadding),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(DialogConstants.DragHandle.width)
                .height(DialogConstants.DragHandle.height)
                .background(
                    color = DialogConstants.DragHandle.color,
                    shape = RoundedCornerShape(DialogConstants.DragHandle.cornerRadius)
                )
        )
    }
}