package com.example.nucleo.ui

import com.example.nucleo.model.Transaction

// Estados para lista de transações
sealed interface TransactionsUiState {
    object Loading : TransactionsUiState
    data class Ready(
        val transactions: List<Transaction>,
        val message: String? = null
    ) : TransactionsUiState
    data class Error(val message: String) : TransactionsUiState
}

// Estados para formulário de transação
sealed interface TransactionFormUiState {
    object Loading : TransactionFormUiState
    data class Ready(
        val transaction: Transaction? = null,
        val isEditing: Boolean = false,
        val message: String? = null
    ) : TransactionFormUiState
    data class Error(val message: String) : TransactionFormUiState
}

// Estados para dashboard
sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Ready(
        val balance: Double,
        val totalIncome: Double,
        val totalExpense: Double,
        val transactionCount: Int,
        val recentTransactions: List<Transaction>,
        val message: String? = null
    ) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

// Estados para estatísticas
sealed interface StatisticsUiState {
    object Loading : StatisticsUiState
    data class Ready(
        val transactions: List<Transaction>,
        val totalIncome: Double,
        val totalExpense: Double,
        val balance: Double,
        val message: String? = null
    ) : StatisticsUiState
    data class Error(val message: String) : StatisticsUiState
}
