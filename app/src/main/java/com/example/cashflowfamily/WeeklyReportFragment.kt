package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class WeeklyReportFragment : Fragment() {

    private lateinit var tvPeriod: TextView
    private lateinit var btnPrev: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WeeklyReportAdapter

    private val calendar = Calendar.getInstance()
    private val reportData = mutableListOf<WeekReport>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report_weekly, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvPeriod = view.findViewById(R.id.tvPeriod)
        btnPrev = view.findViewById(R.id.btnPrev)
        btnNext = view.findViewById(R.id.btnNext)
        recyclerView = view.findViewById(R.id.rv_weekly)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = WeeklyReportAdapter(reportData)
        recyclerView.adapter = adapter

        btnPrev.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateMonth()
        }
        btnNext.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateMonth()
        }

        updateMonth()
    }

    private fun updateMonth() {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("id-ID")
        )
        tvPeriod.text = dateFormat.format(calendar.time)

        reportData.clear()

        for (week in 1..4) {
            val income: Long = 1_250_000L
            val expense: Long = (300_000L..900_000L).random()
            val balance: Long = income - expense
            val weekLabel = "Minggu $week"
            val dateRange = "xx ~ yy"

            val incomeProgress = (income / 20_000L).toInt().coerceAtMost(100)
            val expenseProgress = (expense / 20_000L).toInt().coerceAtMost(100)

            reportData.add(
                WeekReport(
                    weekLabel,
                    dateRange,
                    income,
                    expense,
                    balance,
                    incomeProgress,
                    expenseProgress
                )
            )
        }


        adapter.notifyDataSetChanged()
    }
}
