package com.example.guardbox64.model

data class Locker(
    val id: String = "",
    val name: String = "",
    val isOccupied: Boolean = false,
    val isOpen: Boolean = false,
    val userId: String = "",
    val reservationEndTime: Long? = null
)
