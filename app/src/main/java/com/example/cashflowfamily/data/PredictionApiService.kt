
package com.example.cashflowfamily.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


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
    val prediction_status: String,
    val predicted_popularity_score: Float,
    val prediction_type: String
)

interface PredictionApiService {
    @POST("predict_popularity")
    suspend fun getPrediction(@Body request: PredictionRequest): PredictionResponse
}


object PredictionRetrofitClient {
    private const val BASE_URL = "https://panda5201.pythonanywhere.com/"

    val instance: PredictionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PredictionApiService::class.java)
    }
}