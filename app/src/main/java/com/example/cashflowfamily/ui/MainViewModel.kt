package com.example.cashflowfamily.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.example.cashflowfamily.data.*
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {

    private val allTransactions: LiveData<List<Transaction>> = TransactionRepository.transactionsLiveData

    fun getAllTransactions(): LiveData<List<Transaction>> = allTransactions

    private val _currentDate = MutableLiveData<Calendar>(Calendar.getInstance())
    val currentDate: LiveData<Calendar> = _currentDate

    val groupedTransactionsForCurrentMonth: LiveData<List<TransactionListItem>> =
        _currentDate.switchMap { date ->
            allTransactions.map { transactions ->
                val filtered = filterTransactionsForMonth(transactions, date)
                groupTransactionsByDate(filtered)
            }
        }

    val weeklyReportData: LiveData<List<WeekReport>> =
        _currentDate.switchMap { date ->
            allTransactions.map { transactions ->
                val filtered = filterTransactionsForMonth(transactions, date)
                groupTransactionsByWeek(filtered, date)
            }
        }

    val monthlyReportData: LiveData<List<MonthReport>> =
        _currentDate.switchMap { date ->
            allTransactions.map { transactions ->
                val filtered = filterTransactionsForYear(transactions, date)
                groupTransactionsByMonth(filtered, date.get(Calendar.YEAR))
            }
        }

    val yearlyReportData: LiveData<List<YearReport>> =
        allTransactions.map { transactions ->
            groupTransactionsByYear(transactions)
        }

    fun addTransaction(transaction: Transaction) {
        TransactionRepository.addTransaction(transaction)
    }

    fun getTransactionById(id: Long): Transaction? {
        return TransactionRepository.getTransactionById(id)
    }

    fun updateTransaction(transaction: Transaction) {
        TransactionRepository.updateTransaction(transaction)
    }

    fun deleteTransaction(id: Long) {
        TransactionRepository.deleteTransaction(id)
    }
    fun nextMonth() {
        val newCal = (_currentDate.value ?: Calendar.getInstance()).clone() as Calendar
        newCal.add(Calendar.MONTH, 1)
        _currentDate.value = newCal
    }

    fun prevMonth() {
        val newCal = (_currentDate.value ?: Calendar.getInstance()).clone() as Calendar
        newCal.add(Calendar.MONTH, -1)
        _currentDate.value = newCal
    }

    fun nextYear() {
        val newCal = (_currentDate.value ?: Calendar.getInstance()).clone() as Calendar
        newCal.add(Calendar.YEAR, 1)
        _currentDate.value = newCal
    }

    fun prevYear() {
        val newCal = (_currentDate.value ?: Calendar.getInstance()).clone() as Calendar
        newCal.add(Calendar.YEAR, -1)
        _currentDate.value = newCal
    }

    private fun filterTransactionsForMonth(transactions: List<Transaction>, calendar: Calendar): List<Transaction> {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        return transactions.filter {
            val transactionCal = Calendar.getInstance().apply { time = it.date }
            transactionCal.get(Calendar.YEAR) == year && transactionCal.get(Calendar.MONTH) == month
        }
    }

    private fun filterTransactionsForYear(transactions: List<Transaction>, calendar: Calendar): List<Transaction> {
        val year = calendar.get(Calendar.YEAR)
        return transactions.filter {
            val transactionCal = Calendar.getInstance().apply { time = it.date }
            transactionCal.get(Calendar.YEAR) == year
        }
    }

    private fun groupTransactionsByDate(transactions: List<Transaction>): List<TransactionListItem> {
        if (transactions.isEmpty()) return emptyList()
        val groupedMap = transactions.groupBy {
            val cal = Calendar.getInstance().apply { time = it.date }
            cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
            cal.time
        }
        val resultList = mutableListOf<TransactionListItem>()
        groupedMap.toSortedMap(compareByDescending { it }).forEach { (date, transactionList) ->
            val totalIncome = transactionList.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val totalExpense = transactionList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            resultList.add(TransactionListItem.DateHeader(date, totalIncome, totalExpense))
            transactionList.sortedByDescending { it.date }.forEach { transaction ->
                resultList.add(TransactionListItem.TransactionItem(transaction))
            }
        }
        return resultList
    }

    private fun groupTransactionsByWeek(transactions: List<Transaction>, calendar: Calendar): List<WeekReport> {
        val weekReports = mutableListOf<WeekReport>()
        val monthFormat = SimpleDateFormat("MMM", Locale.forLanguageTag("id-ID"))
        val monthName = monthFormat.format(calendar.time)

        for (week in 1..4) {
            val startDay: Int
            val endDay: Int

            when (week) {
                1 -> { startDay = 1; endDay = 7 }
                2 -> { startDay = 8; endDay = 14 }
                3 -> { startDay = 15; endDay = 21 }
                else -> { startDay = 22; endDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) }
            }

            val dateRange = "$startDay $monthName - $endDay $monthName"
            val transactionsInWeek = transactions.filter {
                val dayOfMonth = Calendar.getInstance().apply { time = it.date }.get(Calendar.DAY_OF_MONTH)
                dayOfMonth in startDay..endDay
            }
            val totalIncome = transactionsInWeek.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val totalExpense = transactionsInWeek.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            weekReports.add(WeekReport("Minggu $week", dateRange, totalIncome, totalExpense))
        }
        return weekReports
    }

    private fun groupTransactionsByMonth(transactions: List<Transaction>, year: Int): List<MonthReport> {
        val monthReports = mutableListOf<MonthReport>()
        val monthFormat = SimpleDateFormat("MMMM", Locale.forLanguageTag("id-ID"))
        for (month in 0..11) {
            val transactionsInMonth = transactions.filter {
                val transactionCal = Calendar.getInstance().apply { time = it.date }
                transactionCal.get(Calendar.MONTH) == month
            }
            val totalIncome = transactionsInMonth.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val totalExpense = transactionsInMonth.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            val monthName = monthFormat.format(Calendar.getInstance().apply { set(Calendar.MONTH, month) }.time)
            monthReports.add(MonthReport(monthName, year, totalIncome, totalExpense))
        }
        return monthReports
    }

    private fun groupTransactionsByYear(transactions: List<Transaction>): List<YearReport> {
        return transactions
            .groupBy { Calendar.getInstance().apply { time = it.date }.get(Calendar.YEAR) }
            .map { (year, transactionList) ->
                val totalIncome = transactionList.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                val totalExpense = transactionList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                YearReport(year, totalIncome, totalExpense)
            }
            .sortedByDescending { it.year }
    }
}