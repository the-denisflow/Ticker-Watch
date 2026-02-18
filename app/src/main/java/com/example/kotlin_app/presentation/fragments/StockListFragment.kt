package com.example.kotlin_app.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kotlin_app.common.Logger
import com.example.kotlin_app.presentation.ui.components.main.MainPage
import com.example.kotlin_app.presentation.viewmodel.MarketViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.example.kotlin_app.presentation.ui.components.homepagelist.composeable.LoadingState

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

        setContent {
            val result = marketViewModel.batchStocks.collectAsStateWithLifecycle()
            if (result.value.isNotEmpty()) {
               MainPage(stockList = result.value, marketViewModel)
            } else {
                LoadingState()
            }
        }
    }
}