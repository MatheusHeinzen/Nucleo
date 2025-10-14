package com.example.nucleo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onBackClick: () -> Unit
) {
    val transactions by TransactionManager.transactions.collectAsState()
    val balance by TransactionManager.balance.collectAsState()

    // Calcular dados para gráficos
    val incomeTotal = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val expenseTotal = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount } * -1

    val categoryExpenses = transactions
        .filter { it.type == TransactionType.EXPENSE }
        .groupBy { it.category }
        .mapValues { (_, transactions) -> transactions.sumOf { it.amount * -1 } }
        .toList()
        .sortedByDescending { it.second }
        .take(5)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estatísticas") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
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
                .padding(16.dp)
        ) {
            // Card Resumo
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Resumo Financeiro",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(
                            title = "Saldo Atual",
                            value = "R$ ${"%.2f".format(balance).replace(".", ",")}",
                            color = MaterialTheme.colorScheme.primary
                        )

                        StatItem(
                            title = "Total Receitas",
                            value = "R$ ${"%.2f".format(incomeTotal).replace(".", ",")}",
                            color = Color(0xFF4CAF50)
                        )

                        StatItem(
                            title = "Total Despesas",
                            value = "R$ ${"%.2f".format(expenseTotal).replace(".", ",")}",
                            color = Color(0xFFF44336)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Gráfico de Receitas vs Despesas
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Receitas vs Despesas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gráfico de barras simples
                    if (incomeTotal > 0 || expenseTotal > 0) {
                        val maxValue = maxOf(incomeTotal, expenseTotal)

                        // Barra Receitas
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Receitas",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.width(80.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Barra
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(24.dp)
                                    .padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .background(Color(0xFF4CAF50))
                                        .width(((incomeTotal / maxValue) * 100).dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Text(
                                        text = "R$ ${"%.0f".format(incomeTotal)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Barra Despesas
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Despesas",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.width(80.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Barra
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(24.dp)
                                    .padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .background(Color(0xFFF44336))
                                        .width(((expenseTotal / maxValue) * 100).dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Text(
                                        text = "R$ ${"%.0f".format(expenseTotal)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Nenhuma transação para exibir",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Gastos por Categoria
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.PieChart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gastos por Categoria",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (categoryExpenses.isNotEmpty()) {
                        val maxExpense = categoryExpenses.maxOfOrNull { it.second } ?: 1.0

                        categoryExpenses.forEach { (category, amount) ->
                            CategoryBarItem(
                                category = category,
                                amount = amount,
                                maxAmount = maxExpense,
                                color = getCategoryColor(category)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    } else {
                        Text(
                            text = "Nenhuma despesa para exibir",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(
    title: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CategoryBarItem(
    category: String,
    amount: Double,
    maxAmount: Double,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(100.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Barra de progresso
        Box(
            modifier = Modifier
                .weight(1f)
                .height(20.dp)
        ) {
            // Fundo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            // Preenchimento
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(color)
                    .width(((amount / maxAmount) * 100).dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "R$ ${"%.0f".format(amount)}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

fun getCategoryColor(category: String): Color {
    return when (category) {
        "Alimentação" -> Color(0xFFFF9800) // Laranja
        "Transporte" -> Color(0xFF2196F3)  // Azul
        "Moradia" -> Color(0xFF9C27B0)     // Roxo
        "Lazer" -> Color(0xFFE91E63)       // Rosa
        "Saúde" -> Color(0xFF4CAF50)       // Verde
        "Educação" -> Color(0xFF009688)    // Teal
        "Trabalho" -> Color(0xFF795548)    // Marrom
        else -> Color(0xFF607D8B)          // Cinza
    }
}