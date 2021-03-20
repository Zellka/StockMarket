package com.example.stocktracker.ui.detail

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.stocktracker.R
import com.example.stocktracker.databinding.FragmentChartBinding
import com.example.stocktracker.viewmodel.ChartFactory
import com.example.stocktracker.viewmodel.ChartViewModel
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.marker.view.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ChartFragment : Fragment() {
    private lateinit var chartViewModel: ChartViewModel
    private lateinit var binding: FragmentChartBinding
    private lateinit var title: String
    private lateinit var currentPrice: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments?.getString("title").toString()
        currentPrice = arguments?.getString("price").toString()
        chartViewModel = ViewModelProvider(this, ChartFactory(activity!!.application, title)).get(
            ChartViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_chart,
            container,
            false
        )
        return binding.root
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isNetworkConnected()) {
            configureLineChart()
            binding.btnBuy.text = context?.getString(R.string.buy_now) + ' ' + currentPrice + '$'
            getChart("1m")
            binding.period1d.setOnClickListener {
                getChart("1d")
            }
            binding.period1w.setOnClickListener {
                getChart("1w")
            }
            binding.period1m.setOnClickListener {
                getChart("1m")
            }
            binding.period12m.setOnClickListener {
                getChart("1y")
            }
            binding.btnBuy.setOnClickListener {
                this.context?.let {
                    showMessage(it, R.layout.buy_message_layout)
                }
            }
        } else {
            this.context?.let {
                showMessage(it, R.layout.connect_message_layout)
            }
        }
    }

    private fun getChart(frequency: String) {
        val pricesHigh: MutableList<Entry> = ArrayList()
        chartViewModel.getStockData(frequency)
        chartViewModel.chartMutableLiveData.observe(
            viewLifecycleOwner,
            { data ->
                for (i in data.prices.indices) {
                    val x: Float = data.prices[i].date.toFloat()
                    val y: Float = data.prices[i].high
                    if (y != 0f) {
                        pricesHigh.add(Entry(x, data.prices[i].high))
                    }
                }
                pricesHigh.sortBy { it.x }
                setLineChartData(pricesHigh, frequency)
            }
        )
    }

    private fun setLineChartData(
        pricesHigh: MutableList<Entry>,
        frequency: String
    ) {
        var period = ""
        when (frequency) {
            "1d" -> period = context?.getString(R.string.day).toString()
            "1w" -> period = context?.getString(R.string.week).toString()
            "1m" -> period = context?.getString(R.string.month).toString()
            "1y" -> period = context?.getString(R.string.year).toString()
        }
        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        val drawableHigh = ContextCompat.getDrawable(this.requireContext(), R.drawable.chart_high)
        val highLineDataSet = LineDataSet(pricesHigh, "$title $period")
        highLineDataSet.setDrawCircles(false)
        highLineDataSet.setDrawCircleHole(false)
        highLineDataSet.circleRadius = 15f
        highLineDataSet.setDrawValues(false)
        highLineDataSet.lineWidth = 3f
        highLineDataSet.color = Color.BLACK
        highLineDataSet.setCircleColor(Color.BLACK)
        highLineDataSet.setDrawFilled(true)
        highLineDataSet.fillDrawable = drawableHigh
        dataSets.add(highLineDataSet)
        val lineData = LineData(dataSets)
        binding.lineChart.data = lineData
        binding.lineChart.invalidate()
    }

    private fun showMessage(context: Context, layout: Int) {
        val view: View =
            LayoutInflater.from(context).inflate(layout, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
            .setPositiveButton("OK") { dialog, id ->
                dialog.cancel()
            }
        builder.create()
        builder.show()
    }

    private fun isNetworkConnected(): Boolean {
        val cm = this.context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting
    }

    private fun configureLineChart() {
        binding.lineChart.setNoDataText(null)
        binding.lineChart.description = null
        val marker = Marker(this.requireContext())
        marker.chartView = binding.lineChart
        binding.lineChart.marker = marker
        binding.lineChart.animateY(2000)
        binding.lineChart.xAxis.isEnabled = false
        binding.lineChart.axisLeft.isEnabled = false
        binding.lineChart.axisRight.isEnabled = false
        binding.lineChart.invalidate()
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String, currentPrice: String) =
            ChartFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                    putString("price", currentPrice)
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
        dateView.text = date.dayOfMonth.toString() + ' ' + date.month.toString()
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}