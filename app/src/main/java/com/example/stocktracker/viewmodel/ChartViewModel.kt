package com.example.stocktracker.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.stocktracker.R
import com.example.stocktracker.api.NetworkClient
import com.example.stocktracker.api.RetrofitServices
import com.example.stocktracker.entity.HistoricalDataResponse
import com.github.mikephil.charting.data.Entry
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChartViewModel(application: Application) : AndroidViewModel(application) {

    private val BASE_URL = "https://wft-geo-db.p.rapidapi.com/"
    private val context = getApplication<Application>().applicationContext
    private val service: RetrofitServices
        get() = NetworkClient.getClient(BASE_URL, context, isNetworkConnected(context))
            .create(RetrofitServices::class.java)

    private lateinit var symbol: String
    var pricesHigh: MutableLiveData<MutableList<Entry>> = MutableLiveData()

    fun setSymbol(title: String) {
        symbol = title
    }

    fun getStockData(frequency: String) {
        val endTime: Long = System.currentTimeMillis() / 1000;
        var startTime: Long = 0
        when (frequency) {
            "1h" -> startTime = endTime - 60 * 60 * 24
            "1w" -> startTime = endTime - 60 * 60 * 24 * 30
            "1m" -> startTime = endTime - 60 * 60 * 24 * 356
        }
        service.getHistoricalData(
            frequency,
            "history",
            startTime.toString(),
            endTime.toString(),
            symbol
        ).enqueue(object : Callback<HistoricalDataResponse> {
            override fun onResponse(
                call: Call<HistoricalDataResponse>,
                response: Response<HistoricalDataResponse>
            ) {
                //val pricesHigh: MutableList<Entry> = ArrayList()
                val tmpList = pricesHigh.value
                if (response.body() != null) {
                    for (i in response.body()!!.prices.indices) {
                        val x: Float = response.body()!!.prices[i].date.toFloat()
                        val y: Float = response.body()!!.prices[i].high
                        if (y != 0f) {
                            tmpList?.add(Entry(x, response.body()!!.prices[i].high))
                        }
                    }
                    tmpList?.sortBy { it.x }
                    pricesHigh.value = tmpList

                }
            }

            override fun onFailure(call: Call<HistoricalDataResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting
    }
}