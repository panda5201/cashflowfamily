package com.example.cashflowfamily

import com.google.gson.annotations.SerializedName

data class Member(
    @SerializedName("id") val id: Long,
    @SerializedName("name") var name: String,
    @SerializedName("email") var email: String,
    @SerializedName("role") var role: String
)