package com.example.cashflowfamily

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cashflowfamily.data.TransactionListItem
import com.example.cashflowfamily.data.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private var items: List<TransactionListItem>,
    private val onTransactionClick: (Long) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    inner class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayNumber: TextView = itemView.findViewById(R.id.tv_day_number)
        private val dayOfWeek: TextView = itemView.findViewById(R.id.tv_day_of_week)
        private val monthYear: TextView = itemView.findViewById(R.id.tv_month_year)
        private val headerIncome: TextView = itemView.findViewById(R.id.tv_header_income)
        private val headerExpense: TextView = itemView.findViewById(R.id.tv_header_expense)
        private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).apply { maximumFractionDigits = 0 }

        fun bind(header: TransactionListItem.DateHeader) {
            val cal = Calendar.getInstance().apply { time = header.date }
            dayNumber.text = cal.get(Calendar.DAY_OF_MONTH).toString()
            dayOfWeek.text = SimpleDateFormat("EEEE", Locale.forLanguageTag("id-ID")).format(header.date)
            monthYear.text = SimpleDateFormat("MM.yyyy", Locale.forLanguageTag("id-ID")).format(header.date)
            headerIncome.text = currencyFormatter.format(header.totalIncome)
            headerExpense.text = currencyFormatter.format(header.totalExpense)
        }
    }

    inner class TransactionItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tv_transaction_title)
        private val amount: TextView = itemView.findViewById(R.id.tv_transaction_amount)
        private val description: TextView = itemView.findViewById(R.id.tv_transaction_description)
        private val proofImage: ImageView = itemView.findViewById(R.id.iv_transaction_proof)
        private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).apply { maximumFractionDigits = 0 }

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION && items[position] is TransactionListItem.TransactionItem) {
                    val item = items[position] as TransactionListItem.TransactionItem
                    onTransactionClick(item.transaction.id)
                }
            }
        }

        fun bind(item: TransactionListItem.TransactionItem) {
            val transaction = item.transaction
            val context = itemView.context

            title.text = transaction.title
            description.text = transaction.description ?: ""
            description.visibility = if (transaction.description.isNullOrBlank()) View.GONE else View.VISIBLE
            amount.text = currencyFormatter.format(transaction.amount)

            val color = if (transaction.type == TransactionType.INCOME) {
                ContextCompat.getColor(context, android.R.color.holo_green_dark)
            } else {
                ContextCompat.getColor(context, android.R.color.holo_red_dark)
            }
            amount.setTextColor(color)

            if (transaction.imageUri != null) {
                proofImage.visibility = View.VISIBLE
                Glide.with(context)
                    .load(transaction.imageUri)
                    .into(proofImage)
            } else {
                proofImage.visibility = View.GONE
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TransactionListItem.DateHeader -> VIEW_TYPE_HEADER
            is TransactionListItem.TransactionItem -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_date_header, parent, false)
            DateHeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_transaction, parent, false)
            TransactionItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val currentItem = items[position]) {
            is TransactionListItem.DateHeader -> (holder as DateHeaderViewHolder).bind(currentItem)
            is TransactionListItem.TransactionItem -> (holder as TransactionItemViewHolder).bind(currentItem)
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<TransactionListItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}