package com.example.kotlin_app.presentation.ui.components

import android.annotation.SuppressLint
import android.provider.CalendarContract.Colors
import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.kotlin_app.R
import com.example.kotlin_app.common.plotDiagram
import com.example.kotlin_app.domain.repository.model.IntervalRangeValidator
import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.domain.repository.model.StockItem
import com.example.kotlin_app.presentation.viewmodel.MarketViewModel
import com.github.mikephil.charting.charts.LineChart


@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailsDialog(
    marketViewModel: MarketViewModel,
    onDismiss: () -> Unit = {},
    ) {

    val displayedStock by marketViewModel.displayedStockProperties.collectAsState()

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss() },
    ) {
        Box(
            Modifier
                .height(800.dp)
                .background(Color.White)

        ) {
            displayedStock.let { item ->
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Header(displayedItem = displayedStock.item)
                    Spacer(modifier = Modifier.height(10.dp))
                    StockChart(
                        displayedRange = item.range,
                        displayedItem = displayedStock.item,
                        marketViewModel = marketViewModel
                    )
                }
            }

        }
    }
}

@Composable
private fun Header(
    displayedItem: StockItem
) {
    Row (
        Modifier
            .fillMaxWidth()
            .width(100.dp)
            .padding(start = 18.dp, end = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    )
    {
        StockInfoRow(displayedItem, iconSize = 50.dp)

        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_share),
            contentDescription = "share image",
            Modifier.size(30.dp)
        )
    }
}

@Composable
private fun StockChart(
    displayedRange: Range,
    displayedItem: StockItem,
    marketViewModel: MarketViewModel
) {
    val innerBarPadding = 10.dp
    val innerButtonPadding = 1.dp
    val totalButtons = 5
    val context = LocalContext.current
    val density = context.resources.displayMetrics.density
    val periodBarWidth = (context.resources.displayMetrics.widthPixels/density).dp - (innerBarPadding * 2)
    val singlePeriodElementWidth = (periodBarWidth / totalButtons) -  (innerButtonPadding * totalButtons)
    AndroidView(
        factory = { context ->
            val view = LayoutInflater.from(context)
                .inflate(R.layout.frame_linechart, null, false)
            val chart = view.findViewById<LineChart>(R.id.line_chart)

            chart.description.isEnabled = false
            chart.setNoDataText("Loading…")

            view
        },
        update = { view ->
            val chart = view.findViewById<LineChart>(R.id.line_chart)
            plotDiagram(displayedItem.prices, chart)
        }
    )
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(innerBarPadding),
        horizontalArrangement = Arrangement.SpaceBetween){
        IntervalRangeValidator.allRanges.map { range ->

            PeriodButton(
              cardColor = if (range == displayedRange)
                  CardDefaults.cardColors(
                      containerColor = Color.Gray
                  ) else
                  CardDefaults.cardColors(
                      containerColor = Color.LightGray
                  )
                ,
                modifier = Modifier
                    .width(singlePeriodElementWidth)
                    .height(50.dp),
                onClick = {marketViewModel.updateDisplayedRange(range)},
                text = range.value
            )
        }
    }
}

@Preview
@Composable
private fun PeriodButton(
    cardColor: CardColors = CardDefaults.cardColors(
        containerColor = Color.LightGray
    ),
    modifier: Modifier = Modifier
        .width(100.dp)
        .height(50.dp),
    text: String = "1D",
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        colors = cardColor,
        onClick = { onClick.invoke() }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
        {
            Text(
                text,
                color = Color.Black,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}