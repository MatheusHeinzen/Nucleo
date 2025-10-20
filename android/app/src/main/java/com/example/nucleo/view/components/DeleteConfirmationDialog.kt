package com.example.nucleo.view.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DeleteConfirmationDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String = "Confirmar Exclusão",
    message: String = "Tem certeza que deseja excluir esta transação? Esta ação não pode ser desfeita.",
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            },
            modifier = modifier
        )
    }
}
