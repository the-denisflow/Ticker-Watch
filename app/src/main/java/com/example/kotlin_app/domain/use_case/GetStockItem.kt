package com.example.kotlin_app.domain.use_case

import com.example.kotlin_app.common.tickers.StockTicker
import com.example.kotlin_app.domain.repository.FinnHubRepository
import com.example.kotlin_app.domain.repository.YahooRepository
import com.example.kotlin_app.domain.repository.model.IntervalRangeValidator.getValidIntervalsFor
import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.domain.repository.model.StockItem
import com.example.kotlin_app.domain.repository.model.toStockItem
import javax.inject.Inject

class GetStockItem @Inject constructor(
    private val yahooRepository: YahooRepository,
    private val finnHubRepository: FinnHubRepository
) {
    suspend operator fun invoke(
        ticker: StockTicker,
        range: Range
    ): StockItem? {
        val chartResult = yahooRepository.getChart(
            ticker = ticker,
            range = range.value,
            interval = getValidIntervalsFor(range).value
        )

        val chart = chartResult.getOrNull() ?: return null

        val logoUrl = if (ticker.logoRes != null) null else {
            runCatching {
                finnHubRepository
                    .getCompanyProfile(ticker.symbol)
                    .getOrNull()
                    ?.logo
            }.getOrNull()
        }

        return chart.toStockItem(
            ticker = ticker,
            logoRes = ticker.logoRes,
            logoUrl = logoUrl
        )
    }
}
