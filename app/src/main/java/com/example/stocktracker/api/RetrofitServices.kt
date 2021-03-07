package com.example.stocktracker.api

import com.example.stocktracker.entity.News
import com.example.stocktracker.entity.Stock
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitServices {
    @GET("actives?apikey=2803cae49d539d5020e02b9de8803106")
    fun getStocks(): Call<MutableList<Stock>>

    @GET("stock_news?apikey=2803cae49d539d5020e02b9de8803106")
    fun getNews(@Query("tickers") tickers: String): Call<MutableList<News>>
}