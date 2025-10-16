package com.example.cashflowfamily.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CategoryRepository {
    private const val PREFS_NAME = "category_prefs"
    private const val INCOME_KEY = "income_categories"
    private const val EXPENSE_KEY = "expense_categories"

    private val defaultIncome = listOf("Gaji", "Bonus", "Hadiah")
    private val defaultExpense = listOf("Makanan", "Transportasi", "Sekolah", "Hiburan")

    // Fungsi untuk mengambil kategori PEMASUKAN
    fun getIncomeCategories(context: Context): MutableList<String> {
        return getCategories(context, INCOME_KEY, defaultIncome)
    }

    // Fungsi untuk mengambil kategori PENGELUARAN
    fun getExpenseCategories(context: Context): MutableList<String> {
        return getCategories(context, EXPENSE_KEY, defaultExpense)
    }

    // Fungsi untuk MENYIMPAN kategori PEMASUKAN
    fun saveIncomeCategories(context: Context, categories: List<String>) {
        saveCategories(context, INCOME_KEY, categories)
    }

    // Fungsi untuk MENYIMPAN kategori PENGELUARAN
    fun saveExpenseCategories(context: Context, categories: List<String>) {
        saveCategories(context, EXPENSE_KEY, categories)
    }

    // Fungsi helper generik
    private fun getCategories(context: Context, key: String, defaults: List<String>): MutableList<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<String>>() {}.type
            Gson().fromJson(json, type)
        } else {
            defaults.toMutableList()
        }
    }

    private fun saveCategories(context: Context, key: String, categories: List<String>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(categories)
        editor.putString(key, json)
        editor.apply()
    }
}