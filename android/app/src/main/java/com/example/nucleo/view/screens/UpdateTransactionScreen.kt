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
import com.example.nucleo.view.components.DatePickerComponent
import com.example.nucleo.view.scaffold.AppScaffold
import com.example.nucleo.viewmodel.TransactionViewModel
import com.example.nucleo.utils.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateTransactionScreen(
    transactionId: Long,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    // Carrega a transação quando a tela é criada
    LaunchedEffect(transactionId) {
        if (transactionId > 0) {
            viewModel.loadTransactionForEdit(transactionId)
        }
    }

    AppScaffold(
        title = "Editar Transação",
        showBackButton = true,
        onBackClick = onBackClick
    ) { padding ->
        
        when (uiState) {
            is TransactionFormUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Carregando transação...")
                    }
                }
            }
            
            is TransactionFormUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Erro",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = (uiState as TransactionFormUiState.Error).message,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onBackClick,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Voltar")
                            }
                        }
                    }
                }
            }
            
            is TransactionFormUiState.Ready -> {
                UpdateTransactionForm(
                    formState = formState,
                    onAmountChange = viewModel::updateAmount,
                    onDescriptionChange = viewModel::updateDescription,
                    onTypeChange = viewModel::updateType,
                    onCategoryChange = viewModel::updateCategory,
                    onDateChange = viewModel::updateDate,
                    onSave = {
                        viewModel.saveTransaction(onSaveClick)
                    },
                    message = (uiState as? TransactionFormUiState.Ready)?.message,
                    onClearMessage = viewModel::clearError,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateTransactionForm(
    formState: com.example.nucleo.viewmodel.TransactionFormData,
    onAmountChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onSave: () -> Unit,
    message: String?,
    onClearMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
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
                onAmountChange(filteredValue)
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
            onValueChange = onDescriptionChange,
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
                onClick = { onTypeChange(TransactionType.INCOME) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (formState.type == TransactionType.INCOME) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("Receita")
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(
                onClick = { onTypeChange(TransactionType.EXPENSE) },
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
                            onCategoryChange(categoryItem)
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
            onDateSelected = onDateChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Exibir mensagem se houver
        message?.let { msg ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (msg.contains("sucesso", true)) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = msg,
                    color = if (msg.contains("sucesso", true)) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Botão Salvar
        Button(
            onClick = {
                onClearMessage()
                onSave()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium,
            enabled = formState.amount.isNotEmpty() && 
                     formState.description.isNotEmpty() &&
                     formState.amount.toDoubleOrNull() != null &&
                     formState.amount.toDoubleOrNull()!! > 0
        ) {
            Text("Atualizar Transação", style = MaterialTheme.typography.labelLarge)
        }
    }
}
