package com.example.kotlin_app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StockEntity::class, SparkStockEntity::class], version = 3)
abstract class AppDatabase: RoomDatabase() {
    abstract fun stockDao(): StockDao
}