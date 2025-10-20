package com.example.nucleo.repository

import android.content.Context
import com.example.nucleo.data.PreferencesManager
import com.example.nucleo.model.Transaction
import com.example.nucleo.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TransactionRepository(context: Context) {
    private val preferencesManager = PreferencesManager(context)
    
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _balance = MutableStateFlow(295.00)
    val balance: StateFlow<Double> = _balance.asStateFlow()

    private var nextId = 4

    init {
        loadData()
    }
    
    private fun loadData() {
        val savedTransactions = preferencesManager.loadTransactions()
        val savedBalance = preferencesManager.loadBalance()
        val savedNextId = preferencesManager.loadNextId()
        
        if (savedTransactions.isNotEmpty()) {
            _transactions.value = savedTransactions
            _balance.value = savedBalance
            nextId = savedNextId
        } else {
            // Dados iniciais se não houver dados salvos
            _transactions.value = listOf(
                Transaction(1, -85.0, "Supermercado", TransactionType.EXPENSE, "Alimentação", "Hoje 14:30"),
                Transaction(2, 500.0, "Freelance", TransactionType.INCOME, "Trabalho", "Ontem 09:15"),
                Transaction(3, -120.0, "Posto Shell", TransactionType.EXPENSE, "Transporte", "15/05 18:40")
            )
            saveData()
        }
    }
    
    private fun saveData() {
        preferencesManager.saveTransactions(_transactions.value)
        preferencesManager.saveBalance(_balance.value)
        preferencesManager.saveNextId(nextId)
    }

    fun addTransaction(transaction: Transaction) {
        val newTransaction = transaction.copy(id = nextId++)
        _transactions.value = _transactions.value + newTransaction
        _balance.value += transaction.amount
        saveData()
    }

    fun deleteTransaction(transactionId: Int) {
        val transaction = _transactions.value.find { it.id == transactionId }
        _transactions.value = _transactions.value.filter { it.id != transactionId }
        transaction?.let { _balance.value -= it.amount }
        saveData()
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
