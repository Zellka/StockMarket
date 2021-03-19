package com.example.stocktracker.ui.main

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.*
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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

class StockFragment : Fragment(), StockClickListener {
    private lateinit var stockViewModel: StockViewModel
    private lateinit var adapter: StockAdapter

    private lateinit var binding: FragmentStockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stockViewModel = ViewModelProvider(this).get(StockViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as? AppCompatActivity)?.supportActionBar?.title = context?.getString(R.string.title_stock)
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
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        adapter = StockAdapter(this)
        binding.recyclerView.adapter = adapter
        getDataList()
        binding.swipeRefresh.setOnRefreshListener {
            binding.progressBar.visibility = View.INVISIBLE
            getDataList()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun getDataList(){
        if (isNetworkConnected()) {
            stockViewModel.getAllStockList()
            binding.progressBar.visibility = View.VISIBLE
            stockViewModel.stocksMutableLiveData.observe(
                viewLifecycleOwner,
                { postModels ->
                    adapter.setList(postModels)
                    binding.progressBar.visibility = View.INVISIBLE
                }
            )
        } else {
            this.context?.let {
                showAlertDialog(
                    it
                )
            };
        }
    }

    private fun showAlertDialog(context: Context) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.connect_message_layout, null)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.stock_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_search) {
            val searchView = item.actionView as SearchView
            searchView.queryHint = context?.getString(R.string.search_hint)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return true
                }
            })
        }
        if (id == R.id.action_favourite) {
            val fragment = FavouriteFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, fragment)?.addToBackStack(null)?.commit()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}