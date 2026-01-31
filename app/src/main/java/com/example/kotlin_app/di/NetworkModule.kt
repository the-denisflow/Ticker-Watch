package com.example.kotlin_app.di

import android.content.Context
import com.example.kotlin_app.common.Logger
import com.example.kotlin_app.common.LoggerImpl
import com.example.kotlin_app.data.remote.FinnHubApi
import com.example.kotlin_app.data.remote.YahooApi
import com.example.kotlin_app.data.repository.FinnHubRepositoryImpl
import com.example.kotlin_app.framework.network.NetworkMonitorImpl
import com.example.kotlin_app.data.repository.YahooRepositoryImpl
import com.example.kotlin_app.domain.repository.FinnHubRepository
import com.example.kotlin_app.domain.network.NetworkMonitor
import com.example.kotlin_app.domain.repository.YahooRepository

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
    fun provideYahooApi(): YahooApi {
        return Retrofit.Builder()
            .baseUrl("https://query1.finance.yahoo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YahooApi::class.java)
    }

    @Provides
    fun provideFinnHubApi(): FinnHubApi {
        return Retrofit.Builder()
            .baseUrl("https://finnhub.io/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FinnHubApi::class.java)
    }

    @Provides
    fun provideYahooRepository(api: YahooApi): YahooRepository {
        return YahooRepositoryImpl(api)
    }

    @Provides
    fun provideFinnHubRepository(api: FinnHubApi, @Named("finnhubToken") token: String): FinnHubRepository {
        return FinnHubRepositoryImpl(api,token)
    }

    @Provides
    @Named("finnhubToken")
    fun provideFinnhubToken(): String = com.example.kotlin_app.BuildConfig.FINNHUB_API_KEY

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