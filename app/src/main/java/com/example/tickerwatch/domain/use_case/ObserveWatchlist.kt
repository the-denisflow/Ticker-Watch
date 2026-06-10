package com.example.tickerwatch.domain.use_case

import com.example.tickerwatch.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveWatchlist @Inject constructor(
    private val watchlistRepository: WatchlistRepository
) {
    operator fun invoke(): Flow<Set<String>> = watchlistRepository.observeSymbols()
}