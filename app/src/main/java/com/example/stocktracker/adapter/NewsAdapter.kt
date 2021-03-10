package com.example.stocktracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stocktracker.R
import com.example.stocktracker.common.NewsClickListener
import com.example.stocktracker.databinding.NewsItemBinding
import com.example.stocktracker.entity.News
import kotlin.collections.ArrayList

class NewsAdapter(private var listener: NewsClickListener) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    private var newsList: MutableList<News> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewsViewHolder {
        val binding: NewsItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.news_item,
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val item = newsList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            listener.showNews(item.url)
        }
    }

    override fun getItemCount(): Int = newsList.size

    class NewsViewHolder(private val binding: NewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: News) {
            binding.news = data
            binding.executePendingBindings()
        }
    }

    fun setList(news: MutableList<News>) {
        newsList = news
        notifyDataSetChanged()
    }
}