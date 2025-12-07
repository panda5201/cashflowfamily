package com.example.cashflowfamily

data class WeekReport(
    val weekTitle: String,
    val dateRange: String,
    val totalIncome: Double,  // Ubah ke Double (sebelumnya mungkin Long)
    val totalExpense: Double, // Ubah ke Double
    val balance: Double = 0.0,
    val incomeProgress: Int = 0,
    val expenseProgress: Int = 0
)