package com.example.guardbox64.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.guardbox64.MainActivity
import com.example.guardbox64.ui.viewmodel.AuthViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.guardbox64.R  // Asegúrate de usar el paquete correcto de tu proyecto
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.draw.clip


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController, viewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top // Asegura que el contenido esté en la parte superior
    ) {
    Box(
        modifier = Modifier
            .size(240.dp)
            .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), shape = CircleShape) // Fondo blanco
            .padding(9.dp) // Espaciado interno
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Imagen del Casillero",
            modifier = Modifier.fillMaxSize().clip(CircleShape) // Recorte en círculo
                .fillMaxSize()
                .clip(CircleShape) // Recorte en círculo
                .align(Alignment.Center) // Centrar la imagen en el Box
        )
    }

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", style = MaterialTheme.typography.labelLarge) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                // Eliminar textColor
            )
        )


        Spacer(modifier = Modifier.height(16.dp))

// Campo de texto para la contraseña
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", style = MaterialTheme.typography.labelLarge) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                // Eliminar textColor
            )
        )


        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar un indicador de carga si es necesario
        if (isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            // Botón de iniciar sesión
            Button(
                onClick = {
                    isLoading = true
                    viewModel.login(email, password, context, {
                        isLoading = false
                        navController.navigate("locker_list")
                    }, { error ->
                        isLoading = false
                        loginError = error
                    })
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Yellow,  // Fondo amarillo
                    contentColor = Color.Black      // Texto negro
                ),
                border = BorderStroke(2.dp, Color.Black) // Contorno de 2dp y color negro
            ) {
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Black  // Aseguramos que el texto sea negro para contraste
                )
            }
        }

        // Mostrar mensaje de error si ocurre
        loginError?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para iniciar sesión con Google
        Button(
            onClick = { (context as MainActivity).signInWithGoogle() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,  // Fondo amarillo
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            border = BorderStroke(2.dp, Color.Black) // Contorno de 2dp y color negro
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Imagen del icono de Google
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Icono de Google",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Iniciar Sesión con Google",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Black  // Aseguramos que el texto sea negro para contraste

                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Texto con un botón para redirigir a la pantalla de registro
        TextButton(onClick = { navController.navigate("register") }) {
            Text(
                text = "¿Aún no tienes una cuenta? Regístrate aquí",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}