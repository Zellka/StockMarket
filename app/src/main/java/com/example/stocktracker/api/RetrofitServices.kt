package com.example.stocktracker.api

import com.example.stocktracker.entity.Stock
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitServices {
    @GET("quotes?apikey=2803cae49d539d5020e02b9de8803106")
    fun getStocks(): Call<MutableList<Stock>>
}