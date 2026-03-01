package com.example.tickerwatch.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StockEntity::class, SparkStockEntity::class, WatchlistEntity::class], version = 4)
abstract class AppDatabase: RoomDatabase() {
    abstract fun stockDao(): StockDao
    abstract fun watchlistDao(): WatchlistDao
}