package com.example.tickerwatch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tickerwatch.common.Logger
import com.example.tickerwatch.common.tickers.StockMarketEnum
import com.example.tickerwatch.data.local.WatchlistDao
import com.example.tickerwatch.data.local.WatchlistEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.tickerwatch.common.tickers.InvalidTicker
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.StockSummary
import com.example.tickerwatch.domain.repository.model.StockSymbol
import com.example.tickerwatch.domain.repository.model.createPlaceholderStockChartView
import com.example.tickerwatch.domain.use_case.FetchStockChartView
import com.example.tickerwatch.domain.use_case.SyncMarketStocks
import com.example.tickerwatch.presentation.model.StockChartViewUiState
import com.example.tickerwatch.presentation.model.StockDialogUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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
    private val fetchStockChartView: FetchStockChartView,
    private val syncMarketStocks: SyncMarketStocks,
    private val watchlistDao: WatchlistDao
) : ViewModel() {
    private val _selectedSymbol = MutableStateFlow<StockSymbol>(StockSymbol.Invalid)
    private val _stockChartViewUiState = MutableStateFlow(StockChartViewUiState(createPlaceholderStockChartView(), Range.ONE_YEAR))
    private var fetchStockDetailsJob: Job? = null
    private var syncJob: Job? = null
    private val _batchStocks = MutableStateFlow<List<StockSummary>>(emptyList())
    private val _sortOption = MutableStateFlow(SortOption.DEFAULT)
    val sortOption: StateFlow<SortOption> = _sortOption
    // Reactive: emits every time the DB watchlist table changes
    val watchlistSymbols: StateFlow<Set<String>> = watchlistDao
        .getAllSymbols()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    val sortedStocks: StateFlow<List<StockSummary>> = combine(
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


    @OptIn(ExperimentalCoroutinesApi::class)
    val dialogStock: StateFlow<StockSummary?> = _selectedSymbol
        .flatMapLatest { symbol ->
            if (!symbol.isValid) flowOf(null)
            else _batchStocks.map { stocks -> stocks.find { it.symbol == symbol } }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val stockDetailState: StateFlow<StockChartViewUiState> = _stockChartViewUiState

    val stockDialogUiState: StateFlow<StockDialogUiState> = combine(
        _stockChartViewUiState,
        dialogStock
    ) { chartView, stockSummary ->
        StockDialogUiState(
            chartView = chartView,
            stockSummary = stockSummary,
            isVisible = stockSummary != null
        ).also {
            logger.info("Combined StockDialogUiState emitted: isVisible=${it.isVisible}, stockSummary=${it.stockSummary?.symbol}")
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, StockDialogUiState())

    init {
        syncBatchStocks()
    }

    fun dismissDialog() {
        logger.info("Dialog dismissed")
        _selectedSymbol.value = StockSymbol.Invalid
        stockDialogUiState.value.reset()
    }

    private fun syncBatchStocks() {
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
        logger.info("Selected symbol: $currentSymbol")
        _selectedSymbol.value = StockSymbol(currentSymbol)
        updateDisplayedRange(Range.ONE_YEAR)
    }


    fun updateDisplayedRange(range: Range) {
        logger.info("Update Displayed Range: ${range.value}")
        fetchStockDetailsJob?.cancel()

        val stateBeforeFetch = _stockChartViewUiState.value
        _stockChartViewUiState.value = stateBeforeFetch.copy(range = range, isLoading = true)

        fetchStockDetailsJob = viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val fetchedItem = fetchStockChartView(symbol = _selectedSymbol.value.value, range = range)
                if (fetchedItem != null) {
                    _stockChartViewUiState.value = StockChartViewUiState(fetchedItem, range, isLoading = false)
                }
            }.onFailure { exception ->
                logger.error("Failed to update displayed range: ${exception.message}")
                _stockChartViewUiState.value = stateBeforeFetch
            }
        }
    }
}