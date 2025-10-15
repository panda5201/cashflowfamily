package com.example.cashflowfamily.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cashflowfamily.R
import com.example.cashflowfamily.data.CategorySummary
import com.example.cashflowfamily.data.Transaction
import com.example.cashflowfamily.data.TransactionType
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.*

class GraphReportFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var pieChart: PieChart
    private lateinit var spinnerType: Spinner
    private lateinit var spinnerRange: Spinner
    private lateinit var spinnerSort: Spinner
    private lateinit var tvPeriod: TextView
    private lateinit var btnPrev: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var listView: ListView

    private var currentCalendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_graph_report, container, false)
        pieChart = view.findViewById(R.id.pieChart)
        spinnerType = view.findViewById(R.id.spinnerType)
        spinnerRange = view.findViewById(R.id.spinnerRange)
        spinnerSort = view.findViewById(R.id.spinnerSort)
        tvPeriod = view.findViewById(R.id.tvPeriod)
        btnPrev = view.findViewById(R.id.btnPrev)
        btnNext = view.findViewById(R.id.btnNext)
        listView = view.findViewById(R.id.listCategory)

        setupSpinners()
        setupNavigation()

        viewModel.getAllTransactions().observe(viewLifecycleOwner) { transactions ->
            updateChart(transactions)
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cegah error sebelum spinner siap
        spinnerType.setSelection(0)
        spinnerRange.setSelection(1)
        spinnerSort.setSelection(0)
    }


    private fun setupSpinners() {
        spinnerType.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,
            listOf("Ringkasan", "Pemasukan", "Pengeluaran"))

        spinnerRange.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,
            listOf("Mingguan", "Bulanan", "Sesuaikan"))

        spinnerSort.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,
            listOf("Terbanyak", "Terdikit", "Nama A-Z", "Nama Z-A"))

        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                viewModel.getAllTransactions().value?.let { updateChart(it) }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerType.onItemSelectedListener = listener
        spinnerRange.onItemSelectedListener = listener
        spinnerSort.onItemSelectedListener = listener
    }

    private fun setupNavigation() {
        updatePeriodLabel()
        btnPrev.setOnClickListener {
            shiftPeriod(-1)
        }
        btnNext.setOnClickListener {
            shiftPeriod(1)
        }
    }

    private fun shiftPeriod(step: Int) {
        when (spinnerRange.selectedItem) {
            "Mingguan" -> currentCalendar.add(Calendar.WEEK_OF_YEAR, step)
            "Bulanan" -> currentCalendar.add(Calendar.MONTH, step)
        }
        updatePeriodLabel()
        viewModel.getAllTransactions().value?.let { updateChart(it) }
    }

    private fun updatePeriodLabel() {
        val format = SimpleDateFormat("MMMM yyyy", Locale("in", "ID"))
        tvPeriod.text = format.format(currentCalendar.time)
    }


    private fun updateChart(transactions: List<Transaction>) {
        if (transactions.isEmpty()) {
            pieChart.clear()
            return
        }

        val filtered = filterByTypeAndRange(transactions)
        val grouped = filtered.groupBy { it.title }.map { (title, items) ->
            CategorySummary(title, items.sumOf { it.amount })
        }.toMutableList()

        sortCategories(grouped)

        val entries = grouped.map { PieEntry(it.total.toFloat(), it.category) }
        val dataSet = PieDataSet(entries, "Kategori")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        val pieData = PieData(dataSet)

        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.centerText = "Total\n${filtered.sumOf { it.amount }}"
        pieChart.animateY(1000)
        pieChart.invalidate()

        val listAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            grouped.map { "${it.category}: Rp ${String.format("%,.0f", it.total)}" }
        )
        listView.adapter = listAdapter
    }

    private fun filterByTypeAndRange(transactions: List<Transaction>): List<Transaction> {
        val cal = Calendar.getInstance()
        val range = spinnerRange.selectedItem.toString()
        val start: Date
        val end: Date

        when (range) {
            "Mingguan" -> {
                cal.time = currentCalendar.time
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                start = cal.time
                cal.add(Calendar.DAY_OF_WEEK, 6)
                end = cal.time
            }
            "Bulanan" -> {
                cal.time = currentCalendar.time
                cal.set(Calendar.DAY_OF_MONTH, 1)
                start = cal.time
                cal.add(Calendar.MONTH, 1)
                cal.add(Calendar.DAY_OF_MONTH, -1)
                end = cal.time
            }
            else -> {
                val now = currentCalendar.time
                start = Date(now.time - 7 * 24 * 60 * 60 * 1000)
                end = now
            }
        }

        val filteredByDate = transactions.filter { it.date.after(start) && it.date.before(end) }

        return when (spinnerType.selectedItem) {
            "Pemasukan" -> filteredByDate.filter { it.type == TransactionType.INCOME }
            "Pengeluaran" -> filteredByDate.filter { it.type == TransactionType.EXPENSE }
            else -> filteredByDate
        }
    }

    private fun sortCategories(categories: MutableList<CategorySummary>) {
        when (spinnerSort.selectedItem) {
            "Terbanyak" -> categories.sortByDescending { it.total }
            "Terdikit" -> categories.sortBy { it.total }
            "Nama A-Z" -> categories.sortBy { it.category }
            "Nama Z-A" -> categories.sortByDescending { it.category }
        }
    }
}
