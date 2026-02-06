package com.example.kotlin_app.data.remote


import com.example.kotlin_app.domain.repository.model.YahooResultDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface YahooApi {
    @GET("v8/finance/chart/{symbol}")
    suspend fun getChart(
        @Path("symbol") symbol: String,
        @Query("interval") interval: String,
        @Query("range") range: String,
        @Query("includePrePost") includePrePost: Boolean = false
    ): YahooResultDto
}