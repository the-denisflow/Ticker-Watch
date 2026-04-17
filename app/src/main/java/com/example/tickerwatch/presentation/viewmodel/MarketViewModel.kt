package com.example.tickerwatch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tickerwatch.common.Logger
import com.example.tickerwatch.common.tickers.StockMarketEnum
import com.example.tickerwatch.data.local.WatchlistDao
import com.example.tickerwatch.data.local.WatchlistEntity
import com.example.tickerwatch.domain.repository.model.StockItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.tickerwatch.common.tickers.InvalidTicker
import com.example.tickerwatch.domain.repository.model.PriceProgressTrend
import com.example.tickerwatch.domain.repository.model.PriceTrend
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.SparkStockUiItem
import com.example.tickerwatch.domain.repository.model.createPlaceholderStockItem
import com.example.tickerwatch.domain.use_case.GetStockItem
import com.example.tickerwatch.domain.use_case.SyncMarketStocks
import com.example.tickerwatch.presentation.ui.components.stockdetaildialog.state.StockState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

enum class SortOption(val label: String) {
    DEFAULT("Default"),
    NAME_ASC("Name A–Z"),
    PRICE_DESC("Price ↓"),
    CHANGE_DESC("Change %"),
    SECTOR("By Sector"),
}

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val logger: Logger,
    private val getStockItem: GetStockItem,
    private val syncMarketStocks: SyncMarketStocks,
    private val watchlistDao: WatchlistDao
) : ViewModel() {

    private val _displayedRange = MutableStateFlow<Range>(Range.ONE_YEAR)
    private val _selectedSymbol = MutableStateFlow<String>("")
    private val _selectedStockDetail = MutableStateFlow<StockItem>(createPlaceholderStockItem())
    private var fetchStockDetailsJob: Job? = null
    private var syncJob: Job? = null

    private val _batchStocks = MutableStateFlow<List<SparkStockUiItem>>(emptyList())
    val batchStocks: StateFlow<List<SparkStockUiItem>> = _batchStocks

    private val _sortOption = MutableStateFlow(SortOption.DEFAULT)
    val sortOption: StateFlow<SortOption> = _sortOption

    // Reactive: emits every time the DB watchlist table changes
    val watchlistSymbols: StateFlow<Set<String>> = watchlistDao
        .getAllSymbols()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    val sortedStocks: StateFlow<List<SparkStockUiItem>> = combine(
        _batchStocks, _sortOption
    ) { stocks, sort ->
        logger.info("combine emitted — stocks size: ${stocks.size}, sort: $sort")
        when (sort) {
            SortOption.DEFAULT -> stocks
            SortOption.NAME_ASC -> stocks.sortedBy { it.ticker.tickerName }
            SortOption.PRICE_DESC -> stocks.sortedByDescending { it.close }
            SortOption.CHANGE_DESC -> stocks.sortedByDescending {
                it.trend.progressPercent
                    .replace("%", "").replace("+", "")
                    .toDoubleOrNull() ?: 0.0
            }
            SortOption.SECTOR -> stocks.sortedWith(
                compareBy(
                    { (it.ticker as? StockMarketEnum)?.sector?.ordinal ?: Int.MAX_VALUE },
                    { it.ticker.tickerName }
                )
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly,
         emptyList()
    )


    val currentSparkItem: StateFlow<SparkStockUiItem?> = combine(
        _selectedSymbol,
        _batchStocks
    ) { symbol, stocks ->
        stocks.find { it.symbol == symbol }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val stockDetailState: StateFlow<StockState> =
        combine(
            _selectedStockDetail,
            _displayedRange
        ) { item, range ->
            StockState(item, range)
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            StockState(_selectedStockDetail.value, _displayedRange.value)
        )

    init {
        syncBatchStocks()
    }

    fun syncBatchStocks() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch(Dispatchers.IO) {
            syncMarketStocks().collect { stocks ->
                _batchStocks.value = stocks
            }
        }
    }

    fun setSortOption(sort: SortOption) {
        _sortOption.value = sort
    }

    fun toggleWatchlist(symbol: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (symbol in watchlistSymbols.value) {
                watchlistDao.delete(symbol)
            } else {
                watchlistDao.insert(WatchlistEntity(symbol))
            }
        }
    }

    fun updateCurrentSymbol(currentSymbol: String) {
        _selectedSymbol.value = currentSymbol
        updateDisplayedRange(Range.ONE_YEAR)
    }

    fun updateDisplayedRange(range: Range) {
        logger.info("Update Displayed Range: ${range.value}")
        fetchStockDetailsJob?.cancel()

        val lastRangeBeforeUpdate = _displayedRange.value
        _displayedRange.value = range

        fetchStockDetailsJob = viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val fetchedItem = getStockItem(
                    symbol = _selectedSymbol.value,
                    range = _displayedRange.value
                )
                if (fetchedItem != null) {
                    _selectedStockDetail.value = fetchedItem
                }
            }.onFailure { exception ->
                logger.error("Failed to update displayed range: ${exception.message}")
                _displayedRange.value = lastRangeBeforeUpdate
            }
        }
    }
}