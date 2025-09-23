package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WeeklyReportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report_weekly, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.rv_weekly)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val dummyData = listOf(
            WeekReport("Minggu 4", "21.09 ~ 27.09", 1250000, 450000, 73, 27),
            WeekReport("Minggu 3", "14.09 ~ 20.09", 1250000, 850000, 60, 40),
            WeekReport("Minggu 2", "07.09 ~ 13.09", 1250000, 300000, 80, 20),
            WeekReport("Minggu 1", "31.08 ~ 06.09", 1250000, 950000, 57, 43)
        )

        recyclerView.adapter = WeeklyReportAdapter(dummyData)
    }
}