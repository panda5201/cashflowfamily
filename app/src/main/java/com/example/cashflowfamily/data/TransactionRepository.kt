package com.example.cashflowfamily.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.Date
import java.util.concurrent.atomic.AtomicLong

object TransactionRepository {

    private val transactions = mutableListOf<Transaction>()
    private val _transactionsLiveData = MutableLiveData<List<Transaction>>(emptyList())
    val transactionsLiveData: LiveData<List<Transaction>> = _transactionsLiveData

    private val idCounter = AtomicLong(0)

    fun addTransaction(transaction: Transaction) {
        val newTransaction = transaction.copy(id = idCounter.incrementAndGet())
        transactions.add(0, newTransaction)
        _transactionsLiveData.value = transactions.toList()
    }
    fun getTransactionById(id: Long): Transaction? {
        return transactions.find { it.id == id }
    }

    fun deleteTransaction(id: Long) {
        transactions.removeIf { it.id == id }
        _transactionsLiveData.value = transactions.toList()
    }

    fun updateTransaction(updatedTransaction: Transaction) {
        val index = transactions.indexOfFirst { it.id == updatedTransaction.id }
        if (index != -1) {
            transactions[index] = updatedTransaction
            _transactionsLiveData.value = transactions.toList()
        }
    }
}