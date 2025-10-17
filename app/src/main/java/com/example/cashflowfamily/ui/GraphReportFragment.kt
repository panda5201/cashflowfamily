package com.example.cashflowfamily.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowfamily.R
import com.example.cashflowfamily.adapter.CategorySummaryAdapter
import com.example.cashflowfamily.data.CategorySummary
import com.example.cashflowfamily.data.Transaction
import com.example.cashflowfamily.data.TransactionType
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

class GraphReportFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var pieChart: PieChart
    private lateinit var spinnerType: Spinner
    private lateinit var spinnerRange: Spinner
    private lateinit var spinnerSort: Spinner
    private lateinit var tvPeriod: TextView
    private lateinit var tvCategoryCount: TextView
    private lateinit var btnPrev: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var rvCategorySummary: RecyclerView
    private lateinit var categoryAdapter: CategorySummaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Grafik"
        return inflater.inflate(R.layout.fragment_graph_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pieChart = view.findViewById(R.id.pieChart)
        spinnerType = view.findViewById(R.id.spinnerType)
        spinnerRange = view.findViewById(R.id.spinnerRange)
        spinnerSort = view.findViewById(R.id.spinnerSort)
        tvPeriod = view.findViewById(R.id.tvPeriod)
        tvCategoryCount = view.findViewById(R.id.tvCategoryCount)
        btnPrev = view.findViewById(R.id.btnPrev)
        btnNext = view.findViewById(R.id.btnNext)
        rvCategorySummary = view.findViewById(R.id.rvCategorySummary)

        setupSpinners()
        setupNavigation()
        setupRecyclerView()
        setupPieChart()

        viewModel.currentDate.observe(viewLifecycleOwner) {
            updatePeriodLabel()
            viewModel.getAllTransactions().value?.let { updateUI(it) }
        }

        viewModel.getAllTransactions().observe(viewLifecycleOwner) { transactions ->
            updateUI(transactions)
        }
    }

    private fun setupPieChart() {
        pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleColor(Color.TRANSPARENT)
            setUsePercentValues(true)
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)
            legend.isEnabled = false
        }
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategorySummaryAdapter(emptyList(), 0.0, emptyList())
        rvCategorySummary.adapter = categoryAdapter
    }

    private fun setupSpinners() {
        spinnerType.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("Ringkasan", "Pemasukan", "Pengeluaran"))
        spinnerRange.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("Mingguan", "Bulanan"))
        spinnerSort.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("Nama (A-Z)", "Nama (Z-A)", "Terbanyak", "Terdikit"))

        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updatePeriodLabel()
                viewModel.getAllTransactions().value?.let { updateUI(it) }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerType.onItemSelectedListener = listener
        spinnerRange.onItemSelectedListener = listener
        spinnerSort.onItemSelectedListener = listener
    }

    private fun setupNavigation() {
        btnPrev.setOnClickListener {
            if (spinnerRange.selectedItem == "Bulanan") viewModel.prevMonth() else viewModel.prevWeek()
        }
        btnNext.setOnClickListener {
            if (spinnerRange.selectedItem == "Bulanan") viewModel.nextMonth() else viewModel.nextWeek()
        }
    }

    private fun updatePeriodLabel() {
        val calendar = viewModel.currentDate.value ?: return
        val format = when (spinnerRange.selectedItem) {
            "Mingguan" -> {
                val startOfWeek = calendar.clone() as Calendar
                startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.firstDayOfWeek)
                val endOfWeek = startOfWeek.clone() as Calendar
                endOfWeek.add(Calendar.DAY_OF_WEEK, 6)
                val dateFormat = SimpleDateFormat("d MMM", Locale.forLanguageTag("in-ID"))
                "${dateFormat.format(startOfWeek.time)} - ${dateFormat.format(endOfWeek.time)}"
            }
            "Bulanan" -> SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("in-ID")).format(calendar.time)
            else -> ""
        }
        tvPeriod.text = format
    }

    private fun updateUI(transactions: List<Transaction>) {
        if (!isAdded) return

        val calendar = viewModel.currentDate.value ?: return

        val filteredTransactions = when (spinnerRange.selectedItem) {
            "Mingguan" -> {
                val startOfWeek = calendar.clone() as Calendar
                startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.firstDayOfWeek)
                startOfWeek.set(Calendar.HOUR_OF_DAY, 0); startOfWeek.set(Calendar.MINUTE, 0); startOfWeek.set(Calendar.SECOND, 0)

                val endOfWeek = startOfWeek.clone() as Calendar
                endOfWeek.add(Calendar.DAY_OF_WEEK, 6)
                endOfWeek.set(Calendar.HOUR_OF_DAY, 23); endOfWeek.set(Calendar.MINUTE, 59); endOfWeek.set(Calendar.SECOND, 59)

                transactions.filter { it.date in startOfWeek.time..endOfWeek.time }
            }
            "Bulanan" -> transactions.filter {
                val trxCal = Calendar.getInstance().apply { time = it.date }
                trxCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                        trxCal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
            }
            else -> emptyList()
        }

        val summaries: MutableList<CategorySummary>
        val chartColors: List<Int>

        when (spinnerType.selectedItem) {
            "Ringkasan" -> {
                val incomeTotal = filteredTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                val expenseTotal = filteredTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                summaries = mutableListOf()
                if (incomeTotal > 0) summaries.add(CategorySummary("Pemasukan", incomeTotal))
                if (expenseTotal > 0) {
                    summaries.add(CategorySummary("Pengeluaran", -expenseTotal))
                }

                chartColors = listOf(Color.parseColor("#42A5F5"), Color.parseColor("#EF5350"))
            }
            "Pemasukan" -> {
                summaries = filteredTransactions.filter { it.type == TransactionType.INCOME }
                    .groupBy { it.title }
                    .map { (title, items) -> CategorySummary(title, items.sumOf { it.amount }) }
                    .toMutableList()
                chartColors = ColorTemplate.JOYFUL_COLORS.toList()
            }
            "Pengeluaran" -> {
                summaries = filteredTransactions.filter { it.type == TransactionType.EXPENSE }
                    .groupBy { it.title }
                    .map { (title, items) -> CategorySummary(title, items.sumOf { it.amount }) }
                    .toMutableList()
                chartColors = ColorTemplate.VORDIPLOM_COLORS.toList()
            }
            else -> {
                summaries = mutableListOf()
                chartColors = emptyList()
            }
        }

        when (spinnerSort.selectedItem) {
            "Nama (A-Z)" -> summaries.sortBy { it.category }
            "Nama (Z-A)" -> summaries.sortByDescending { it.category }
            "Terbanyak" -> summaries.sortByDescending { abs(it.total) }
            "Terdikit" -> summaries.sortBy { abs(it.total) }
        }

        val totalForPercentage = summaries.sumOf { abs(it.total) }
        val entries = summaries.map { PieEntry(abs(it.total).toFloat(), it.category) }

        val dataSet = PieDataSet(entries, "").apply {
            colors = chartColors
            valueTextColor = Color.BLACK
            valueTextSize = 12f
            valueFormatter = PercentFormatter(pieChart)
            setDrawValues(summaries.size < 5)
        }

        val totalBalance = summaries.sumOf { it.total }
        val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("in-ID"))
        formatter.maximumFractionDigits = 0

        pieChart.centerText = formatter.format(totalBalance)
        pieChart.setCenterTextSize(20f)
        pieChart.setCenterTextColor(Color.BLACK)

        pieChart.data = PieData(dataSet)
        pieChart.invalidate()
        pieChart.animateY(1000)

        tvCategoryCount.text = "${summaries.size} Kategori"
        categoryAdapter.updateData(summaries, totalForPercentage, chartColors)
    }
}