package com.example.cashflowfamily

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowfamily.data.WeekReport
import java.text.NumberFormat
import java.util.Locale

class WeeklyReportAdapter(private var reports: List<WeekReport>) :
    RecyclerView.Adapter<WeeklyReportAdapter.WeekViewHolder>() {

    class WeekViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weekTitle: TextView = itemView.findViewById(R.id.tv_week_title)
        val dateRange: TextView = itemView.findViewById(R.id.tv_date_range)
        val incomeText: TextView = itemView.findViewById(R.id.tv_week_income)
        val expenseText: TextView = itemView.findViewById(R.id.tv_week_expense)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_weekly_report, parent, false)
        return WeekViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        val report = reports[position]
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }

        holder.weekTitle.text = report.weekTitle
        holder.dateRange.text = report.dateRange
        holder.incomeText.text = "Pemasukan: ${formatter.format(report.totalIncome)}"
        holder.expenseText.text = "Pengeluaran: ${formatter.format(report.totalExpense)}"
    }

    override fun getItemCount() = reports.size

    fun updateData(newReports: List<WeekReport>) {
        reports = newReports
        notifyDataSetChanged()
    }
}