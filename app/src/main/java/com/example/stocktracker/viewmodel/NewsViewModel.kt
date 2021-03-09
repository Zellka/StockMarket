package com.example.stocktracker.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.stocktracker.api.NetworkClient
import com.example.stocktracker.api.RetrofitServices
import com.example.stocktracker.entity.News
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val BASE_URL = "https://financialmodelingprep.com/api/v3/"
    private val context = getApplication<Application>().applicationContext
    private val service: RetrofitServices
        get() = NetworkClient.getClient(BASE_URL, context, isInternet)
            .create(RetrofitServices::class.java)

    var newsMutableLiveData: MutableLiveData<MutableList<News>> =
        MutableLiveData<MutableList<News>>()

    private var isInternet = false
    private lateinit var symbol: String


    fun setSymbol(title: String) {
        symbol = title
    }

    fun setNetworkConnected(connect: Boolean) {
        isInternet = connect
    }

    fun getAllNews() {
        service.getNews(symbol).enqueue(object : Callback<MutableList<News>> {
            override fun onResponse(
                call: Call<MutableList<News>>,
                response: Response<MutableList<News>>
            ) {
                if (response.body() != null) {
                    newsMutableLiveData.value = response.body()
                } else {
                    Toast.makeText(context, "server error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MutableList<News>>, t: Throwable) {
                Toast.makeText(context, "server error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}