package com.daffa_34076492.nutritrack.workers

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AuthManager {
    private lateinit var prefs: SharedPreferences
    private const val KEY_USER_ID = "user_id"

    private var _userId by mutableStateOf(0)
    val userId: Int
        get() = _userId

    fun init(context: Context) {
        prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        _userId = prefs.getInt(KEY_USER_ID, 0) // Load saved user ID on init
    }

    fun login(userId: Int) {
        _userId = userId
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun logout() {
        _userId = 0
        prefs.edit().remove(KEY_USER_ID).apply()
    }

    fun isLoggedIn() = _userId != 0
}