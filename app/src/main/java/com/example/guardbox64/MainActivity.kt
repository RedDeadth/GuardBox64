package com.example.guardbox64

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.example.guardbox64.ui.theme.AppTheme // Asegúrate de importar tu tema aquí

class MainActivity : ComponentActivity() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa Firebase
        FirebaseApp.initializeApp(this)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // Configuración de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Debes usar tu client_id de OAuth 2.0
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val userId = authViewModel.loadSession(this) // Cargar la sesión
        val startDestination = if (userId != null) "locker_list" else "login"

        // Aplicar el tema globalmente
        setContent {
            AppTheme { // Envolver todo el contenido dentro del tema
                navController = rememberNavController() // Crear el controlador de navegación
                NavGraph(navController = navController, startDestination = startDestination)
            }
        }
    }

    fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Manejar el resultado del flujo de Google Sign-In
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // El inicio de sesión fue exitoso, obtener el idToken
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    authViewModel.signInWithGoogle(idToken, this, navController, {
                        // Autenticación exitosa, navega a la pantalla "locker_list"
                        navController.navigate("locker_list") {
                            popUpTo("login") { inclusive = true }
                        }
                    }, { errorMessage ->
                        // Mostrar el error
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    })
                }
            } catch (e: ApiException) {
                // El inicio de sesión falló
                Log.e("MainActivity", "Google sign in failed", e)
            }
        }
    }

    companion object {
        const val RC_SIGN_IN = 9001
    }
}
