package com.example.cashflowfamily

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class WeeklyReportAdapter(private val reportList: List<WeekReport>) :
    RecyclerView.Adapter<WeeklyReportAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val weekTitle: TextView = view.findViewById(R.id.tv_week_title)
        val dateRange: TextView = view.findViewById(R.id.tv_week_dates)
        val incomeAmount: TextView = view.findViewById(R.id.tv_week_income)
        val expenseAmount: TextView = view.findViewById(R.id.tv_week_expense)
        val incomeProgress: ProgressBar = view.findViewById(R.id.progress_income)
        val expenseProgress: ProgressBar = view.findViewById(R.id.progress_expense)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_weekly_report, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = reportList[position]

        val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
        formatter.maximumFractionDigits = 0

        holder.weekTitle.text = item.weekTitle
        holder.dateRange.text = item.dateRange
        holder.incomeAmount.text = formatter.format(item.income)
        holder.expenseAmount.text = formatter.format(item.expense)
        holder.incomeProgress.progress = item.incomeProgress
        holder.expenseProgress.progress = item.expenseProgress
    }

    override fun getItemCount() = reportList.size
}