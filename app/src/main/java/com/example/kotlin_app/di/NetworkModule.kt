package com.example.tickerwatch.di

import android.content.Context
import com.example.tickerwatch.common.Logger
import com.example.tickerwatch.common.LoggerImpl
import com.example.tickerwatch.data.remote.api.YahooApi
import com.example.tickerwatch.framework.network.NetworkMonitorImpl
import com.example.tickerwatch.data.repository.YahooRepositoryImpl
import com.example.tickerwatch.domain.network.NetworkMonitor
import com.example.tickerwatch.domain.repository.YahooRepository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideYahooApi(): YahooApi {
        return Retrofit.Builder()
            .baseUrl("https://query1.finance.yahoo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YahooApi::class.java)
    }

    @Provides
    @Singleton
    fun provideYahooRepository(api: YahooApi, logger: Logger): YahooRepository {
        return YahooRepositoryImpl(api, logger)
    }

    @Provides
    @Named("finnhubToken")
    fun provideFinnhubToken(): String = com.example.tickerwatch.BuildConfig.FINNHUB_API_KEY

    @Provides
    @Singleton
    fun provideLogger(): Logger = LoggerImpl()

    @Provides
    @Singleton
    fun provideNetworkMonitorImpl(@ApplicationContext context: Context): NetworkMonitorImpl =
        NetworkMonitorImpl(context)

    @Provides
    @Singleton
    fun provideNetworkMonitor(impl: NetworkMonitorImpl): NetworkMonitor = impl}