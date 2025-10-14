package com.example.cashflowfamily

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class TransaksiAdapter(private val listTransaksi: List<Transaksi>) :
    RecyclerView.Adapter<TransaksiAdapter.TransaksiViewHolder>() {

    // Describes an item view and metadata about its place within the RecyclerView.
    class TransaksiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDeskripsi: TextView = itemView.findViewById(R.id.tv_deskripsi)
        val tvTanggal: TextView = itemView.findViewById(R.id.tv_tanggal)
        val tvJumlah: TextView = itemView.findViewById(R.id.tv_jumlah)
    }

    // Creates new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaksiViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaksi, parent, false)
        return TransaksiViewHolder(view)
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: TransaksiViewHolder, position: Int) {
        val transaksi = listTransaksi[position]
        holder.tvDeskripsi.text = transaksi.deskripsi
        holder.tvTanggal.text = transaksi.tanggal

        // Format the currency
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.tvJumlah.text = numberFormat.format(transaksi.jumlah)

        // Optionally, you can set text color based on positive/negative value
        val context = holder.itemView.context
        if (transaksi.jumlah < 0) {
            holder.tvJumlah.setTextColor(context.getColor(android.R.color.holo_red_dark))
        } else {
            holder.tvJumlah.setTextColor(context.getColor(android.R.color.holo_green_dark))
        }
    }

    // Returns the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = listTransaksi.size
}
