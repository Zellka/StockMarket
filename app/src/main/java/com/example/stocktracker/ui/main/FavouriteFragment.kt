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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stocktracker.R
import com.example.stocktracker.adapter.StockAdapter
import com.example.stocktracker.common.StockClickListener
import com.example.stocktracker.databinding.FragmentStockBinding
import com.example.stocktracker.entity.Stock
import com.example.stocktracker.ui.detail.CardActivity
import com.example.stocktracker.viewmodel.StockViewModel

class FavouriteFragment : Fragment(), StockClickListener {
    private lateinit var stockViewModel: StockViewModel
    private lateinit var adapter: StockAdapter

    private lateinit var binding: FragmentStockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        stockViewModel = ViewModelProvider(this).get(StockViewModel::class.java)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.title = context?.getString(R.string.title_favourite)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_stock,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stockViewModel.getAllStockList()
        adapter = StockAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)

        stockViewModel.getFavouriteList()
        stockViewModel.favouriteMutableLiveData.observe(viewLifecycleOwner) { data ->
            adapter.setList(data)
        }

        binding.swipeRefresh.setOnRefreshListener {
            stockViewModel.getFavouriteList()
            stockViewModel.favouriteMutableLiveData.observe(viewLifecycleOwner) { data ->
                adapter.setList(data)
            }
            binding.swipeRefresh.isRefreshing = false
        }
        binding.recyclerView.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.supportFragmentManager?.popBackStack()
                (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
                (activity as? AppCompatActivity)?.supportActionBar?.title = "Stock"
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