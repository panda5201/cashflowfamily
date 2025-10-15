package com.example.cashflowfamily.adapter

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowfamily.R
import com.example.cashflowfamily.data.CategorySummary
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

class CategorySummaryAdapter(
    private var summaries: List<CategorySummary>,
    private var totalAmount: Double,
    private var colors: List<Int> // Dijadikan var agar bisa diubah
) : RecyclerView.Adapter<CategorySummaryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_summary, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Safety check untuk mencegah crash jika daftar warna kosong
        if (colors.isEmpty()) return

        val summary = summaries[position]
        val color = colors[position % colors.size]
        holder.bind(summary, totalAmount, color)
    }

    override fun getItemCount(): Int = summaries.size

    // --- PERUBAHAN DI SINI: Metode updateData sekarang menerima warna baru ---
    fun updateData(newSummaries: List<CategorySummary>, newTotal: Double, newColors: List<Int>) {
        summaries = newSummaries
        totalAmount = newTotal
        colors = newColors
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPercentage: TextView = itemView.findViewById(R.id.tvCategoryPercentage)
        private val tvName: TextView = itemView.findViewById(R.id.tvCategoryName)
        private val tvTotal: TextView = itemView.findViewById(R.id.tvCategoryTotal)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        fun bind(summary: CategorySummary, totalAmount: Double, color: Int) {
            val percentage = if (totalAmount > 0) (abs(summary.total) / totalAmount) * 100 else 0.0
            tvPercentage.text = String.format(Locale.US, "(%.1f%%)", percentage)
            tvName.text = summary.category

            val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("in-ID"))
            formatter.maximumFractionDigits = 0
            tvTotal.text = formatter.format(abs(summary.total))

            progressBar.progress = percentage.toInt()
            // Menggunakan PorterDuff.Mode.SRC_IN untuk kompatibilitas lebih luas
            progressBar.progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }
}