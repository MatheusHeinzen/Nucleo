package com.example.nucleo.model

data class Transaction(
    val id: Int,
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
