package com.example.cashflowfamily.data

data class WeekReport(
    val weekTitle: String,
    val dateRange: String,
    val totalIncome: Double,
    val totalExpense: Double
)