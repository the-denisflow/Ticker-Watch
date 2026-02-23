package com.example.kotlin_app.domain.use_case

import com.example.kotlin_app.common.Logger
import com.example.kotlin_app.domain.network.NetworkMonitor
import com.example.kotlin_app.domain.repository.model.Range
import javax.inject.Inject

class SyncMarketStocks @Inject constructor(
    private val saveStocksInDb: SaveStocksInDb,
    private val loadStocksFromDb: LoadStocksFromDb,
    private val networkMonitor: NetworkMonitor,
    private val logger: Logger
) {
    suspend operator fun invoke(range: Range) {
        if (networkMonitor.isOnline.value) {
            logger.info("Device is online")
        } else {
            logger.info("Device is offline")
        }
    }
}
