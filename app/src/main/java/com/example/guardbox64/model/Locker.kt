package com.example.guardbox64.model

data class Locker(
    val id: String = "",
    val name: String = "",
    val occupied: Boolean = false,
    val open: Boolean = false,
    val userId: String = "",
    val reservationEndTime: Long? = null,
    val sharedWith: List<String> = emptyList()
)
