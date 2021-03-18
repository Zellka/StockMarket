package com.example.stocktracker.data

import com.example.stocktracker.entity.Stock
import io.paperdb.Paper

class FavouriteList {
    companion object {
        fun updateItem(stockItem: Stock) {
            val favouriteList = getFavouriteList()
            val targetItem = favouriteList.singleOrNull { it.ticker == stockItem.ticker }
            if (targetItem == null) {
                favouriteList.add(stockItem)
            } else {
                favouriteList.remove(stockItem)
            }
            saveStock(favouriteList)
        }

        fun isFavourite(stockItem: Stock): Boolean {
            val favouriteList = getFavouriteList()
            return favouriteList.contains(stockItem)
        }

        private fun saveStock(stock: MutableList<Stock>) {
            Paper.book().write("stock", stock)
        }

        fun getFavouriteList(): MutableList<Stock> {
            return Paper.book().read("stock", mutableListOf())
        }
    }
}