package com.example.nucleo

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var category by remember { mutableStateOf("Alimentação") }
    var date by remember { mutableStateOf(java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date())) } // Data atual

    // Função para salvar
    fun saveTransaction() {
        if (amount.isNotEmpty() && description.isNotEmpty()) {
            val amountValue = amount.toDoubleOrNull() ?: 0.0
            val finalAmount = if (transactionType == TransactionType.EXPENSE) -amountValue else amountValue

            val newTransaction = Transaction(
                id = 0, // Será gerado automaticamente
                amount = finalAmount,
                description = description,
                type = transactionType,
                category = category,
                date = date
            )

            TransactionManager.addTransaction(newTransaction)
            onSaveClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova Transação") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { saveTransaction() },
                        enabled = amount.isNotEmpty() && description.isNotEmpty()
                    ) {
                        Text("Salvar")
                    }
                }
            )
        }
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
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Valor") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ex: 50,00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Descrição
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ex: Almoço restaurante") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tipo
            Text("Tipo", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { transactionType = TransactionType.INCOME },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (transactionType == TransactionType.INCOME) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Receita")
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = { transactionType = TransactionType.EXPENSE },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (transactionType == TransactionType.EXPENSE) Color(0xFFF44336) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Despesa")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Categoria - AGORA EDITÁVEL
            var categoryExpanded by remember { mutableStateOf(false) }
            val categories = listOf("Alimentação", "Transporte", "Moradia", "Lazer", "Saúde", "Educação", "Trabalho", "Outros")

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = category,
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
                                category = categoryItem
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Data - AGORA EDITÁVEL
            var dateExpanded by remember { mutableStateOf(false) }
            val dates = listOf(
                java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date()), // Hoje
                java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(System.currentTimeMillis() - 86400000)), // Ontem
                java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(System.currentTimeMillis() - 172800000)), // Anteontem
                "15/05/2024",
                "10/05/2024"
            )

            ExposedDropdownMenuBox(
                expanded = dateExpanded,
                onExpandedChange = { dateExpanded = !dateExpanded }
            ) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    label = { Text("Data") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dateExpanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = dateExpanded,
                    onDismissRequest = { dateExpanded = false }
                ) {
                    dates.forEach { dateItem ->
                        DropdownMenuItem(
                            text = { Text(dateItem) },
                            onClick = {
                                date = dateItem
                                dateExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botão Salvar GRANDE - AGORA FUNCIONA!
            Button(
                onClick = { saveTransaction() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = amount.isNotEmpty() && description.isNotEmpty()
            ) {
                Text("Salvar Transação", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}