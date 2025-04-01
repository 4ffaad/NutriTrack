package com.daffa_34076492.nutritrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.daffa_34076492.nutritrack.ui.theme.NutriTrack_Daffa_34076492Theme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve userId from the Intent
        val userId = intent.getStringExtra("userId") ?: ""

        // Now pass the userId to your NavigationGraph
        setContent {
            NutriTrack_Daffa_34076492Theme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        NavigationGraph(innerPadding, navController, userId)
                    }
                }
            }
        }
    }
}



    @Composable
    fun NavigationGraph(
        innerPadding: PaddingValues,
        navController: NavHostController,
        userId: String
    ) {
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") {
                // Pass userId to InitScreen
                HomeScreen(innerPadding, userId)
            }
            composable("insights") {
                InsightScreen(innerPadding)
            }
            composable("nutricoach") {
                NutricoachScreen(innerPadding)
            }
            composable("settings") {
                SettingsScreen(innerPadding)
            }
        }
    }

    @Composable
    fun HomeScreen(innerPadding: PaddingValues, userId: String) {

        val foodScore = 85 // Replace with actual food score data

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Display the username
            Text(
                text = "Hello",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            // Display the username
            Text(
                text = "$userId",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Description about the food intake questionnaire
                Text(
                    text = "You’ve already filled in your Food Intake Questionnaire, but you can change details here:",
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f) // Allows text to take available space
                )

                Spacer(modifier = Modifier.width(8.dp))

                val context = LocalContext.current
                Button(
                    onClick = {
                        // Navigate back to QuestionnaireActivity
                        val intent = Intent(context, QuestionnaireActivity::class.java)
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(40.dp) // Ensures the button isn't too large
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", color = Color.White)
                }
            }

            // Food Score Image
            Image(
            painter = painterResource(id = R.drawable.healthy_diet),
            contentDescription = "Food Score Image",
            modifier = Modifier.fillMaxWidth() // Adjust this if needed
            )

            Spacer(modifier = Modifier.height(16.dp))
            // Food Score Display
            Text(
                text = "My Score",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically, // Aligns items in the center
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Adds spacing between elements
            ) {

                // Detailed score text
                Text(
                    text = "Your Food Quality score",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))                 // Score value
                Text(
                    text = "$foodScore/100",
                    fontSize = 22.sp,
                    color = Color(0xFF4CAF50) // Green color for score
                )
            }
            Spacer(modifier = Modifier.height(50.dp))

            // Food Quality Score Info
            Text(
                text = "What is the Food Quality Score?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.",
                fontSize = 14.sp,
                textAlign = TextAlign.Left
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "This personalized measurement considers various food groups including vegetables, fruits, whole grains, and proteins to give you practical insights for making healthier food choices.",
                fontSize = 14.sp,
                textAlign = TextAlign.Left
            )
        }
    }
    // Composable function for displaying the Reports screen.
    @Composable
    fun InsightScreen(innerPadding: PaddingValues) {
        TODO("Not yet implemented")
    }

    // Composable function for displaying the nutricoach screen.
    @Composable
    fun NutricoachScreen(innerPadding: PaddingValues) {
        TODO("Not yet implemented")
    }

    // Composable function for displaying the Settings screen.
    @Composable
    fun SettingsScreen(innerPadding: PaddingValues) {
        TODO("Not yet implemented")
    }



    @Composable
    fun BottomNavigationBar(navController: NavHostController) {
        // State to track the currently selected item in the bottom navigation bar.
        var selectedItem by remember { mutableIntStateOf(0) }
        // List of navigation items: "home", "reports", "settings".
        val items = listOf(
            "Home",
            "Insights",
            "NutriCoach",
            "Settings"
        )

        // NavigationBar composable to define the bottom navigation bar.
        NavigationBar {
            // Iterate through each item in the 'items' List along with its index.
            items.forEachIndexed { index, item ->
                // NavigationBarItem for each item in the List.
                NavigationBarItem(
                    // Define the icon based on the item's name.
                    icon = {
                        when (item) {
                            // If the item is "Home", show the Home icon.
                            "Home" -> Icon(Icons.Filled.Home, contentDescription = "Home")
                            // If the item is "Insights", show the insights icon.
                            "Insights" -> Icon(
                                painter = painterResource(id = R.drawable.insights),
                                contentDescription = "NutriCoach"
                            )
                            // If the item is "NutriCoach", show the NutriCoach icon.
                            "NutriCoach" -> Icon(
                                painter = painterResource(id = R.drawable.nutritrack_agent),
                                contentDescription = "NutriCoach"
                            )
                            // If the item is "settings", show the Settings icon.
                            "Settings" -> Icon(Icons.Filled.Settings, contentDescription = "Settings")
                        }
                    },
                    // Display the item's name as the label.
                    label = { Text(item) },
                    // Determine if this item is currently selected.
                    selected = selectedItem == index,
                    // Actions to perform when this item is clicked.
                    onClick = {
                        // Update the selectedItem state to the current index.
                        selectedItem = index
                        // Navigate to the corresponding screen based on the item's name.
                        navController.navigate(item)
                    }
                )
            }
            // Close NavigationBarItem.
        }
    }