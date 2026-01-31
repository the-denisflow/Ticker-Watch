package com.example.kotlin_app.presentation.ui.components.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.kotlin_app.domain.repository.model.StockItem
import com.example.kotlin_app.presentation.ui.components.homepagelist.composeable.LoadingState
import com.example.kotlin_app.presentation.ui.components.homepagelist.composeable.StockList
import com.example.kotlin_app.presentation.viewmodel.MarketViewModel
import com.example.kotlin_app.presentation.ui.uimodels.TopIconData
import com.example.kotlin_app.presentation.ui.utils.MainPageData.topIconAdd
import com.example.kotlin_app.presentation.ui.utils.MainPageData.topIconSort
import com.example.kotlin_app.presentation.ui.utils.MainPageDimens.headerHeight
import com.example.kotlin_app.presentation.ui.utils.MainPageDimens.headerHorizontalMargin
import com.example.kotlin_app.presentation.ui.utils.MainPageDimens.headerTopMargin
import com.example.kotlin_app.presentation.ui.utils.MainPageDimens.iconSize

@Composable
fun MainPage( stockList: List<StockItem>,
              marketViewModel: MarketViewModel) {
    if (stockList.isEmpty()) {
        LoadingState()
    } else {
        Column {
            MainPageHeader()
            StockList(stockList, marketViewModel)
        }
    }
}


@Preview
@Composable
fun MainPageHeader() {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .height(headerHeight)
            .padding(top = headerTopMargin)
    ){
        Row (modifier = Modifier.fillMaxWidth().padding(
            horizontal = headerHorizontalMargin),
            horizontalArrangement = Arrangement.SpaceBetween) {
            TopIcon(data = topIconSort)
            TopIcon(data = topIconAdd)
        }
    }
}

@Composable
fun TopIcon(
    data : TopIconData
) {
    Icon(
        modifier = Modifier.size(iconSize),
        imageVector = data.imageVector,
        contentDescription = data.contentDescription,
        tint = Color.Unspecified
    )
}