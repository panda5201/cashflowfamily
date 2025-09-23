package com.example.cashflowfamily

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class MonthlyReportAdapter(private val monthList: MutableList<MonthReport>) :
    RecyclerView.Adapter<MonthlyReportAdapter.MonthViewHolder>() {

    private var selectedPosition = -1

    inner class MonthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val monthTextView: TextView = itemView.findViewById(R.id.tv_month)
        val expenseTextView: TextView = itemView.findViewById(R.id.tv_expense)
        val balanceTextView: TextView = itemView.findViewById(R.id.tv_balance)

        init {
            itemView.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(selectedPosition)
                    selectedPosition = bindingAdapterPosition
                    notifyItemChanged(selectedPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_report, parent, false)
        return MonthViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val item = monthList[position]

        val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
        formatter.maximumFractionDigits = 0

        holder.monthTextView.text = item.month
        holder.expenseTextView.text = formatter.format(item.expense)
        holder.balanceTextView.text = formatter.format(item.balance)

        if (position == selectedPosition) {
            holder.monthTextView.setBackgroundResource(R.drawable.button_background_selected)
        } else {
            holder.monthTextView.setBackgroundResource(R.drawable.button_background_normal)
        }
    }

    override fun getItemCount() = monthList.size

    fun setSelected(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position

        if (previousPosition != -1) {
            notifyItemChanged(previousPosition)
        }
        notifyItemChanged(selectedPosition)
    }
}