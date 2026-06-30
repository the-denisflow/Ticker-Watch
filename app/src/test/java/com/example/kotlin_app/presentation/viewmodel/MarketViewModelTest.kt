package com.example.kotlin_app.presentation.viewmodel

import com.example.kotlin_app.util.MainDispatcherRule
import com.example.tickerwatch.common.Logger
import com.example.tickerwatch.common.tickers.CryptoEnum
import com.example.tickerwatch.common.tickers.StockMarketEnum
import com.example.tickerwatch.domain.repository.model.PriceProgressTrend
import com.example.tickerwatch.domain.repository.model.PriceTrend
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.StockChartState
import com.example.tickerwatch.domain.repository.model.StockSummary
import com.example.tickerwatch.domain.repository.model.StockSymbol
import com.example.tickerwatch.domain.use_case.FetchStockChartState
import com.example.tickerwatch.domain.use_case.SyncMarketStocks
import com.example.tickerwatch.presentation.model.SortOption
import com.example.tickerwatch.presentation.model.StockSheetUiState
import com.example.tickerwatch.presentation.viewmodel.MarketViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class MarketViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val loggerMock: Logger = mockk(relaxed = true)
    private val fetchStockChartStateMock: FetchStockChartState = mockk()
    private val syncMarketStocksMock: SyncMarketStocks = mockk()

    // TECH sector stock
    private val appleStock = StockSummary(
        symbol = StockSymbol("AAPL"),
        close = 150.0,
        trend = PriceProgressTrend(PriceTrend.UP, "+1.50%"),
        ticker = StockMarketEnum.APPLE
    )

    // FINANCE sector stock
    private val visaStock = StockSummary(
        symbol = StockSymbol("V"),
        close = 270.0,
        trend = PriceProgressTrend(PriceTrend.UP, "+0.50%"),
        ticker = StockMarketEnum.VISA
    )

    // Crypto
    private val bitcoinStock = StockSummary(
        symbol = StockSymbol("BTC-USD"),
        close = 50000.0,
        trend = PriceProgressTrend(PriceTrend.UP, "+3.00%"),
        ticker = CryptoEnum.BITCOIN
    )

    private val allStocks = listOf(appleStock, visaStock, bitcoinStock)

    private lateinit var viewModelUnderTest: MarketViewModel

    @Before
    fun setUp() {
        every { syncMarketStocksMock.invoke() } returns flowOf(allStocks)
        viewModelUnderTest = MarketViewModel(
            loggerMock,
            fetchStockChartStateMock,
            syncMarketStocksMock,
            ioDispatcher = mainDispatcherRule.testDispatcher
        )
    }

    @Test
    fun sortOption_whenInvokedForTheFirstTime_shouldReturnDefaultOption() {
        assertEquals(expected = SortOption.DEFAULT, actual = viewModelUnderTest.sortOption.value)
    }

    @Test
    fun sortOption_whenSetSortOptionIsInvoked_shouldReturnTheRightOption() {
        SortOption.entries.forEach { option ->
            viewModelUnderTest.setSortOption(option)
            assertEquals(
                expected = option,
                actual = viewModelUnderTest.sortOption.value,
                message =  "setSortOption($option) did not update sortOption"
            )
        }
    }

    @Test
    fun testStockDialogUiState() {
        coEvery { fetchStockChartStateMock.invoke(symbol = appleStock.symbol.value, range = Range.ONE_YEAR) } returns aChartState()
        viewModelUnderTest.updateCurrentSymbol(appleStock.symbol.value)

        val expectedChartUiState = StockSheetUiState(aChartState(), Range.ONE_YEAR, isLoading = false)

        assertEquals(expectedChartUiState, viewModelUnderTest.stockDialogUiState.value.chartUiState)
    }

    private fun aChartState() = StockChartState(
        ticker = StockMarketEnum.APPLE,
        longName = "Apple Inc.",
        shortName = "Apple",
        price = 150.0
    )
}