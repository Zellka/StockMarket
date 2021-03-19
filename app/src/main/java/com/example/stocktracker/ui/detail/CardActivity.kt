package com.example.stocktracker.ui.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.stocktracker.R
import com.example.stocktracker.adapter.SectionsPagerAdapter
import com.example.stocktracker.entity.Stock
import com.example.stocktracker.viewmodel.CardViewModel
import com.google.android.material.tabs.TabLayout

@Suppress("UNREACHABLE_CODE")
class CardActivity : AppCompatActivity() {
    private lateinit var cardViewModel: CardViewModel
    private lateinit var stockItem: Stock

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        cardViewModel = ViewModelProvider(this).get(CardViewModel::class.java)

        val arguments = intent.extras
        val title = arguments?.getString("TICKER_STOCK").toString()
        val subtitle = arguments?.getString("COMPANY_NAME_STOCK").toString()
        val price = arguments?.getString("PRICE_STOCK").toString()
        val changes = arguments?.getString("CHANGES_STOCK").toString()
        val changesPercentage = arguments?.getString("CHANGES_PERCENTAGE_STOCK").toString()
        stockItem = Stock(title, subtitle, price, changesPercentage, changes)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = title
        supportActionBar!!.subtitle = subtitle

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, title)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        val item = menu!!.findItem(R.id.action_favourite)
        if (cardViewModel.isFavourite(stockItem)) {
            item.icon = applicationContext.getDrawable(R.drawable.ic_star_select_24)
        } else {
            item.icon = applicationContext.getDrawable(R.drawable.ic_star_24)
        }
        return true
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            this.finish()
            return true
        }
        if (id == R.id.action_favourite) {
            if (cardViewModel.isFavourite(stockItem)) {
                item.icon = applicationContext.getDrawable(R.drawable.ic_star_24)
            } else {
                item.icon = applicationContext.getDrawable(R.drawable.ic_star_select_24)
            }
            cardViewModel.updateItem(stockItem)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}