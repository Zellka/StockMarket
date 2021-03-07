package com.example.stocktracker.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.stocktracker.api.NetworkClient
import com.example.stocktracker.api.RetrofitServices
import com.example.stocktracker.entity.News
import com.example.stocktracker.entity.Stock
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsViewModel (application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    private val service: RetrofitServices
        get() = NetworkClient.getClient(context, isNetworkConnected(context))
            .create(RetrofitServices::class.java)

    var newsMutableLiveData: MutableLiveData<MutableList<News>> = MutableLiveData<MutableList<News>>()
    var stocks = MutableLiveData<String>()

    private lateinit var symbol: String

    fun setSymbol(title:String){
        symbol = title
    }

    fun getAllNews() {
        service.getNews(symbol).enqueue(object : Callback<MutableList<News>> {

            override fun onResponse(
                call: Call<MutableList<News>>,
                response: Response<MutableList<News>>
            ) {
                newsMutableLiveData.value = response.body()
            }

            override fun onFailure(call: Call<MutableList<News>>, t: Throwable) {
                stocks.value = "err"
            }
        })
    }

    private fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting
    }

//    fun updateItem(stockItem: Stock) {
//        val favouriteList = getFavouriteList()
//
//        val targetItem = favouriteList.singleOrNull { it.symbol == stockItem.symbol }
//        if (targetItem == null) {
//            favouriteList.add(stockItem)
//        } else {
//            favouriteList.remove(stockItem)
//        }
//        saveStock(favouriteList)
//    }
//
//    private fun saveStock(stock: MutableList<Stock>) {
//        Paper.book().write("stock", stock)
//    }
//
//    fun getFavouriteList(): MutableList<Stock> {
//        return Paper.book().read("stock", mutableListOf())
//    }
}