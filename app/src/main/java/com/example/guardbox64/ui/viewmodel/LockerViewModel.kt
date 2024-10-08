package com.example.guardbox64.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.example.guardbox64.model.Locker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*

class LockerViewModel : ViewModel() {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("lockers")

    private val _lockers = MutableLiveData<List<Locker>>(emptyList())
    val lockers: LiveData<List<Locker>> = _lockers

    init {
        loadLockers()
    }

    fun reserveLocker(
        lockerId: String,
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val lockerRef = database.child(lockerId)
        val updates = mapOf<String, Any>(
            "occupied" to true,
            "userId" to userId
        )

        // Actualiza los campos ocupados y userId en una sola llamada
        lockerRef.updateChildren(updates)
            .addOnSuccessListener {
                onSuccess() // Notificar éxito
            }
            .addOnFailureListener { e ->
                onFailure("Error reservando el casillero: ${e.message}")
            }
    }

    fun loadLockers() {
        val lockersRef = FirebaseDatabase.getInstance().getReference("lockers")
        lockersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lockerList = mutableListOf<Locker>()
                for (lockerSnapshot in snapshot.children) {
                    val locker = lockerSnapshot.getValue(Locker::class.java)
                        ?.copy(id = lockerSnapshot.key ?: "")
                    locker?.let { lockerList.add(it) }
                }
                _lockers.value = lockerList // Actualizar el LiveData
                android.util.Log.d("Firebase", "Casilleros cargados correctamente.")
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("Firebase", "Error al cargar casilleros: ${error.message}")
                _lockers.value = emptyList() // Retornar lista vacía en caso de error
            }
        })
    }

    fun addLocker(locker: Locker) {
        val newLockerRef = database.push() // Crea una nueva referencia en la base de datos
        val newLocker = locker.copy(id = newLockerRef.key ?: "") // Usa la clave generada

        newLockerRef.setValue(newLocker)
            .addOnSuccessListener {
                // Aquí es donde debes obtener la lista actual de lockers y agregar el nuevo casillero
                val currentLockers = _lockers.value.orEmpty()
                    .toMutableList() // Asegúrate de obtener la lista actual o una lista vacía
                currentLockers.add(newLocker) // Agrega el nuevo casillero a la lista
                _lockers.postValue(currentLockers) // Actualiza el LiveData con la lista actualizada

                android.util.Log.d(
                    "RealtimeDatabase",
                    "Casillero añadido correctamente con ID: ${newLocker.id}"
                )
            }
            .addOnFailureListener { e ->
                android.util.Log.e("RealtimeDatabase", "Error al añadir casillero: ", e)
            }
    }
    fun updateLockerOpenState(lockerId: String, isOpen: Boolean) {
        val lockerRef = database.child(lockerId)
        lockerRef.child("open").setValue(isOpen)
            .addOnSuccessListener {
                android.util.Log.d("Firebase", "Estado de apertura actualizado a: $isOpen")
            }
            .addOnFailureListener { e ->
                android.util.Log.e("Firebase", "Error al actualizar estado de apertura: ${e.message}")
            }
    }
}