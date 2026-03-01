package com.example.kotlin_app.di

import android.content.Context
import androidx.room.Room
import com.example.kotlin_app.data.local.AppDatabase
import com.example.kotlin_app.data.local.StockDao
import com.example.kotlin_app.data.local.WatchlistDao
import com.example.kotlin_app.data.repository.DbRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, "my-database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideStockDao(database: AppDatabase) = database.stockDao()

    @Provides
    @Singleton
    fun provideWatchlistDao(database: AppDatabase): WatchlistDao = database.watchlistDao()

    @Provides
    @Singleton
    fun provideDbRepository(stockDao: StockDao) = DbRepository(stockDao)
}