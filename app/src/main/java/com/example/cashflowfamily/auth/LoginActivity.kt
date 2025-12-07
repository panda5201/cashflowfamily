package com.example.cashflowfamily.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cashflowfamily.MainActivity
import com.example.cashflowfamily.R
import com.example.cashflowfamily.data.ApiClient
import com.example.cashflowfamily.data.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister) // Link Sign Up
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

        // --- 1. LOGIKA TOMBOL LOGIN (API) ---
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Format email tidak valid!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Panggil API Login
            ApiClient.instance.login(email, password).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val loginResult = response.body()

                    if (response.isSuccessful && loginResult != null && loginResult.status == "success") {
                        val user = loginResult.data

                        Toast.makeText(applicationContext, "Selamat datang, ${user?.name}!", Toast.LENGTH_SHORT).show()

                        // Pindah ke MainActivity bawa data user
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("USER_ROLE", user?.role) // "Admin" atau "Anggota Keluarga"
                        intent.putExtra("USER_EMAIL", user?.email)
                        intent.putExtra("USER_NAME", user?.name)

                        startActivity(intent)
                        finish() // Tutup LoginActivity agar tidak bisa di-back
                    } else {
                        Toast.makeText(applicationContext, "Login Gagal: ${loginResult?.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Error Koneksi: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // --- 2. LOGIKA TOMBOL SIGN UP (PINDAH HALAMAN) ---
        // Ini adalah bagian yang memperbaiki masalah kamu:
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // --- 3. LOGIKA LUPA PASSWORD ---
        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
}