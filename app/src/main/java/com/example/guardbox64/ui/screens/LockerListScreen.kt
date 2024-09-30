package com.example.guardbox64.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.guardbox64.model.Locker
import com.example.guardbox64.ui.viewmodel.LockerViewModel
import com.example.guardbox64.utils.AddLockerDialog
import com.example.guardbox64.utils.LockerItem
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LockerListScreen(
    lockerViewModel: LockerViewModel,
    navController: NavHostController,
    lockers: List<Locker>,
    onAddLockerClick: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    val lockers by lockerViewModel.lockers // Observa los cambios en lockers

    // Filtrar los casilleros en tres categorías
    val reservedLockers = lockers.filter { it.isOccupied && it.userId == FirebaseAuth.getInstance().currentUser?.uid }
    val freeLockers = lockers.filter { !it.isOccupied }
    val occupiedLockers = lockers.filter { it.isOccupied && it.userId != FirebaseAuth.getInstance().currentUser?.uid }

    Column(modifier = Modifier.fillMaxSize()) {
        // Mostrar los casilleros reservados en un carrusel
        if (reservedLockers.isNotEmpty()) {
            Text(
                text = "Tus Casilleros Reservados",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reservedLockers) { locker ->
                    LockerItem(locker = locker) {
                        navController.navigate("locker_details/${locker.id}")
                    }
                }
            }
        }

        // Mostrar los casilleros libres
        if (freeLockers.isNotEmpty()) {
            Text(
                text = "Casilleros Libres",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(freeLockers) { locker ->
                    LockerItem(locker = locker) {
                        navController.navigate("locker_details/${locker.id}")
                    }
                }
            }
        }

        // Mostrar los casilleros ocupados
        if (occupiedLockers.isNotEmpty()) {
            Text(
                text = "Casilleros Ocupados",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(occupiedLockers) { locker ->
                    LockerItem(locker = locker) {
                        navController.navigate("locker_details/${locker.id}")
                    }
                }
            }
        }

        if (lockers.isEmpty()) {
            Text(
                text = "No hay casilleros disponibles.",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre los elementos y el botón
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir Casillero")
        }

        // Mostrar diálogo para agregar casillero
        if (showDialog) {
            AddLockerDialog(
                onAdd = { newLocker ->
                    lockerViewModel.addLocker(newLocker) // Lógica para agregar un nuevo casillero
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}
