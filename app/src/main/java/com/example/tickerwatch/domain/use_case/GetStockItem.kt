package com.example.tickerwatch.domain.use_case

import com.example.tickerwatch.common.tickers.TickerRegistry.replaceSymbolWithTickerEnum
import com.example.tickerwatch.domain.repository.YahooRepository
import com.example.tickerwatch.domain.repository.model.IntervalRangeValidator.getValidIntervalsFor
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.StockChartState
import com.example.tickerwatch.domain.repository.model.toStockChartState
import javax.inject.Inject

class FetchStockChartState @Inject constructor(
    private val yahooRepository: YahooRepository
) {
    suspend operator fun invoke(
        symbol: String,
        range: Range
    ): StockChartState? {
        val chartResult = yahooRepository.getSingleChart(
            symbol = symbol,
            range = range.value,
            interval = getValidIntervalsFor(range).value
        )

        val chart = chartResult.getOrNull() ?: return null
        val ticker = replaceSymbolWithTickerEnum(symbol)

        return chart.toStockChartState(
            ticker = ticker
        )
    }
}