package com.example.nucleo.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.nucleo.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFilters(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedType: TransactionType?,
    onTypeChange: (TransactionType?) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilters by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        // Barra de busca
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Buscar transações") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpar")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        // Botão para mostrar/ocultar filtros
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = { showFilters = !showFilters }
            ) {
                Icon(
                    imageVector = if (showFilters) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (showFilters) "Ocultar filtros" else "Mostrar filtros"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Filtros")
            }
            
            if (searchQuery.isNotEmpty() || selectedType != null || selectedCategory != "Todas") {
                TextButton(onClick = {
                    onSearchQueryChange("")
                    onTypeChange(null)
                    onCategoryChange("Todas")
                }) {
                    Text("Limpar filtros")
                }
            }
        }
        
        // Filtros expandíveis
        if (showFilters) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Filtro por tipo
                    Text(
                        text = "Tipo de Transação",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        FilterChip(
                            selected = selectedType == null,
                            onClick = { onTypeChange(null) },
                            label = { Text("Todas") },
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        FilterChip(
                            selected = selectedType == TransactionType.INCOME,
                            onClick = { onTypeChange(TransactionType.INCOME) },
                            label = { Text("Receitas") },
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        FilterChip(
                            selected = selectedType == TransactionType.EXPENSE,
                            onClick = { onTypeChange(TransactionType.EXPENSE) },
                            label = { Text("Despesas") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Filtro por categoria
                    Text(
                        text = "Categoria",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    var categoryExpanded by remember { mutableStateOf(false) }
                    val categories = listOf("Todas", "Alimentação", "Transporte", "Moradia", "Lazer", "Saúde", "Educação", "Trabalho", "Outros")
                    
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                            }
                        )
                        
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        onCategoryChange(category)
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
