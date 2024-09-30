package com.example.guardbox64.utils

import com.example.guardbox64.model.Locker
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Función suspendida para obtener un casillero por su ID
suspend fun getLockerById(lockerId: String): Locker? {
    val firestore = FirebaseFirestore.getInstance()
    return try {
        // Acceder a la colección "lockers" y buscar el documento con el ID específico
        val documentSnapshot = firestore.collection("lockers").document(lockerId).get().await()

        // Verificar si el documento existe
        if (documentSnapshot.exists()) {
            // Convertir el documento a un objeto Locker
            documentSnapshot.toObject(Locker::class.java)
        } else {
            null // Retornar null si el casillero no existe
        }
    } catch (e: Exception) {
        // Manejo de errores
        null
    }
}
