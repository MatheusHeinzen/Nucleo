package com.example.nucleo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nucleo.model.Transaction
import com.example.nucleo.model.TransactionType
import com.example.nucleo.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TransactionViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    fun updateAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun updateType(type: TransactionType) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun updateCategory(category: String) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun updateDate(date: String) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun addTransaction(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        
        if (!isValidTransaction(currentState)) {
            _uiState.value = currentState.copy(error = "Preencha todos os campos corretamente")
            return
        }

        viewModelScope.launch {
            try {
                val amountValue = currentState.amount.toDoubleOrNull() ?: 0.0
                val finalAmount = if (currentState.type == TransactionType.EXPENSE) -amountValue else amountValue

                val transaction = Transaction(
                    id = 0, // Será definido pelo repository
                    amount = finalAmount,
                    description = currentState.description,
                    type = currentState.type,
                    category = currentState.category,
                    date = currentState.date
                )

                transactionRepository.addTransaction(transaction)
                resetForm()
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = currentState.copy(error = "Erro ao adicionar transação: ${e.message}")
            }
        }
    }

    private fun isValidTransaction(state: TransactionUiState): Boolean {
        return state.amount.isNotBlank() && 
               state.description.isNotBlank() &&
               state.amount.toDoubleOrNull() != null &&
               state.amount.toDoubleOrNull()!! > 0
    }

    private fun resetForm() {
        _uiState.value = TransactionUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class TransactionUiState(
    val amount: String = "",
    val description: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val category: String = "Alimentação",
    val date: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
    val error: String? = null
)