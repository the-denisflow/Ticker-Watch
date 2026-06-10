package com.example.tickerwatch.domain.use_case

import com.example.tickerwatch.domain.repository.WatchlistRepository
import javax.inject.Inject

class ToggleWatchlist @Inject constructor(
    private val watchlistRepository: WatchlistRepository
) {
    suspend operator fun invoke(symbol: String, isWatchlisted: Boolean) {
        if (isWatchlisted) watchlistRepository.remove(symbol)
        else watchlistRepository.add(symbol)
    }
}