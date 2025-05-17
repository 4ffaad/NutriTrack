package com.daffa_34076492.nutritrack

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.daffa_34076492.nutritrack.ViewModels.PatientViewModel
import androidx.compose.material.icons.Icons
import com.daffa_34076492.nutritrack.ui.theme.NutriTrack_Daffa_34076492Theme
import androidx.compose.material3.Icon


/**
 * RegisterActivity shows the user registration screen.
 * It sets up the ViewModel and theme and hosts the RegisterScreen composable.
 */
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

/**
 * Composable that shows the registration form with fields for user ID,
 * phone number, full name, password, and confirm password.
 * It handles input state, validation, and interaction with the ViewModel.
 *
 * @param patientViewModel The ViewModel responsible for patient data handling.
 * @param modifier Modifier for styling.
 */
@Composable
fun RegisterScreen(patientViewModel: PatientViewModel, modifier: Modifier = Modifier) {
    var userId by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var successMessage by rememberSaveable { mutableStateOf("") }
    var alreadyRegistered by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var name by rememberSaveable { mutableStateOf("") }

    // Password visibility states
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 40.dp, start = 24.dp, end = 24.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text("Register", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            // User ID input with validation if already registered
            item {
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
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // Show message if already registered
            if (alreadyRegistered == true) {
                item {
                    Text(
                        text = "This user has already registered.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Phone number input
            item {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Phone Number") },
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }

            // Full name input
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Full Name") },
                    shape = RoundedCornerShape(16.dp)
                )
            }

            // Password input with toggle visibility
            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    shape = RoundedCornerShape(16.dp),
                    enabled = alreadyRegistered != true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }

            // Confirm password input with toggle visibility
            item {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Confirm password") },
                    shape = RoundedCornerShape(16.dp),
                    enabled = alreadyRegistered != true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }

            // Show error message if any
            if (errorMessage.isNotEmpty()) {
                item {
                    Text(errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxWidth())
                }
            }

            // Show success message if any
            if (successMessage.isNotEmpty()) {
                item {
                    Text(
                        text = successMessage,
                        color = Green,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }

            // Register button
            item {
                Button(
                    onClick = {
                        val idInt = userId.toIntOrNull()
                        if (idInt == null) {
                            errorMessage = "Invalid user ID"
                            successMessage = ""
                            return@Button
                        }

                        if (alreadyRegistered == true) {
                            errorMessage = "This user has already registered"
                            successMessage = ""
                            return@Button
                        }

                        errorMessage = patientViewModel.validatePassword(password, confirmPassword)
                        if (errorMessage.isNotEmpty()) {
                            successMessage = ""
                            return@Button
                        }

                        patientViewModel.registerUser(
                            userId = idInt,
                            phone = phoneNumber,
                            password = password,
                            name = name
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
            }

            // Back to login button
            item {
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
}
