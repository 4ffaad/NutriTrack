package com.daffa_34076492.nutritrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.daffa_34076492.nutritrack.workers.AuthManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AuthManager.init(this)
        val targetActivity = if (AuthManager.isLoggedIn()) {
            HomeActivity::class.java
        } else {
            WelcomeActivity::class.java
        }
        startActivity(Intent(this, targetActivity))
        finish()
    }
}


