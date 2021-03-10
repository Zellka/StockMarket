package com.example.stocktracker.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.stocktracker.R
import com.example.stocktracker.adapter.StockAdapter
import com.example.stocktracker.common.StockClickListener
import com.example.stocktracker.entity.Stock
import com.example.stocktracker.ui.detail.CardActivity
import com.example.stocktracker.viewmodel.StockViewModel

class FavouriteFragment : Fragment(), StockClickListener {

    private lateinit var stockViewModel: StockViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StockAdapter
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        stockViewModel = ViewModelProvider(this).get(StockViewModel::class.java)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stock, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stockViewModel.getAllStockList()

        recyclerView = view.findViewById(R.id.recycler_view)
        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        adapter = StockAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        adapter.setList(stockViewModel.getFavouriteList())
        swipeRefresh.setOnRefreshListener {
            adapter.setList(stockViewModel.getFavouriteList())
            swipeRefresh.isRefreshing = false
        }
        recyclerView.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.supportFragmentManager?.popBackStack()
                (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun changeFavouriteList(stockItem: Stock) {
        Toast.makeText(this.context, stockItem.ticker, Toast.LENGTH_SHORT).show()
        stockViewModel.updateItem(stockItem)
    }

    override fun showDetailsStock(stockItem: Stock) {
        val intent = Intent(this.context, CardActivity::class.java)
        val bundle = bundleOf(
            Pair("TICKER_STOCK", stockItem.ticker),
            Pair("COMPANY_NAME_STOCK", stockItem.companyName)
        )
        intent.putExtras(bundle)
        startActivity(intent)
    }
}