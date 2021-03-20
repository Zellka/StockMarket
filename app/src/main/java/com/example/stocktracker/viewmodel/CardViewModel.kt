package com.example.stocktracker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.stocktracker.data.FavouriteList
import com.example.stocktracker.entity.Stock

class CardViewModel : ViewModel() {
    fun updateItem(stockItem: Stock) {
        FavouriteList.updateItem(stockItem)
    }

    fun isFavourite(stockItem: Stock): Boolean {
        return FavouriteList.isFavourite(stockItem)
    }
}