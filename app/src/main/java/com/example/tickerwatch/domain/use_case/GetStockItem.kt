package com.example.tickerwatch.domain.use_case

import com.example.tickerwatch.common.tickers.TickerRegistry.replaceSymbolWithTickerEnum
import com.example.tickerwatch.domain.repository.YahooRepository
import com.example.tickerwatch.domain.repository.model.IntervalRangeValidator.getValidIntervalsFor
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.StockChartView
import com.example.tickerwatch.domain.repository.model.toStockChartView
import javax.inject.Inject

class FetchStockChartView @Inject constructor(
    private val yahooRepository: YahooRepository
) {
    suspend operator fun invoke(
        symbol: String,
        range: Range
    ): StockChartView? {
        val chartResult = yahooRepository.getSingleChart(
            symbol = symbol,
            range = range.value,
            interval = getValidIntervalsFor(range).value
        )

        val chart = chartResult.getOrNull() ?: return null
        val ticker = replaceSymbolWithTickerEnum(symbol)

        return chart.toStockChartView(
            ticker = ticker
        )
    }
}