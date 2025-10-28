package com.example.nucleo.data

import androidx.room.*
import com.example.nucleo.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    
    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun observeAll(): Flow<List<Transaction>>
    
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): Transaction?
    
    @Insert
    suspend fun insert(transaction: Transaction): Long
    
    @Update
    suspend fun update(transaction: Transaction)
    
    @Delete
    suspend fun delete(transaction: Transaction)
    
    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("SELECT * FROM transactions WHERE type = 'INCOME'")
    fun getIncomeTransactions(): Flow<List<Transaction>>
    
    @Query("SELECT * FROM transactions WHERE type = 'EXPENSE'")
    fun getExpenseTransactions(): Flow<List<Transaction>>
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME'")
    suspend fun getTotalIncome(): Double?
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE'")
    suspend fun getTotalExpense(): Double?
    
    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getTransactionCount(): Int
}
