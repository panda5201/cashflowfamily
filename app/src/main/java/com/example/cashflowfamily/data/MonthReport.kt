package com.example.cashflowfamily.data

data class MonthReport(
    val monthName: String,
    val year: Int,
    val totalIncome: Double,
    val totalExpense: Double
)