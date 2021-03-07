package com.example.stocktracker.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocktracker.R
import com.example.stocktracker.adapter.NewsAdapter
import com.example.stocktracker.adapter.StockAdapter
import com.example.stocktracker.viewmodel.NewsViewModel
import com.example.stocktracker.viewmodel.StockViewModel


class NewsFragment : Fragment() {

    private var title: String? = null
    private lateinit var newsViewModel: NewsViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString("title")
        }
        newsViewModel = ViewModelProviders.of(this).get(NewsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title?.let { newsViewModel.setSymbol(it) }
        newsViewModel.getAllNews()

        progressBar = view.findViewById(R.id.progress_news)
        recyclerView = view.findViewById(R.id.recycler_news)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        adapter = NewsAdapter()
        recyclerView.adapter = adapter

        progressBar.visibility = View.VISIBLE
        newsViewModel.newsMutableLiveData.observe(
            viewLifecycleOwner,
            { postModels ->
                adapter.setList(postModels)
                progressBar.visibility = View.INVISIBLE
            },
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String) =
            NewsFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                }
            }
    }
}