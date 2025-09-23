package com.example.cashflowfamily.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowfamily.R
import com.example.cashflowfamily.model.Anak

class DataAnakAdapter(
    private val listAnak: List<Anak>,
    private val onItemClick: (Anak) -> Unit
) : RecyclerView.Adapter<DataAnakAdapter.AnakViewHolder>() {

    inner class AnakViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNama: TextView = itemView.findViewById(R.id.tvNamaAnak)
        private val tvDetail: TextView = itemView.findViewById(R.id.tvDetailAnak)
        private val imgAnak: ImageView = itemView.findViewById(R.id.imgAnak)

        fun bind(anak: Anak) {
            tvNama.text = anak.nama
            tvDetail.text = "Umur ${anak.umur} - ${anak.sekolah}"
            imgAnak.setImageResource(R.drawable.ic_child)

            itemView.setOnClickListener { onItemClick(anak) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnakViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_anak, parent, false)
        return AnakViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnakViewHolder, position: Int) {
        holder.bind(listAnak[position])
    }

    override fun getItemCount(): Int = listAnak.size
}
