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
        loadLockers()

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
        val newLocker = locker.copy(id = firestore.collection("lockers").document().id)
        firestore.collection("lockers").document(newLocker.id).set(newLocker)
            .addOnSuccessListener {
                val updatedList = lockers.value.toMutableList()
                updatedList.add(newLocker)
                lockers.value = updatedList
                android.util.Log.d("Firestore", "Casillero añadido correctamente con ID: ${newLocker.id}")
            }
            .addOnFailureListener { e ->
                android.util.Log.e("Firestore", "Error al añadir casillero: ", e)
            }
    }
}