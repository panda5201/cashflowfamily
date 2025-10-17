package com.example.cashflowfamily.auth

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cashflowfamily.R

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val btnContinue = findViewById<Button>(R.id.btnContinue)


        ivBack.setOnClickListener {
            finish()
        }

        btnContinue.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Email harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Format email tidak valid!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Link reset password dikirim ke $email (Mode Offline)", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}