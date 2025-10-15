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

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

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

            // --- LOGIKA SEMENTARA TANPA DATABASE ---
            Toast.makeText(this, "Login berhasil! (Mode Offline)", Toast.LENGTH_SHORT).show()

            // =======================================================
            // PENAMBAHAN BARU: Menentukan peran berdasarkan email
            // =======================================================
            val userRole: String
            if (email.equals("admin@gmail.com", ignoreCase = true)) {
                userRole = "Admin"
            } else {
                userRole = "Anggota Keluarga"
            }
            // =======================================================

            // Buat intent untuk pindah ke MainActivity
            val intent = Intent(this, MainActivity::class.java)

            // Kirim peran (role) ke MainActivity
            intent.putExtra("USER_ROLE", userRole)
            intent.putExtra("USER_EMAIL", email) // Kirim juga email untuk ditampilkan

            startActivity(intent)
            finish()
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
}