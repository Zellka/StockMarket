package com.example.stocktracker.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stocktracker.api.NetworkClient
import com.example.stocktracker.api.RetrofitServices
import com.example.stocktracker.entity.News
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsViewModel(application: Application, private val ticker: String) : AndroidViewModel(application) {

    private val BASE_URL = "https://financialmodelingprep.com/api/v3/"
    private val context = getApplication<Application>().applicationContext
    private val service: RetrofitServices
        get() = NetworkClient.getClient(BASE_URL, context)
            .create(RetrofitServices::class.java)

    var newsMutableLiveData: MutableLiveData<MutableList<News>> =
        MutableLiveData<MutableList<News>>()

    fun getAllNews() {
        service.getNews(ticker).enqueue(object : Callback<MutableList<News>> {
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

@Suppress("UNCHECKED_CAST")
class NewsFactory(private val app: Application, private val ticker: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(app, ticker) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}