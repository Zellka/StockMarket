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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.stocktracker.R
import com.example.stocktracker.databinding.FragmentChartBinding
import com.example.stocktracker.viewmodel.ChartFactory
import com.example.stocktracker.viewmodel.ChartViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.marker.view.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ChartFragment : Fragment() {
    private lateinit var chartViewModel: ChartViewModel
    private lateinit var binding: FragmentChartBinding

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
    ): View {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_chart,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isNetworkConnected()) {
            configureLineChart()
            chartViewModel.getStockData("1m", binding.lineChart)
            binding.period1d.setOnClickListener {
                chartViewModel.getStockData("1d", binding.lineChart)
            }
            binding.period1w.setOnClickListener {
                chartViewModel.getStockData("1w", binding.lineChart)
            }
            binding.period1m.setOnClickListener {
                chartViewModel.getStockData("1m", binding.lineChart)
            }
            binding.period12m.setOnClickListener {
                chartViewModel.getStockData("1y", binding.lineChart)
            }
        } else {
            this.context?.let {
                showAlertDialog(
                    it
                )
            }
        }
    }

    private fun showAlertDialog(context: Context) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.custom_dialog, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
            .setPositiveButton("OK") { dialog, id ->  dialog.cancel()
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