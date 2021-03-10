package com.example.stocktracker.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.stocktracker.api.NetworkClient
import com.example.stocktracker.api.RetrofitServices
import com.example.stocktracker.entity.Stock
import io.paperdb.Paper
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
        val favouriteList = getFavouriteList()

        val targetItem = favouriteList.singleOrNull { it.ticker == stockItem.ticker }
        if (targetItem == null) {
            favouriteList.add(stockItem)
        } else {
            favouriteList.remove(stockItem)
        }
        saveStock(favouriteList)
    }

    private fun saveStock(stock: MutableList<Stock>) {
        Paper.book().write("stock", stock)
    }

    fun getFavouriteList(): MutableList<Stock> {
        return Paper.book().read("stock", mutableListOf())
    }
}