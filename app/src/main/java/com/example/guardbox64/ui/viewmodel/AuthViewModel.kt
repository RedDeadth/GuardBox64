package com.example.guardbox64.ui.viewmodel

import android.util.Log
import com.google.firebase.auth.AuthResult
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.content.Context

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun register(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                onSuccess()
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error durante el registro", e)
                val errorMessage = when (e) {
                    is FirebaseAuthWeakPasswordException -> "La contraseña es demasiado débil"
                    is FirebaseAuthInvalidCredentialsException -> "El email no es válido"
                    is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con este email"
                    else -> "Error: ${e.message}"
                }
                onFailure(errorMessage)
            }
        }
    }
    fun login(
        email: String,
        password: String,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                saveSession(auth.currentUser?.uid ?: "", context)
                onSuccess()
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error durante el inicio de sesión", e)
                val errorMessage = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Credenciales inválidas"
                    is FirebaseAuthWeakPasswordException -> "La contraseña es incorrecta"
                    else -> "Error: ${e.message}"
                }
                onFailure(errorMessage)
            }
        }
    }
    fun loadSession(context: Context): String? {
        val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("user_id", null)
    }
}
private fun saveUserSession(context: Context, isLoggedIn: Boolean) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("is_logged_in", isLoggedIn)
    editor.apply()
}
private fun saveSession(userId: String, context: Context) {
    val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString("user_id", userId)
        apply()
    }
}


