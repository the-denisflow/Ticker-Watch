package com.example.tickerwatch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tickerwatch.common.Logger
import com.example.tickerwatch.common.tickers.StockMarketEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.tickerwatch.common.tickers.CryptoEnum
import com.example.tickerwatch.common.tickers.Sector
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.StockSummary
import com.example.tickerwatch.domain.repository.model.StockSymbol
import com.example.tickerwatch.domain.repository.model.createPlaceholderStockChartState
import com.example.tickerwatch.domain.use_case.FetchStockChartState
import com.example.tickerwatch.domain.use_case.SyncMarketStocks
import com.example.tickerwatch.presentation.model.SortOption
import com.example.tickerwatch.presentation.model.StockDialogUiState
import com.example.tickerwatch.presentation.model.StockSheetUiState
import com.example.tickerwatch.presentation.screen.main.component.marketlist.sectorfilter.SectorFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val logger: Logger,
    private val fetchStockChartState: FetchStockChartState,
    private val syncMarketStocks: SyncMarketStocks,
) : ViewModel() {
    private val _selectedSymbol = MutableStateFlow<StockSymbol>(StockSymbol.Invalid)
    private val stockChartUiState = MutableStateFlow(
        StockSheetUiState(
            createPlaceholderStockChartState(),
            Range.ONE_YEAR
        )
    )
    private val _activeFilter = MutableStateFlow(SectorFilter.ALL)
    val activeFilter = _activeFilter.asStateFlow()

    private var fetchStockDetailsJob: Job? = null
    private var syncJob: Job? = null
    private val _batchStocks = MutableStateFlow<List<StockSummary>>(emptyList())
    private val _sortOption = MutableStateFlow(SortOption.DEFAULT)

    init {
        syncBatchStocks()
    }

    /**
     * Currently selected symbol.
     *
     * Exposes [_sortOption] as a read-only flow. Updated via [setSortOption];
     * consumed by [sortedStocks] to reorder the list whenever the user picks
     * a new option.
     */
    val sortOption: StateFlow<SortOption> = _sortOption

    /**
     * Market list after applying the active [SortOption] and [SectorFilter]
     *
     * Re-emits whenever any of its three upstream flows change: [_batchStocks], [_sortOption],
     * or [_activeFilter]. Sorting is applied before filtering, so sector chips always reflect
     * the chosen order within each category.
     */
    val sortedStocks: StateFlow<List<StockSummary>> = combine(
        _batchStocks, _sortOption, _activeFilter
    ) { stocks, sort, activeFilter ->
        logger.info("combine emitted — stocks size: ${stocks.size}, sort: $sort")
        val sorted = when (sort) {
            SortOption.DEFAULT -> stocks
            SortOption.NAME_ASC -> stocks.sortedBy { it.ticker.tickerName }
            SortOption.PRICE_DESC -> stocks.sortedByDescending { it.close }
            SortOption.CHANGE_DESC -> stocks.sortedByDescending {
                it.trend.progressPercent
                    .replace("%", "").replace("+", "")
                    .toDoubleOrNull() ?: 0.0
            }
        }
        when (activeFilter) {
            SectorFilter.ALL -> sorted
            SectorFilter.FINANCE -> sorted.filter { (it.ticker as? StockMarketEnum)?.sector == Sector.FINANCE }
            SectorFilter.TECH -> sorted.filter { (it.ticker as? StockMarketEnum)?.sector == Sector.TECHNOLOGY }
            SectorFilter.HEALTH -> sorted.filter { (it.ticker as? StockMarketEnum)?.sector == Sector.HEALTHCARE }
            SectorFilter.CRYPTO -> sorted.filter { it.ticker is CryptoEnum }
        }

    }.stateIn(viewModelScope, SharingStarted.Eagerly,
         emptyList()
    )

    fun setFilter(filter: SectorFilter) {
        _activeFilter.value = filter
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dialogStock: StateFlow<StockSummary?> = _selectedSymbol
        .flatMapLatest { symbol ->
            if (!symbol.isValid) flowOf(null)
            else _batchStocks.map { stocks -> stocks.find { it.symbol == symbol } }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /**
     * Full state consumed by the stock detail bottom sheet.
     *
     * Combines [stockChartUiState] (chart data, fetched async per range) with [dialogStock]
     * (the matching [StockSummary] resolved from the already loaded batch)
     */
    val stockDialogUiState: StateFlow<StockDialogUiState> = combine(
        stockChartUiState,
        dialogStock
    ) { chartUiState, stockSummary ->
        StockDialogUiState(
            chartUiState = chartUiState,
            stockSummary = stockSummary,
            isLoaded = stockSummary != null
        ).also {
            logger.info("Combined StockDialogUiState emitted: isVisible=${it.isLoaded}, stockSummary=${it.stockSummary?.symbol}")
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, StockDialogUiState())

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

    fun updateCurrentSymbol(currentSymbol: String) {
        logger.info("Selected symbol: $currentSymbol")
        _selectedSymbol.value = StockSymbol(currentSymbol)
        updateDisplayedRange(Range.ONE_YEAR)
    }

    fun updateDisplayedRange(range: Range) {
        logger.info("Update Displayed Range: ${range.value}")
        fetchStockDetailsJob?.cancel()

        val stateBeforeFetch = stockChartUiState.value
        stockChartUiState.value = stateBeforeFetch.copy(range = range, isLoading = true)

        fetchStockDetailsJob = viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val fetchedItem = fetchStockChartState(symbol = _selectedSymbol.value.value, range = range)
                if (fetchedItem != null) {
                    stockChartUiState.value = StockSheetUiState(fetchedItem, range, isLoading = false)
                }
            }.onFailure { exception ->
                logger.error("Failed to update displayed range: ${exception.message}")
                stockChartUiState.value = stateBeforeFetch
            }
        }
    }
}