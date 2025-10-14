package com.example.cashflowfamily

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowfamily.data.YearReport
import java.text.NumberFormat
import java.util.Locale

class YearlyReportAdapter(private var reports: List<YearReport>) :
    RecyclerView.Adapter<YearlyReportAdapter.YearViewHolder>() {

    class YearViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val yearTitle: TextView = itemView.findViewById(R.id.tv_year_title)
        val incomeText: TextView = itemView.findViewById(R.id.tv_year_income)
        val expenseText: TextView = itemView.findViewById(R.id.tv_year_expense)
        val balanceText: TextView = itemView.findViewById(R.id.tv_year_balance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_yearly_report, parent, false)
        return YearViewHolder(view)
    }

    override fun onBindViewHolder(holder: YearViewHolder, position: Int) {
        val report = reports[position]
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
        val balance = report.totalIncome - report.totalExpense

        holder.yearTitle.text = report.year.toString()
        holder.incomeText.text = "Total Pemasukan: ${formatter.format(report.totalIncome)}"
        holder.expenseText.text = "Total Pengeluaran: ${formatter.format(report.totalExpense)}"
        holder.balanceText.text = "Saldo Akhir: ${formatter.format(balance)}"
    }

    override fun getItemCount() = reports.size

    fun updateData(newReports: List<YearReport>) {
        reports = newReports
        notifyDataSetChanged()
    }
}