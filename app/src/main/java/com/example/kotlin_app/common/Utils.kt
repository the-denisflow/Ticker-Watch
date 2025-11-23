package com.example.kotlin_app.common

import android.graphics.Color
import com.example.kotlin_app.domain.repository.model.Range
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun plotDiagram(
    closePrices: List<Double>?,
    timestampList: List<Int>?,
    displayedRange: Range,
    chart: LineChart
) {
    val entries = closePrices?.mapIndexed { index, price ->
        val timestamp = timestampList?.getOrNull(index)?.toFloat() ?: index.toFloat()

        Entry(timestamp, price.toFloat())
    }

    val dataSet = LineDataSet(entries, "Close Prices").apply {
        color = Color.BLUE
        valueTextColor = Color.BLACK
        setDrawCircles(false)
        lineWidth = 2f
        mode = LineDataSet.Mode.CUBIC_BEZIER
    }

    val lineData = LineData(dataSet)
    chart.data = lineData

    chart.xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        setDrawGridLines(true)
        granularity = when(displayedRange) {
            Range.ONE_DAY -> 3600f
            Range.SIX_MONTHS -> 86400f * 7
            Range.ONE_YEAR -> 86400f * 30
            Range.FIVE_YEARS -> 86400f * 365
        }
        isGranularityEnabled = true
        textColor = Color.BLACK
        gridColor = Color.LTGRAY
        valueFormatter = TimestampValueFormatter(range = displayedRange)
        setLabelCount(8, false)
        textSize = 10f
        yOffset = 5f
    }

    chart.axisLeft.apply {
        setDrawGridLines(true)
        gridColor = Color.LTGRAY
        textColor = Color.BLACK
    }

    chart.axisRight.isEnabled = false
    chart.description.text = "Stock close prices"
    chart.invalidate()
}

class TimestampValueFormatter(range: Range) : ValueFormatter() {
    private val dateFormat = getFormat(range)

    private fun getFormat(
        range: Range
    ): SimpleDateFormat {
        return when(range) {
            Range.ONE_DAY -> SimpleDateFormat("HH:mm", Locale.getDefault())
            Range.FIVE_YEARS -> SimpleDateFormat("MM.yy", Locale.getDefault())
            Range.ONE_YEAR -> SimpleDateFormat("dd.MM", Locale.getDefault())
            Range.SIX_MONTHS -> SimpleDateFormat("dd.MM", Locale.getDefault())
        }
    }

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val timestamp = value.toLong()
        return dateFormat.format(Date(timestamp * 1000))
    }
}
