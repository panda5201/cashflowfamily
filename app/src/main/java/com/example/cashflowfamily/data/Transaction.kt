package com.example.cashflowfamily.data

import android.net.Uri
import java.util.Date

enum class TransactionType {
    INCOME, EXPENSE
}

data class Transaction(
    val id: Long,
    var title: String,
    var amount: Double,
    var type: TransactionType,
    var date: Date,
    var description: String?,
    var imageUri: Uri?
)