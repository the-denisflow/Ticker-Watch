package com.example.tickerwatch.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
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
            MaterialTheme(
                colorScheme = lightColorScheme(
                    surface = Color.White,
                    background = Color.White,
                    onSurface = Color(0xFF1C1C1E),
                    primary = Color(0xFF007AFF),
                )
            ) {
                val sortedStocks by marketViewModel.sortedStocks.collectAsStateWithLifecycle()
                val sortOption by marketViewModel.sortOption.collectAsStateWithLifecycle()
                val watchlistSymbols by marketViewModel.watchlistSymbols.collectAsStateWithLifecycle()
                val stockDialogUiState by marketViewModel.stockDialogUiState.collectAsStateWithLifecycle()
                val activeFilter by marketViewModel.activeFilter.collectAsStateWithLifecycle()

                MainPage(
                    stockList = sortedStocks.ifEmpty { placeholders },
                    stockDialogUiState = stockDialogUiState ,
                    sortOption = sortOption,
                    watchlistSymbols = watchlistSymbols,
                    onSymbolSelected = marketViewModel::updateCurrentSymbol,
                    onRangeChange = marketViewModel::updateDisplayedRange,
                    onSortChange = marketViewModel::setSortOption,
                    onToggleWatchlist = marketViewModel::toggleWatchlist,
                    dismissDialog = marketViewModel::dismissDialog,
                    activeFilter = activeFilter,
                    onFilterSelected = marketViewModel::setFilter
                )
            }
        }
    }
}