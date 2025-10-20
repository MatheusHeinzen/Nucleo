package com.example.nucleo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nucleo.model.Transaction
import com.example.nucleo.model.TransactionType
import com.example.nucleo.repository.TransactionRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val transactions: StateFlow<List<Transaction>> = transactionRepository.transactions
    val balance: StateFlow<Double> = transactionRepository.balance

    // Calcula estatísticas em tempo real
    val dashboardStats = combine(
        transactions,
        balance
    ) { transactions, balance ->
        DashboardStats(
            balance = balance,
            incomeTotal = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount },
            expenseTotal = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { kotlin.math.abs(it.amount) },
            transactionCount = transactions.size,
            lastTransaction = transactions.maxByOrNull { it.id }
        )
    }

    fun deleteTransaction(transactionId: Int) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transactionId)
        }
    }

    fun getIncomeTotal(): Double {
        return transactionRepository.getIncomeTotal()
    }

    fun getExpenseTotal(): Double {
        return transactionRepository.getExpenseTotal()
    }
}

data class DashboardStats(
    val balance: Double,
    val incomeTotal: Double,
    val expenseTotal: Double,
    val transactionCount: Int,
    val lastTransaction: Transaction?
)