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
import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.domain.repository.model.createPlaceholderStockItem
import com.example.kotlin_app.domain.use_case.GetStockItem
import com.example.kotlin_app.domain.use_case.SyncMarketStocks
import com.example.kotlin_app.presentation.ui.components.stockdetaildialog.state.StockState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val logger: Logger,
    private val syncMarketStocks: SyncMarketStocks,
    private val getStockItem: GetStockItem
): ViewModel() {

    private val _displayedRange = MutableStateFlow<Range>(Range.ONE_YEAR)
    private val _currentTicker = MutableStateFlow<StockItem>(createPlaceholderStockItem())
    private val _currentStockList = MutableStateFlow<List<StockItem>>(emptyList())
    val currentStockList: StateFlow<List<StockItem>> = _currentStockList.asStateFlow()

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

    init {
        fetchStockList()
    }

    private fun fetchStockList() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                syncMarketStocks(_displayedRange.value)
            }.onSuccess { stocks ->
                _currentStockList.value = stocks
            }.onFailure { exception ->
                logger.error("Failed to fetch stock list : ${exception.message}")
            }
        }
    }

    fun updateCurrentSymbol(stockTicker: StockItem) {
        _currentTicker.value = stockTicker
        updateDisplayedRange(Range.ONE_YEAR)
    }

    fun updateDisplayedRange(range: Range) {
        runCatching {
            viewModelScope.launch(Dispatchers.IO) {
                _currentTicker.value = getStockItem(
                    ticker = _currentTicker.value.ticker,
                    range = _displayedRange.value
                ) ?: createPlaceholderStockItem()
            }
        }.onSuccess {
            logger.info("Update Displayed Range: ${range.value}")
            _displayedRange.value = range
        }.onFailure {
            logger.error("Failed to fetch stock item with the range ${range.value}")
        }
    }
}

