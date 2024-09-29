package com.example.guardbox64.navigator

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.guardbox64.ui.screens.RegisterScreen
import com.example.guardbox64.ui.screens.ConfirmationScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.guardbox64.ui.screens.RegisterScreen
import com.example.guardbox64.ui.screens.ConfirmationScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "register") {
        composable("register") { RegisterScreen(navController) }
        composable("confirmation") { ConfirmationScreen() }

    }
}
