package com.example.tickerwatch.data.repository

import com.example.tickerwatch.data.local.WatchlistDao
import com.example.tickerwatch.data.local.WatchlistEntity
import com.example.tickerwatch.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WatchlistRepositoryImpl @Inject constructor(
    private val watchlistDao: WatchlistDao
) : WatchlistRepository {

    override fun observeSymbols(): Flow<Set<String>> =
        watchlistDao.getAllSymbols().map { it.toSet() }

    override suspend fun add(symbol: String) =
        watchlistDao.insert(WatchlistEntity(symbol))

    override suspend fun remove(symbol: String) =
        watchlistDao.delete(symbol)
}