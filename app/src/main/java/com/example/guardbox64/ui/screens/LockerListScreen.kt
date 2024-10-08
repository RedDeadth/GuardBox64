package com.example.guardbox64.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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

@Composable
fun LockerListScreen(
    lockerViewModel: LockerViewModel,
    navController: NavHostController,
    onAddLockerClick: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    // Observa los cambios en lockers
    val lockers by lockerViewModel.lockers.observeAsState(emptyList())
    val uniqueLockers = lockers.distinctBy { it.id }

    // Filtrar los casilleros en tres categorías
    val reservedLockers = uniqueLockers.filter { it.occupied && it.userId == FirebaseAuth.getInstance().currentUser?.uid }
    val freeLockers = uniqueLockers.filter { !it.occupied }
    val occupiedLockers = uniqueLockers.filter { it.occupied && it.userId != FirebaseAuth.getInstance().currentUser?.uid }

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

        if (uniqueLockers.isEmpty()) {
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
@Composable
fun LockerItem(locker: Locker, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen del casillero
        Image(
            painter = painterResource(id = R.drawable.lockericon), // Reemplaza con tu imagen
            contentDescription = "Imagen del Casillero",
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        // Nombre del casillero y su estado (Ocupado o Libre)
        Column {
            Text(text = locker.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = if (locker.occupied) "Ocupado" else "Libre",
                color = if (locker.occupied) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )


        }
    }
}
