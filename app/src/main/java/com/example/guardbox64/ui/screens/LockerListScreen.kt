package com.example.guardbox64.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.guardbox64.ui.viewmodel.AuthViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guardbox64.R
import com.example.guardbox64.model.Locker

@Composable
fun LockerListScreen(navController: NavController, lockerList: List<Locker>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Lista de Casilleros", style = MaterialTheme.typography.titleLarge)

        // Iteramos sobre la lista de casilleros y mostramos cada uno
        lockerList.forEach { locker ->
            LockerItem(locker = locker, onClick = {
                navController.navigate("locker_details/${locker.id}")
            })
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
        // Imagen del casillero (aquí puedes reemplazar con una imagen adecuada)
        Image(
            painter = painterResource(id = R.drawable.lockericon), // Placeholder de casillero
            contentDescription = "Imagen del Casillero",
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        // Nombre del casillero y su estado (Ocupado o Libre)
        Column {
            Text(text = locker.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = if (locker.isOccupied) "Ocupado" else "Libre",
                color = if (locker.isOccupied) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }
    }
}
