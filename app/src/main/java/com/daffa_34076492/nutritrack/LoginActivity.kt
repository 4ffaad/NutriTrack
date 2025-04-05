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
import androidx.core.content.edit


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

    // Load user IDs and phone numbers for dropdown
    val userIdPhonePairs by remember {
        derivedStateOf { loadUserIdsWithPhoneNumbers(context) }
    }
    val userIds = userIdPhonePairs.map { it.first }.distinct()

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

            Button(
                onClick = {
                    val user = findUser(context, userId, phoneNumber)
                    if (user != null) {
                        // Save userId in SharedPreferences within "Questionnaire.xml"
                        val sharedPref = context.getSharedPreferences("Questionnaire", Context.MODE_PRIVATE)
                        sharedPref.edit{
                            putString("userId", userId)
                        }
                        val intent = Intent(context, QuestionnaireActivity::class.java).apply {}
                        context.startActivity(intent)
                    } else {

                        errorMessage = "Invalid User ID or Phone Number"
                    }
                },
                modifier = Modifier.fillMaxWidth()
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
 * Loads only User_ID and PhoneNumber pairs from CSV for dropdown population.
 * This is more efficient than loading all data since we only need these two fields.
 *
 * @param context Android context for accessing assets
 * @return List of pairs where first is User_ID and second is PhoneNumber
 */
fun loadUserIdsWithPhoneNumbers(context: Context): List<Pair<String, String>> {
    // Create a mutable list to store the ID-Phone pairs
    val userPairs = mutableListOf<Pair<String, String>>()

    try {
        // Open the CSV file from assets
        context.assets.open("data.csv").use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // Read the header row to get column positions
                val columns = reader.readLine().split(",")

                // Find the index of our target columns
                val phoneIndex = columns.indexOf("PhoneNumber")
                val idIndex = columns.indexOf("User_ID")

                // Read each subsequent line
                reader.forEachLine { line ->
                    val values = line.split(",")
                    // Ensure we have enough columns before accessing
                    if (values.size > maxOf(phoneIndex, idIndex)) {
                        // Add the ID-Phone pair to our list
                        userPairs.add(values[idIndex] to values[phoneIndex])
                    }
                }
            }
        }
    } catch (e: Exception) {
        // Log errors but return what we have (fail gracefully)
        println("Error loading user IDs: ${e.message}")
    }

    // Return distinct pairs to avoid duplicates
    return userPairs.distinct()
}


/**
 * Finds a specific user by both User_ID AND PhoneNumber.
 * Returns the user's record if found, null otherwise.
 */
fun findUser(context: Context, userId: String, phoneNumber: String): Map<String, String>? {
    try {
        context.assets.open("data.csv").use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                val columns = reader.readLine().split(",")

                // Use traditional while loop instead of forEachLine
                // to allow early return with found record
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    line?.let { currentLine ->
                        val values = currentLine.split(",")
                        if (values.size == columns.size) {
                            val record = columns.zip(values).toMap()
                            if (record["User_ID"] == userId &&
                                record["PhoneNumber"] == phoneNumber) {
                                return record
                            }
                        }
                    }
                }
            }
        }
    } catch (e: Exception) {
        println("Error finding user: ${e.message}")
    }
    return null
}