package com.example.guardbox64.navigator

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.guardbox64.ui.screens.RegisterScreen
import com.example.guardbox64.ui.screens.ConfirmationScreen
import com.example.guardbox64.ui.screens.LoginScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("register") { RegisterScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("confirmation") { ConfirmationScreen(navController) }

    }
}
