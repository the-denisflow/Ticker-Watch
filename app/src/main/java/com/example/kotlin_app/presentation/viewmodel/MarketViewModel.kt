package com.example.kotlin_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kotlin_app.common.Logger
import com.example.kotlin_app.domain.repository.model.StockItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.kotlin_app.common.tickers.TickerRegistry
import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.domain.repository.model.SparkStockUiItem
import com.example.kotlin_app.domain.repository.model.createPlaceholderStockItem
import com.example.kotlin_app.domain.use_case.GetStockItem
import com.example.kotlin_app.domain.use_case.GetStocksBatch
import com.example.kotlin_app.presentation.ui.components.stockdetaildialog.state.StockState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val logger: Logger,
    private val getStockItem: GetStockItem,
    private val getStocksBatch: GetStocksBatch
): ViewModel() {

    private val _displayedRange = MutableStateFlow<Range>(Range.ONE_YEAR)
    private val _currentTickerSymbol = MutableStateFlow<String>("")
    private val _currentDisplayedTicker = MutableStateFlow<StockItem>(createPlaceholderStockItem())
    private var fetchStockDetailsJob : Job? = null
    private var fetchStocksBatchJob : Job? = null

    private val _batchStocks = MutableStateFlow<List<SparkStockUiItem>>(emptyList())
    val batchStocks: StateFlow<List<SparkStockUiItem>> = _batchStocks.asStateFlow()

    val stockState: StateFlow<StockState> =
        combine(
            _currentDisplayedTicker,
            _displayedRange
        ) { item, range ->
            StockState(item, range)
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            StockState(createPlaceholderStockItem(), Range.ONE_YEAR)
        )

    init {
        fetchStocksBatch()
    }

    fun fetchStocksBatch() {
        val symbols = TickerRegistry.allStockMarketTickers.joinToString(",") { it.symbol }
        fetchStocksBatchJob?.cancel()

        fetchStocksBatchJob = viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                getStocksBatch(symbols = symbols, tickers = TickerRegistry.allStockMarketTickers)
            }.onSuccess { result ->
                _batchStocks.value = result
            }.onFailure {
                logger.error("Failed to fetch stocks batch: ${it.message}")
            }
        }
    }

    fun updateCurrentSymbol(currentSymbol: String) {
        _currentTickerSymbol.value = currentSymbol
        updateDisplayedRange(Range.ONE_YEAR)
    }

    fun updateDisplayedRange(range: Range) {
            logger.info("Update Displayed Range: ${range.value}")
            fetchStockDetailsJob?.cancel()

            val lastRangeBeforeUpdate = _displayedRange.value
            _displayedRange.value = range

           fetchStockDetailsJob =  viewModelScope.launch(Dispatchers.IO) {
               runCatching {
                val fetchedItem = getStockItem(
                       symbol = _currentTickerSymbol.value,
                       range = _displayedRange.value
                   )
                   if (fetchedItem != null) {
                       _currentDisplayedTicker.value = fetchedItem
                   }
               }.onFailure { exception ->
                   logger.error("Failed to update displayed range: ${exception.message}")
                   _displayedRange.value = lastRangeBeforeUpdate
               }
            }
    }
}

