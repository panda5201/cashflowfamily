package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2

class DetailAnakFragment : Fragment() {

    private lateinit var tvNamaAnak: TextView
    private lateinit var tvPemasukan: TextView
    private lateinit var tvPengeluaran: TextView
    private lateinit var tvSaldo: TextView
    private lateinit var btnTambah: Button
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_anak, container, false)

        tvNamaAnak = view.findViewById(R.id.tv_nama_anak)
        tvPemasukan = view.findViewById(R.id.tv_total_pemasukan)
        tvPengeluaran = view.findViewById(R.id.tv_total_pengeluaran)
        tvSaldo = view.findViewById(R.id.tv_saldo)
        btnTambah = view.findViewById(R.id.btnTambahTransaksi)
        viewPager = view.findViewById(R.id.viewPagerChild)

        val nama = arguments?.getString("nama") ?: "Anak"
        tvNamaAnak.text = "Laporan Anak - $nama"

        tvPemasukan.text = "Pemasukan: Rp200.000"
        tvPengeluaran.text = "Pengeluaran: Rp150.000"
        tvSaldo.text = "Saldo: Rp50.000"

        val adapter = ChildReportPagerAdapter(this)
        viewPager.adapter = adapter

        val tabLayout = view.findViewById<com.google.android.material.tabs.TabLayout>(R.id.tabLayoutChild)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Harian"
                1 -> "Mingguan"
                2 -> "Bulanan"
                else -> "Tahunan"
            }
        }.attach()

        btnTambah.setOnClickListener {
            findNavController().navigate(R.id.action_detailAnakFragment_to_formTransaksiFragment)
        }

        return view
    }
}
