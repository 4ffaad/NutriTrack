package com.daffa_34076492.nutritrack

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.daffa_34076492.nutritrack.ViewModels.PatientViewModel
import com.daffa_34076492.nutritrack.ui.theme.NutriTrack_Daffa_34076492Theme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NutriTrack_Daffa_34076492Theme {
                val viewModel: PatientViewModel = ViewModelProvider(
                    this,
                    PatientViewModel.PatientViewModelFactory(this@RegisterActivity)
                )[PatientViewModel::class.java]

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegisterScreen(
                        patientViewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun RegisterScreen(patientViewModel: PatientViewModel, modifier: Modifier) {
    var userId by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var successMessage by rememberSaveable { mutableStateOf("") }
    var alreadyRegistered by rememberSaveable { mutableStateOf<Boolean?>(null) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Header ---
            Text("Register", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))


            OutlinedTextField(
                value = userId,
                onValueChange = {
                    userId = it
                    val idInt = it.toIntOrNull()
                    if (idInt != null) {
                        patientViewModel.checkIfPasswordSet(idInt) { isSet ->
                            alreadyRegistered = isSet
                        }
                    } else {
                        alreadyRegistered = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("My ID (Provided by your Clinician)") },
                shape = RoundedCornerShape(16.dp)
            )

            if (alreadyRegistered == true) {
                Text(
                    text = "This user has already registered.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- PHONE, PASSWORD, CONFIRM PASSWORD (unchanged) ---
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Phone Number") },
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                shape = RoundedCornerShape(16.dp),
                enabled = alreadyRegistered != true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Confirm password") },
                shape = RoundedCornerShape(16.dp),
                enabled = alreadyRegistered != true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Error / Success messages ---
            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
            if (successMessage.isNotEmpty()) {
                Text(
                    text = successMessage,
                    color = Green,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // --- REGISTER BUTTON ---
            Button(
                onClick = {
                    val idInt = userId.toIntOrNull()
                    if (idInt == null) {
                        errorMessage = "Invalid user ID"
                        return@Button
                    }

                    // ✅ Prevent duplicate registration
                    if (alreadyRegistered == true) {
                        errorMessage = "This user has already registered"
                        successMessage = ""
                        return@Button
                    }

                    // Validate password match
                    errorMessage = patientViewModel.validatePassword(password, confirmPassword)
                    if (errorMessage.isNotEmpty()) {
                        return@Button
                    }

                    // Register
                    patientViewModel.registerUser(
                        userId = idInt,
                        phone = phoneNumber,
                        password = password
                    ) { success ->
                        if (success) {
                            successMessage = "Registration complete."
                            errorMessage = ""
                        } else {
                            errorMessage = "User ID not found or update failed"
                            successMessage = ""
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- BACK TO LOGIN BUTTON ---
            Button(
                onClick = {
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    if (context is Activity) context.finish()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Login", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}


