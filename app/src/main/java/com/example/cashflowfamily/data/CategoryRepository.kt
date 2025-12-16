package com.example.cashflowfamily.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object CategoryRepository {

    val categoriesLiveData = MutableLiveData<List<Category>>()
    private var appContext: Context? = null

    fun setContext(context: Context) {
        appContext = context
    }

    // Fungsi untuk memuat kategori dari API
    fun fetchCategories() {
        ApiClient.instance.getCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    categoriesLiveData.value = response.body()
                    Log.d("CategoryRepo", "Categories fetched: ${response.body()?.size} items")
                } else {
                    Log.e("CategoryRepo", "Failed to fetch categories: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Log.e("CategoryRepo", "Error fetching categories: ${t.message}")
            }
        })
    }

    // Fungsi untuk menambahkan kategori baru via API
    fun addCategory(name: String, type: String, callback: (Boolean, String) -> Unit) {
        ApiClient.instance.addCategory(name, type).enqueue(object : Callback<CategoryAddResponse> {
            override fun onResponse(call: Call<CategoryAddResponse>, response: Response<CategoryAddResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    fetchCategories() // Refresh daftar setelah berhasil menambah
                    callback(true, response.body()?.message ?: "Kategori berhasil ditambahkan")
                } else {
                    val errorMessage = response.body()?.message ?: "Gagal menambahkan kategori"
                    Log.e("CategoryRepo", "Add category failed: $errorMessage")
                    callback(false, errorMessage)
                }
            }

            override fun onFailure(call: Call<CategoryAddResponse>, t: Throwable) {
                Log.e("CategoryRepo", "Error adding category: ${t.message}")
                callback(false, "Error koneksi: ${t.message}")
            }
        })
    }

    // Fungsi untuk mengambil kategori PEMASUKAN
    fun getIncomeCategories(): List<String> {
        return categoriesLiveData.value?.filter { it.type == TransactionType.INCOME.name }?.map { it.name }?.toMutableList() ?: mutableListOf()
    }

    // Fungsi untuk mengambil kategori PENGELUARAN
    fun getExpenseCategories(): List<String> {
        return categoriesLiveData.value?.filter { it.type == TransactionType.EXPENSE.name }?.map { it.name }?.toMutableList() ?: mutableListOf()
    }
}