package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class YearlyReportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report_yearly, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.rv_yearly)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Membuat data dummy
        val dummyData = listOf(
            YearlyReport("2025", 60000000, 18000000, 42000000),
            YearlyReport("2024", 58000000, 22000000, 36000000),
            YearlyReport("2023", 55000000, 15000000, 40000000)
        )

        recyclerView.adapter = YearlyReportAdapter(dummyData)
    }
}