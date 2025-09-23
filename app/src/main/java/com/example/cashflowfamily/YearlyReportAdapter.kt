package com.example.cashflowfamily

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class YearlyReportAdapter(private val reportList: List<YearlyReport>) :
    RecyclerView.Adapter<YearlyReportAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val yearTitle: TextView = view.findViewById(R.id.tv_year_title)
        val yearBalance: TextView = view.findViewById(R.id.tv_year_balance)
        val yearIncome: TextView = view.findViewById(R.id.tv_year_income)
        val yearExpense: TextView = view.findViewById(R.id.tv_year_expense)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_yearly, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = reportList[position]

        val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
        formatter.maximumFractionDigits = 0

        holder.yearTitle.text = item.year
        holder.yearBalance.text = formatter.format(item.finalBalance)
        holder.yearIncome.text = formatter.format(item.totalIncome)
        holder.yearExpense.text = formatter.format(item.totalExpense)
    }

    override fun getItemCount() = reportList.size
}