package com.example.cashflowfamily

data class Member(
    val id: Long,
    var name: String,
    var email: String,
    var role: String // "Admin" atau "Anggota Keluarga"
)