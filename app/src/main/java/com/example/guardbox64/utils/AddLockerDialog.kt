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
fun AddLockerDialog(
    onAdd: (Locker) -> Unit,
    onDismiss: () -> Unit
) {
    var lockerName by remember { mutableStateOf("") }
    var lockerId by remember { mutableStateOf("") }
    var isOccupied by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Casillero") },
        text = {
            Column {
                TextField(
                    value = lockerId,
                    onValueChange = { lockerId = it },
                    label = { Text("ID del Casillero") }
                )
                TextField(
                    value = lockerName,
                    onValueChange = { lockerName = it },
                    label = { Text("Nombre del Casillero") }
                )
                // Aquí puedes agregar más campos si es necesario
                // Por ejemplo, para indicar si el casillero está ocupado
                // TextField(...) para más información
            }
        },
        confirmButton = {
            Button(onClick = {
                val newLocker = Locker(name = lockerName, isOccupied = isOccupied)
                onAdd(newLocker)
                onDismiss()
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
