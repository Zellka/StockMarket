package com.example.stocktracker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.stocktracker.R
import com.example.stocktracker.adapter.StockAdapter
import com.example.stocktracker.common.ItemClickListener
import com.example.stocktracker.databinding.ActivityMainBinding
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stocktracker.entity.Stock
import com.example.stocktracker.viewmodel.StockViewModel
import io.paperdb.Paper

class MainActivity : AppCompatActivity(), ItemClickListener {

    lateinit var binding: ActivityMainBinding
    private lateinit var stockViewModel: StockViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StockAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        Paper.init(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        stockViewModel = ViewModelProviders.of(this).get(StockViewModel::class.java)
        binding.executePendingBindings()

        stockViewModel.getAllStockList()

        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StockAdapter(this)
        recyclerView.adapter = adapter

        progressBar.visibility = View.VISIBLE
        stockViewModel.stocksMutableLiveData.observe(
            this,
            Observer { postModels ->
                adapter.setList(postModels)
                progressBar.visibility = View.INVISIBLE
            },
        )
    }

    override fun addToFavouriteList(stockItem: Stock) {
        TODO("Not yet implemented")
    }

}