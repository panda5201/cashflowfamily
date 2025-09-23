package com.example.cashflowfamily

data class MonthReport(
    val month: String,
    val expense: Long,
    val balance: Long,
    var isSelected: Boolean = false
)