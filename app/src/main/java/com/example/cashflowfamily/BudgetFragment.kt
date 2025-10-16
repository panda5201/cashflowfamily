package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowfamily.adapter.BudgetAdapter
import com.example.cashflowfamily.data.BudgetRepository

class BudgetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvBudgets = view.findViewById<RecyclerView>(R.id.rv_budgets)
        val btnSave = view.findViewById<Button>(R.id.btn_save_budgets)

        val budgets = BudgetRepository.getBudgets(requireContext())
        val adapter = BudgetAdapter(budgets)

        rvBudgets.layoutManager = LinearLayoutManager(requireContext())
        rvBudgets.adapter = adapter

        btnSave.setOnClickListener {
            BudgetRepository.saveBudgets(requireContext(), budgets)
            Toast.makeText(requireContext(), "Pengaturan anggaran disimpan", Toast.LENGTH_SHORT).show()
        }
    }
}