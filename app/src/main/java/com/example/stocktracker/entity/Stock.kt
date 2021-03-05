package com.example.stocktracker.entity

data class Stock(
    val symbol: String,
    val name: String,
    val price: String,
    val changesPercentage: String,
    val change: String
)