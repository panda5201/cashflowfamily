package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class MonthlyReportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report_monthly, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.rv_monthly)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val reportData = mutableListOf<MonthReport>()
        for (month in months) {
            reportData.add(MonthReport(month, 0, 0))
        }

        val adapter = MonthlyReportAdapter(reportData)
        recyclerView.adapter = adapter

        val currentMonthIndex = Calendar.getInstance().get(Calendar.MONTH)
        adapter.setSelected(currentMonthIndex)
        recyclerView.scrollToPosition(currentMonthIndex) // Scroll ke posisi bulan ini
    }
}