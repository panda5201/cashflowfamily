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
import java.util.*

class MonthlyReportFragment : Fragment() {

    private lateinit var tvPeriod: TextView
    private lateinit var btnPrev: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MonthlyReportAdapter

    private val calendar = Calendar.getInstance()
    private val reportData = mutableListOf<MonthReport>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report_monthly, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvPeriod = view.findViewById(R.id.tvPeriod)
        btnPrev = view.findViewById(R.id.btnPrev)
        btnNext = view.findViewById(R.id.btnNext)
        recyclerView = view.findViewById(R.id.rv_monthly)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = MonthlyReportAdapter(reportData)
        recyclerView.adapter = adapter

        btnPrev.setOnClickListener {
            calendar.add(Calendar.YEAR, -1)
            updateYear()
        }
        btnNext.setOnClickListener {
            calendar.add(Calendar.YEAR, 1)
            updateYear()
        }

        updateYear()
    }

    private fun updateYear() {
        val year = calendar.get(Calendar.YEAR)
        tvPeriod.text = year.toString()

        reportData.clear()
        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

        for (m in months.indices) {
            val expense: Long = 100_000L * (m + 1)
            val income: Long = 500_000L
            val balance: Long = income - expense

            reportData.add(MonthReport("${months[m]} $year", expense, balance))
        }

        adapter.notifyDataSetChanged()

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        if (year == currentYear) {
            val currentMonthIndex = Calendar.getInstance().get(Calendar.MONTH)
            adapter.setSelected(currentMonthIndex)
            recyclerView.scrollToPosition(currentMonthIndex)
        } else {
            adapter.setSelected(-1)
        }
    }
}
