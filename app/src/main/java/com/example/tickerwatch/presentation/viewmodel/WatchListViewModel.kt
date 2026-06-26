package com.example.tickerwatch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tickerwatch.domain.use_case.ObserveWatchlist
import com.example.tickerwatch.domain.use_case.ToggleWatchlist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchListViewModel @Inject constructor(
    private val observeWatchlist: ObserveWatchlist,
    private val toggleWatchlistUseCase: ToggleWatchlist
) : ViewModel()  {

    /**
     * Set of stock symbols the user has bookmarket.
     *
     * Backed by [observeWatchlist], which streams the Room watchlist table.
     * Eagerly shared so the UI and [toggleWatchlist] always have a fresh snapshot
     * without waiting for a collector.
     * Emits an empty set  until the first DB read completes.
     */
    val watchlistSymbols: StateFlow<Set<String>> = observeWatchlist()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    /**
     * Adds or removes [symbol] from the watchlist.
     *
     * Delegates to [ToggleWatchlist], passing the current membership state derived
     * from [watchlistSymbols] so the use case knows whether to insert or delete.
     *
     * Runs on [Dispatchers.IO] since the operation hits the Room DB.
     * The resulting DB change propagates back automatically through
     * [watchlistSymbols].
     *
     * @param symbol the ticker symbol to bookmark or un-bookmark (e.g. '"AAPL"')
     *
     */
    fun toggleWatchlist(symbol: String) {
        viewModelScope.launch(Dispatchers.IO) {
            toggleWatchlistUseCase(symbol, isWatchlisted = symbol in watchlistSymbols.value)
        }
    }
}