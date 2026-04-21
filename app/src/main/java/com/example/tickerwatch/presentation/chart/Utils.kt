package com.example.tickerwatch.presentation.chart

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import com.example.tickerwatch.domain.repository.model.PriceTrend
import com.example.tickerwatch.domain.repository.model.Range
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

fun plotDiagram(
    closePrices: List<Double?>?,
    timestampList: List<Int>?,
    displayedRange: Range,
    chart: LineChart,
    trend: PriceTrend = PriceTrend.NEUTRAL
) {
    val lineColor = when (trend) {
        PriceTrend.UP -> Color.rgb(46, 125, 50)
        PriceTrend.DOWN -> Color.rgb(198, 40, 40)
        PriceTrend.NEUTRAL -> Color.rgb(100, 100, 100)
    }
    val gradientTop = when (trend) {
        PriceTrend.UP -> Color.argb(80, 46, 125, 50)
        PriceTrend.DOWN -> Color.argb(80, 198, 40, 40)
        PriceTrend.NEUTRAL -> Color.argb(40, 100, 100, 100)
    }

    val entries = closePrices?.mapIndexedNotNull { index, price ->
        if (price == null) return@mapIndexedNotNull null
        val timestamp = timestampList?.getOrNull(index)?.toFloat() ?: index.toFloat()
        Entry(timestamp, price.toFloat())
    }

    val dataSet = LineDataSet(entries, "").apply {
        color = lineColor
        setDrawValues(false)
        setDrawCircles(false)
        lineWidth = 2f
        mode = LineDataSet.Mode.CUBIC_BEZIER
        setDrawFilled(true)
        fillDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(gradientTop, Color.TRANSPARENT)
        )
        setDrawHighlightIndicators(true)
        highLightColor = lineColor
        highlightLineWidth = 1f
    }

    chart.data = LineData(dataSet)

    chart.xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        setDrawGridLines(false)
        setDrawAxisLine(false)
        granularity = when (displayedRange) {
            Range.ONE_DAY -> 3600f
            Range.SIX_MONTHS -> 86400f * 7
            Range.ONE_YEAR -> 86400f * 30
            Range.FIVE_YEARS -> 86400f * 365
        }
        isGranularityEnabled = true
        textColor = Color.rgb(130, 130, 130)
        textSize = 10f
        yOffset = 8f
        valueFormatter = TimestampValueFormatter(range = displayedRange)
        setLabelCount(5, true)
    }

    chart.axisLeft.apply {
        setDrawGridLines(true)
        gridColor = Color.argb(30, 0, 0, 0)
        setDrawAxisLine(false)
        textColor = Color.rgb(130, 130, 130)
        textSize = 10f
        xOffset = 8f
    }

    chart.axisRight.isEnabled = false
    chart.description.isEnabled = false
    chart.legend.isEnabled = false
    chart.setTouchEnabled(true)
    chart.setPinchZoom(false)
    chart.isDragEnabled = false
    chart.isHighlightPerDragEnabled = true
    chart.marker = ChartMarkerView(chart.context, displayedRange)
    chart.invalidate()
}