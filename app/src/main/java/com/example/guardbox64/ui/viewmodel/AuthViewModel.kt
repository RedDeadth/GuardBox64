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
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
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
}