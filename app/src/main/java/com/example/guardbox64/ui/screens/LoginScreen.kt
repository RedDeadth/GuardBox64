package com.example.guardbox64.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.guardbox64.ui.viewmodel.AuthViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

import androidx.navigation.NavController
import com.example.guardbox64.MainActivity

@Composable
fun LoginScreen(navController: NavHostController, viewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    val authState by viewModel.authState.observeAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(onClick = {
                isLoading = true
                viewModel.login(email, password, context, {
                    isLoading = false
                    // Navegar a la pantalla principal/home después de iniciar sesión
                    navController.navigate("locker_list")
                }, { error ->
                    isLoading = false
                    loginError = error
                })
            }) {
                Text("Iniciar Sesión")
            }
        }
        // Mostrar error en caso de fallar el inicio de sesión
        loginError?.let { error ->
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Llama a la función de inicio de sesión con Google
            (context as MainActivity).signInWithGoogle()
        }) {
            Text("Iniciar Sesión con Google")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Enlace a la pantalla de registro
        TextButton(onClick = {
            // Navegar a la pantalla de registro
            navController.navigate("register")
        }) {
            Text(text = "¿Aún no tienes una cuenta? Regístrate aquí")
        }
    }
}
