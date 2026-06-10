package com.example.tickerwatch.di

import android.content.Context
import androidx.room.Room
import com.example.tickerwatch.data.local.AppDatabase
import com.example.tickerwatch.data.local.StockDao
import com.example.tickerwatch.data.local.WatchlistDao
import com.example.tickerwatch.data.repository.DbRepository
import com.example.tickerwatch.data.repository.WatchlistRepositoryImpl
import com.example.tickerwatch.domain.repository.WatchlistRepository
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

    @Provides
    @Singleton
    fun provideWatchlistRepository(watchlistDao: WatchlistDao): WatchlistRepository =
        WatchlistRepositoryImpl(watchlistDao)
}