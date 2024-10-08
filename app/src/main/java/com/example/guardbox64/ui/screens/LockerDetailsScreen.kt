package com.example.guardbox64.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.guardbox64.R
import com.example.guardbox64.model.Locker
import com.example.guardbox64.ui.viewmodel.LockerViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LockerDetailsScreen(
    navController: NavController,
    lockerId: String,
    lockerViewModel: LockerViewModel = viewModel()
) {
    val context = LocalContext.current
    val lockerList by lockerViewModel.lockers.observeAsState(emptyList()) // Observa cambios en LiveData
    val locker = lockerList.find { it.id == lockerId } // Encuentra el casillero específico
    val isLoading = locker == null // Cargando si no hay casillero encontrado

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Detalles del Casillero", style = MaterialTheme.typography.titleLarge)

        // Mostrar un indicador de carga mientras se obtiene el casillero
        if (isLoading) {
            Text("Cargando...") // Mensaje de carga
        } else if (locker == null) {
            Text("Error al cargar los detalles del casillero.") // Mensaje de error
        } else {
            // Imagen del casillero
            Image(
                painter = painterResource(id = R.drawable.lockericon), // Placeholder de imagen
                contentDescription = "Imagen del Casillero",
                modifier = Modifier.size(128.dp)
            )

            // Nombre del casillero
            Text(text = locker.name, style = MaterialTheme.typography.titleMedium)

            // Estado del casillero (Ocupado o Libre)
            Text(
                text = if (locker.occupied) "Estado: Ocupado" else "Estado: Libre",
                color = if (locker.occupied) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )

            // Mostrar tiempo de reserva si está ocupado
            locker.reservationEndTime?.let { endTime ->
                Text(text = "Reservado hasta: ${formatTime(endTime)}")
            }

            // Botón para reservar solo si está libre
            if (!locker.occupied) {
                Button(onClick = {
                    lockerViewModel.reserveLocker(
                        lockerId,
                        FirebaseAuth.getInstance().currentUser?.uid ?: "",
                        onSuccess = {
                            Toast.makeText(context, "Casillero reservado exitosamente", Toast.LENGTH_SHORT).show()
                            navController.navigate("locker_list") // Navegar a la lista de casilleros
                        },
                        onFailure = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show() // Mostrar error
                        }
                    )
                }) {
                    Text("Reservar")
                }
            }
        }
    }
}

// Función para formatear la hora de fin de reserva
fun formatTime(timeInMillis: Long): String {
    // Puedes usar SimpleDateFormat o cualquier otra forma para formatear el tiempo
    return java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(timeInMillis)
}