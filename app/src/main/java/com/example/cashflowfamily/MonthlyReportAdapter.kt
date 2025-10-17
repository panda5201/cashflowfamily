package com.example.cashflowfamily

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowfamily.data.MonthReport
import java.text.NumberFormat
import java.util.Locale

class MonthlyReportAdapter(private var reports: List<MonthReport>) :
    RecyclerView.Adapter<MonthlyReportAdapter.MonthViewHolder>() {

    class MonthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val monthName: TextView = itemView.findViewById(R.id.tv_month_name)
        val incomeText: TextView = itemView.findViewById(R.id.tv_month_income)
        val expenseText: TextView = itemView.findViewById(R.id.tv_month_expense)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_monthly_report, parent, false)
        return MonthViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val report = reports[position]
        val localeID = Locale.forLanguageTag("id-ID")
        val formatter = NumberFormat.getCurrencyInstance(localeID).apply {
            maximumFractionDigits = 0
        }

        holder.monthName.text = report.monthName
        holder.incomeText.text = formatter.format(report.totalIncome)
        holder.expenseText.text = formatter.format(report.totalExpense)
    }

    override fun getItemCount() = reports.size

    fun updateData(newReports: List<MonthReport>) {
        reports = newReports
        notifyDataSetChanged()
    }
}
