package com.example.guardbox64.utils

import com.example.guardbox64.model.Locker

fun getLockerById(lockerId: String): Locker {
    val mockLockers = getMockedLockers()
    return mockLockers.first { it.id == lockerId }
}
