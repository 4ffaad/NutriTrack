package com.daffa_34076492.nutritrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.daffa_34076492.nutritrack.ui.theme.NutriTrack_Daffa_34076492Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NutriTrack_Daffa_34076492Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Navigate to WelcomeActivity after setup
                    val intent = Intent(this, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

}