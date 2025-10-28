package com.example.nucleo.repository

import com.example.nucleo.data.TransactionDao
import com.example.nucleo.model.Transaction
import com.example.nucleo.model.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    
    // Mantém compatibilidade com StateFlow
    private val _balance = MutableStateFlow(295.00)
    val balance: StateFlow<Double> = _balance.asStateFlow()
    
    // Usa Flow do Room diretamente
    val transactions: Flow<List<Transaction>> = transactionDao.observeAll()
    
    init {
        // Inicializa com dados de exemplo se necessário
        initializeWithSampleData()
    }
    
    private fun initializeWithSampleData() {
        // Verifica se precisa adicionar dados iniciais em background
        CoroutineScope(Dispatchers.IO).launch {
            val count = transactionDao.getTransactionCount()
            if (count == 0) {
                // Adiciona dados iniciais
                val sampleTransactions = listOf(
                    Transaction(
                        id = 0L,
                        amount = -85.0,
                        description = "Supermercado",
                        type = TransactionType.EXPENSE,
                        category = "Alimentação",
                        date = "Hoje 14:30"
                    ),
                    Transaction(
                        id = 0L,
                        amount = 500.0,
                        description = "Freelance",
                        type = TransactionType.INCOME,
                        category = "Trabalho",
                        date = "Ontem 09:15"
                    ),
                    Transaction(
                        id = 0L,
                        amount = -120.0,
                        description = "Posto Shell",
                        type = TransactionType.EXPENSE,
                        category = "Transporte",
                        date = "15/05 18:40"
                    )
                )
                
                sampleTransactions.forEach { transaction ->
                    transactionDao.insert(transaction)
                }
                
                updateBalance()
            }
        }
    }
    
    suspend fun addTransaction(transaction: Transaction): Long = withContext(Dispatchers.IO) {
        val id = transactionDao.insert(transaction)
        updateBalance()
        id
    }
    
    suspend fun updateTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        transactionDao.update(transaction)
        updateBalance()
    }
    
    suspend fun deleteTransaction(transactionId: Long) = withContext(Dispatchers.IO) {
        transactionDao.deleteById(transactionId)
        updateBalance()
    }
    
    // Método de compatibilidade para ID Int
    suspend fun deleteTransaction(transactionId: Int) {
        deleteTransaction(transactionId.toLong())
    }
    
    suspend fun getTransactionById(id: Long): Transaction? = withContext(Dispatchers.IO) {
        transactionDao.getById(id)
    }
    
    suspend fun getIncomeTotal(): Double = withContext(Dispatchers.IO) {
        transactionDao.getTotalIncome() ?: 0.0
    }
    
    suspend fun getExpenseTotal(): Double = withContext(Dispatchers.IO) {
        Math.abs(transactionDao.getTotalExpense() ?: 0.0)
    }
    
    private suspend fun updateBalance() {
        val income = getIncomeTotal()
        val expense = getExpenseTotal()
        _balance.value = income - expense
    }
}
