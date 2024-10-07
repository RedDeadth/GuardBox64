package com.example.guardbox64.navigator

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.guardbox64.ui.screens.RegisterScreen
import com.example.guardbox64.ui.screens.ConfirmationScreen
import com.example.guardbox64.ui.screens.LockerDetailsScreen
import com.example.guardbox64.ui.screens.LockerListScreen
import com.example.guardbox64.ui.screens.LoginScreen
import com.example.guardbox64.ui.viewmodel.LockerViewModel
import com.example.guardbox64.ui.viewmodel.AuthViewModel

@Composable
fun NavGraph(navController: NavHostController, startDestination: String) {
    val lockerViewModel: LockerViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { LoginScreen(navController, authViewModel) }
        composable("register") { RegisterScreen(navController) }
        composable("locker_list") {
            LockerListScreen(
                lockerViewModel = lockerViewModel,
                navController = navController,
                onAddLockerClick = {
                    // Aquí puedes definir cómo recoger los datos del nuevo casillero,
                    // por ejemplo, usando un diálogo
                }
            )
        }
        composable("locker_details/{lockerId}") { backStackEntry ->
            val lockerId = backStackEntry.arguments?.getString("lockerId")
            lockerId?.let {
                LockerDetailsScreen(navController, lockerId)
            }
        }
        composable("reservation_confirmation/{lockerId}") { backStackEntry ->
            val lockerId = backStackEntry.arguments?.getString("lockerId")
        }
    }
}
