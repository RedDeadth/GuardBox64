package com.example.guardbox64.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardbox64.model.Locker
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class LockerViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _lockers = MutableStateFlow<List<Locker>>(emptyList())
    val lockers: StateFlow<List<Locker>> get() = _lockers

    init {
        fetchLockers()
    }

    fun fetchLockers() {
        viewModelScope.launch {
            db.collection("lockers").get().addOnSuccessListener { result ->
                val lockerList = result.map { it.toObject(Locker::class.java) }
                _lockers.value = lockerList
            }
        }
    }

    fun toggleLocker(locker: Locker) {
        val updatedLocker = locker.copy(isOpen = !locker.isOpen)
        db.collection("lockers").document(locker.id).set(updatedLocker)
        fetchLockers()  // Recargar la lista
    }
}
