package com.example.guardbox64.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.guardbox64.R
import com.example.guardbox64.model.Locker
import com.example.guardbox64.ui.screens.formatTime

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

            // Mostrar la fecha de fin de reserva, si existe
            locker.reservationEndTime?.let { endTime ->
                Text(text = "Reservado hasta: ${formatTime(endTime)}")
            }
        }
    }
}
