package com.example.cashflowfamily.data

import com.google.gson.annotations.SerializedName

data class NotificationItem(
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("created_at")
    val date: String,

    @SerializedName("child_name")
    val childName: String
)