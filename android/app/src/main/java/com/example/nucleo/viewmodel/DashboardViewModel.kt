package com.example.nucleo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nucleo.model.Transaction
import com.example.nucleo.model.TransactionType
import com.example.nucleo.repository.TransactionRepository
import com.example.nucleo.ui.DashboardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    // Mantém compatibilidade com as telas existentes
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
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

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            try {
                _uiState.value = DashboardUiState.Loading
                
                transactionRepository.transactions
                    .catch { error ->
                        _uiState.value = DashboardUiState.Error("Erro ao carregar transações: ${error.message}")
                    }
                    .collect { transactionList ->
                        _transactions.value = transactionList
                        
                        val totalIncome = transactionRepository.getIncomeTotal()
                        val totalExpense = transactionRepository.getExpenseTotal()
                        
                        _uiState.value = DashboardUiState.Ready(
                            balance = balance.value,
                            totalIncome = totalIncome,
                            totalExpense = totalExpense,
                            transactionCount = transactionList.size,
                            recentTransactions = transactionList.sortedByDescending { it.id }.take(5)
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error("Erro ao carregar dashboard: ${e.message}")
            }
        }
    }

    fun deleteTransaction(transactionId: Int) {
        viewModelScope.launch {
            try {
                transactionRepository.deleteTransaction(transactionId)
                
                when (val currentState = _uiState.value) {
                    is DashboardUiState.Ready -> {
                        _uiState.value = currentState.copy(message = "Transação excluída com sucesso!")
                    }
                    else -> { /* No action needed */ }
                }
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error("Erro ao excluir transação: ${e.message}")
            }
        }
    }

    fun clearMessage() {
        when (val currentState = _uiState.value) {
            is DashboardUiState.Ready -> {
                _uiState.value = currentState.copy(message = null)
            }
            else -> { /* No action needed */ }
        }
    }

    fun getIncomeTotal(): Double {
        return transactions.value.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }

    fun getExpenseTotal(): Double {
        return transactions.value.filter { it.type == TransactionType.EXPENSE }.sumOf { kotlin.math.abs(it.amount) }
    }
}

data class DashboardStats(
    val balance: Double,
    val incomeTotal: Double,
    val expenseTotal: Double,
    val transactionCount: Int,
    val lastTransaction: Transaction?
)