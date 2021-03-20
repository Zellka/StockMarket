package com.example.stocktracker.common

import com.example.stocktracker.entity.Stock

interface StockClickListener {
    fun addToFavourites(stockItem: Stock)
    fun showStock(stockItem: Stock)
}