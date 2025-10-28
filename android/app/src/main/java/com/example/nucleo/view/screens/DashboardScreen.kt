package com.example.nucleo.view.screens

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nucleo.model.Transaction
import com.example.nucleo.model.TransactionType
import com.example.nucleo.view.components.TransactionItem
import com.example.nucleo.view.components.DashboardStatsCard
import com.example.nucleo.view.components.TransactionList
import com.example.nucleo.view.scaffold.AppBottomNavigation
import com.example.nucleo.viewmodel.DashboardViewModel
import com.example.nucleo.viewmodel.DashboardStats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    currentRoute: String,
    onNewTransaction: () -> Unit,
    onProfileClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onNavigate: (String) -> Unit,
    onDeleteTransaction: (Int) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val dashboardStats by viewModel.dashboardStats.collectAsStateWithLifecycle(
        initialValue = DashboardStats(0.0, 0.0, 0.0, 0, null)
    )
    val allTransactions by viewModel.transactions.collectAsStateWithLifecycle()
    val recentTransactions = allTransactions.sortedByDescending { it.id }.take(5)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nucleo") },
                actions = {
                    IconButton(onClick = onStatisticsClick) {
                        Icon(Icons.Default.BarChart, contentDescription = "Estatísticas")
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Settings, contentDescription = "Configurações")
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
            // Card de Estatísticas
            DashboardStatsCard(stats = dashboardStats)

            Spacer(modifier = Modifier.height(24.dp))

            // Últimas transações
            Text(
                text = "Últimas Transações",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Lista de transações
            TransactionList(
                transactions = recentTransactions,
                onDeleteTransaction = onDeleteTransaction,
                onEditTransaction = { transactionId ->
                    onNavigate("update_transaction/$transactionId")
                }
            )
        }
    }
}