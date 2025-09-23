package com.example.cashflowfamily

data class WeekReport(
    val weekTitle: String,
    val dateRange: String,
    val income: Long,
    val expense: Long,
    val incomeProgress: Int,
    val expenseProgress: Int
)