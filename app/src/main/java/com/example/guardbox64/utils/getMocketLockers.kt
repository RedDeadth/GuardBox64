package com.example.guardbox64.utils

import com.example.guardbox64.model.Locker

fun getMockedLockers(): List<Locker> {
    return listOf(
        Locker(id = "1", name = "Casillero 1", isOccupied = false, isOpen = false),
        Locker(id = "2", name = "Casillero 2", isOccupied = true, isOpen = false, reservationEndTime = System.currentTimeMillis() + 3600000),
        Locker(id = "3", name = "Casillero 3", isOccupied = false, isOpen = false)
    )
}
