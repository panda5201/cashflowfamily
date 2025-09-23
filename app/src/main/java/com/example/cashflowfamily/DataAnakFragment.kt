package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowfamily.adapter.DataAnakAdapter
import com.example.cashflowfamily.model.Anak

class DataAnakFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_data_anak, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_anak)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Dummy data anak
        val listAnak = listOf(
            Anak("Budi", 10, "SDN 1 Jakarta"),
            Anak("Siti", 12, "SMPN 3 Bandung"),
            Anak("Rudi", 8, "SDN 5 Tangerang")
        )

        val adapter = DataAnakAdapter(listAnak) { anak ->
            val bundle = Bundle().apply {
                putString("nama", anak.nama)
            }
            findNavController().navigate(R.id.detailAnakFragment, bundle)
        }

        recyclerView.adapter = adapter

        return view
    }
}
