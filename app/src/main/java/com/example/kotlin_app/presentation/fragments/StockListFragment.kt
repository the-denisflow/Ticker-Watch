package com.example.kotlin_app.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.kotlin_app.common.Logger
import com.example.kotlin_app.presentation.ui.components.homepagelist.composeable.LoadingState
import com.example.kotlin_app.presentation.ui.components.homepagelist.composeable.StockList
import com.example.kotlin_app.presentation.ui.components.main.MainPage
import com.example.kotlin_app.presentation.viewmodel.MarketViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StockListFragment: Fragment() {
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

        viewLifecycleOwner.lifecycleScope.launch {
            marketViewModel.currentStockList.collect { stockList ->
                setContent {
                    MainPage(stockList, marketViewModel)
                }
            }
        }
    }
}