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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guardbox64.R
import com.example.guardbox64.utils.getLockerById

@Composable
fun LockerDetailsScreen(navController: NavController, lockerId: String) {
    // Suponiendo que tienes un método para obtener el casillero por su ID
    val locker = getLockerById(lockerId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Detalles del Casillero", style = MaterialTheme.typography.titleLarge)

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
            text = if (locker.isOccupied) "Estado: Ocupado" else "Estado: Libre",
            color = if (locker.isOccupied) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )

        // Mostrar tiempo de reserva si está ocupado
        locker.reservationEndTime?.let { endTime ->
            Text(text = "Reservado hasta: ${formatTime(endTime)}")
        }

        // Botón para reservar solo si está libre
        if (!locker.isOccupied) {
            Button(onClick = {
                // Lógica para reservar el casillero
                navController.navigate("reservation_confirmation/${locker.id}")
            }) {
                Text("Reservar")
            }
        }
    }
}

// Función para formatear la hora de fin de reserva
fun formatTime(timeInMillis: Long): String {
    // Puedes usar SimpleDateFormat o cualquier otra forma para formatear el tiempo
    return java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(timeInMillis)
}
