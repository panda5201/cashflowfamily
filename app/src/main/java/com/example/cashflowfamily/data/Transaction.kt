package com.example.cashflowfamily.data

import java.util.Date
import com.google.gson.annotations.SerializedName
enum class TransactionType {
    INCOME, EXPENSE
}

data class Transaction(
    val id: Long,

    @SerializedName("member_id")
    val memberId: Long,

    val title: String,

    val amount: Double,

    val type: String,

    val date: Long,

    val description: String?,

    @SerializedName("image_uri")
    var imageUri: String?
)