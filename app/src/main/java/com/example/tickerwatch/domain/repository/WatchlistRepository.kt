package com.example.tickerwatch.domain.repository

import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {
    fun observeSymbols(): Flow<Set<String>>
    suspend fun add(symbol: String)
    suspend fun remove(symbol: String)
}