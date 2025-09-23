package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class FormTransaksiFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_form_transaksi, container, false)

        val etJudul = view.findViewById<EditText>(R.id.et_judul)
        val etNominal = view.findViewById<EditText>(R.id.et_nominal)
        val etKeterangan = view.findViewById<EditText>(R.id.et_keterangan)
        val btnSimpan = view.findViewById<Button>(R.id.btn_simpan)

        btnSimpan.setOnClickListener {
            Toast.makeText(requireContext(), "Transaksi tersimpan (dummy)", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
