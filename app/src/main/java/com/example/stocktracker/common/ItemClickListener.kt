package com.example.stocktracker.common

import com.example.stocktracker.entity.Stock

interface ItemClickListener {
    fun addToFavouriteList(stockItem: Stock)
}