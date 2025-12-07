package com.example.cashflowfamily.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream

object TransactionRepository {

    val transactionsLiveData = MutableLiveData<List<Transaction>>()
    private var appContext: Context? = null

    // PENTING: Panggil ini di MainActivity.onCreate agar bisa baca file gambar
    fun setContext(context: Context) {
        appContext = context
    }

    // --- FUNGSI HELPER: UBAH GAMBAR KE BASE64 STRING ---
    private fun uriToBase64(uri: Uri): String {
        if (appContext == null) return ""
        return try {
            val inputStream: InputStream? = appContext!!.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()
            // Kompres ke JPEG 50% agar ringan dikirim
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    // --- 1. AMBIL DATA (READ) ---
    fun fetchTransactions() {
        ApiClient.instance.getTransactions().enqueue(object : Callback<List<Transaction>> {
            override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                if (response.isSuccessful) {
                    transactionsLiveData.value = response.body()
                    Log.d("DEBUG_REPO", "Data fetched: ${response.body()?.size} items")
                } else {
                    Log.e("DEBUG_REPO", "Fetch failed: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                Log.e("DEBUG_REPO", "Fetch Error: ${t.message}")
            }
        })
    }

    // --- 2. TAMBAH DATA (CREATE) ---
    fun addTransaction(transaction: Transaction) {
        // Logika Gambar: Jika ada URI lokal, ubah jadi Base64
        val imageString = if (!transaction.imageUri.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(transaction.imageUri)
                uriToBase64(uri)
            } catch (e: Exception) { "" }
        } else { "" }

        ApiClient.instance.addTransaction(
            transaction.title,
            transaction.amount,
            transaction.type,
            transaction.date,
            transaction.description ?: "",
            imageString // Kirim Base64
        ).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("DEBUG_REPO", "Add Success")
                    fetchTransactions() // Refresh list
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("DEBUG_REPO", "Add Error: ${t.message}")
            }
        })
    }

    // --- 3. UPDATE DATA (UPDATE) ---
    fun updateTransaction(transaction: Transaction) {
        // Logika Pintar untuk Gambar saat Edit:
        // 1. Jika imageUri adalah URL (http...), berarti user TIDAK ganti foto. Kirim string kosong "".
        // 2. Jika imageUri adalah Path Lokal (content://...), berarti user GANTI foto. Ubah jadi Base64.

        val imageString = if (!transaction.imageUri.isNullOrEmpty()) {
            if (transaction.imageUri!!.startsWith("http")) {
                "" // Jangan kirim apa-apa, biar PHP pakai gambar lama
            } else {
                try {
                    val uri = Uri.parse(transaction.imageUri)
                    uriToBase64(uri)
                } catch (e: Exception) { "" }
            }
        } else { "" }

        ApiClient.instance.updateTransaction(
            transaction.id,
            transaction.title,
            transaction.amount,
            transaction.type,
            transaction.date,
            transaction.description ?: "",
            imageString // Kirim Base64 (atau kosong jika tidak ganti foto)
        ).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("DEBUG_REPO", "Update Success")
                    fetchTransactions()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("DEBUG_REPO", "Update Error: ${t.message}")
            }
        })
    }

    // --- 4. HAPUS DATA (DELETE) ---
    fun deleteTransaction(id: Long) {
        ApiClient.instance.deleteTransaction(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) fetchTransactions()
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {}
        })
    }

    // --- 5. GET BY ID ---
    fun getTransactionById(id: Long): Transaction? {
        // Cari di list lokal yang sudah didownload
        return transactionsLiveData.value?.find { it.id == id }
    }
}