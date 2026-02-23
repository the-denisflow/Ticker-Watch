package com.example.kotlin_app.presentation.fragments

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
import com.example.kotlin_app.common.Logger
import com.example.kotlin_app.domain.repository.model.Range
import com.example.kotlin_app.presentation.ui.components.homepagelist.composeable.LoadingState
import com.example.kotlin_app.presentation.ui.components.main.MainPage
import com.example.kotlin_app.presentation.viewmodel.MarketViewModel
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
            val batchStocks by marketViewModel.batchStocks.collectAsStateWithLifecycle()
            val stockState by marketViewModel.stockState.collectAsStateWithLifecycle()
            val currentSparkItem by marketViewModel.currentSparkItem.collectAsStateWithLifecycle()
            val onSymbolSelected: (String) -> Unit = remember { { symbol -> marketViewModel.updateCurrentSymbol(symbol) } }
            val onRangeChange: (Range) -> Unit = remember { { range -> marketViewModel.updateDisplayedRange(range) } }

            if (batchStocks.isNotEmpty()) {
                MainPage(
                    stockList = batchStocks,
                    stockState = stockState,
                    currentSparkItem = currentSparkItem,
                    onSymbolSelected = onSymbolSelected,
                    onRangeChange = onRangeChange
                )
            } else {
                LoadingState()
            }
        }
    }
}