package com.example.nucleo.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
