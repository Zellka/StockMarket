package com.example.stocktracker.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.stocktracker.api.NetworkClient
import com.example.stocktracker.api.RetrofitServices
import com.example.stocktracker.data.FavouriteList
import com.example.stocktracker.entity.Stock
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StockViewModel(application: Application) : AndroidViewModel(application) {

    private val BASE_URL = "https://financialmodelingprep.com/api/v3/"
    private val context = getApplication<Application>().applicationContext
    private val service: RetrofitServices
        get() = NetworkClient.getClient(BASE_URL, context)
            .create(RetrofitServices::class.java)

    var stocksMutableLiveData: MutableLiveData<MutableList<Stock>> =
        MutableLiveData<MutableList<Stock>>()

    var favouriteMutableLiveData: MutableLiveData<MutableList<Stock>> =
        MutableLiveData<MutableList<Stock>>()

    fun getAllStockList() {
        service.getStocks().enqueue(object : Callback<MutableList<Stock>> {

            override fun onResponse(
                call: Call<MutableList<Stock>>,
                response: Response<MutableList<Stock>>
            ) {
                if (response.body() != null) {
                    stocksMutableLiveData.value = response.body()
                } else {
                    Toast.makeText(context, "server error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MutableList<Stock>>, t: Throwable) {
                Toast.makeText(context, "server error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun updateItem(stockItem: Stock) {
        FavouriteList.updateItem(stockItem)
    }

    fun getFavouriteList(){
        favouriteMutableLiveData.value = FavouriteList.getFavouriteList()
    }
}