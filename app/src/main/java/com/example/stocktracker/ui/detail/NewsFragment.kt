package com.example.stocktracker.ui.detail

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocktracker.R
import com.example.stocktracker.adapter.NewsAdapter
import com.example.stocktracker.common.NewsClickListener
import com.example.stocktracker.viewmodel.NewsFactory
import com.example.stocktracker.viewmodel.NewsViewModel

class NewsFragment : Fragment(), NewsClickListener {
    private lateinit var newsViewModel: NewsViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title: String = arguments?.getString("title").toString()
        newsViewModel = ViewModelProvider(
            this,
            NewsFactory(activity!!.application, title)
        ).get(NewsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById(R.id.progress_news)
        recyclerView = view.findViewById(R.id.recycler_news)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        adapter = NewsAdapter(this)
        recyclerView.adapter = adapter
        if (isNetworkConnected()) {
            newsViewModel.getAllNews()
            progressBar.visibility = View.VISIBLE
            newsViewModel.newsMutableLiveData.observe(
                viewLifecycleOwner,
                { postModels ->
                    adapter.setList(postModels)
                    progressBar.visibility = View.INVISIBLE
                },
            )
        }
    }

    private fun isNetworkConnected(): Boolean {
        val cm = this.context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting
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

    override fun showNews(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}