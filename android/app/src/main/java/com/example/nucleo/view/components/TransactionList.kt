package com.example.nucleo.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nucleo.model.Transaction
import com.example.nucleo.model.TransactionType

@Composable
fun TransactionList(
    transactions: List<Transaction>,
    onDeleteTransaction: (Int) -> Unit,
    onEditTransaction: ((Long) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<TransactionType?>(null) }
    var selectedCategory by remember { mutableStateOf("Todas") }
    
    // Filtrar transações
    val filteredTransactions = remember(transactions, searchQuery, selectedType, selectedCategory) {
        transactions.filter { transaction ->
            val matchesSearch = searchQuery.isEmpty() || 
                transaction.description.contains(searchQuery, ignoreCase = true) ||
                transaction.category.contains(searchQuery, ignoreCase = true)
            
            val matchesType = selectedType == null || transaction.type == selectedType
            
            val matchesCategory = selectedCategory == "Todas" || transaction.category == selectedCategory
            
            matchesSearch && matchesType && matchesCategory
        }
    }
    
    Column(modifier = modifier) {
        // Filtros
        TransactionFilters(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            selectedType = selectedType,
            onTypeChange = { selectedType = it },
            selectedCategory = selectedCategory,
            onCategoryChange = { selectedCategory = it },
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Resultados
        if (filteredTransactions.isEmpty()) {
            EmptyStateCard(
                title = if (transactions.isEmpty()) "Nenhuma transação" else "Nenhum resultado",
                subtitle = if (transactions.isEmpty()) 
                    "Adicione sua primeira transação usando o botão +" 
                else 
                    "Tente ajustar os filtros de busca"
            )
        } else {
            // Contador de resultados
            Text(
                text = "${filteredTransactions.size} de ${transactions.size} transações",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredTransactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onDelete = { onDeleteTransaction(transaction.id.toInt()) },
                        onEdit = onEditTransaction?.let { edit -> { edit(transaction.id) } }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
