package com.example.stocktracker.api

import com.example.stocktracker.entity.News
import com.example.stocktracker.entity.Stock
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitServices {
    @GET("quotes?apikey=2803cae49d539d5020e02b9de8803106")
    fun getStocks(): Call<MutableList<Stock>>

    @GET("stock_news?tickers={id}&apikey=2803cae49d539d5020e02b9de8803106")
    fun getNews(@Path("id") productId: String): Call<MutableList<News>>
}