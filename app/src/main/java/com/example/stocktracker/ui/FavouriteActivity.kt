package com.example.stocktracker.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocktracker.R
import com.example.stocktracker.adapter.StockAdapter
import com.example.stocktracker.common.ItemClickListener
import com.example.stocktracker.entity.Stock
import com.example.stocktracker.viewmodel.StockViewModel

class FavouriteActivity : AppCompatActivity(), ItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StockAdapter
    private lateinit var stockViewModel: StockViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_favourite)

        recyclerView = findViewById(R.id.recycler_view)
        adapter = StockAdapter(this)
        stockViewModel = ViewModelProviders.of(this).get(StockViewModel::class.java)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.setList(stockViewModel.getFavouriteList())
        recyclerView.adapter = adapter
    }

    override fun changeFavouriteList(stockItem: Stock) {
        Toast.makeText(this, stockItem.symbol, Toast.LENGTH_SHORT).show()
        stockViewModel.addItem(stockItem)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}