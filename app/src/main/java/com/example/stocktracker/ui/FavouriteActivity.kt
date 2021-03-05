package com.example.stocktracker.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocktracker.R
import com.example.stocktracker.adapter.StockAdapter
import com.example.stocktracker.common.FavouriteList
import com.example.stocktracker.common.ItemClickListener
import com.example.stocktracker.entity.Stock

class FavouriteActivity : AppCompatActivity(), ItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StockAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_favourite)

        recyclerView = findViewById(R.id.recycler_view)
        adapter = StockAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.setList(FavouriteList.getFavouriteList())
        recyclerView.adapter = adapter
    }

    override fun changeFavouriteList(stockItem: Stock) {
        Toast.makeText(this, stockItem.symbol, Toast.LENGTH_SHORT).show()
        FavouriteList.addItem(stockItem)
    }

    override fun showDetailsStock(stockItem: Stock) {
        Toast.makeText(this, stockItem.symbol, Toast.LENGTH_SHORT).show()
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