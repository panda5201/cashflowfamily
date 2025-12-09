// File: com.example.cashflowfamily.data/PredictionApiService.kt

package com.example.cashflowfamily.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// --- Data Classes (Harus cocok dengan 9 fitur di Python) ---

data class PredictionRequest(
    val danceability: Double,
    val energy: Double,
    val valence: Double,
    val tempo: Double,
    val acousticness: Double,
    val instrumentalness: Double,
    val liveness: Double,
    val speechiness: Double,
    val duration_ms: Double
)

data class PredictionResponse(
    val status: String,
    val prediction_status: String, // "Populer" atau "Kurang Populer"
    val predicted_popularity_score: Float,
    val prediction_type: String
)

// --- Interface Service untuk API ML ---

interface PredictionApiService {
    @POST("predict_popularity")
    // Menggunakan @Body untuk mengirim 9 fitur dalam format JSON
    suspend fun getPrediction(@Body request: PredictionRequest): PredictionResponse
}

// --- Retrofit Client Khusus untuk API ML ---

object PredictionRetrofitClient {
    // 10.0.2.2 adalah IP untuk Emulator Android
    private const val BASE_URL = "http://10.0.2.2:5000/"

    val instance: PredictionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PredictionApiService::class.java)
    }
}