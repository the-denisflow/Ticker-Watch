package com.example.kotlin_app.presentation.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.example.kotlin_app.common.Logger
import com.example.kotlin_app.common.tickers.StockTicker
import com.example.kotlin_app.common.tickers.StockTicker.Companion.allTickers
import com.example.kotlin_app.domain.repository.YahooRepository
import com.example.kotlin_app.domain.repository.model.StockItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.kotlin_app.common.tickers.StockTicker.Companion.toStockTicker
import com.example.kotlin_app.data.local.toDomain
import com.example.kotlin_app.data.local.toEntity
import com.example.kotlin_app.data.repository.DbRepository
import com.example.kotlin_app.domain.repository.FinnHubRepository
import com.example.kotlin_app.domain.network.NetworkMonitor
import com.example.kotlin_app.domain.repository.model.IntervalRangeValidator.getValidIntervalsFor
import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.domain.repository.model.createPlaceholderStockItem
import com.example.kotlin_app.domain.repository.model.toStockItem
import com.example.kotlin_app.presentation.ui.components.stockdetaildialog.state.StockState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val logger: Logger,
    private val yahooRepository: YahooRepository,
    private val finnHubRepository: FinnHubRepository,
    private val dbRepository: DbRepository,
    private val networkMonitor: NetworkMonitor
): ViewModel() {

    private val _displayedRange = MutableStateFlow<Range>(Range.ONE_YEAR)
    private val _currentTicker = MutableStateFlow<StockItem>(createPlaceholderStockItem())

    val stockstate: StateFlow<StockState> =
        combine(
            _currentTicker,
            _displayedRange
        ) {
                item, range ->
            StockState(item, range)
        }.stateIn(viewModelScope, SharingStarted.Eagerly,
            StockState(createPlaceholderStockItem(), Range.ONE_YEAR)
        )

    private val _currentStockList = MutableStateFlow<List<StockItem>>(emptyList())
    val currentStockList: StateFlow<List<StockItem>> = _currentStockList

    private var networkJob: Job? = null

    @SuppressLint("SuspiciousIndentation")
    private suspend fun fetchLogoUrl(ticker: StockTicker): String? {
        val result = finnHubRepository.getCompanyProfile(ticker.symbol)
        return result.getOrNull()?.logo
    }

    private suspend fun saveAllTickersInDb(stocks: List<StockItem>) {
        logger.info("Saving all tickers in DB")
        dbRepository.saveStocks(stocks.map { it.toEntity() })
    }

    private fun fetchStockList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val stockList = coroutineScope {
                    allTickers.map { ticker ->
                        async {
                            getStockItem(
                                ticker = ticker,
                                range = _displayedRange.value
                            )
                        }
                    }.awaitAll()
                }
                _currentStockList.value = stockList
                saveAllTickersInDb(stockList)
            } catch (e: Exception) {
                logger.error("Error fetching stock list: ${e.message}")
            }
        }
    }


    fun updateCurrentSymbol(stockTicker: StockItem) {
        _currentTicker.value = stockTicker
        updateDisplayedRange(Range.ONE_YEAR)
    }

    fun updateDisplayedRange(range: Range) {
        logger.info("Update Displayed Range: ${range.value}")
        _displayedRange.value = range

        viewModelScope.launch(Dispatchers.IO) {
            val stockItem = getStockItem(
                ticker = _currentTicker.value.ticker,
                range = range
            )
            _currentTicker.value = stockItem
        }
    }


    fun unregisterNetworkObserver() {
        networkMonitor.unregisterNetworkCallback()
        networkJob?.cancel()
        networkJob = null
    }

    fun registerNetworkObserver() {
        if(networkJob != null) return

        networkJob = viewModelScope.launch {
            networkMonitor.registerNetworkCallback()

            networkMonitor.isOnline.collect { isOnline ->
                if (isOnline) {
                    logger.info("Device is online")
                    fetchStockList()
                }
                else {
                    logger.info("Device is offline")
                    if (_currentStockList.value.isEmpty() && dbRepository.getAllStocks().isNotEmpty()) {
                        val dbStocks = dbRepository.getAllStocks()
                            .map { it.toDomain(toStockTicker(it.symbol)) }
                            .filter { it.ticker != StockTicker.IVALIDTICKER }
                        _currentStockList.value = dbStocks
                    }
                }
            }
        }
    }

    private suspend fun getStockItem(ticker: StockTicker, range: Range): StockItem {
        val chartResult = yahooRepository.getChart(ticker = ticker,range = range.value, interval = getValidIntervalsFor(range).value)
        val chart = chartResult.getOrNull()

        if (chart != null && chartResult.isSuccess) {
            val logoUrl = if (ticker.logoRes != null) null else runCatching {
                fetchLogoUrl(ticker)
            }.getOrNull()

            return chart.toStockItem(
                ticker = ticker,
                logoRes = ticker.logoRes,
                logoUrl = logoUrl
            )
        }
        return createPlaceholderStockItem()
    }
}

