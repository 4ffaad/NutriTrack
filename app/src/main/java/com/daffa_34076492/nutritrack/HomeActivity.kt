package com.daffa_34076492.nutritrack
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import java.io.BufferedReader
import java.io.InputStreamReader
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.compose.currentBackStackEntryAsState
import com.daffa_34076492.nutritrack.ui.theme.NutriTrack_Daffa_34076492Theme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NutriTrack_Daffa_34076492Theme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                        NavigationGraph(innerPadding, navController)
                }
            }
        }
    }
}

@Composable
fun NavigationGraph(
    innerPadding: PaddingValues,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            // Pass userId to InitScreen
            HomeScreen(innerPadding, navController)
        }
        composable("insights") {
            InsightScreen(innerPadding, navController)
        }
        composable("nutricoach") {
            NutricoachScreen(innerPadding)
        }
        composable("settings") {
            SettingsScreen(innerPadding)
        }
    }
}

// Helper function to get the food score
fun getFoodScoreForUser(userId: String, foodScores: List<Map<String, String>>): Double {
    val userScoreData = foodScores.find { it["User_ID"]?.trim() == userId.trim() }
    return userScoreData?.let {
        val gender = it["Sex"]
        when (gender?.lowercase()) {
            "male" -> it["HEIFAtotalscoreMale"]?.toDoubleOrNull() ?: 0.0
            "female" -> it["HEIFAtotalscoreFemale"]?.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    } ?: 0.0
}

@Composable
fun HomeScreen(innerPadding: PaddingValues, navController: NavHostController) {
    val context = LocalContext.current
    var userId by remember { mutableStateOf("") }
    val sharedPreferences = context.getSharedPreferences("Questionnaire", Context.MODE_PRIVATE)
    sharedPreferences.getString("userId", "") ?: ""
    userId = getUserIdFromPreferences(context)

    val foodScores = getUserFoodScores(context)
    val foodScore = getFoodScoreForUser(userId, foodScores)

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

        // Display the username (userId retrieved from SharedPreferences)
        Text(
            text = ",$userId",
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
            Button(
                onClick = {
                    // Navigate back to QuestionnaireActivity
                    val intent = Intent(context, QuestionnaireActivity::class.java)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "My Score",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "See all scores",
                        fontSize = 15.sp,
                        color = Color.Gray,
                        modifier = Modifier.clickable {
                            navController.navigate("insights")

                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Adds spacing between elements
                ) {
                    // Detailed score text
                    Text(
                        text = "Your Food Quality score",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Score value
                    Text(
                        text = "$foodScore/100",
                        fontSize = 22.sp,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }

        // Food Quality Score Info
        Text(
            text = "What is the Food Quality Score?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.",
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This personalized measurement considers various food groups including vegetables, fruits, whole grains, and proteins to give you practical insights for making healthier food choices.",
            fontSize = 14.sp,
        )
    }
}

// Helper Function to retrieve userId from SharedPreferences
fun getUserIdFromPreferences(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("Questionnaire", Context.MODE_PRIVATE)
    return sharedPreferences.getString("userId", "") ?: ""
}

@Composable
fun InsightScreen(innerPadding: PaddingValues, navController: NavHostController) {
    val context = LocalContext.current
    var userId by remember { mutableStateOf("") }
    userId = getUserIdFromPreferences(context)
    val foodScores = getUserFoodScores(context)
    val foodScore = getFoodScoreForUser(userId, foodScores)
    val userScoreData = foodScores.find { it["User_ID"]?.trim() == userId.trim() }

    val progressBarItems = listOf(
        "Discretionary Foods" to "DiscretionaryHEIFAscore",
        "Vegetables" to "VegetablesHEIFAscore",
        "Fruit" to "FruitHEIFAscore",
        "Grains and Cereals" to "GrainsandcerealsHEIFAscore",
        "Whole Grains" to "WholegrainsHEIFAscore",
        "Meat and Alternatives" to "MeatandalternativesHEIFAscore",
        "Dairy and Alternatives" to "DairyandalternativesHEIFAscore",
        "Sodium" to "SodiumHEIFAscore",
        "Alcohol" to "AlcoholHEIFAscore",
        "Water" to "WaterHEIFAscore",
        "Sugar" to "SugarHEIFAscore",
        "Unsaturated Fat" to "UnsaturatedFatHEIFAscore",
        "Saturated Fat" to "SaturatedFatHEIFAscore"
    )

    // Define max values for categories
    val categoryMaxValues = mapOf(
        // Categories with a max of 5
        "Grains and Cereals" to 5f,
        "Whole Grains" to 5f,
        "Water" to 5f,
        "Alcohol" to 5f,
        "Unsaturated Fat" to 5f,
        "Saturated Fat" to 5f,

        // Categories with a max of 10 (default)
        "Discretionary Foods" to 10f,
        "Vegetables" to 10f,
        "Fruit" to 10f,
        "Meat and Alternatives" to 10f,
        "Dairy and Alternatives" to 10f,
        "Sodium" to 10f,
        "Sugar" to 10f
    )

    // Extract scores based on gender
    val gender = userScoreData?.get("Sex")
    val progressData = progressBarItems.map { (category, prefix) ->
        val score = when (gender?.lowercase()) {
            "male" -> userScoreData["${prefix}Male"]?.toFloatOrNull() ?: 0f
            "female" -> userScoreData["${prefix}Female"]?.toFloatOrNull() ?: 0f
            else -> 0f
        }
        category to score
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        // Heading for the screen
        Text(
            text = "Insight Food Score",
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.headlineMedium
        )

        Column {
            progressData.forEach { (category, score) ->
                val maxScore = categoryMaxValues[category] ?: 10f
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Column 1: Food Category (left-aligned)
                    Text(
                        text = category,
                        modifier = Modifier
                            .weight(1.5f) // More space for category text
                            .wrapContentWidth(Alignment.Start),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                    // Column 2: Slider
                    LinearProgressIndicator(
                        progress = score / maxScore, // Normalize score to 0-1 range
                        modifier = Modifier
                            .weight(2f)
                            .padding(horizontal = 4.dp)
                            .height(6.dp),
                        color = MaterialTheme.colorScheme.primary, // Customize color
                        trackColor = Color.LightGray // Background color for the progress bar
                    )

                    // Column 3: Score (right-aligned)
                    Text(
                        text = "${score.toInt()}/${maxScore.toInt()}", // Display correct max
                        modifier = Modifier
                            .weight(0.8f)
                            .wrapContentWidth(Alignment.End),
                        fontSize = 12.sp,
                        textAlign = TextAlign.End
                    )
                }
            }
        }

        // Total food quality score
        Text(
            text = "Total Food Quality Score:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Progress Bar with score displayed next to it
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // Align vertically in the center
        ) {
            // Progress Bar
            LinearProgressIndicator(
                progress = foodScore.toInt() / 100f,
                modifier = Modifier
                    .weight(2f) // Take up most of the row
                    .padding(horizontal = 4.dp)
                    .height(6.dp),
                color = MaterialTheme.colorScheme.primary, // Customize color
                trackColor = Color.LightGray // Background color for the progress bar
            )

            // Display the score next to the progress bar
            Spacer(modifier = Modifier.width(8.dp)) // Add space between the bar and the score
            Text(
                text = "${foodScore.toInt()}/100", // Display score with 100 as the max value
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary, // Customize text color
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val context = LocalContext.current
            val shareText = "My Food Quality Score is $foodScore/100 using NutriTrack! Try it out!"


            Button(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    // Launch chooser
                    context.startActivity(
                        Intent.createChooser(shareIntent, "Share via")
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Share")
            }

            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {navController.navigate("nutricoach") },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Improve My Diet")
            }
        }
    }
}

@Composable
fun NutricoachScreen(innerPadding: PaddingValues) {
    TODO("Not yet implemented")
}

@Composable
fun SettingsScreen(innerPadding: PaddingValues) {
    TODO("Not yet implemented")
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Home", "Insights", "NutriCoach", "Settings")
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    selectedItem = when (currentRoute) {
        "home" -> 0
        "insights" -> 1
        "nutricoach" -> 2
        "settings" -> 3
        else -> selectedItem
    }
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    when (item) {
                        "Home" -> Icon(Icons.Filled.Home, contentDescription = "Home")
                        "Insights" -> Icon(
                            painter = painterResource(id = R.drawable.insights),
                            contentDescription = "NutriCoach"
                        )
                        "NutriCoach" -> Icon(
                            painter = painterResource(id = R.drawable.nutritrack_agent),
                            contentDescription = "NutriCoach"
                        )
                        "Settings" -> Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    } },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    // Navigate to the corresponding screen
                    when (item) {
                        "Home" -> navController.navigate("home")
                        "Insights" -> navController.navigate("insights")
                        "NutriCoach" -> navController.navigate("nutricoach")
                        "Settings" -> navController.navigate("settings")
                    }
                }
            )
        }
    }
}

fun getUserFoodScores(context: Context): List<Map<String, String>> {
    val userFoodScores = mutableListOf<Map<String, String>>()
    try {
        // Open the CSV file from assets
        context.assets.open("data.csv").use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // Read the header row to get column names
                val columns = reader.readLine().split(",")

                // Find the indices of the relevant columns (all HEIFA scores)
                val userIdIndex = columns.indexOf("User_ID")
                val sexIndex = columns.indexOf("Sex")
                val maleScoreIndex = columns.indexOf("HEIFAtotalscoreMale")
                val femaleScoreIndex = columns.indexOf("HEIFAtotalscoreFemale")

                // Discretionary scores
                val discretionaryMaleIndex = columns.indexOf("DiscretionaryHEIFAscoreMale")
                val discretionaryFemaleIndex = columns.indexOf("DiscretionaryHEIFAscoreFemale")

                // Vegetables scores
                val vegetablesMaleIndex = columns.indexOf("VegetablesHEIFAscoreMale")
                val vegetablesFemaleIndex = columns.indexOf("VegetablesHEIFAscoreFemale")

                // Fruit scores
                val fruitMaleIndex = columns.indexOf("FruitHEIFAscoreMale")
                val fruitFemaleIndex = columns.indexOf("FruitHEIFAscoreFemale")

                // Grains and cereals scores
                val grainsMaleIndex = columns.indexOf("GrainsandcerealsHEIFAscoreMale")
                val grainsFemaleIndex = columns.indexOf("GrainsandcerealsHEIFAscoreFemale")

                // Whole grains scores
                val wholeGrainsMaleIndex = columns.indexOf("WholegrainsHEIFAscoreMale")
                val wholeGrainsFemaleIndex = columns.indexOf("WholegrainsHEIFAscoreFemale")

                // Meat and alternatives scores
                val meatMaleIndex = columns.indexOf("MeatandalternativesHEIFAscoreMale")
                val meatFemaleIndex = columns.indexOf("MeatandalternativesHEIFAscoreFemale")

                // Dairy and alternatives scores
                val dairyMaleIndex = columns.indexOf("DairyandalternativesHEIFAscoreMale")
                val dairyFemaleIndex = columns.indexOf("DairyandalternativesHEIFAscoreFemale")

                // Sodium scores
                val sodiumMaleIndex = columns.indexOf("SodiumHEIFAscoreMale")
                val sodiumFemaleIndex = columns.indexOf("SodiumHEIFAscoreFemale")

                // Alcohol scores
                val alcoholMaleIndex = columns.indexOf("AlcoholHEIFAscoreMale")
                val alcoholFemaleIndex = columns.indexOf("AlcoholHEIFAscoreFemale")

                // Water scores
                val waterMaleIndex = columns.indexOf("WaterHEIFAscoreMale")
                val waterFemaleIndex = columns.indexOf("WaterHEIFAscoreFemale")

                // Sugar scores
                val sugarMaleIndex = columns.indexOf("SugarHEIFAscoreMale")
                val sugarFemaleIndex = columns.indexOf("SugarHEIFAscoreFemale")

                // Unsaturated fat scores
                val unsaturatedFatMaleIndex = columns.indexOf("UnsaturatedFatHEIFAscoreMale")
                val unsaturatedFatFemaleIndex = columns.indexOf("UnsaturatedFatHEIFAscoreFemale")

                // Read each subsequent line
                reader.forEachLine { line ->
                    val values = line.split(",")

                    // Extract all the relevant columns for each row
                    val userScoreData = mutableMapOf<String, String>()
                    userScoreData["User_ID"] = values[userIdIndex]
                    userScoreData["Sex"] = values[sexIndex]
                    userScoreData["HEIFAtotalscoreMale"] = values[maleScoreIndex]
                    userScoreData["HEIFAtotalscoreFemale"] = values[femaleScoreIndex]
                    userScoreData["DiscretionaryHEIFAscoreMale"] = values[discretionaryMaleIndex]
                    userScoreData["DiscretionaryHEIFAscoreFemale"] = values[discretionaryFemaleIndex]
                    userScoreData["VegetablesHEIFAscoreMale"] = values[vegetablesMaleIndex]
                    userScoreData["VegetablesHEIFAscoreFemale"] = values[vegetablesFemaleIndex]
                    userScoreData["FruitHEIFAscoreMale"] = values[fruitMaleIndex]
                    userScoreData["FruitHEIFAscoreFemale"] = values[fruitFemaleIndex]
                    userScoreData["GrainsandcerealsHEIFAscoreMale"] = values[grainsMaleIndex]
                    userScoreData["GrainsandcerealsHEIFAscoreFemale"] = values[grainsFemaleIndex]
                    userScoreData["WholegrainsHEIFAscoreMale"] = values[wholeGrainsMaleIndex]
                    userScoreData["WholegrainsHEIFAscoreFemale"] = values[wholeGrainsFemaleIndex]
                    userScoreData["MeatandalternativesHEIFAscoreMale"] = values[meatMaleIndex]
                    userScoreData["MeatandalternativesHEIFAscoreFemale"] = values[meatFemaleIndex]
                    userScoreData["DairyandalternativesHEIFAscoreMale"] = values[dairyMaleIndex]
                    userScoreData["DairyandalternativesHEIFAscoreFemale"] = values[dairyFemaleIndex]
                    userScoreData["SodiumHEIFAscoreMale"] = values[sodiumMaleIndex]
                    userScoreData["SodiumHEIFAscoreFemale"] = values[sodiumFemaleIndex]
                    userScoreData["AlcoholHEIFAscoreMale"] = values[alcoholMaleIndex]
                    userScoreData["AlcoholHEIFAscoreFemale"] = values[alcoholFemaleIndex]
                    userScoreData["WaterHEIFAscoreMale"] = values[waterMaleIndex]
                    userScoreData["WaterHEIFAscoreFemale"] = values[waterFemaleIndex]
                    userScoreData["SugarHEIFAscoreMale"] = values[sugarMaleIndex]
                    userScoreData["SugarHEIFAscoreFemale"] = values[sugarFemaleIndex]
                    userScoreData["UnsaturatedFatHEIFAscoreMale"] = values[unsaturatedFatMaleIndex]
                    userScoreData["UnsaturatedFatHEIFAscoreFemale"] = values[unsaturatedFatFemaleIndex]
                    userFoodScores.add(userScoreData)
                }
            }
        }
    } catch (e: Exception) {
        // Log errors but fail gracefully
        println("Error loading food scores: ${e.message}")
    }

    return userFoodScores
}