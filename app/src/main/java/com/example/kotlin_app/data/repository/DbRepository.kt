package com.example.kotlin_app.data.repository

import com.example.kotlin_app.data.local.SparkStockEntity
import com.example.kotlin_app.data.local.StockDao
import com.example.kotlin_app.data.local.StockEntity
import javax.inject.Inject

class DbRepository @Inject constructor(private val stockDao: StockDao) {
    suspend fun getAllStocks() = stockDao.getAll()
    suspend fun saveStocks(stocks: List<StockEntity>) = stockDao.insertAll(stocks.toTypedArray())

    suspend fun getAllSparkStocks() = stockDao.getAllSpark()
    suspend fun saveSparkStocks(stocks: List<SparkStockEntity>) = stockDao.insertAllSpark(stocks)
}