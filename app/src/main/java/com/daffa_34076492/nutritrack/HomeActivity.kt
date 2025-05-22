package com.daffa_34076492.nutritrack

import DailyTipWorker
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.runtime.LaunchedEffect
import coil.compose.AsyncImage
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import com.daffa_34076492.nutritrack.ViewModels.PatientViewModel
import com.daffa_34076492.nutritrack.workers.AuthManager
import com.daffa_34076492.nutritrack.ui.theme.NutriTrack_Daffa_34076492Theme
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.daffa_34076492.nutritrack.ViewModels.FruitViewModel
import com.daffa_34076492.nutritrack.ViewModels.FruitViewModelFactory
import com.daffa_34076492.nutritrack.data.MotivationalMessageRepository
import com.daffa_34076492.nutritrack.data.PatientRepository
import com.daffa_34076492.nutritrack.workers.NotificationHelper
import java.util.Calendar
import java.util.concurrent.TimeUnit
import android.Manifest


class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }


        val request = PeriodicWorkRequestBuilder<DailyTipWorker>(8, TimeUnit.HOURS)
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_tip_3x",
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )


        val patientRepo = PatientRepository.getInstance(applicationContext)
        val motivationalRepo = MotivationalMessageRepository.getInstance(applicationContext)
        val patientViewModel = ViewModelProvider(
            this,
            PatientViewModel.PatientViewModelFactory(this)
        )[PatientViewModel::class.java]
        val fruitFactory = FruitViewModelFactory(motivationalRepo, patientRepo)
        val fruitViewModel = ViewModelProvider(this, fruitFactory)[FruitViewModel::class.java]

        // 🎨 Compose UI
        setContent {
            NutriTrack_Daffa_34076492Theme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    NavigationGraph(
                        innerPadding = innerPadding,
                        navController = navController,
                        patientViewModel = patientViewModel,
                        fruitViewModel = fruitViewModel,
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationHelper.showNotification(this, "NutriCoach Tip", "Thanks for enabling notifications!")
        }
    }
}
fun calculateInitialDelay(): Long {
    val now = Calendar.getInstance()

    val morning = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 8); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }
    val afternoon = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 14); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }
    val evening = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 20); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }

    val nextTimes = listOf(morning, afternoon, evening)
        .filter { it.after(now) }

    val nextRun = if (nextTimes.isNotEmpty()) nextTimes.minByOrNull { it.timeInMillis }!! else morning.apply { add(Calendar.DATE, 1) }

    return nextRun.timeInMillis - now.timeInMillis
}

/**
 * Defines the navigation graph for the app using Jetpack Compose Navigation.
 *
 * @param innerPadding Padding values from the Scaffold to apply to screens.
 * @param navController The NavHostController to handle navigation actions.
 * @param patientViewModel The shared ViewModel for patient data and state.
 */
@Composable
fun NavigationGraph(
    innerPadding: PaddingValues,
    navController: NavHostController,
    patientViewModel: PatientViewModel,
    fruitViewModel: FruitViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                innerPadding = innerPadding,
                navController = navController,
                viewModel = patientViewModel
            )
        }
        composable("insights") {
            InsightScreen(
                innerPadding = innerPadding,
                navController = navController,
                viewModel = patientViewModel
            )
        }
        composable("nutricoach") {
            NutricoachScreen(
                innerPadding = innerPadding,
                navController = navController,
                patientViewModel = patientViewModel,
                fruitViewModel = fruitViewModel
            )
        }
        composable("settings") {
            SettingsScreen(
                innerPadding = innerPadding,
                navController = navController,
                viewModel = patientViewModel
            )
        }
        composable("clinician_login") {
            ClinicianLoginScreen(
                viewModel = patientViewModel,
                fruitViewModel = fruitViewModel,
                onAuthenticated = {
                    // Navigate to clinician dashboard and remove login from back stack
                    navController.navigate("clinician_dashboard") {
                        popUpTo("clinician_login") { inclusive = true }
                    }
                }
            )
        }
    }
}

/**
 * Home screen composable displaying user's HEIFA score and navigation options.
 *
 * @param innerPadding Padding values from the Scaffold.
 * @param navController NavHostController to handle navigation actions.
 * @param viewModel The PatientViewModel providing data for the screen.
 */
@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    navController: NavHostController,
    viewModel: PatientViewModel
) {
    val context = LocalContext.current
    val userId = AuthManager.userId

    LaunchedEffect(userId) {
        viewModel.loadHEIFAScore(userId)
        viewModel.loadPatientData(userId)
    }

    val heifaScore by viewModel.heifaScore.collectAsState()
    val patientData by viewModel.patientData.collectAsState()
    val name = patientData?.name

    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Greeting section
        item {
            Text(
                text = "Hello",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Text(
                text = name ?: "User",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )

        }

        // Info with Edit button to open the questionnaire
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "You’ve already filled in your Food Intake Questionnaire, but you can change details here:",
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
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
        }

        // Food score illustration image
        item {
            Image(
                painter = painterResource(id = R.drawable.healthy_diet),
                contentDescription = "Food Score Image",
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Card showing the Food Quality score with navigation to insights
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Your Food Quality score",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$heifaScore/100",
                            fontSize = 22.sp,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }
        }

        // Explanation of Food Quality Score title
        item {
            Text(
                text = "What is the Food Quality Score?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Explanation text paragraphs
        item {
            Text(
                text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.",
                fontSize = 14.sp
            )
        }
        item {
            Text(
                text = "This personalized measurement considers various food groups including vegetables, fruits, whole grains, and proteins to give you practical insights for making healthier food choices.",
                fontSize = 14.sp
            )
        }
    }
}

/**
 * InsightScreen displays detailed HEIFA scores for a patient, including category-wise progress bars,
 * total Food Quality Score, and options to share the score or navigate to the Nutricoach screen.
 *
 * @param innerPadding Padding values from the scaffold or parent layout to apply consistent spacing.
 * @param navController Navigation controller used to navigate between app screens.
 * @param viewModel ViewModel that provides patient data and handles data loading logic.
 *
 * Behavior:
 * - Observes patient data from the ViewModel and shows a loading indicator until data is loaded.
 * - Displays progress bars for multiple food categories with their respective scores.
 * - Shows total Food Quality Score with a progress bar and formatted text.
 * - Provides "Share" button to share the total score via other apps.
 * - Provides "Improve My Diet" button to navigate to the Nutricoach screen.
 */
@SuppressLint("DefaultLocale")
@Composable
fun InsightScreen(
    innerPadding: PaddingValues,
    navController: NavHostController,
    viewModel: PatientViewModel
) {
    val context = LocalContext.current
    val userId = AuthManager.userId

    val patient by viewModel.patientData.collectAsState()

    // Load patient when the screen appears
    LaunchedEffect(userId) {
        viewModel.loadPatientData(userId)
    }

    if (patient == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val progressBarItems = listOf(
            "Discretionary Foods" to patient!!.discretionaryHEIFAScore,
            "Vegetables" to patient!!.vegetablesHEIFAScore,
            "Fruit" to patient!!.fruitHEIFAScore,
            "Grains and Cereals" to patient!!.grainsAndCerealsHEIFAScore,
            "Whole Grains" to patient!!.wholeGrainsHEIFAScore,
            "Meat and Alternatives" to patient!!.meatAndAlternativesHEIFAScore,
            "Dairy and Alternatives" to patient!!.dairyAndAlternativesHEIFAScore,
            "Sodium" to patient!!.sodiumHEIFAScore,
            "Alcohol" to patient!!.alcoholHEIFAScore,
            "Water" to patient!!.waterHEIFAScore,
            "Sugar" to patient!!.sugarHEIFAScore,
            "Saturated Fat" to patient!!.saturatedFatHEIFAScore,
            "Unsaturated Fat" to patient!!.unsaturatedFatHEIFAScore
        )

        val categoryMaxValues = mapOf(
            "Grains and Cereals" to 5f,
            "Whole Grains" to 5f,
            "Water" to 5f,
            "Alcohol" to 5f,
            "Unsaturated Fat" to 5f,
            "Saturated Fat" to 5f,
            // Default 10 for the rest
            "Discretionary Foods" to 10f,
            "Vegetables" to 10f,
            "Fruit" to 10f,
            "Meat and Alternatives" to 10f,
            "Dairy and Alternatives" to 10f,
            "Sodium" to 10f,
            "Sugar" to 10f
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Insight Food Score",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            items(progressBarItems) { (category, score) ->
                val maxScore = categoryMaxValues[category] ?: 10f
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = category,
                        modifier = Modifier
                            .weight(1.5f)
                            .wrapContentWidth(Alignment.Start),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                    LinearProgressIndicator(
                        progress = score.toFloat() / maxScore,
                        modifier = Modifier
                            .weight(2f)
                            .padding(horizontal = 4.dp)
                            .height(6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.LightGray
                    )
                    Text(
                        text = String.format("%.1f/%.0f", score, maxScore),
                        modifier = Modifier
                            .weight(0.8f)
                            .wrapContentWidth(Alignment.End),
                        fontSize = 12.sp,
                        textAlign = TextAlign.End
                    )
                }
            }

            item {
                Text(
                    text = "Total Food Quality Score:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = patient!!.HEIFATotalScore.toFloat() / 100f,
                        modifier = Modifier
                            .weight(2f)
                            .padding(horizontal = 4.dp)
                            .height(6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.LightGray
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = String.format("%.1f/100", patient!!.HEIFATotalScore),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val shareText =
                        "My Food Quality Score is ${patient!!.HEIFATotalScore.toInt()}/100 using NutriTrack! Try it out!"

                    Button(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Share")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = { navController.navigate("nutricoach") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Improve My Diet")
                    }
                }
            }
        }
    }
}


@Composable
fun NutricoachScreen(
    innerPadding: PaddingValues,
    navController: NavHostController,
    patientViewModel: PatientViewModel,
    fruitViewModel: FruitViewModel
) {
    val searchQuery = fruitViewModel.searchQuery
    val selectedFruit by fruitViewModel.selectedFruit
    val isLoading by fruitViewModel.isLoading
    val errorMessage by fruitViewModel.fruitErrorMessage
    val userId = AuthManager.userId
    val isOptimal by patientViewModel.isOptimal.observeAsState(initial = false)
    val motivationalMessage by fruitViewModel.motivationalMessage.collectAsState()
    var showHistoryDialog by remember { mutableStateOf(false) }
    val allMessages by fruitViewModel.allMessages.collectAsState()




    LaunchedEffect(userId) {
        userId?.let {
            patientViewModel.checkIfUserOptimal(it)
            fruitViewModel.fetchMotivationalMessage(it)

        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "NutriCoach",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = if (isOptimal) "✅ Your diet is optimal!" else "⚠️ Your diet needs improvement.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isOptimal) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
        }

        if (isOptimal) {
            item {
                AsyncImage(
                    model = "https://picsum.photos/600/400",
                    contentDescription = "Random motivational image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            item {
                Text(
                    text = "Great job! Your diet is optimal 🎉",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            item {
                Text(
                    text = "Your fruit score could be better. Search for a fruit like 'Banana', 'Orange', or 'Apple' to learn more.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { newQuery -> fruitViewModel.onSearchQueryChange(newQuery) },
                    label = { Text("Enter fruit name") },
                    placeholder = { Text("e.g. Banana, Orange, Apple") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = {
                            if (searchQuery.isNotBlank()) {
                                fruitViewModel.searchFruit(searchQuery)
                            }
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                )
            }

            item {
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    errorMessage != null -> {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    selectedFruit != null -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Name: ${selectedFruit!!.name}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Family: ${selectedFruit!!.family}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = "Calories: ${selectedFruit!!.nutritions.calories}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Fat: ${selectedFruit!!.nutritions.fat}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Sugar: ${selectedFruit!!.nutritions.sugar}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Carbohydrates: ${selectedFruit!!.nutritions.carbohydrates}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Protein: ${selectedFruit!!.nutritions.protein}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
            item {
                Divider()
            }
            item {
                Text(
                    text = "Motivational Message (AI)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

            }


        }

        item {
            val message = motivationalMessage

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (message.isNullOrBlank()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }


        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { fruitViewModel.fetchMotivationalMessage(userId) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp)
                ) {
                    Text("Get Motivation")
                }
                Button(
                    onClick = {
                        fruitViewModel.loadMessagesForUser(userId ?: 0)
                        showHistoryDialog = true
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.Default.History, contentDescription = "History", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("AI Tips History")
                }
            }
        }

        item {
            if (showHistoryDialog) {
                Dialog(
                    onDismissRequest = { showHistoryDialog = false }
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .heightIn(min = 200.dp, max = 500.dp)
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.background,
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "AI Tips History",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(onClick = { showHistoryDialog = false }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close")
                                }
                            }
                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            if (allMessages.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No motivational tips saved yet.")
                                }
                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    allMessages.forEach { msg ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color.White),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Text(
                                                    text = msg.message,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = Color.Black
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Clear Messages",
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .clickable {
                                            fruitViewModel.clearAllMessagesForUser(userId)
                                        }
                                        .padding(8.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }



    }
}

/**
 * SettingsScreen displays the user account details and account management options such as Logout and Clinician Login.
 *
 * @param innerPadding Padding values from parent layout (e.g., Scaffold) to maintain consistent spacing.
 * @param navController Navigation controller used to navigate between screens.
 * @param viewModel ViewModel providing patient data and handling data loading.
 *
 * Behavior:
 * - Loads and observes patient data using the current authenticated user ID.
 * - Shows account info like name, user ID, phone number, and sex inside a styled card.
 * - Provides clickable options for Logout and Clinician Login.
 * - Logout clears authentication and navigates back to MainActivity.
 * - Clinician Login navigates to the clinician login screen.
 */
@Composable
fun SettingsScreen(
    innerPadding: PaddingValues,
    navController: NavHostController,
    viewModel: PatientViewModel
) {
    val userId = AuthManager.userId
    val patient by viewModel.patientData.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadPatientData(userId)
    }

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            Text(
                text = "Account",
                style = MaterialTheme.typography.labelLarge.copy(color = Color.Gray),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    patient?.let {
                        InfoRow(Icons.Default.Person, "Name", it.name.toString())
                        Divider()
                        InfoRow(Icons.Default.Badge, "User ID", it.userId.toString())
                        Divider()
                        InfoRow(Icons.Default.Phone, "Phone", it.phoneNumber)
                        Divider()
                        InfoRow(
                            icon = if (it.sex.equals("Male", ignoreCase = true)) Icons.Default.Male else Icons.Default.Female,
                            label = "Sex",
                            value = it.sex
                        )
                    } ?: Text("Loading patient data…")
                }
            }
        }

        item {
            Divider()
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "Account Management",
                        style = MaterialTheme.typography.labelLarge.copy(color = Color.Gray),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    SettingsClickableTextRow(
                        text = "Logout",
                        icon = Icons.AutoMirrored.Filled.Logout,
                        onClick = {
                            AuthManager.logout()
                            val intent = Intent(context, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            context.startActivity(intent)
                        }
                    )
                    Divider()
                    SettingsClickableTextRow(
                        text = "Clinician Login",
                        icon = Icons.Filled.AdminPanelSettings,
                        onClick = {
                            navController.navigate("clinician_login")
                        }
                    )
                }
            }
        }
    }
}

/**
 * ClinicianLoginScreen provides a secure login form for clinicians to authenticate with a passphrase.
 *
 * @param viewModel ViewModel managing authentication state, input, and error messages.
 * @param onAuthenticated Callback invoked after successful authentication to proceed with navigation or actions.
 *
 * Behavior:
 * - Displays a password input field with toggle visibility option.
 * - Shows authentication errors if any.
 * - Upon successful authentication, starts the ClinicianActivity and calls the onAuthenticated callback.
 */
@Composable
fun ClinicianLoginScreen(
    viewModel: PatientViewModel,
    fruitViewModel: FruitViewModel,
    onAuthenticated: () -> Unit
) {
    val isAuthenticated = viewModel.isAuthenticated
    val passphraseInput = viewModel.passphraseInput
    val errorMessage = viewModel.errorMessage
    val patternInsights by fruitViewModel.patternInsights.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    val isLoading by fruitViewModel.isLoadingDataPatterns.collectAsState()



    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            viewModel.fetchAverageHeifaScores()
        }
    }

    val maleAverage by viewModel.averageHeifaMale.collectAsState()
    val femaleAverage by viewModel.averageHeifaFemale.collectAsState()


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isAuthenticated) {
            // ───────── HEIFA Scores Section ─────────
            item {
                Text(
                    text = "HEIFA Score Averages",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(80.dp)
                        .padding(vertical = 8.dp)
                        .shadow(12.dp, shape = RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Male,
                            contentDescription = "Male Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "Male Average: ${maleAverage?.let { "%.2f".format(it) } ?: "--"}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(80.dp)
                        .padding(vertical = 8.dp)
                        .shadow(12.dp, shape = RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Female,
                            contentDescription = "Female Icon",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "Female Average: ${femaleAverage?.let { "%.2f".format(it) } ?: "--"}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                    }
                }
            }

            item {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            }

// ───────── Analysis Trigger Button ─────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { fruitViewModel.analyzeDataPatterns() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading // disable button when loading
                    ) {
                        Text("Analyze Data Patterns")
                    }


                    Spacer(modifier = Modifier.height(12.dp))

                    if (isLoading) {
                        CircularProgressIndicator()
                    }
                }
            }


                if (patternInsights.isNotEmpty()) {
                    item {
                        Text(
                            text = "AI-Generated Insights:",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 8.dp)
                        )
                    }

                    items(patternInsights) { pattern ->
                        val regex = Regex("""^(\d+\.)\s+(.*)$""")
                        val match = regex.find(pattern)
                        val annotatedText = if (match != null) {
                            val number = match.groupValues[1]
                            val text = match.groupValues[2].replace("**", "")
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(number + " ")
                                }
                                append(text)
                            }
                        } else {
                            AnnotatedString(pattern.replace("**", ""))
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text(
                                text = annotatedText,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                softWrap = true
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }

                }


        } else {
            // ───────── Login Form ─────────
            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MedicalServices,
                                    contentDescription = "Clinician Icon",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(end = 12.dp)
                                )
                                Text(
                                    text = "Clinician Login",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            OutlinedTextField(
                                value = passphraseInput,
                                onValueChange = { viewModel.passphraseInput = it },
                                label = { Text("Clinician Key") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(imageVector = icon, contentDescription = null)
                                    }
                                }
                            )

                            Button(
                                onClick = { viewModel.authenticate() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(8.dp, RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AdminPanelSettings,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(end = 8.dp)
                                )
                                Text(
                                    text = "Clinician Login",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }

                            // 🔹 Optional Insights for unauthenticated (preview mode)
                            if (patternInsights.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "AI-Generated Insights:",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                patternInsights.forEach { pattern ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        elevation = CardDefaults.cardElevation(4.dp)
                                    ) {
                                        Text(
                                            text = pattern,
                                            modifier = Modifier.padding(16.dp),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }

                            if (errorMessage != null) {
                                Text(
                                    text = errorMessage,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



/**
 * InfoRow displays an icon alongside a label and value in a horizontal row, used for showing key-value pairs.
 *
 * @param icon The icon to display at the start of the row.
 * @param label The label describing the value.
 * @param value The value text to display next to the label.
 */
@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * SettingsClickableTextRow creates a clickable row with an icon and text, useful for menu options in settings.
 *
 * @param text The text to display.
 * @param icon The icon shown alongside the text.
 * @param contentColor The tint color for the icon and text (default is onSurface color).
 * @param onClick Lambda function to invoke when the row is clicked.
 */
@Composable
fun SettingsClickableTextRow(
    text: String,
    icon: ImageVector,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor
        )
    }
}

/**
 * BottomNavigationBar displays a navigation bar at the bottom of the screen with tabs for Home, Insights, NutriCoach, and Settings.
 *
 * @param navController The NavHostController used to navigate between screens and observe the current navigation state.
 *
 * Behavior:
 * - Tracks the currently selected navigation item based on the current route.
 * - Updates selection state dynamically when navigation changes.
 * - On item click, navigates to the corresponding screen route.
 * - Uses icons (both vector and custom drawable resources) and labels to represent each tab.
 */
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
