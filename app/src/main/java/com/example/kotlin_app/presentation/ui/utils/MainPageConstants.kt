package com.example.kotlin_app.presentation.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.ui.unit.dp
import com.example.kotlin_app.presentation.ui.uimodels.TopIconData

object MainPageDimens {
    val iconSize = 35.dp
    val headerTopMargin = 30.dp
    val headerHorizontalMargin = 10.dp
    val headerHeight = 60.dp
}

object MainPageData {
    val topIconAdd = TopIconData (contentDescription = "add", imageVector = Icons.Default.Add )
    val topIconSort = TopIconData (contentDescription = "sort", imageVector = Icons.Default.MoreHoriz )
}