package com.example.stocktracker.common

import com.example.stocktracker.entity.Stock
import io.paperdb.Paper

class FavouriteList {

    companion object {
        fun addItem(stockItem: Stock) {
            val favouriteList = getFavouriteList()

            val targetItem = favouriteList.singleOrNull { it.symbol == stockItem.symbol }
            if (targetItem == null) {
                favouriteList.add(stockItem)
            } else {
                favouriteList.remove(stockItem)
            }
            saveStock(favouriteList)
        }

        private fun saveStock(stock: MutableList<Stock>) {
            Paper.book().write("stock", stock)
        }

        fun getFavouriteList(): MutableList<Stock> {
            return Paper.book().read("stock", mutableListOf())
        }
    }
}