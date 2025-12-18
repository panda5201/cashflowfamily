package com.example.cashflowfamily

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowfamily.data.ApiClient
import com.example.cashflowfamily.data.NotificationItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val rvNotif = findViewById<RecyclerView>(R.id.rv_notifications)
        val tvEmpty = findViewById<TextView>(R.id.tv_empty_state)

        rvNotif.layoutManager = LinearLayoutManager(this)
        ApiClient.instance.getNotifications().enqueue(object : Callback<List<NotificationItem>> {
            override fun onResponse(call: Call<List<NotificationItem>>, response: Response<List<NotificationItem>>) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!

                    if (data.isNotEmpty()) {
                        rvNotif.visibility = View.VISIBLE
                        tvEmpty.visibility = View.GONE

                        // Pasang Adapter
                        rvNotif.adapter = NotificationAdapter(data)
                    } else {
                        rvNotif.visibility = View.GONE
                        tvEmpty.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this@NotificationActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<NotificationItem>>, t: Throwable) {
                Toast.makeText(this@NotificationActivity, "Error Koneksi: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}