package com.example.nucleo

import java.util.Date

data class Transaction(
    val id: Int,
    val amount: Double,
    val description: String,
    val type: TransactionType, // INCOME ou EXPENSE
    val category: String,
    val date: String
)

enum class TransactionType {
    INCOME,
    EXPENSE
}