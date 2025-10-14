package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowfamily.ui.MainViewModel
import java.util.Calendar

class MonthlyReportFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: MonthlyReportAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report_monthly, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvPeriod = view.findViewById<TextView>(R.id.tvPeriod)
        val btnPrev = view.findViewById<ImageButton>(R.id.btnPrev)
        val btnNext = view.findViewById<ImageButton>(R.id.btnNext)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_monthly)

        adapter = MonthlyReportAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        viewModel.currentDate.observe(viewLifecycleOwner) { calendar ->
            tvPeriod.text = calendar.get(Calendar.YEAR).toString()
        }

        viewModel.monthlyReportData.observe(viewLifecycleOwner) { monthReports ->
            adapter.updateData(monthReports)
        }

        btnPrev.setOnClickListener {
            viewModel.prevYear()
        }
        btnNext.setOnClickListener {
            viewModel.nextYear()
        }
    }
}