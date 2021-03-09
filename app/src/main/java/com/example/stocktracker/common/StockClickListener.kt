package com.example.stocktracker.common

import com.example.stocktracker.entity.Stock

interface StockClickListener {
    fun changeFavouriteList(stockItem: Stock)
    fun showDetailsStock(stockItem: Stock)
}