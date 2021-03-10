package com.example.stocktracker.ui.detail

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.stocktracker.R
import com.example.stocktracker.viewmodel.ChartFactory
import com.example.stocktracker.viewmodel.ChartViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ChartFragment : Fragment() {
    private lateinit var chartViewModel: ChartViewModel

    private lateinit var lineChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title: String = arguments?.getString("title").toString()
        chartViewModel = ViewModelProvider(this, ChartFactory(activity!!.application, title)).get(ChartViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lineChart = view.findViewById(R.id.line_chart)
        if (isNetworkConnected()) {
            configureLineChart()
            chartViewModel.getStockData("1m", lineChart)
            val btn1d: Button = view.findViewById(R.id.period1d)
            val btn1m: Button = view.findViewById(R.id.period1m)
            val btn1y: Button = view.findViewById(R.id.period12m)
            btn1d.setOnClickListener {
                chartViewModel.getStockData("1d", lineChart)
            }
            btn1m.setOnClickListener {
                chartViewModel.getStockData("1m", lineChart)
            }
            btn1y.setOnClickListener {
                chartViewModel.getStockData("1y", lineChart)

            }
        } else {
            this.context?.let {
                showAlertDialog(
                    it, "No internet connection",
                    "No internet connection"
                )
            };
        }
    }

    private fun showAlertDialog(context: Context, title: String, message: String) {
        val alertDialog: AlertDialog = AlertDialog.Builder(context).create();
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.show()
    }

    private fun isNetworkConnected(): Boolean {
        val cm = this.context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting
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