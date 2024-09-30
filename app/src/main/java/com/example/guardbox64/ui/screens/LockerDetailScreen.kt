package com.example.guardbox64.ui.screens

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guardbox64.R
import com.example.guardbox64.model.Locker
import com.example.guardbox64.utils.getLockerById
import kotlinx.coroutines.launch

@Composable
fun LockerDetailsScreen(navController: NavController, lockerId: String) {
    // Estado para almacenar el casillero
    var locker by remember { mutableStateOf<Locker?>(null) } // Estado del casillero

    // Efecto para cargar el casillero al entrar en la pantalla
    LaunchedEffect(lockerId) { // Se ejecuta cada vez que lockerId cambia
        locker = getLockerById(lockerId) // Carga el casillero de manera suspendida
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Detalles del Casillero", style = MaterialTheme.typography.titleLarge)

        // Mostrar un indicador de carga mientras se obtiene el casillero
        if (locker == null) {
            Text("Cargando...") // Mensaje de carga
        } else {
            // Imagen del casillero
            Image(
                painter = painterResource(id = R.drawable.lockericon), // Placeholder de imagen
                contentDescription = "Imagen del Casillero",
                modifier = Modifier.size(128.dp)
            )

            // Nombre del casillero
            Text(text = locker?.name ?: "Sin nombre", style = MaterialTheme.typography.titleMedium)

            // Estado del casillero (Ocupado o Libre)
            Text(
                text = if (locker?.isOccupied == true) "Estado: Ocupado" else "Estado: Libre",
                color = if (locker?.isOccupied == true) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )

            // Mostrar tiempo de reserva si está ocupado
            locker?.reservationEndTime?.let { endTime ->
                Text(text = "Reservado hasta: ${formatTime(endTime)}")
            }

            // Botón para reservar solo si está libre
            if (locker?.isOccupied != true) {
                Button(onClick = {
                    // Lógica para reservar el casillero
                    navController.navigate("reservation_confirmation/${locker?.id}")
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