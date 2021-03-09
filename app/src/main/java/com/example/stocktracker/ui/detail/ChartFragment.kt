package com.example.stocktracker.ui.detail

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.stocktracker.R
import com.example.stocktracker.api.RetrofitServices
import com.example.stocktracker.entity.HistoricalDataResponse
import com.example.stocktracker.viewmodel.ChartViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ChartFragment : Fragment() {
    private var title: String? = null
    private lateinit var chartViewModel: ChartViewModel

    private lateinit var lineChart: LineChart

    val BASE_URL = "https://wft-geo-db.p.rapidapi.com/"
    private lateinit var services: RetrofitServices
    private val cacheSize = (10 * 1024 * 1024).toLong()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString("title")
        }
        chartViewModel = ViewModelProviders.of(this).get(ChartViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title?.let { chartViewModel.setSymbol(it) }

        lineChart = view.findViewById(R.id.line_chart)

        setupApi()
        configureLineChart()
        getStockData("1m")
        val btn1d: Button = view.findViewById(R.id.period1d)
        val btn1m: Button = view.findViewById(R.id.period1m)
        val btn1y: Button = view.findViewById(R.id.period12m)
        btn1d.setOnClickListener {
            getStockData("1h")
        }
        btn1m.setOnClickListener {
            getStockData("1w")
        }
        btn1y.setOnClickListener {
            getStockData("1m")
        }
    }

    private fun getStockData(frequency: String) {
        val endTime: Long = System.currentTimeMillis() / 1000;
        var startTime: Long = 0
        when (frequency) {
            "1h" -> startTime = endTime - 60 * 60 * 24
            "1w" -> startTime = endTime - 60 * 60 * 24 * 30
            "1m" -> startTime = endTime - 60 * 60 * 24 * 356
        }
        services.getHistoricalData(
            frequency,
            "history",
            startTime.toString(),
            endTime.toString(),
            title
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
                    setLineChartData(pricesHigh)
                }
            }

            override fun onFailure(call: Call<HistoricalDataResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setupApi() {
        val cache = Cache(this.context?.cacheDir, cacheSize)
        val client = OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor { chain ->
                var request = chain.request()
                request = if (isNetworkConnected())
                    request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
                else
                    request.newBuilder().header(
                        "Cache-Control",
                        "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7
                    ).build()
                chain.proceed(request)
            }
        services = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitServices::class.java)
    }

    private fun isNetworkConnected(): Boolean {
        val cm = this.context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting
    }

    private fun setLineChartData(
        pricesHigh: MutableList<Entry>
    ) {
        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        val highLineDataSet = LineDataSet(
            pricesHigh,
            title + " Price"
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

    private fun configureLineChart() {
        val desc = Description()
        desc.text = "Stock Price History"
        desc.textSize = 28.0F
        lineChart.description = desc
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            private val mFormat: SimpleDateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)
            override fun getFormattedValue(value: Float): String {
                val millis = value.toLong() * 1000L
                return mFormat.format(Date(millis))
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String) =
            ChartFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                }
            }
    }
}