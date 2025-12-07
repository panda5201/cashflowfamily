package com.example.cashflowfamily.data // <--- WAJIB INI

data class WeekReport(
    val weekTitle: String,
    val dateRange: String,
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double,
    val incomeProgress: Int,
    val expenseProgress: Int
)