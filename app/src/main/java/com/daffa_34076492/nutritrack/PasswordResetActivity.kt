package com.daffa_34076492.nutritrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.daffa_34076492.nutritrack.ViewModels.PatientViewModel
import com.daffa_34076492.nutritrack.ui.theme.NutriTrack_Daffa_34076492Theme
import kotlinx.coroutines.delay
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import kotlinx.coroutines.launch

class PasswordResetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelFactory = PatientViewModel.PatientViewModelFactory(applicationContext)
        val viewModel = ViewModelProvider(this, viewModelFactory)[PatientViewModel::class.java]

        setContent {
            NutriTrack_Daffa_34076492Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PasswordResetScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding),
                        onResetSuccess = {
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }
}



@Composable
fun PasswordResetScreen(
    viewModel: PatientViewModel,
    modifier: Modifier = Modifier,
    onResetSuccess: () -> Unit
) {
    val userId by viewModel.userIdInput
    val phoneNumber by viewModel.phoneNumberInput
    val newPassword by viewModel.newPasswordInput
    val confirmPassword by viewModel.confirmPasswordInput
    val resetMessage by viewModel.resetMessage

    // Password visibility state
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Image(
                painter = painterResource(id = R.drawable.nutritrack_logo),
                contentDescription = "NutriTrack Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 8.dp)
            )
        }
        item {
            Text(
                text = "NutriTrack",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        item {
            Text(
                text = "Reset Password",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = userId,
                        onValueChange = viewModel::onUserIdChange,
                        label = { Text("User ID") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = viewModel::onPhoneNumberChange,
                        label = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = viewModel::onNewPasswordChange,
                        label = { Text("New Password") },
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (newPasswordVisible)
                                Icons.Default.Visibility
                            else Icons.Default.VisibilityOff
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(imageVector = image, contentDescription = if (newPasswordVisible) "Hide password" else "Show password")
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        label = { Text("Confirm Password") },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (confirmPasswordVisible)
                                Icons.Default.Visibility
                            else Icons.Default.VisibilityOff
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(imageVector = image, contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password")
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        item {
            Button(
                onClick = {
                    scope.launch {
                        val success = viewModel.resetPassword()
                        if (success) {
                            delay(2000)  // Show success message for 2 seconds
                            onResetSuccess()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = MaterialTheme.shapes.large,
                        clip = false
                    ),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Reset Password")
            }
        }
        item {
            resetMessage?.let { msg ->
                Text(
                    text = msg,
                    color = if (msg.contains("success", ignoreCase = true)) Color(0xFF4CAF50) else Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

