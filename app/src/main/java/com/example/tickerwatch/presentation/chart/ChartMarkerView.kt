package com.example.tickerwatch.presentation.chart

import android.content.Context
import android.widget.TextView
import com.example.tickerwatch.R
import com.example.tickerwatch.domain.repository.model.Range
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChartMarkerView(context: Context, range: Range) : MarkerView(context, R.layout.chart_marker) {

    private val priceView: TextView = findViewById(R.id.marker_price)
    private val dateView: TextView = findViewById(R.id.marker_date)

    private val dateFormat = SimpleDateFormat(
        when (range) {
            Range.ONE_DAY -> "HH:mm"
            Range.SIX_MONTHS, Range.ONE_YEAR -> "dd MMM"
            Range.FIVE_YEARS -> "MMM yy"
        },
        Locale.getDefault()
    )

    override fun refreshContent(e: Entry, highlight: Highlight) {
        priceView.text = "\$%.2f".format(e.y)
        dateView.text = dateFormat.format(Date(e.x.toLong() * 1000))
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF = MPPointF(-(width / 2f), -height.toFloat() - 10f)
}