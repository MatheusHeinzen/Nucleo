package com.example.nucleo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nucleo.model.Transaction
import com.example.nucleo.model.TransactionType
import com.example.nucleo.repository.TransactionRepository
import com.example.nucleo.ui.TransactionFormUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransactionFormUiState>(TransactionFormUiState.Loading)
    val uiState: StateFlow<TransactionFormUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(TransactionFormData())
    val formState: StateFlow<TransactionFormData> = _formState.asStateFlow()

    init {
        _uiState.value = TransactionFormUiState.Ready()
    }

    fun loadTransactionForEdit(transactionId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = TransactionFormUiState.Loading
                val transaction = transactionRepository.getTransactionById(transactionId)
                if (transaction != null) {
                    _formState.value = TransactionFormData(
                        amount = Math.abs(transaction.amount).toString(),
                        description = transaction.description,
                        type = transaction.type,
                        category = transaction.category,
                        date = transaction.date
                    )
                    _uiState.value = TransactionFormUiState.Ready(transaction, isEditing = true)
                } else {
                    _uiState.value = TransactionFormUiState.Error("Transação não encontrada")
                }
            } catch (e: Exception) {
                _uiState.value = TransactionFormUiState.Error("Erro ao carregar transação: ${e.message}")
            }
        }
    }

    fun updateAmount(amount: String) {
        _formState.value = _formState.value.copy(amount = amount)
    }

    fun updateDescription(description: String) {
        _formState.value = _formState.value.copy(description = description)
    }

    fun updateType(type: TransactionType) {
        _formState.value = _formState.value.copy(type = type)
    }

    fun updateCategory(category: String) {
        _formState.value = _formState.value.copy(category = category)
    }

    fun updateDate(date: String) {
        _formState.value = _formState.value.copy(date = date)
    }

    fun saveTransaction(onSuccess: () -> Unit) {
        val currentFormState = _formState.value
        val currentUiState = _uiState.value
        
        if (!isValidTransaction(currentFormState)) {
            _uiState.value = TransactionFormUiState.Error("Preencha todos os campos corretamente")
            return
        }

        viewModelScope.launch {
            try {
                val amountValue = currentFormState.amount.toDoubleOrNull() ?: 0.0
                val finalAmount = if (currentFormState.type == TransactionType.EXPENSE) -amountValue else amountValue

                val transaction = Transaction(
                    id = if (currentUiState is TransactionFormUiState.Ready && currentUiState.isEditing) 
                        currentUiState.transaction?.id ?: 0L else 0L,
                    amount = finalAmount,
                    description = currentFormState.description,
                    type = currentFormState.type,
                    category = currentFormState.category,
                    date = currentFormState.date
                )

                if (currentUiState is TransactionFormUiState.Ready && currentUiState.isEditing) {
                    transactionRepository.updateTransaction(transaction)
                    _uiState.value = TransactionFormUiState.Ready(message = "Transação atualizada com sucesso!")
                } else {
                    transactionRepository.addTransaction(transaction)
                    _uiState.value = TransactionFormUiState.Ready(message = "Transação criada com sucesso!")
                    resetForm()
                }
                
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = TransactionFormUiState.Error("Erro ao salvar transação: ${e.message}")
            }
        }
    }

    private fun isValidTransaction(state: TransactionFormData): Boolean {
        return state.amount.isNotBlank() && 
               state.description.isNotBlank() &&
               state.amount.toDoubleOrNull() != null &&
               state.amount.toDoubleOrNull()!! > 0
    }

    private fun resetForm() {
        _formState.value = TransactionFormData()
    }

    fun clearError() {
        when (val currentState = _uiState.value) {
            is TransactionFormUiState.Error -> {
                _uiState.value = TransactionFormUiState.Ready()
            }
            is TransactionFormUiState.Ready -> {
                _uiState.value = currentState.copy(message = null)
            }
            else -> { /* No action needed for Loading */ }
        }
    }
}

data class TransactionFormData(
    val amount: String = "",
    val description: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val category: String = "Alimentação",
    val date: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
)