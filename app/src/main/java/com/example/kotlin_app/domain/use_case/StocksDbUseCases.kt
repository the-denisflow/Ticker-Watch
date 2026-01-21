package com.example.kotlin_app.domain.use_case

import com.example.kotlin_app.data.local.toEntity
import com.example.kotlin_app.data.repository.DbRepository
import com.example.kotlin_app.domain.repository.model.StockItem
import javax.inject.Inject

class LoadStocksFromDb  @Inject constructor(private val dbRepository: DbRepository) {
    suspend operator fun invoke() = dbRepository.getAllStocks()
}

class SaveStocksInDb @Inject constructor(private val dbRepository: DbRepository){
    suspend operator fun invoke(stocks: List<StockItem>) {
        dbRepository.saveStocks(
                stocks.map { it.toEntity() })
    }
}