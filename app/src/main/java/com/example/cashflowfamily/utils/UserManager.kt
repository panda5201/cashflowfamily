package com.example.cashflowfamily.utils

import android.content.Context
import android.content.SharedPreferences

object UserManager {

    private const val PREFS_NAME = "user_prefs"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUser(id: Long, name: String, email: String, role: String) {
        sharedPreferences.edit().apply {
            putLong(KEY_USER_ID, id)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_ROLE, role)
            apply()
        }
    }

    fun clearUser() {
        sharedPreferences.edit().clear().apply()
    }

    fun getUserId(): Long {
        return sharedPreferences.getLong(KEY_USER_ID, -1L)
    }

    fun getUserRole(): String? {
        return sharedPreferences.getString(KEY_USER_ROLE, null)
    }

    fun getUserName(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.contains(KEY_USER_ID)
    }
}
