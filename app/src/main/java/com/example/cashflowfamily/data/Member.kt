package com.example.cashflowfamily.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Member(
    val id: Long,
    val name: String,
    val email: String,
    val role: String,

    var currentExpense: Double = 0.0,
    var limitBudget: Double = 0.0

) : Parcelable