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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nucleo.model.TransactionType
import com.example.nucleo.view.scaffold.AppScaffold
import com.example.nucleo.viewmodel.TransactionViewModel
import com.example.nucleo.di.AppModule
import com.example.nucleo.view.components.DatePickerComponent
import com.example.nucleo.utils.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val transactionRepository = AppModule.transactionRepository
    val viewModel = remember { TransactionViewModel(transactionRepository) }
    val uiState by viewModel.uiState.collectAsState()

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
                value = uiState.amount,
                onValueChange = { value ->
                    // Permitir apenas números, vírgula e ponto
                    val filteredValue = value.filter { it.isDigit() || it == ',' || it == '.' }
                    viewModel.updateAmount(filteredValue)
                },
                label = { Text("Valor") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ex: 50,00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = uiState.amount.isNotEmpty() && uiState.amount.toDoubleOrNull() == null,
                supportingText = {
                    if (uiState.amount.isNotEmpty()) {
                        val parsed = CurrencyUtils.parseCurrency(uiState.amount)
                        parsed?.let {
                            Text(
                                text = CurrencyUtils.formatCurrency(it),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )

            if (uiState.amount.isNotEmpty() && uiState.amount.toDoubleOrNull() == null) {
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
                value = uiState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ex: Almoço restaurante") },
                isError = uiState.description.isNotEmpty() && uiState.description.length < 3
            )

            if (uiState.description.isNotEmpty() && uiState.description.length < 3) {
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
                        contentColor = if (uiState.type == TransactionType.INCOME) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Receita")
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = { viewModel.updateType(TransactionType.EXPENSE) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (uiState.type == TransactionType.EXPENSE) Color(0xFFF44336) else MaterialTheme.colorScheme.onSurfaceVariant
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
                    value = uiState.category,
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
                selectedDate = uiState.date,
                onDateSelected = viewModel::updateDate,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Exibir erro se houver
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Botão Salvar
            Button(
                onClick = { 
                    viewModel.clearError()
                    viewModel.addTransaction {
                        onSaveClick() // Navega de volta para o dashboard
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = uiState.amount.isNotEmpty() && 
                         uiState.description.isNotEmpty() &&
                         uiState.amount.toDoubleOrNull() != null &&
                         uiState.amount.toDoubleOrNull()!! > 0
            ) {
                Text("Salvar Transação", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}