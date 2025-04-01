package com.daffa_34076492.nutritrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daffa_34076492.nutritrack.ui.theme.NutriTrack_Daffa_34076492Theme
import java.io.BufferedReader
import java.io.InputStreamReader


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriTrack_Daffa_34076492Theme {
                // Acts as a container that fills the available space and applies a background color
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    val context = LocalContext.current
    var userId by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Load CSV data
    val userData = loadCSVData(context)
    val userIds = userData.map { it["User_ID"] ?: "" }.distinct()

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
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Log in",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(24.dp))

            // User ID Field
            Text(
                text = "My ID (Provided by your Clinician)",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start)
            )

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
                        .padding(vertical = 8.dp)
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
                            text = { Text(id) },
                            onClick = {
                                userId = id
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number Field
            Text(
                text = "Phone number",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start)
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                placeholder = { Text("Enter your number", color = Color.LightGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Error Message
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Info Text
            Text(
                text = "This app is only for pre-registered users. Please have your ID and phone number handy before continuing.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Continue Button
            Button(
                onClick = {
                    // Validate credentials
                    val isValid = userData.any {
                        it["User_ID"] == userId && it["PhoneNumber"] == phoneNumber
                    }
                    if (isValid) {
                        // Pass userId to QuestionnaireActivity using Intent
                        val intent = Intent(context, QuestionnaireActivity::class.java).apply {
                            putExtra("userId", userId) // Pass the actual userId
                        }
                        context.startActivity(intent)
                    } else {
                        errorMessage = "Invalid User ID or Phone Number"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Continue",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontal line at bottom
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

/**
 * Loads and parses user nutrition data from a CSV file in the assets folder.
 * @param context Android context to access assets
 * @return List of user records, where each record is a map of field names to values
 */
fun loadCSVData(context: Context): List<Map<String, String>> {
    val userRecords = mutableListOf<Map<String, String>>()

    try {
        // 1. Open the CSV file from assets
        context.assets.open("data.csv").use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->

                // 2. Read and parse the header row containing column names
                val columnNames = reader.readLine().split(",")

                // 3. Process each subsequent data row
                reader.forEachLine { csvRow ->
                    // Split row into individual values
                    val rowValues = csvRow.split(",")

                    // Verify the row has the expected number of columns
                    if (rowValues.size == columnNames.size) {
                        // 4. Create key-value pairs by combining column names with row values
                        val fieldValuePairs = columnNames.zip(rowValues)

                        // 5. Convert pairs to a map and add to results
                        val userRecord = fieldValuePairs.toMap()
                        userRecords.add(userRecord)
                    } else {
                        // Log mismatch but continue processing
                        println("Skipping malformed row. Expected ${columnNames.size} columns, found ${rowValues.size}")
                    }
                }
            }
        }
    } catch (e: Exception) {
        // Handle file access or parsing errors
        println("Error loading nutrition data: ${e.message}")
        e.printStackTrace()
    }

    return userRecords
}