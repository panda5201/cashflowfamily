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

    fun getBudgets(context: Context): MutableList<Budget> {
        val masterCategories = CategoryRepository.getExpenseCategories(context)

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(BUDGET_KEY, null)
        val savedBudgets: MutableList<Budget> = if (json != null) {
            val type = object : TypeToken<MutableList<Budget>>() {}.type
            Gson().fromJson(json, type)
        } else {
            mutableListOf()
        }

        val syncedBudgets = mutableListOf<Budget>()
        for (categoryName in masterCategories) {
            val existingBudget = savedBudgets.find { it.categoryName.equals(categoryName, ignoreCase = true) }
            if (existingBudget != null) {
                syncedBudgets.add(existingBudget)
            } else {
                syncedBudgets.add(Budget(categoryName, 0.0))
            }
        }
        return syncedBudgets
    }

    fun getBudgetForCategory(context: Context, category: String): Budget? {
        return getBudgets(context).find { it.categoryName.equals(category, ignoreCase = true) }
    }
}