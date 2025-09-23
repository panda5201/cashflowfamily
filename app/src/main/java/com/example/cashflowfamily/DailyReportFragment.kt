package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageButton
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DailyReportFragment : Fragment() {

    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report_daily, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvPeriod = view.findViewById<TextView>(R.id.tvPeriod)
        val btnPrev = view.findViewById<ImageButton>(R.id.btnPrev)
        val btnNext = view.findViewById<ImageButton>(R.id.btnNext)

        val tvIncome = view.findViewById<TextView>(R.id.tvTotalIncome)
        val tvExpense = view.findViewById<TextView>(R.id.tvTotalExpense)
        val tvBalance = view.findViewById<TextView>(R.id.tvTotalBalance)

        val rvDaily = view.findViewById<RecyclerView>(R.id.rvDailyReport)
        rvDaily.layoutManager = LinearLayoutManager(requireContext())

        // Dummy data awal
        var dummyData = generateDummyData()
        rvDaily.adapter = MonthlyReportAdapter(dummyData.toMutableList())

        // Hitung total dummy
        updateTotals(dummyData, tvIncome, tvExpense, tvBalance)
        updatePeriodTitle(tvPeriod)

        // Navigasi periode
        btnPrev.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updatePeriodTitle(tvPeriod)

            dummyData = generateDummyData() // bisa ganti sesuai bulan
            rvDaily.adapter = MonthlyReportAdapter(dummyData.toMutableList())
            updateTotals(dummyData, tvIncome, tvExpense, tvBalance)
        }

        btnNext.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updatePeriodTitle(tvPeriod)

            dummyData = generateDummyData()
            rvDaily.adapter = MonthlyReportAdapter(dummyData.toMutableList())
            updateTotals(dummyData, tvIncome, tvExpense, tvBalance)
        }
    }

    private fun updatePeriodTitle(tv: TextView) {
        val format = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("id-ID"))
        tv.text = format.format(calendar.time)
    }

    private fun updateTotals(data: List<MonthReport>, tvIncome: TextView, tvExpense: TextView, tvBalance: TextView) {
        val totalIncome = 1_250_000 // dummy
        val totalExpense = data.sumOf { it.expense }
        val balance = totalIncome - totalExpense

        tvIncome.text = "Pemasukan: Rp${String.format("%,d", totalIncome)}"
        tvExpense.text = "Pengeluaran: Rp${String.format("%,d", totalExpense)}"
        tvBalance.text = "Saldo: Rp${String.format("%,d", balance)}"
    }

    private fun generateDummyData(): List<MonthReport> {
        return listOf(
            MonthReport("01", 50_000, 200_000),
            MonthReport("02", 75_000, 125_000),
            MonthReport("03", 100_000, 25_000)
        )
    }
}

