package com.example.stocktracker.ui

import com.example.stocktracker.R
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocktracker.adapter.StockAdapter
import com.example.stocktracker.common.ItemClickListener
import com.example.stocktracker.entity.Stock
import com.example.stocktracker.viewmodel.StockViewModel
import androidx.appcompat.widget.SearchView

class StockFragment : Fragment(), ItemClickListener {

    private lateinit var stockViewModel: StockViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StockAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        stockViewModel = ViewModelProviders.of(this).get(StockViewModel::class.java)
        return inflater.inflate(R.layout.fragment_stock, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stockViewModel.getAllStockList()

        progressBar = view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        adapter = StockAdapter(this)
        recyclerView.adapter = adapter

        progressBar.visibility = View.VISIBLE
        stockViewModel.stocksMutableLiveData.observe(
            viewLifecycleOwner,
            { postModels ->
                adapter.setList(postModels)
                progressBar.visibility = View.INVISIBLE
            },
        )
    }

    override fun changeFavouriteList(stockItem: Stock) {
        Toast.makeText(this.context, stockItem.symbol, Toast.LENGTH_SHORT).show()
        stockViewModel.updateItem(stockItem)
    }

    override fun showDetailsStock(stockItem: Stock) {
        Toast.makeText(this.context, stockItem.symbol, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_search) {
            val searchView = item.actionView as SearchView
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