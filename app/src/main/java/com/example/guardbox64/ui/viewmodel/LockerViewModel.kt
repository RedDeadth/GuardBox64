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



class LockerViewModel : ViewModel() {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("lockers")

    var lockers = mutableStateOf<List<Locker>>(emptyList())
        private set

    init {
        loadLockers()
    }
    fun reserveLocker(lockerId: String, userId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val lockerRef = FirebaseDatabase.getInstance().getReference("lockers/$lockerId")
        lockerRef.child("Occupied").setValue(true)
            .addOnSuccessListener {
                lockerRef.child("userId").setValue(userId)
                onSuccess()
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
                    val locker = lockerSnapshot.getValue(Locker::class.java)?.copy(id = lockerSnapshot.key ?: "")
                    locker?.let { lockerList.add(it) }
                }
                lockers.value = lockerList // Actualizar la lista de casilleros
                android.util.Log.d("Firebase", "Casilleros cargados correctamente.")
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("Firebase", "Error al cargar casilleros: ${error.message}")
                lockers.value = emptyList() // Retornar lista vacía en caso de error
            }
        })
    }

    fun addLocker(locker: Locker) {
        val newLockerRef = database.push() // Crea una nueva referencia en la base de datos
        val newLocker = locker.copy(id = newLockerRef.key ?: "") // Usa la clave generada
        newLockerRef.setValue(newLocker)
            .addOnSuccessListener {
                val updatedList = lockers.value.toMutableList()
                updatedList.add(newLocker)
                lockers.value = updatedList
                android.util.Log.d("RealtimeDatabase", "Casillero añadido correctamente con ID: ${newLocker.id}")
            }
            .addOnFailureListener { e ->
                android.util.Log.e("RealtimeDatabase", "Error al añadir casillero: ", e)
            }
    }
}
