package com.example.guardbox64.ui.screens

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.TextField
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockerDetailsScreen(
    navController: NavController,
    lockerId: String,
    lockerViewModel: LockerViewModel = viewModel()
) {
    val context = LocalContext.current
    val lockerList by lockerViewModel.lockers.observeAsState(emptyList())
    val locker = lockerList.find { it.id == lockerId }
    var showTimeDialog by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf<Long?>(null) }
    var showCustomTimeDialog by remember { mutableStateOf(false) }
    var showEndReservationDialog by remember { mutableStateOf(false) }
    var isCountdownActive by remember { mutableStateOf(false) }
    var countdownTime by remember { mutableStateOf(5) }
    var countdownJob: Job? by remember { mutableStateOf(null) }

    val isLoading = locker == null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título principal
        Text(
            text = "Detalles del Casillero",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 16.dp) // Espaciado inferior
        )

        if (isLoading) {
            Text(
                text = "Cargando...",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 18.sp
                ),
                modifier = Modifier.padding(vertical = 8.dp) // Espaciado vertical
            )
        } else if (locker == null) {
            Text(
                text = "Error al cargar los detalles del casillero.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(vertical = 8.dp) // Espaciado vertical
            )
        } else {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), shape = CircleShape) // Fondo blanco
                    .padding(24.dp) // Espaciado interno
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Imagen del Casillero",
                    modifier = Modifier
                        .size(200.dp) // Cambia el tamaño según lo que necesites
                        .clip(CircleShape) // Recorte en círculo
                )
            }

            Text(
                text = locker.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = if (locker.occupied) "Estado: Ocupado" else "Estado: Libre",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (locker.occupied) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic // Usa la referencia completa
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            locker.reservationEndTime?.let { endTime ->
                Text(
                    text = "Reservado hasta: ${formatTime(endTime)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 16.sp,
                        color = Color(0xFF757575),
                        fontWeight = FontWeight.Light
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }


            // Lógica del switch del casillero
            if (locker.occupied && locker.userId == FirebaseAuth.getInstance().currentUser?.uid) {
                var isOpen by remember { mutableStateOf(locker.open) }

                // Switch para abrir/cerrar el casillero
                Switch(
                    checked = isOpen,
                    onCheckedChange = { checked ->
                        isOpen = checked
                        lockerViewModel.updateLockerOpenState(lockerId, checked)
                    }
                )
                Text(
                    text = if (isOpen) "Casillero Abierto" else "Casillero Cerrado",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(vertical = 8.dp) // Espaciado vertical
                )
            } else if (locker.occupied) {
                Text(
                    text = "No puedes gestionar la apertura de este casillero.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(vertical = 8.dp) // Espaciado vertical
                )
            }

            if (!locker.occupied) {
                Button(
                    onClick = { showTimeDialog = true },  // Mostrar diálogo para seleccionar tiempo
                    modifier = Modifier
                        .padding(16.dp) // Margen alrededor del botón
                        .fillMaxWidth(), // Ocupa el ancho completo
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD6C215),  // Color de fondo del botón
                        contentColor = Color.White           // Color del texto dentro del botón
                    ),
                    shape = RoundedCornerShape(8.dp),       // Bordes redondeados
                    border = BorderStroke(2.dp, Color.Black) // Borde negro alrededor del botón
                ) {
                    Text(
                        text = "Reservar",
                        fontSize = 18.sp                    // Tamaño de fuente del texto
                    )
                }
            }

            if (locker != null) {
                // Verifica si el casillero está ocupado y pertenece al usuario actual
                if (locker.occupied
                    && locker.userId == FirebaseAuth.getInstance().currentUser?.uid
                    && locker.reservationEndTime != null
                    && locker.reservationEndTime > System.currentTimeMillis()
                ) {
                    // Botón para finalizar la reserva
                    Button(onClick = { showEndReservationDialog = true }) {
                        Text("Finalizar Reserva")
                    }

                    // Diálogo de confirmación
                    if (showEndReservationDialog) {
                        AlertDialog(
                            onDismissRequest = { showEndReservationDialog = false },
                            title = {
                                Text(
                                    text = "Has retirado tus pertenencias?",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            },
                            text = {
                                Text(
                                    text = "Confirma que has retirado tus pertenencias para finalizar la reserva.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        showEndReservationDialog = false
                                        isCountdownActive = true // Inicia la cuenta regresiva

                                        // Iniciar la cuenta regresiva
                                        countdownJob = CoroutineScope(Dispatchers.Main).launch {
                                            while (countdownTime > 0) {
                                                delay(1000L) // Espera 1 segundo
                                                countdownTime -= 1
                                            }
                                            // Finalizar la reserva cuando la cuenta regresiva termine
                                            lockerViewModel.endReservation(
                                                lockerId,
                                                onSuccess = {
                                                    // Actualizar el estado del casillero a cerrado
                                                    lockerViewModel.updateLockerOpenState(lockerId, isOpen = false) // Asegúrate de pasar 'isOpen' aquí
                                                    Toast.makeText(context, "Reserva finalizada", Toast.LENGTH_SHORT).show()
                                                },
                                                onFailure = { error ->
                                                    Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                                                }
                                            )
                                            isCountdownActive = false
                                        }
                                    }
                                ) {
                                    Text("Sí")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showEndReservationDialog = false }) {
                                    Text("No")
                                }
                            }
                        )
                    }

                    // Barra de cuenta regresiva y botón de cancelar
                    if (isCountdownActive) {
                        // Mostrar barra de progreso (de 5 segundos)
                        LinearProgressIndicator(
                            progress = (5 - countdownTime) / 5f,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Finalizando en $countdownTime segundos...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier.padding(vertical = 8.dp) // Espaciado vertical
                        )

                        // Botón para cancelar la finalización
                        Button(onClick = {
                            countdownJob?.cancel() // Cancelar la cuenta regresiva
                            countdownJob = null // Limpiar la referencia del Job
                            isCountdownActive = false
                            countdownTime = 5 // Restablecer el tiempo
                        }) {
                            Text("Cancelar Finalización")
                        }
                    }
                }
            }
        }




    }
        // Diálogo para seleccionar tiempo de reserva
        if (showTimeDialog) {
            AlertDialog(
                onDismissRequest = { showTimeDialog = false },
                title = { Text("Selecciona el tiempo de reserva") },
                text = {
                    Column {
                        // Botón para 1 hora
                        Button(onClick = {
                            val currentTime = System.currentTimeMillis()
                            selectedTime = 1 * 3600000L  // 1 hora
                            showTimeDialog = false  // Cerrar diálogo al seleccionar
                            val reservationEndTime =
                                currentTime + selectedTime!!  // Calcular el tiempo de fin de reserva

                            lockerViewModel.reserveLocker(
                                lockerId,
                                FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                reservationEndTime,
                                onSuccess = {
                                    Toast.makeText(context, "Reserva exitosa", Toast.LENGTH_SHORT)
                                        .show()
                                },
                                onFailure = { error ->
                                    Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )
                        }) {
                            Text("1 hora")
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Botón para 2 horas
                        Button(onClick = {
                            val currentTime = System.currentTimeMillis()
                            selectedTime = 2 * 3600000L  // 2 horas
                            showTimeDialog = false
                            val reservationEndTime = currentTime + selectedTime!!

                            lockerViewModel.reserveLocker(
                                lockerId,
                                FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                reservationEndTime,
                                onSuccess = {
                                    Toast.makeText(context, "Reserva exitosa", Toast.LENGTH_SHORT)
                                        .show()
                                },
                                onFailure = { error ->
                                    Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )
                        }) {
                            Text("2 horas")
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Botón para 5 horas
                        Button(onClick = {
                            val currentTime = System.currentTimeMillis()
                            selectedTime = 5 * 3600000L  // 5 horas
                            showTimeDialog = false
                            val reservationEndTime = currentTime + selectedTime!!

                            lockerViewModel.reserveLocker(
                                lockerId,
                                FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                reservationEndTime,
                                onSuccess = {
                                    Toast.makeText(context, "Reserva exitosa", Toast.LENGTH_SHORT)
                                        .show()
                                },
                                onFailure = { error ->
                                    Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )
                        }) {
                            Text("5 horas")
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Botón para 12 horas
                        Button(onClick = {
                            val currentTime = System.currentTimeMillis()
                            selectedTime = 12 * 3600000L  // 12 horas
                            showTimeDialog = false
                            val reservationEndTime = currentTime + selectedTime!!

                            lockerViewModel.reserveLocker(
                                lockerId,
                                FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                reservationEndTime,
                                onSuccess = {
                                    Toast.makeText(context, "Reserva exitosa", Toast.LENGTH_SHORT)
                                        .show()
                                },
                                onFailure = { error ->
                                    Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )
                        }) {
                            Text("12 horas")
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Botón para tiempo personalizado
                        Button(onClick = {
                            showTimeDialog = false  // Cerrar el diálogo principal
                            // Mostrar un cuadro de diálogo para ingresar tiempo personalizado
                            showCustomTimeDialog = true
                        }) {
                            Text("Tiempo personalizado")
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        showTimeDialog = false  // Cerrar diálogo al cancelar
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Diálogo para tiempo personalizado (implementa la lógica según tus necesidades)
        if (showCustomTimeDialog) {
            CustomTimeDialog(
                onTimeSelected = { customTimeInMillis -> // Recibe el tiempo total en milisegundos
                    selectedTime = customTimeInMillis // Usa el tiempo total
                    showCustomTimeDialog = false // Cerrar el cuadro de diálogo

                    val currentTime = System.currentTimeMillis()
                    val reservationEndTime = currentTime + selectedTime!!

                    lockerViewModel.reserveLocker(
                        lockerId,
                        FirebaseAuth.getInstance().currentUser?.uid ?: "",
                        reservationEndTime,
                        onSuccess = {
                            Toast.makeText(context, "Reserva exitosa", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = { error ->
                            Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                onCancel = {
                    showCustomTimeDialog = false // Cerrar el cuadro de diálogo
                }
            )
        }


    }


fun formatTime(timeInMillis: Long): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return dateFormat.format(timeInMillis)
}
@Composable
fun CustomTimeDialog(
    onTimeSelected: (Long) -> Unit, // Se espera un tiempo en horas
    onCancel: () -> Unit
) {
    var timeInput by remember { mutableStateOf("") }
    var minutesInput by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { onCancel() },
        title = { Text("Tiempo Personalizado") },
        text = {
            Column {
                Text("Ingresa el tiempo en horas:")
                TextField(
                    value = timeInput,
                    onValueChange = { timeInput = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    label = { Text("Horas") }
                )
                // Campo para minutos
                TextField(
                    value = minutesInput,
                    onValueChange = { minutesInput = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    label = { Text("Minutos") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Verificar si los inputs no están vacíos y son números válidos
                    val hours = timeInput.toIntOrNull() ?: 0
                    val minutes = minutesInput.toIntOrNull() ?: 0
                    val totalTimeInMillis = (hours * 3600000L) + (minutes * 60000L) // Convertir a milisegundos
                    onTimeSelected(totalTimeInMillis) // Devolver el tiempo total en milisegundos
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(onClick = { onCancel() }) {
                Text("Cancelar")
            }
        }
    )
}
