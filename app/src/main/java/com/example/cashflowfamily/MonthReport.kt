package com.example.cashflowfamily

data class MonthReport(
    val monthName: String,
    val totalExpense: Long, // Ubah ke Long agar cocok dengan ViewModel
    val totalBalance: Long  // Ubah ke Long (sebelumnya mungkin Int/Double)
)