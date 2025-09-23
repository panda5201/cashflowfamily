package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class YearlyReportFragment : Fragment() {

    private lateinit var tvTotalIncome: TextView
    private lateinit var tvTotalExpense: TextView
    private lateinit var tvTotalBalance: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report_yearly, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTotalIncome = view.findViewById(R.id.tvTotalIncome)
        tvTotalExpense = view.findViewById(R.id.tvTotalExpense)
        tvTotalBalance = view.findViewById(R.id.tvTotalBalance)
        recyclerView = view.findViewById(R.id.rv_yearly)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val dummyData = listOf(
            YearlyReport("2025", 60_000_000L, 18_000_000L, 42_000_000L),
            YearlyReport("2024", 58_000_000L, 22_000_000L, 36_000_000L),
            YearlyReport("2023", 55_000_000L, 15_000_000L, 40_000_000L)
        )

        recyclerView.adapter = YearlyReportAdapter(dummyData)

        val totalIncome = dummyData.sumOf { it.totalIncome }
        val totalExpense = dummyData.sumOf { it.totalExpense }
        val totalBalance = dummyData.sumOf { it.finalBalance }

        tvTotalIncome.text = "Total Pemasukan: Rp$totalIncome"
        tvTotalExpense.text = "Total Pengeluaran: Rp$totalExpense"
        tvTotalBalance.text = "Total Saldo: Rp$totalBalance"

    }
}
