package com.example.nucleo.view.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nucleo.model.TransactionType
import com.example.nucleo.ui.TransactionFormUiState
import com.example.nucleo.view.scaffold.AppScaffold
import com.example.nucleo.viewmodel.TransactionViewModel
import com.example.nucleo.view.components.DatePickerComponent
import com.example.nucleo.utils.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    AppScaffold(
        title = "Nova Transação",
        showBackButton = true,
        onBackClick = onBackClick
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Valor
            OutlinedTextField(
                value = formState.amount,
                onValueChange = { value ->
                    // Permitir apenas números, vírgula e ponto
                    val filteredValue = value.filter { it.isDigit() || it == ',' || it == '.' }
                    viewModel.updateAmount(filteredValue)
                },
                label = { Text("Valor") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ex: 50,00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = formState.amount.isNotEmpty() && formState.amount.toDoubleOrNull() == null,
                supportingText = {
                    if (formState.amount.isNotEmpty()) {
                        val parsed = CurrencyUtils.parseCurrency(formState.amount)
                        parsed?.let {
                            Text(
                                text = CurrencyUtils.formatCurrency(it),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )

            if (formState.amount.isNotEmpty() && formState.amount.toDoubleOrNull() == null) {
                Text(
                    text = "Digite um valor válido",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Descrição
            OutlinedTextField(
                value = formState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ex: Almoço restaurante") },
                isError = formState.description.isNotEmpty() && formState.description.length < 3
            )

            if (formState.description.isNotEmpty() && formState.description.length < 3) {
                Text(
                    text = "Descrição deve ter pelo menos 3 caracteres",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tipo
            Text("Tipo", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { viewModel.updateType(TransactionType.INCOME) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (formState.type == TransactionType.INCOME) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Receita")
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = { viewModel.updateType(TransactionType.EXPENSE) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (formState.type == TransactionType.EXPENSE) Color(0xFFF44336) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Despesa")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Categoria
            var categoryExpanded by remember { mutableStateOf(false) }
            val categories = listOf("Alimentação", "Transporte", "Moradia", "Lazer", "Saúde", "Educação", "Trabalho", "Outros")

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = formState.category,
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    label = { Text("Categoria") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { categoryItem ->
                        DropdownMenuItem(
                            text = { Text(categoryItem) },
                            onClick = {
                                viewModel.updateCategory(categoryItem)
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Data
            DatePickerComponent(
                selectedDate = formState.date,
                onDateSelected = viewModel::updateDate,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Exibir mensagem se houver
            val currentUiState = uiState
            when (currentUiState) {
                is TransactionFormUiState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = currentUiState.message,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                is TransactionFormUiState.Ready -> {
                    currentUiState.message?.let { message ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(
                                text = message,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                else -> { /* Loading state - no message */ }
            }

            // Botão Salvar
            Button(
                onClick = { 
                    viewModel.clearError()
                    viewModel.saveTransaction {
                        onSaveClick() // Navega de volta para o dashboard
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = formState.amount.isNotEmpty() && 
                         formState.description.isNotEmpty() &&
                         formState.amount.toDoubleOrNull() != null &&
                         formState.amount.toDoubleOrNull()!! > 0 &&
                         uiState !is TransactionFormUiState.Loading
            ) {
                if (uiState is TransactionFormUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Salvar Transação", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}