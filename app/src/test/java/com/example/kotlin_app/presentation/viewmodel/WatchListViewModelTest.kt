package com.example.kotlin_app.presentation.viewmodel

import com.example.kotlin_app.util.MainDispatcherRule
import com.example.tickerwatch.domain.use_case.ObserveWatchlist
import com.example.tickerwatch.domain.use_case.ToggleWatchlist
import com.example.tickerwatch.presentation.viewmodel.WatchListViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
@OptIn(ExperimentalCoroutinesApi::class)
class WatchListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeWatchlist: ObserveWatchlist = mockk()
    private val toggleWatchlist: ToggleWatchlist = mockk()

    private lateinit var viewModel: WatchListViewModel

    @Before
    fun setUp() {
        every { observeWatchlist() } returns flowOf(setOf("AAPL", "MSFT"))
        viewModel = WatchListViewModel(observeWatchlist, toggleWatchlist)
    }
}