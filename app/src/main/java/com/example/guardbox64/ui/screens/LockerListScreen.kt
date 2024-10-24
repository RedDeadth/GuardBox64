package com.example.guardbox64.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.guardbox64.R
import com.example.guardbox64.model.Locker
import com.example.guardbox64.ui.viewmodel.LockerViewModel
import com.example.guardbox64.utils.AddLockerDialog
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.draw.clip

@Composable
fun LockerListScreen(
    lockerViewModel: LockerViewModel,
    navController: NavHostController,
    onAddLockerClick: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    val lockers by lockerViewModel.lockers.observeAsState(emptyList())
    val uniqueLockers = lockers.distinctBy { it.id }

    val reservedLockers = uniqueLockers.filter { it.occupied && it.userId == FirebaseAuth.getInstance().currentUser?.uid }
    val freeLockers = uniqueLockers.filter { !it.occupied }
    val occupiedLockers = uniqueLockers.filter { it.occupied && it.userId != FirebaseAuth.getInstance().currentUser?.uid }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) { // Padding general
        // Sección de casilleros reservados
        if (reservedLockers.isNotEmpty()) {
            Text(
                text = "Tus Casilleros Reservados",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            LazyRow(
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reservedLockers) { locker ->
                    LockerItem(locker = locker) {
                        navController.navigate("locker_details/${locker.id}")
                    }
                }
            }
        }

        // Sección de casilleros libres
        if (freeLockers.isNotEmpty()) {
            Text(
                text = "Casilleros Libres",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(freeLockers) { locker ->
                    LockerItem(locker = locker) {
                        navController.navigate("locker_details/${locker.id}")
                    }
                }
            }
        }

        // Sección de casilleros ocupados
        if (occupiedLockers.isNotEmpty()) {
            Text(
                text = "Casilleros Ocupados",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(occupiedLockers) { locker ->
                    LockerItem(locker = locker) {
                        navController.navigate("locker_details/${locker.id}")
                    }
                }
            }
        }

        if (uniqueLockers.isEmpty()) {
            Text(
                text = "No hay casilleros disponibles.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
        ) {
            Text("Añadir Casillero", style = MaterialTheme.typography.bodyLarge)
        }

        if (showDialog) {
            AddLockerDialog(
                onAdd = { newLocker ->
                    lockerViewModel.addLocker(newLocker)
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}

@Composable
fun LockerItem(locker: Locker, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícono del casillero en un círculo negro
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), shape = CircleShape) // Fondo blanco
                .padding(8.dp) // Espaciado interno
        ) {
            Image(
                painter = painterResource(id = R.drawable.lockericon),
                contentDescription = "Imagen del Casillero",
                modifier = Modifier.fillMaxSize().clip(CircleShape) // Recorte en círculo
            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = locker.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = if (locker.occupied) "Ocupado" else "Libre",
                color = if (locker.occupied) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
