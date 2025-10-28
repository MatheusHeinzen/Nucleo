package com.example.nucleo.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.nucleo.model.Transaction
import com.example.nucleo.model.TransactionType
import com.example.nucleo.utils.CurrencyUtils

@Composable
fun TransactionItem(
    transaction: Transaction,
    onDelete: () -> Unit,
    onEdit: (() -> Unit)? = null
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier
            .padding(bottom = 8.dp)
            .then(
                if (onEdit != null) {
                    Modifier.clickable { onEdit() }
                } else Modifier
            )
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
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = CurrencyUtils.formatCurrency(transaction.amount, showSign = true),
                color = if (transaction.amount > 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                style = MaterialTheme.typography.bodyLarge
            )

            Row {
                // Botão editar (se disponível)
                onEdit?.let { editAction ->
                    IconButton(onClick = editAction) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                    }
                }
                
                // Botão deletar
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = Color(0xFFF44336))
                }
            }
        }
    }
    
    // Dialog de confirmação
    DeleteConfirmationDialog(
        isVisible = showDeleteDialog,
        onDismiss = { showDeleteDialog = false },
        onConfirm = onDelete
    )
}
