package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TransaksiFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaksi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvTransaksi: RecyclerView = view.findViewById(R.id.rv_transaksi)
        rvTransaksi.layoutManager = LinearLayoutManager(context)
        rvTransaksi.adapter = TransaksiAdapter(createDummyFamilyTransaksi())

        val btnTambah: Button = view.findViewById(R.id.btnTambahTransaksi)
        btnTambah.setOnClickListener {
            findNavController().navigate(R.id.action_transaksiFragment_to_formTransaksiFragment)
        }
    }

    private fun createDummyFamilyTransaksi(): List<Transaksi> {
        return listOf(
            Transaksi("Gaji Ayah", "23 Sep 2025", 7500000.0),
            Transaksi("Bayar SPP Sekolah Anak", "22 Sep 2025", -550000.0),
            Transaksi("Belanja Bulanan Supermarket", "21 Sep 2025", -1200000.0),
            Transaksi("Uang Saku Mingguan Anak", "20 Sep 2025", -150000.0)
        )
    }
}