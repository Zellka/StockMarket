package com.example.stocktracker.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stocktracker.R
import com.example.stocktracker.common.ItemClickListener
import com.example.stocktracker.databinding.StockItemBinding
import com.example.stocktracker.entity.Stock
import java.util.*
import kotlin.collections.ArrayList

class StockAdapter(private var listener: ItemClickListener) :
    RecyclerView.Adapter<StockAdapter.StockViewHolder>(), Filterable {

    //private var listener: ItemClickListener = MainActivity()

    private var stocks: MutableList<Stock> = ArrayList()
    private var stockFilterList: MutableList<Stock> = ArrayList()

    private val SIZE_LIMIT: Int = 20

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StockViewHolder {
        val binding: StockItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.stock_item,
            parent,
            false
        )
        return StockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockAdapter.StockViewHolder, position: Int) {
        val item = stockFilterList[position]
        holder.bind(item, listener, position)
    }

    override fun getItemCount(): Int {
        return if (stockFilterList.size > SIZE_LIMIT) {
            SIZE_LIMIT
        } else {
            stockFilterList.size
        }
    }

    class StockViewHolder(private val binding: StockItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("CheckResult")
        fun bind(data: Stock, listener: ItemClickListener, position: Int) {
            binding.stock = data
            binding.btnAddToFavourite.setOnClickListener {
                listener.changeFavouriteList(data)
            }
            binding.executePendingBindings()
        }
    }

    fun setList(stockList: MutableList<Stock>) {
        stockFilterList = stockList
        stocks = stockList
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    stockFilterList = stocks
                }
                else {
                    val resultList: MutableList<Stock> = ArrayList()
                    for (row in stocks) {
                        if (row.symbol.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    stockFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = stockFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                stockFilterList = results?.values as MutableList<Stock>
                notifyDataSetChanged()
            }
        }
    }
}