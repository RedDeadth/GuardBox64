package com.example.guardbox64.ui.viewmodel

import android.os.Looper
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
import android.os.Handler
import androidx.compose.runtime.remember
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue

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
        reservationEndTime: Long?,  // Cambiar Long a Long?
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {


        val lockerRef = database.child(lockerId)
        val updates = mapOf<String, Any?>(  // Cambiar Any a Any?
            "occupied" to true,
            "userId" to userId,
            "reservationEndTime" to reservationEndTime // Ahora puede ser null
        )

        // Actualiza los campos ocupados y userId en una sola llamada
        lockerRef.updateChildren(updates)
            .addOnSuccessListener {
                if (reservationEndTime != null) {  // Solo iniciar el temporizador si reservationEndTime no es null
                    startReservationTimer(lockerId, reservationEndTime)  // Agrega esto
                }
                onSuccess() // Notificar éxito
            }
            .addOnFailureListener { e ->
                onFailure("Error reservando el casillero: ${e.message}")
            }
    }


    private fun loadLockers() {
        val lockersRef = FirebaseDatabase.getInstance().getReference("lockers")
        lockersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lockerList = mutableListOf<Locker>()
                val lockerIds = mutableSetOf<String>()
                val currentTime = System.currentTimeMillis()

                for (lockerSnapshot in snapshot.children) {
                    val locker = lockerSnapshot.getValue(Locker::class.java)
                        ?.copy(id = lockerSnapshot.key ?: "")

                    if (locker != null && locker.id !in lockerIds) {
                        // Verifica si la reserva ha expirado
                        if (locker.reservationEndTime != null && locker.reservationEndTime <= currentTime) {
                            // Si la reserva ha expirado, actualiza el casillero a no ocupado
                            val lockerRef = database.child(locker.id)
                            val updates = mapOf<String, Any?>(
                                "occupied" to false,
                                "userId" to "",
                                "reservationEndTime" to null
                            )
                            lockerRef.updateChildren(updates)
                        }

                        // Agregar el casillero a la lista
                        lockerList.add(locker)
                        lockerIds.add(locker.id)
                    }
                }

                // Actualizar el LiveData con los casilleros cargados (incluyendo los expirados)
                _lockers.value = lockerList
                android.util.Log.d("Firebase", "Casilleros cargados correctamente.")
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("Firebase", "Error al cargar casilleros: ${error.message}")
                _lockers.value = emptyList() // Retornar lista vacía en caso de error
            }
        })
    }


    fun updateLockerOpenState(lockerId: String, isOpen: Boolean) {
        val lockerRef = database.child(lockerId)
        lockerRef.child("open").setValue(isOpen)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    android.util.Log.d("Firebase", "Estado de apertura actualizado a: $isOpen")
                    // Aquí podrías notificar a través de LiveData si es necesario
                } else {
                    android.util.Log.e("Firebase", "Error al actualizar estado de apertura: ${task.exception?.message}")
                    // Maneja el error de forma adecuada, como notificar al usuario
                }
            }
    }
    private fun startReservationTimer(lockerId: String, reservationEndTime: Long) {
        val currentTime = System.currentTimeMillis()

        if (currentTime < reservationEndTime) {
            val delayInMillis = reservationEndTime - currentTime
            Handler(Looper.getMainLooper()).postDelayed({
                expireReservation(lockerId)  // Expirar la reserva cuando el tiempo finaliza
            }, delayInMillis)
        }
    }
    private fun expireReservation(lockerId: String) {
        val lockerRef = database.child(lockerId)
        val updates = mapOf<String, Any>(
            "occupied" to false,
            "userId" to "",


        )

        lockerRef.updateChildren(updates)
            .addOnSuccessListener {
                android.util.Log.d("Firebase", "Reserva del casillero expirada correctamente.")
            }
            .addOnFailureListener { e:Exception ->
                android.util.Log.e("Firebase", "Error al expirar reserva del casillero: ${e.message}")
            }
    }
    fun endReservation(lockerId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val lockerRef = database.child(lockerId)
        val updates = mapOf<String, Any>(
            "occupied" to false,
            "userId" to "",
        )

        lockerRef.updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Error desconocido") }
    }

    fun addUserToLocker(lockerId: String, userId: String) {
        val lockerRef = FirebaseDatabase.getInstance().getReference("lockers/$lockerId")

        lockerRef.child("sharedWith").get().addOnSuccessListener { snapshot ->
            val currentSharedWith = snapshot.getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()
            if (!currentSharedWith.contains(userId)) {
                val updatedSharedWith = currentSharedWith + userId
                lockerRef.child("sharedWith").setValue(updatedSharedWith)
                    .addOnSuccessListener {
                        // Opcional: Manejar el éxito, como mostrar un mensaje
                    }
                    .addOnFailureListener { error ->
                        // Opcional: Manejar el error, como mostrar un mensaje
                    }
            } else {
                // El usuario ya está compartido, manejar esto si es necesario
            }
        }
    }



}


