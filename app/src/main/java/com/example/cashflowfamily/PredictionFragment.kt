// File: com.example.cashflowfamily/PredictionFragment.kt

package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.cashflowfamily.data.PredictionRequest
import com.example.cashflowfamily.data.PredictionRetrofitClient
import kotlinx.coroutines.launch

class PredictionFragment : Fragment() {

    // Deklarasi 9 EditText untuk fitur
    private lateinit var etDanceability: EditText
    private lateinit var etEnergy: EditText
    private lateinit var etValence: EditText
    private lateinit var etTempo: EditText
    private lateinit var etAcousticness: EditText
    private lateinit var etInstrumentalness: EditText
    private lateinit var etLiveness: EditText
    private lateinit var etSpeechiness: EditText
    private lateinit var etDurationMs: EditText // Durasi dalam milidetik

    private lateinit var btnPredict: Button
    private lateinit var tvResult: TextView

    private val apiService = PredictionRetrofitClient.instance

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_prediction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- 1. INISIALISASI VIEWS (Sesuaikan ID Anda di XML) ---
        etDanceability = view.findViewById(R.id.etDanceability)
        etEnergy = view.findViewById(R.id.etEnergy)
        etValence = view.findViewById(R.id.etValence)
        etTempo = view.findViewById(R.id.etTempo)
        etAcousticness = view.findViewById(R.id.etAcousticness)
        etInstrumentalness = view.findViewById(R.id.etInstrumentalness)
        etLiveness = view.findViewById(R.id.etLiveness)
        etSpeechiness = view.findViewById(R.id.etSpeechiness)
        etDurationMs = view.findViewById(R.id.etDurationMs)

        btnPredict = view.findViewById(R.id.btnPredict)
        tvResult = view.findViewById(R.id.tvResult)

        // Contoh: Set nilai default agar mudah diuji (Danceability 0.7, Tempo 120, Durasi 200000ms)
        etDanceability.setText("0.7")
        etTempo.setText("120.0")
        etDurationMs.setText("200000.0")


        // --- 2. LOGIKA KLIK TOMBOL ---
        btnPredict.setOnClickListener {
            predictSong()
        }
    }

    private fun predictSong() {
        // Fungsi pembantu untuk mengambil input dan mengonversinya ke Double
        fun getDoubleInput(editText: EditText): Double? {
            return editText.text.toString().trim().toDoubleOrNull()
        }

        // 1. Ambil input dan validasi
        val danceability = getDoubleInput(etDanceability) ?: run { showToast("Danceability invalid."); return }
        val energy = getDoubleInput(etEnergy) ?: run { showToast("Energy invalid."); return }
        val valence = getDoubleInput(etValence) ?: run { showToast("Valence invalid."); return }
        val tempo = getDoubleInput(etTempo) ?: run { showToast("Tempo invalid."); return }
        val acousticness = getDoubleInput(etAcousticness) ?: run { showToast("Acousticness invalid."); return }
        val instrumentalness = getDoubleInput(etInstrumentalness) ?: run { showToast("Instrumentalness invalid."); return }
        val liveness = getDoubleInput(etLiveness) ?: run { showToast("Liveness invalid."); return }
        val speechiness = getDoubleInput(etSpeechiness) ?: run { showToast("Speechiness invalid."); return }
        val durationMs = getDoubleInput(etDurationMs) ?: run { showToast("DurationMs invalid."); return }


        viewLifecycleOwner.lifecycleScope.launch {
            try {
                tvResult.text = "Memprediksi skor popularitas..."

                // 2. Buat objek Request dengan 9 fitur
                val requestBody = PredictionRequest(
                    danceability, energy, valence, tempo, acousticness,
                    instrumentalness, liveness, speechiness, durationMs
                )

                val response = apiService.getPrediction(requestBody)

                // 3. Update UI dengan hasil
                if (response.status == "success") {
                    tvResult.text = """
                        Hasil Prediksi: ${response.prediction_status}
                        Skor Popularitas: ${String.format("%.2f", response.predicted_popularity_score)} / 100
                    """.trimIndent()
                    showToast("Prediksi berhasil!")
                } else {
                    tvResult.text = "Prediksi Gagal: ${response.prediction_status}"
                    showToast("Error: ${response.prediction_status}")
                }

            } catch (e: Exception) {
                // Tangani kegagalan koneksi atau error server
                tvResult.text = "ERROR KONEKSI: Pastikan API Flask berjalan di port 5000. Pesan: ${e.message}"
                showToast("Koneksi API Gagal!")
                e.printStackTrace()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}