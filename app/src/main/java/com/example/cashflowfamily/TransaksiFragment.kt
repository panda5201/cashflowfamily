package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class TransaksiFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transaksi, container, false)

        val btnTambah = view.findViewById<Button>(R.id.btnTambahTransaksi)
        btnTambah.setOnClickListener {
            findNavController().navigate(R.id.action_transaksiFragment_to_formTransaksiFragment)
        }

        return view
    }
}
