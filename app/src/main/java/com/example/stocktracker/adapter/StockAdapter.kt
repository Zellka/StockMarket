package com.example.stocktracker.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stocktracker.R
import com.example.stocktracker.common.StockClickListener
import com.example.stocktracker.data.FavouriteList
import com.example.stocktracker.databinding.StockItemBinding
import com.example.stocktracker.entity.Stock
import kotlinx.android.synthetic.main.stock_item.view.*
import java.util.*
import kotlin.collections.ArrayList

class StockAdapter(private var listener: StockClickListener) :
    RecyclerView.Adapter<StockAdapter.StockViewHolder>(), Filterable {
    private var stocks: MutableList<Stock> = ArrayList()
    private var stockFilterList: MutableList<Stock> = ArrayList()

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

    private var colors = arrayOf("#F0F4F7", "#FFFFFF")

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val item = stockFilterList[position]
        holder.bind(item, listener)
        holder.itemView.setBackgroundColor(Color.parseColor(colors[position % 2]))
        holder.itemView.price_current.text = holder.itemView.price_current.text.toString() + '$'
        holder.itemView.setOnClickListener {
            listener.showStock(item)
        }
        if (holder.itemView.change_percentage.text.contains('+')) {
            holder.itemView.change_percentage.setTextColor(Color.parseColor("#24B25D"))
            holder.itemView.price_change.setTextColor(Color.parseColor("#24B25D"))
            holder.itemView.price_change.text =
                '+' + holder.itemView.price_change.text.toString() + '$'
        } else {
            holder.itemView.change_percentage.setTextColor(Color.parseColor("#B22424"))
            holder.itemView.price_change.setTextColor(Color.parseColor("#B22424"))
            holder.itemView.price_change.text = holder.itemView.price_change.text.toString() + '$'
        }
        if (FavouriteList.isFavourite(item)) {
            holder.itemView.btn_add_to_favourite.setImageResource(R.drawable.ic_star_select_16)
        } else {
            holder.itemView.btn_add_to_favourite.setImageResource(R.drawable.ic_star_16)
        }
    }

    override fun getItemCount(): Int = stockFilterList.size

    class StockViewHolder(private val binding: StockItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("CheckResult")
        fun bind(data: Stock, listener: StockClickListener) {
            binding.stock = data
            var flag = !FavouriteList.isFavourite(data)
            binding.btnAddToFavourite.setOnClickListener {
                listener.addToFavourites(data)
                if (flag) {
                    binding.btnAddToFavourite.setImageResource(R.drawable.ic_star_select_16)
                    flag = !flag
                } else {
                    binding.btnAddToFavourite.setImageResource(R.drawable.ic_star_16)
                    flag = !flag
                }
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
                } else {
                    val resultList: MutableList<Stock> = ArrayList()
                    for (row in stocks) {
                        if (row.ticker.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        } else if (row.companyName.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
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