package com.example.financialliteracyrealestate.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.financialliteracyrealestate.ui.calculator.CalculatorScreen
import com.example.financialliteracyrealestate.ui.dashboard.DashboardScreen
import com.example.financialliteracyrealestate.ui.learn.LearnScreen
import com.example.financialliteracyrealestate.ui.savings.SavingsGoalsScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Dashboard : Screen("dashboard", "Home", Icons.Filled.Home)
    data object Goals : Screen("goals", "Goals", Icons.Filled.AccountBalance)
    data object Calculator : Screen("calculator", "Calculator", Icons.Filled.Calculate)
    data object Learn : Screen("learn", "Learn", Icons.Filled.School)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Dashboard,
        Screen.Goals,
        Screen.Calculator,
        Screen.Learn
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen()
            }
            composable(Screen.Goals.route) {
                SavingsGoalsScreen(
                    onNavigateBack = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Screen.Calculator.route) {
                CalculatorScreen()
            }
            composable(Screen.Learn.route) {
                LearnScreen()
            }
        }
    }
}