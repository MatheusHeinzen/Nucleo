package com.example.nucleo.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val amount: Double,
    val description: String,
    val type: TransactionType,
    val category: String,
    val date: String
)

enum class TransactionType {
    INCOME,
    EXPENSE
}
