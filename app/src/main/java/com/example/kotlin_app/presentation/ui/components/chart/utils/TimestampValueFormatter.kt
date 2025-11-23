package com.example.kotlin_app.presentation.ui.components.chart.utils

import com.example.kotlin_app.domain.repository.model.Range
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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