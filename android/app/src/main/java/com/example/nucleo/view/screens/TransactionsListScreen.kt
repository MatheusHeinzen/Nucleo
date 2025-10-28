package com.example.nucleo.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nucleo.view.components.TransactionList
import com.example.nucleo.view.scaffold.AppBottomNavigation
import com.example.nucleo.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsListScreen(
    currentRoute: String,
    onNewTransaction: () -> Unit,
    onNavigate: (String) -> Unit,
    onDeleteTransaction: (Int) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val allTransactions by viewModel.transactions.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todas as Transações") },
                actions = {
                    IconButton(onClick = onNewTransaction) {
                        Icon(Icons.Default.Add, contentDescription = "Nova Transação")
                    }
                }
            )
        },
        bottomBar = {
            AppBottomNavigation(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
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
            // Estatísticas rápidas
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        title = "Total",
                        value = "${allTransactions.size}",
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    HorizontalDivider(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                    
                    StatItem(
                        title = "Receitas",
                        value = "${allTransactions.count { it.amount > 0 }}",
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    HorizontalDivider(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                    
                    StatItem(
                        title = "Despesas",
                        value = "${allTransactions.count { it.amount < 0 }}",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Histórico de Transações",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Lista de todas as transações
            TransactionList(
                transactions = allTransactions.sortedByDescending { it.id },
                onDeleteTransaction = onDeleteTransaction,
                onEditTransaction = { transactionId ->
                    onNavigate("update_transaction/$transactionId")
                }
            )
        }
    }
}

@Composable
private fun StatItem(
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
    }
}
