package com.example.nucleo

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nucleo.view.screens.*
import com.example.nucleo.viewmodel.DashboardViewModel

@Composable
fun NucleoApp() {
    val navController = rememberNavController()
    val dashboardViewModel: DashboardViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                currentRoute = "dashboard",
                onNewTransaction = {
                    navController.navigate("transaction")
                },
                onProfileClick = {
                    navController.navigate("profile")
                },
                onStatisticsClick = {
                    navController.navigate("statistics")
                },
                onNavigate = { route ->
                    navController.navigate(route)
                },
                onDeleteTransaction = { transactionId ->
                    dashboardViewModel.deleteTransaction(transactionId)
                }
            )
        }

        composable("transaction") {
            TransactionScreen(
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "update_transaction/{transactionId}",
            arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: 0L
            UpdateTransactionScreen(
                transactionId = transactionId,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }

        composable("transactions") {
            TransactionsListScreen(
                currentRoute = "transactions",
                onNewTransaction = {
                    navController.navigate("transaction")
                },
                onNavigate = { route ->
                    navController.navigate(route)
                },
                onDeleteTransaction = { transactionId ->
                    dashboardViewModel.deleteTransaction(transactionId)
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("statistics") {
            val transactions by dashboardViewModel.transactions.collectAsState()
            val balance by dashboardViewModel.balance.collectAsState()

            StatisticsScreen(
                transactions = transactions,
                balance = balance,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}