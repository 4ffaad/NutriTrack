package com.daffa_34076492.nutritrack.auth

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

object AuthManager {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_USER_ID = "key_user_id"

    private lateinit var prefs: android.content.SharedPreferences

    private var _userId by mutableStateOf(0) // default to 0 = logged out
    val userId: Int
        get() = _userId

    // Call this once from your Application or first Activity
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _userId = prefs.getInt(KEY_USER_ID, 0) // load saved userId, default 0
    }

    fun login(userId: Int) {
        _userId = userId
        prefs.edit().putInt(KEY_USER_ID, userId).apply() // save persistently
    }

    fun logout() {
        _userId = 0
        prefs.edit().remove(KEY_USER_ID).apply()
    }
}
