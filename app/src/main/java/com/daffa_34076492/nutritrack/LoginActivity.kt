package com.daffa_34076492.nutritrack

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daffa_34076492.nutritrack.ui.theme.NutriTrack_Daffa_34076492Theme
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.daffa_34076492.nutritrack.ViewModels.PatientViewModel
import com.daffa_34076492.nutritrack.auth.AuthManager
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NutriTrack_Daffa_34076492Theme {
                val viewModel: PatientViewModel = ViewModelProvider(
                    this,
                    PatientViewModel.PatientViewModelFactory(this@LoginActivity)
                )[PatientViewModel::class.java]

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(innerPadding, viewModel)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(paddingValues: PaddingValues, patientViewModel: PatientViewModel) {
    var userId by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val userIds by patientViewModel.userIds.observeAsState(emptyList())
    val context = LocalContext.current
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp, start = 24.dp, end = 24.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    )
 {
        item {
            Text(
                text = "Log in",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text(
                text = "My ID (Provided by your Clinician)",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = userId,
                    onValueChange = { userId = it },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    userIds.forEach { id ->
                        DropdownMenuItem(
                            text = { Text(id.toString()) },
                            onClick = {
                                userId = id.toString()
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Password",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Enter your password", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = description)
                    }
                }
            )
        }

        item {
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            Text(
                text = "This app is only for pre-registered users. Please have your ID and phone number handy before continuing.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
     item {
         val context = LocalContext.current
         ForgotPasswordText {
             val intent = Intent(context, PasswordResetActivity::class.java)
             context.startActivity(intent)
         }

     }


        item {
            Button(
                onClick = {
                    val idInt = userId.toIntOrNull()
                    if (idInt != null) {
                        patientViewModel.verifyLogin(idInt, password) { success ->
                            if (success) {
                                errorMessage = ""
                                AuthManager.login(idInt)

                                val intent = Intent(context, QuestionnaireActivity::class.java)
                                context.startActivity(intent)

                                if (context is Activity) {
                                    context.finish()
                                }
                            } else {
                                errorMessage = "Invalid user ID or password"
                            }
                        }
                    } else {
                        errorMessage = "Please select a valid user ID."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Log In",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        item {
            Button(
                onClick = {
                    val intent = Intent(context, RegisterActivity::class.java)
                    context.startActivity(intent)
                    if (context is Activity) {
                        context.finish()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Register",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        item {
            Divider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun ForgotPasswordText(onClick: () -> Unit) {
    val annotatedString = buildAnnotatedString {
        append("Forgot your password? ")
        pushStringAnnotation(tag = "RESET", annotation = "reset")
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)) {
            append("Press here")
        }
        pop()
    }

    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "RESET", start = offset, end = offset)
                .firstOrNull()?.let { _ ->
                    onClick()
                }
        },
        style = MaterialTheme.typography.bodyMedium
    )
}

