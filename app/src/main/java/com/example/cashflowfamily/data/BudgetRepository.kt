package com.example.cashflowfamily.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object BudgetRepository {
    private const val PREFS_NAME = "budget_prefs"
    private const val BUDGET_KEY = "budgets"

    fun saveBudgets(context: Context, budgets: List<Budget>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(budgets)
        editor.putString(BUDGET_KEY, json)
        editor.apply()
    }

    // --- FUNGSI getBudgets SEKARANG DISINKRONKAN DENGAN CATEGORY REPOSITORY ---
    fun getBudgets(context: Context): MutableList<Budget> {
        // 1. Ambil master kategori pengeluaran terbaru
        val masterCategories = CategoryRepository.getExpenseCategories(context)

        // 2. Ambil data budget yang sudah tersimpan
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(BUDGET_KEY, null)
        val savedBudgets: MutableList<Budget> = if (json != null) {
            val type = object : TypeToken<MutableList<Budget>>() {}.type
            Gson().fromJson(json, type)
        } else {
            mutableListOf()
        }

        // 3. Sinkronkan: Buat daftar budget baru yang sesuai dengan master kategori
        val syncedBudgets = mutableListOf<Budget>()
        for (categoryName in masterCategories) {
            // Cari budget yang sudah ada untuk kategori ini
            val existingBudget = savedBudgets.find { it.categoryName.equals(categoryName, ignoreCase = true) }
            if (existingBudget != null) {
                // Jika ada, gunakan yang sudah ada
                syncedBudgets.add(existingBudget)
            } else {
                // Jika tidak ada (ini adalah kategori baru), buat budget baru dengan nilai 0
                syncedBudgets.add(Budget(categoryName, 0.0))
            }
        }
        return syncedBudgets
    }

    fun getBudgetForCategory(context: Context, category: String): Budget? {
        return getBudgets(context).find { it.categoryName.equals(category, ignoreCase = true) }
    }
}