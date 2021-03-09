package com.example.stocktracker.entity

data class HistoricalDataResponse(
    val prices: List<StockPrice>,
    val isPending: Boolean,
    val firstTradeDate: Long,
    val id: String
)

data class StockPrice(
    val date: Long,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val volume: Int,
    val adjclose: Float
)