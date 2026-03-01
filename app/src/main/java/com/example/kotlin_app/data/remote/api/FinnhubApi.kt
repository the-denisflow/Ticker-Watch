package com.example.tickerwatch.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Query


interface FinnHubApi {
    @GET("stock/profile2")
    suspend fun getCompanyProfile(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): CompanyProfile
}

data class CompanyProfile(
    val name: String?,
    val logo: String?,
    val ticker: String?
)