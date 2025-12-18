package com.example.cashflowfamily.data

import com.google.gson.annotations.SerializedName

data class ResponseModel(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    // TAMBAHAN PENTING (Biar error 'warning' hilang):
    @SerializedName("warning")
    val warning: String? = null
)