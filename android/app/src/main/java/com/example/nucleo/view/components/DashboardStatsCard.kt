package com.example.nucleo.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nucleo.viewmodel.DashboardStats
import com.example.nucleo.utils.CurrencyUtils

@Composable
fun DashboardStatsCard(
    stats: DashboardStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                text = CurrencyUtils.formatCurrency(stats.balance),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Receita
                StatColumn(
                    title = "Receita",
                    value = CurrencyUtils.formatCurrency(stats.incomeTotal),
                    color = Color(0xFF4CAF50),
                    icon = Icons.Default.TrendingUp
                )

                // Divisor
                HorizontalDivider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                // Despesa
                StatColumn(
                    title = "Despesa",
                    value = CurrencyUtils.formatCurrency(stats.expenseTotal),
                    color = Color(0xFFF44336),
                    icon = Icons.Default.TrendingDown
                )

                // Divisor
                HorizontalDivider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                // Total de Transações
                StatColumn(
                    title = "Total",
                    value = "${stats.transactionCount}",
                    color = MaterialTheme.colorScheme.primary,
                    icon = Icons.Default.Receipt
                )
            }
        }
    }
}

@Composable
private fun StatColumn(
    title: String,
    value: String,
    color: Color,
    icon: ImageVector
) {
    Column(
        modifier = Modifier.fillMaxWidth(0.3f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}