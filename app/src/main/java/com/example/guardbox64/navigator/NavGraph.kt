package com.example.guardbox64.navigator

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.guardbox64.ui.screens.RegisterScreen
import com.example.guardbox64.ui.screens.ConfirmationScreen
import com.example.guardbox64.ui.screens.LockerDetailsScreen
import com.example.guardbox64.ui.screens.LockerListScreen
import com.example.guardbox64.ui.screens.LoginScreen
import com.example.guardbox64.utils.getMockedLockers

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("confirmation") {
            LockerListScreen(navController, lockerList = getMockedLockers())
        }
        composable("locker_details/{lockerId}") { backStackEntry ->
            val lockerId = backStackEntry.arguments?.getString("lockerId")
            lockerId?.let {
                LockerDetailsScreen(navController, lockerId)
            }
        }
        composable("reservation_confirmation/{lockerId}") { backStackEntry ->
            val lockerId = backStackEntry.arguments?.getString("lockerId")
            // Aquí manejas la pantalla de confirmación de reserva
        }
    }
}
