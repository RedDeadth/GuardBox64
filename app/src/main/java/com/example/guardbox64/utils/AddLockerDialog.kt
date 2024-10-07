package com.example.guardbox64.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.guardbox64.model.Locker

@Composable
fun AddLockerDialog(onAdd: (Locker) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Nuevo Casillero") },
        text = {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del Casillero") }
            )
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank()) {
                    val newLocker = Locker(
                        name = name,
                        occupied = false,
                        open = false,
                        userId = "",
                        reservationEndTime = null
                    )
                    onAdd(newLocker)
                }
            }) {
                Text("Añadir")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}