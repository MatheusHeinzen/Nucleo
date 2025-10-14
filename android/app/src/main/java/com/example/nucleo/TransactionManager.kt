package com.example.nucleo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object TransactionManager {
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _balance = MutableStateFlow(295.00)
    val balance: StateFlow<Double> = _balance.asStateFlow()

    private var nextId = 4 // Já temos 3 transações iniciais

    init {
        // Transações iniciais
        _transactions.value = listOf(
            Transaction(1, -85.0, "Supermercado", TransactionType.EXPENSE, "Alimentação", "Hoje 14:30"),
            Transaction(2, 500.0, "Freelance", TransactionType.INCOME, "Trabalho", "Ontem 09:15"),
            Transaction(3, -120.0, "Posto Shell", TransactionType.EXPENSE, "Transporte", "15/05 18:40")
        )
    }

    fun addTransaction(transaction: Transaction) {
        val newTransaction = transaction.copy(id = nextId++)
        _transactions.value = _transactions.value + newTransaction

        // Atualiza saldo
        _balance.value += transaction.amount
    }

    fun deleteTransaction(transactionId: Int) {
        val transaction = _transactions.value.find { it.id == transactionId }
        _transactions.value = _transactions.value.filter { it.id != transactionId }

        // Atualiza saldo
        transaction?.let {
            _balance.value -= it.amount
        }
    }

    fun getIncomeTotal(): Double {
        return _transactions.value
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
    }

    fun getExpenseTotal(): Double {
        return _transactions.value
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
    }
}