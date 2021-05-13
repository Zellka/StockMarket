package com.example.stocktracker.api

import com.example.stocktracker.entity.HistoricalDataResponse
import com.example.stocktracker.entity.News
import com.example.stocktracker.entity.Stock
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitServices {
    @GET("actives?apikey=key")
    fun getStocks(): Call<MutableList<Stock>>

    @GET("stock_news?apikey=key")
    fun getNews(@Query("tickers") tickers: String): Call<MutableList<News>>

    @Headers(
        "x-rapidapi-host: apidojo-yahoo-finance-v1.p.rapidapi.com",
        "x-rapidapi-key: key"
    )
    @GET("stock/v2/get-historical-data")
    fun getHistoricalData(
        @Query("frequency") frequency: String,
        @Query("filter") filter: String,
        @Query("period1") period1: String,
        @Query("period2") period2: String,
        @Query("symbol") symbol: String?
    ): Call<HistoricalDataResponse>
}
