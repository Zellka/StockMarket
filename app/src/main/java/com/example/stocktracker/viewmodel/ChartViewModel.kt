package com.example.stocktracker.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stocktracker.R
import com.example.stocktracker.api.NetworkClient
import com.example.stocktracker.api.RetrofitServices
import com.example.stocktracker.entity.HistoricalDataResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChartViewModel(application: Application, private val ticker: String) : AndroidViewModel(
    application
) {
    private val BASE_URL = "https://wft-geo-db.p.rapidapi.com/"
    private val context = getApplication<Application>().applicationContext

    var chartMutableLiveData: MutableLiveData<HistoricalDataResponse> =
        MutableLiveData<HistoricalDataResponse>()

    private val service: RetrofitServices
        get() {
            return NetworkClient.getClient(BASE_URL, context)
                .create(RetrofitServices::class.java)
        }

    fun getStockData(frequency: String) {
        val endTime: Long = System.currentTimeMillis() / 1000;
        var startTime: Long = 0
        when (frequency) {
            "1d" -> startTime = endTime - 60 * 60 * 24
            "1w" -> startTime = endTime - 60 * 60 * 24 * 7
            "1m" -> startTime = endTime - 60 * 60 * 24 * 30
            "1y" -> startTime = endTime - 60 * 60 * 24 * 356
        }
        service.getHistoricalData(
            frequency,
            "history",
            startTime.toString(),
            endTime.toString(),
            ticker
        ).enqueue(object : Callback<HistoricalDataResponse> {
            override fun onResponse(
                call: Call<HistoricalDataResponse>,
                response: Response<HistoricalDataResponse>
            ) {
                if (response.body() != null) {
                    chartMutableLiveData.value = response.body()
                } else {
                    Toast.makeText(
                        context,
                        context?.getString(R.string.server_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<HistoricalDataResponse>, t: Throwable) {
                Toast.makeText(
                    context,
                    context?.getString(R.string.server_error),
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }
}

@Suppress("UNCHECKED_CAST")
class ChartFactory(private val app: Application, private val ticker: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChartViewModel::class.java)) {
            return ChartViewModel(app, ticker) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}