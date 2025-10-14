package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowfamily.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DailyReportFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report_daily, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvPeriod = view.findViewById<TextView>(R.id.tvPeriod)
        val btnPrev = view.findViewById<ImageButton>(R.id.btnPrev)
        val btnNext = view.findViewById<ImageButton>(R.id.btnNext)
        val rvDaily = view.findViewById<RecyclerView>(R.id.rvDailyReport)


        transactionAdapter = TransactionAdapter(emptyList()) { transactionId ->
            val action = DashboardFragmentDirections.actionDashboardFragmentToFormTransaksiFragment(transactionId)
            findNavController().navigate(action)
        }

        rvDaily.layoutManager = LinearLayoutManager(requireContext())
        rvDaily.adapter = transactionAdapter

        viewModel.currentDate.observe(viewLifecycleOwner) { calendar ->
            val format = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("id-ID"))
            tvPeriod.text = format.format(calendar.time)
        }

        viewModel.groupedTransactionsForCurrentMonth.observe(viewLifecycleOwner) { groupedList ->
            transactionAdapter.updateData(groupedList)
        }

        btnPrev.setOnClickListener {
            viewModel.prevMonth()
        }
        btnNext.setOnClickListener {
            viewModel.nextMonth()
        }
    }
}