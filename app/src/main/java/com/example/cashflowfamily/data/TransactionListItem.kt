package com.example.cashflowfamily.data

import java.util.Date

sealed class TransactionListItem {
    /**
     * @param date
     * @param totalIncome
     * @param totalExpense
     */
    data class DateHeader(
        val date: Date,
        val totalIncome: Double,
        val totalExpense: Double
    ) : TransactionListItem()

    data class TransactionItem(
        val transaction: Transaction
    ) : TransactionListItem()
}