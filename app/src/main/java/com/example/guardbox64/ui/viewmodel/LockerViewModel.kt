package com.example.guardbox64.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.example.guardbox64.model.Locker
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.logging.Log

class LockerViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    var lockers = mutableStateOf<List<Locker>>(emptyList())
        private set

    init {
        loadLockers() // Cargar los casilleros al inicializar el ViewModel
    }

    private fun loadLockers() {
        firestore.collection("lockers").get()
            .addOnSuccessListener { documents ->
                val lockerList = documents.mapNotNull { document ->
                    document.toObject(Locker::class.java).copy(id = document.id)
                }
                lockers.value = lockerList // Actualizar la lista de casilleros
                android.util.Log.d("Firestore", "Casilleros cargados correctamente.")
            }
            .addOnFailureListener { e ->
                android.util.Log.e("Firestore", "Error al cargar casilleros: ", e)
                lockers.value = emptyList() // Retornar lista vacía en caso de error
            }
    }

    fun addLocker(locker: Locker) {
        // Verificar que locker.id no esté vacío antes de intentar añadirlo a Firebase
        if (locker.id.isNotEmpty()) {
            firestore.collection("lockers").document(locker.id).set(locker)
                .addOnSuccessListener {
                    loadLockers() // Recargar los casilleros después de añadir uno nuevo
                    android.util.Log.d("Firestore", "Casillero añadido correctamente con ID: ${locker.id}")
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("Firestore", "Error al añadir casillero: ", e)
                }
        } else {
            android.util.Log.e("Firestore", "La ID del casillero está vacía. No se puede añadir.")
        }
    }
}