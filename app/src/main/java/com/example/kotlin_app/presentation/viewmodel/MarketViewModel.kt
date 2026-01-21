package com.example.kotlin_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kotlin_app.common.Logger
import com.example.kotlin_app.common.tickers.StockTicker
import com.example.kotlin_app.domain.repository.model.StockItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.kotlin_app.common.tickers.StockTicker.Companion.toStockTicker
import com.example.kotlin_app.data.local.toDomain
import com.example.kotlin_app.domain.network.NetworkMonitor
import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.domain.repository.model.createPlaceholderStockItem
import com.example.kotlin_app.domain.use_case.GetMarketStocks
import com.example.kotlin_app.domain.use_case.LoadStocksFromDb
import com.example.kotlin_app.domain.use_case.SaveStocksInDb
import com.example.kotlin_app.presentation.ui.components.stockdetaildialog.state.StockState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val logger: Logger,
    private val loadStocksFromDb: LoadStocksFromDb,
    private val saveStocksInDb: SaveStocksInDb,
    private val networkMonitor: NetworkMonitor,
    private val getMarketStocks: GetMarketStocks
): ViewModel() {

    private val _displayedRange = MutableStateFlow<Range>(Range.ONE_YEAR)
    private val _currentTicker = MutableStateFlow<StockItem>(createPlaceholderStockItem())
    
    val stockState: StateFlow<StockState> =
        combine(
            _currentTicker,
            _displayedRange
        ) { item, range ->
            StockState(item, range)
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            StockState(createPlaceholderStockItem(), Range.ONE_YEAR)
        )

    private val _currentStockList = MutableStateFlow<List<StockItem>>(emptyList())
    val currentStockList: StateFlow<List<StockItem>> = _currentStockList

    private var networkJob: Job? = null

    init {
        registerNetworkObserver()
    }


    private fun loadFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dbStocks = loadStocksFromDb()
                    .map { it.toDomain(toStockTicker(it.symbol)) }
                    .filter { it.ticker != StockTicker.IVALIDTICKER }
                _currentStockList.value = dbStocks
            } catch (e: Exception) {
                logger.error("Error loading from database: ${e.message}")
            }
        }
    }

    private fun fetchStockList() {
        viewModelScope.launch(Dispatchers.IO) {
         _currentStockList.value = getMarketStocks(_displayedRange.value)
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
         _currentStockList.value = getMarketStocks(_displayedRange.value)
        }
    }

    fun registerNetworkObserver() {
        if (networkJob != null) return

        networkJob = viewModelScope.launch {
            networkMonitor.registerNetworkCallback()

            networkMonitor.isOnline.collect { isOnline ->
                if (isOnline) {
                    logger.info("Device is online")
                    fetchStockList()
                    saveStocksInDb(_currentStockList.value)
                } else {
                    logger.info("Device is offline")
                    loadFromDatabase()
                }
            }
        }
    }

    fun unregisterNetworkObserver() {
        networkMonitor.unregisterNetworkCallback()
        networkJob?.cancel()
        networkJob = null
    }

    override fun onCleared() {
        super.onCleared()
        unregisterNetworkObserver()
    }
}

