package com.example.tickerwatch.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tickerwatch.common.Logger
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.domain.repository.model.placeholders
import com.example.tickerwatch.presentation.screen.main.MainPage
import com.example.tickerwatch.presentation.viewmodel.MarketViewModel
import com.example.tickerwatch.presentation.viewmodel.SortOption
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StockListFragment : Fragment() {
    private val marketViewModel: MarketViewModel by viewModels()
    @Inject
    lateinit var logger: Logger

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )

        setContent {
            val sortedStocks by marketViewModel.sortedStocks.collectAsStateWithLifecycle()
            val stockState by marketViewModel.stockDetailState.collectAsStateWithLifecycle()
            val currentSparkItem by marketViewModel.currentSparkItem.collectAsStateWithLifecycle()
            val sortOption by marketViewModel.sortOption.collectAsStateWithLifecycle()
            val watchlistSymbols by marketViewModel.watchlistSymbols.collectAsStateWithLifecycle()

            val onSymbolSelected: (String) -> Unit = remember { { symbol -> marketViewModel.updateCurrentSymbol(symbol) } }
            val onRangeChange: (Range) -> Unit = remember { { range -> marketViewModel.updateDisplayedRange(range) } }
            val onSortChange: (SortOption) -> Unit = remember { { sort -> marketViewModel.setSortOption(sort) } }
            val onToggleWatchlist: (String) -> Unit = remember { { symbol -> marketViewModel.toggleWatchlist(symbol) } }

            // Shimmer placeholder is a UI concern, kept here so the ViewModel stays data-only
            val listOfStocks = sortedStocks.ifEmpty { placeholders }

            MainPage(
                stockList = listOfStocks,
                stockState = stockState,
                currentSparkItem = currentSparkItem,
                sortOption = sortOption,
                watchlistSymbols = watchlistSymbols,
                onSymbolSelected = onSymbolSelected,
                onRangeChange = onRangeChange,
                onSortChange = onSortChange,
                onToggleWatchlist = onToggleWatchlist
            )
        }
    }
}