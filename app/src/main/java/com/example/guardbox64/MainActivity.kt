package com.example.guardbox64

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.guardbox64.ui.viewmodel.LockerViewModel
import com.google.firebase.FirebaseApp
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.guardbox64.ui.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.guardbox64.navigator.NavGraph
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    private lateinit var authViewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa Firebase
        FirebaseApp.initializeApp(this)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        val userId = authViewModel.loadSession(this) // Cargar la sesión
        val startDestination = if (userId != null) "locker_list" else "login"
        setContent {
            val navController = rememberNavController() // Crear el controlador de navegación
            NavGraph(navController = navController, startDestination = startDestination)
        }
    }
}