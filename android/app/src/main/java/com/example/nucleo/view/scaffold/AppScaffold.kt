package com.example.nucleo.view.scaffold

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String,
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (showBackButton && onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                }
            )
        },
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffoldWithBottomBar(
    title: String,
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    bottomBar: @Composable () -> Unit,
    floatingActionButton: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (showBackButton && onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                }
            )
        },
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        content = content
    )
}

@Composable
fun AppBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "dashboard",
            onClick = { onNavigate("dashboard") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = currentRoute == "transactions",
            onClick = { onNavigate("transactions") },
            icon = { Icon(Icons.Default.Receipt, contentDescription = "Transacoes") },
            label = { Text("Transacoes") }
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { onNavigate("profile") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )
    }
}