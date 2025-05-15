package com.daffa_34076492.nutritrack.auth

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

object AuthManager {
    private var _userId by mutableStateOf(0) // default to invalid ID (e.g., 0)
    val userId: Int
        get() = _userId

    fun login(userId: Int) {
        _userId = userId
    }

    fun logout() {
        _userId = 0 // or any sentinel value like -1 if 0 is valid
    }
}

