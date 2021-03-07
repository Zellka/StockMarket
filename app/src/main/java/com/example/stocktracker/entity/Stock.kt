package com.example.stocktracker.entity

data class Stock(
    val ticker: String,
    val companyName: String,
    val price: String,
    val changesPercentage: String,
    val changes: String
)