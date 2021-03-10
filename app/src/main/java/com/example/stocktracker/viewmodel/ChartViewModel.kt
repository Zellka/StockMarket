package com.example.stocktracker.viewmodel

import android.app.Application
import android.graphics.Color
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stocktracker.api.NetworkClient
import com.example.stocktracker.api.RetrofitServices
import com.example.stocktracker.entity.HistoricalDataResponse
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ChartViewModel(application: Application, private val ticker: String) : AndroidViewModel(application) {
    private val BASE_URL = "https://wft-geo-db.p.rapidapi.com/"
    private val context = getApplication<Application>().applicationContext
    private val service: RetrofitServices
        get() {
            return NetworkClient.getClient(BASE_URL, context)
                .create(RetrofitServices::class.java)
        }

    fun getStockData(frequency: String, lineChart: LineChart) {
        val endTime: Long = System.currentTimeMillis() / 1000;
        var startTime: Long = 0
        when (frequency) {
            "1d" -> startTime = endTime - 60 * 60 * 24
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
                val pricesHigh: MutableList<Entry> = ArrayList()
                if (response.body() != null) {
                    for (i in response.body()!!.prices.indices) {
                        val x: Float = response.body()!!.prices[i].date.toFloat()
                        val y: Float = response.body()!!.prices[i].high
                        if (y != 0f) {
                            pricesHigh.add(Entry(x, response.body()!!.prices[i].high))
                        }
                    }
                    pricesHigh.sortBy { it.x }
                    setLineChartData(pricesHigh, ticker, lineChart)
                } else {
                    Toast.makeText(context, "server error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<HistoricalDataResponse>, t: Throwable) {
                Toast.makeText(context, "server error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setLineChartData(
        pricesHigh: MutableList<Entry>, title: String, lineChart: LineChart
    ) {
        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        val highLineDataSet = LineDataSet(
            pricesHigh,
            "$title Price"
        )
        highLineDataSet.setDrawCircles(true)
        highLineDataSet.circleRadius = 4f
        highLineDataSet.setDrawValues(false)
        highLineDataSet.lineWidth = 3f
        highLineDataSet.color = Color.GREEN
        highLineDataSet.setCircleColor(Color.GREEN)
        dataSets.add(highLineDataSet)
        val lineData = LineData(dataSets)
        lineChart.data = lineData
        lineChart.invalidate()
    }
}

@Suppress("UNCHECKED_CAST")
class ChartFactory(private val app: Application, private val ticker: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChartViewModel::class.java)) {
            return ChartViewModel(app, ticker) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}