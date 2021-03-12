package com.example.stocktracker.ui.detail

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.stocktracker.R
import com.example.stocktracker.viewmodel.ChartFactory
import com.example.stocktracker.viewmodel.ChartViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.marker.view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ChartFragment : Fragment() {
    private lateinit var chartViewModel: ChartViewModel

    private lateinit var lineChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title: String = arguments?.getString("title").toString()
        chartViewModel = ViewModelProvider(this, ChartFactory(activity!!.application, title)).get(
            ChartViewModel::class.java
        )
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
            val btn1w: Button = view.findViewById(R.id.period1w)
            val btn1m: Button = view.findViewById(R.id.period1m)
            val btn1y: Button = view.findViewById(R.id.period12m)
            btn1d.setOnClickListener {
                chartViewModel.getStockData("1d", lineChart)
            }
            btn1w.setOnClickListener {
                chartViewModel.getStockData("1w", lineChart)
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
        lineChart.setNoDataText(null)
        lineChart.description = null
        val marker = Marker(this.requireContext())
        marker.chartView = lineChart
        lineChart.marker = marker
        lineChart.animateY(2000)
        lineChart.xAxis.isEnabled = false
        lineChart.axisLeft.isEnabled = false
        lineChart.axisRight.isEnabled = false
        lineChart.invalidate()
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

class Marker(context: Context) : MarkerView(context, R.layout.marker) {
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun refreshContent(entry: Entry, highlight: Highlight) {
        super.refreshContent(entry, highlight)

        val format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val timestamp = entry.x.toLong()
        val timestampAsDateString = DateTimeFormatter.ISO_INSTANT
            .format(java.time.Instant.ofEpochSecond(timestamp))

        val date = LocalDate.parse(timestampAsDateString, format)

        valueView.text = entry.y.toString() + '$'
        dateView.text = date. dayOfMonth.toString() + ' ' + date.month.toString()
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}