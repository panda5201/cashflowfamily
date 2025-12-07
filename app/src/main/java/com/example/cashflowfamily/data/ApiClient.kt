package com.example.cashflowfamily.data

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date

object ApiClient {
    // Ganti IP sesuai IP Laptop kamu jika run di HP asli (misal: 192.168.1.5)
    // Gunakan 10.0.2.2 jika run di Emulator Android Studio
    private const val BASE_URL = "http://10.0.2.2/cashflow_api/"

    private val gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, com.google.gson.JsonDeserializer { json, _, _ ->
            Date(json.asJsonPrimitive.asLong)
        })
        .create()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)) // <--- Pakai gson custom tadi
            .build()
        retrofit.create(ApiService::class.java)
    }
}