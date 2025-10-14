package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowfamily.ui.MainViewModel
import java.text.NumberFormat
import java.util.Locale

class YearlyReportFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var adapter: YearlyReportAdapter
    private lateinit var tvTotalIncome: TextView
    private lateinit var tvTotalExpense: TextView
    private lateinit var tvTotalBalance: TextView

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
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_yearly)

        adapter = YearlyReportAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.yearlyReportData.observe(viewLifecycleOwner) { yearReports ->
            adapter.updateData(yearReports)

            val totalIncome = yearReports.sumOf { it.totalIncome }
            val totalExpense = yearReports.sumOf { it.totalExpense }
            val totalBalance = totalIncome - totalExpense

            val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).apply {
                maximumFractionDigits = 0
            }

            tvTotalIncome.text = "Total Pemasukan: ${formatter.format(totalIncome)}"
            tvTotalExpense.text = "Total Pengeluaran: ${formatter.format(totalExpense)}"
            tvTotalBalance.text = "Total Saldo: ${formatter.format(totalBalance)}"
        }
    }
}