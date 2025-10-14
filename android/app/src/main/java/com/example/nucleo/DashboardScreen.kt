package com.example.nucleo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNewTransaction: () -> Unit,
    onProfileClick: () -> Unit,
    onStatisticsClick: () -> Unit
) {
    val transactions by TransactionManager.transactions.collectAsState()
    val balance by TransactionManager.balance.collectAsState()
    val incomeTotal = remember { TransactionManager.getIncomeTotal() }
    val expenseTotal = remember { TransactionManager.getExpenseTotal() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nucleo") },
                actions = {
                    IconButton(onClick = onStatisticsClick) {
                        Icon(Icons.Default.BarChart, contentDescription = "Estatísticas")
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Settings, contentDescription = "Configuracoes")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Receipt, contentDescription = "Transacoes") },
                    label = { Text("Transacoes") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onProfileClick,
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewTransaction,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova Transacao")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Card de Saldo
            Card(
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Saldo Disponivel",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "R$ ${"%.2f".format(balance).replace(".", ",")}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Row(
                        modifier = Modifier.padding(top = 24.dp)
                    ) {
                        // Receita
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Receita", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("R$ ${"%.2f".format(incomeTotal).replace(".", ",")}", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF4CAF50))
                        }

                        // Divisor
                        HorizontalDivider(
                            modifier = Modifier
                                .width(1.dp)
                                .height(24.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )

                        // Despesa
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Despesa", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("R$ ${"%.2f".format(expenseTotal).replace(".", ",")}", style = MaterialTheme.typography.bodyLarge, color = Color(0xFFF44336))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Últimas transações
            Text(
                text = "Últimas Transações",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Lista de transações
            LazyColumn {
                items(transactions.take(5)) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onDelete = { TransactionManager.deleteTransaction(transaction.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onDelete: () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (transaction.category) {
                    "Alimentação" -> Icons.Default.ShoppingCart
                    "Trabalho" -> Icons.Default.Computer
                    "Transporte" -> Icons.Default.DirectionsCar
                    else -> Icons.Default.Receipt
                },
                contentDescription = null,
                tint = if (transaction.type == TransactionType.INCOME) Color(0xFF4CAF50) else Color(0xFFF44336),
                modifier = Modifier.padding(end = 16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(transaction.description, style = MaterialTheme.typography.bodyLarge)
                Text(transaction.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Text(
                text = if (transaction.amount > 0) "+R$ ${transaction.amount.toInt()}" else "R$ ${transaction.amount.toInt()}",
                color = if (transaction.amount > 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                style = MaterialTheme.typography.bodyLarge
            )

            // Botão deletar
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = Color(0xFFF44336))
            }
        }
    }
}