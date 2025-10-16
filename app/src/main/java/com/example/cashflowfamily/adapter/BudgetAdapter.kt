package com.example.cashflowfamily.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowfamily.R
import com.example.cashflowfamily.data.Budget

class BudgetAdapter(private val budgetList: MutableList<Budget>) :
    RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_budget, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        holder.bind(budgetList[position])
    }

    override fun getItemCount(): Int = budgetList.size

    inner class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategory: TextView = itemView.findViewById(R.id.tv_category_name)
        private val etAmount: EditText = itemView.findViewById(R.id.et_budget_amount)

        fun bind(budget: Budget) {
            tvCategory.text = budget.categoryName
            if (budget.amount > 0) {
                etAmount.setText(budget.amount.toInt().toString())
            } else {
                etAmount.setText("")
            }

            etAmount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    budget.amount = s.toString().toDoubleOrNull() ?: 0.0
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }
}