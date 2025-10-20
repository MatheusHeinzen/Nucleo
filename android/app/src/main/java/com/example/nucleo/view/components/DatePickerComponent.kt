package com.example.nucleo.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerComponent(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    
    // Converter string para millis se necessário
    LaunchedEffect(selectedDate) {
        if (selectedDate.isNotEmpty()) {
            try {
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = format.parse(selectedDate)
                date?.let { selectedDateMillis = it.time }
            } catch (e: Exception) {
                // Se não conseguir parsear, usa data atual
                selectedDateMillis = System.currentTimeMillis()
            }
        }
    }
    
    OutlinedTextField(
        value = selectedDate,
        onValueChange = { },
        label = { Text("Data") },
        modifier = modifier
            .fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Selecionar Data")
            }
        }
    )
    
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Date(millis)
                            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            onDateSelected(format.format(date))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Selecionar Data") },
        text = content,
        confirmButton = confirmButton,
        dismissButton = dismissButton
    )
}
