package com.daffa_34076492.nutritrack

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daffa_34076492.nutritrack.ui.theme.NutriTrack_Daffa_34076492Theme
import java.util.*
import androidx.lifecycle.ViewModelProvider
import com.daffa_34076492.nutritrack.ViewModels.FoodIntakeViewModel
import com.daffa_34076492.nutritrack.auth.AuthManager
import com.daffa_34076492.nutritrack.data.model.FoodIntake

import kotlin.jvm.java

class QuestionnaireActivity : ComponentActivity() {
    private lateinit var viewModel: FoodIntakeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = FoodIntakeViewModel.Factory(applicationContext)
        viewModel = ViewModelProvider(this, factory)[FoodIntakeViewModel::class.java]

        val userId = AuthManager.userId

        setContent {
            NutriTrack_Daffa_34076492Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    QuestionnaireScreen(innerPadding, viewModel, userId)
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireScreen(
    paddingValues: PaddingValues,
    viewModel: FoodIntakeViewModel,
    userId: Int
) {
    val context = LocalContext.current

    // Initializing state variables for form fields
    var selectedPersona by remember { mutableStateOf("") }
    var biggestMealTime by remember { mutableStateOf("") }
    var sleepTime by remember { mutableStateOf("") }
    var wakeUpTime by remember { mutableStateOf("") }

    // Track food category selections
    var foodCategoriesState by remember {
        mutableStateOf(
            mapOf(
                "Fruits" to false,
                "Vegetables" to false,
                "Grains" to false,
                "Red Meat" to false,
                "Seafood" to false,
                "Poultry" to false,
                "Fish" to false,
                "Eggs" to false,
                "Nuts/Seeds" to false
            )
        )
    }

    // Trigger data load when the userId changes or when the screen is recomposed
    LaunchedEffect(userId) {
        viewModel.loadFoodIntake(userId)
    }

    // Collect the latest food intake data from the ViewModel
    val foodIntake by viewModel.foodIntake.collectAsState(initial = null)

    // Update UI values when food intake data is available
    foodIntake?.let {
        selectedPersona = it.persona
        biggestMealTime = it.biggestMealTime
        sleepTime = it.sleepTime
        wakeUpTime = it.wakeUpTime

        // Update food categories based on fetched data
        foodCategoriesState = mapOf(
            "Fruits" to it.eatsFruits,
            "Vegetables" to it.eatsVegetables,
            "Grains" to it.eatsGrains,
            "Red Meat" to it.eatsRedMeat,
            "Seafood" to it.eatsSeafood,
            "Poultry" to it.eatsPoultry,
            "Fish" to it.eatsFish,
            "Eggs" to it.eatsEggs,
            "Nuts/Seeds" to it.eatsNutsSeeds
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Intake Questionnaire") },
                navigationIcon = {
                    IconButton(onClick = { (context as? Activity)?.finish() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Display the checkboxes for each food category
                FoodCategoryCheckboxes(
                    foodCategoriesState = foodCategoriesState,
                    onCategoryChange = { category, isChecked ->
                        foodCategoriesState = foodCategoriesState.toMutableMap().also {
                            it[category] = isChecked
                        }
                    }
                )
                PersonaModals()
                // Persona selection dropdown
                PersonaDropdown(
                    selectedPersona = selectedPersona,
                    onPersonaSelected = { selectedPersona = it }
                )

                // Date and Time inputs for meal times and sleep schedule
                DateandTime(
                    biggestMealTime = biggestMealTime,
                    onBiggestMealTimeChange = { biggestMealTime = it },
                    sleepTime = sleepTime,
                    onSleepTimeChange = { sleepTime = it },
                    wakeUpTime = wakeUpTime,
                    onWakeUpTimeChange = { wakeUpTime = it }
                )

                // Save button to store the form data
                Button(
                    onClick = {
                        // Validate that all required fields are filled
                        if (selectedPersona.isBlank() ||
                            biggestMealTime.isBlank() ||
                            sleepTime.isBlank() ||
                            wakeUpTime.isBlank()
                        ) {
                            Toast.makeText(context, "Please fill all the required fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Create a FoodIntake object with the updated data
                        val updatedFoodIntake = FoodIntake(
                            userId = userId,
                            persona = selectedPersona,
                            eatsFruits = foodCategoriesState["Fruits"] ?: false,
                            eatsVegetables = foodCategoriesState["Vegetables"] ?: false,
                            eatsGrains = foodCategoriesState["Grains"] ?: false,
                            eatsRedMeat = foodCategoriesState["Red Meat"] ?: false,
                            eatsSeafood = foodCategoriesState["Seafood"] ?: false,
                            eatsPoultry = foodCategoriesState["Poultry"] ?: false,
                            eatsFish = foodCategoriesState["Fish"] ?: false,
                            eatsEggs = foodCategoriesState["Eggs"] ?: false,
                            eatsNutsSeeds = foodCategoriesState["Nuts/Seeds"] ?: false,
                            biggestMealTime = biggestMealTime,
                            sleepTime = sleepTime,
                            wakeUpTime = wakeUpTime
                        )

                        // Save the updated food intake data using the ViewModel
                        viewModel.saveFoodIntake(updatedFoodIntake)

                        // Navigate back to the HomeActivity after saving
                        val intent = Intent(context, HomeActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                ) {
                    Text(text = "Save")
                }
            }
        }
    )
}




@Composable
fun FoodCategoryCheckboxes(
    foodCategoriesState: Map<String, Boolean>,
    onCategoryChange: (String, Boolean) -> Unit
) {
    val foodCategories = foodCategoriesState.keys.toList()

    Column {
        Text(
            text = "Tick all the food categories you can eat",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val columns = List(3) { mutableListOf<String>() }
                foodCategories.forEachIndexed { index, category ->
                    columns[index % 3].add(category)
                }

                columns.forEach { columnItems ->
                    Column(modifier = Modifier.weight(1f)) {
                        columnItems.forEach { category ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = foodCategoriesState[category] == true,
                                    onCheckedChange = { isChecked ->
                                        onCategoryChange(category, isChecked)
                                    }
                                )
                                Text(
                                    text = category,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))
    }
}

// Displays modal dialogs for each persona description
@Composable
fun PersonaModals() {
    // Modal visibility states for each persona
    var showHealthDevotee by remember { mutableStateOf(false) }
    var showMindfulEater by remember { mutableStateOf(false) }
    var showWellnessStriver by remember { mutableStateOf(false) }
    var showBalanceSeeker by remember { mutableStateOf(false) }
    var showHealthProcrastinator by remember { mutableStateOf(false) }
    var showFoodCarefree by remember { mutableStateOf(false) }

    // Buttons to show each persona modal
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Your Persona",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            )
        Text(
            text = "People can be broadly classified into 6 different types based on their eating preferences. Click on each button below to find out the different types, and select the type that best fits you!",
            fontSize = 14.sp,
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = { showHealthDevotee = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Health Devotee", fontSize = 10.sp)
                }
                Button(
                    onClick = { showMindfulEater = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Mindful Eater", fontSize = 10.sp)
                }
                Button(
                    onClick = { showWellnessStriver = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Wellness Striver", fontSize = 10.sp)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showBalanceSeeker = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Balance Seeker", fontSize = 10.sp)
                }
                Button(
                    onClick = { showHealthProcrastinator = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("Health Procrastinator", fontSize = 10.sp)
                }
                Button(
                    onClick = { showFoodCarefree = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Food Carefree", fontSize = 10.sp)
                }
            }
        }
    }

    // Dialogs for each persona
    if (showHealthDevotee) {
        PersonaDialog(
            title = "Health Devotee",
            description = "I’m passionate about healthy eating & health plays a big part in my life. I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy.",
            imageRes = R.drawable.persona_1,
            onDismiss = { showHealthDevotee = false }
        )
    }
    if (showMindfulEater) {
        PersonaDialog(
            title = "Mindful Eater",
            description = "I’m health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media.",
            imageRes = R.drawable.persona_2,
            onDismiss = { showMindfulEater = false }
        )
    }
    if (showWellnessStriver) {
        PersonaDialog(
            title = "Wellness Striver",
            description = "\tI aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I’ve tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I’ll give it a go.",
            imageRes = R.drawable.persona_3,
            onDismiss = { showWellnessStriver = false }
        )
    }
    if (showBalanceSeeker) {
        PersonaDialog(
            title = "Balance Seeker",
            description = "\tI try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn’t have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips.",
            imageRes = R.drawable.persona_4,
            onDismiss = { showBalanceSeeker = false }
        )
    }
    if (showHealthProcrastinator) {
        PersonaDialog(
            title = "Health Procrastinator",
            description = "\tI’m contemplating healthy eating but it’s not a priority for me right now. I know the basics about what it means to be healthy, but it doesn’t seem relevant to me right now. I have taken a few steps to be healthier but I am not motivated to make it a high priority because I have too many other things going on in my life.",
            imageRes = R.drawable.persona_5,
            onDismiss = { showHealthProcrastinator = false }
        )
    }
    if (showFoodCarefree) {
        PersonaDialog(
            title = "Food Carefree",
            description = "\tI’m not bothered about healthy eating. I don’t really see the point and I don’t think about it. I don’t really notice healthy eating tips or recipes and I don’t care what I eat.",
            imageRes = R.drawable.persona_6,
            onDismiss = { showFoodCarefree = false }
        )
    }
    Spacer(Modifier.height(10.dp))
}

// Generic dialog for displaying persona details
@Composable
fun PersonaDialog(title: String, description: String, imageRes: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "$title Image",
                )
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        text = { Text(description, textAlign = TextAlign.Center) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonaDropdown(
    selectedPersona: String,
    onPersonaSelected: (String) -> Unit
) {
    val personas = listOf(
        "Health Devotee", "Mindful Eater", "Wellness Striver",
        "Balance Seeker", "Health Procrastinator", "Food Carefree"
    )

    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = "Select your persona", fontWeight = FontWeight.Bold)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedPersona,
                onValueChange = {}, // No manual typing, dropdown-only
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text("Persona") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = OutlinedTextFieldDefaults.colors()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                personas.forEach { persona ->
                    DropdownMenuItem(
                        text = { Text(persona) },
                        onClick = {
                            expanded = false
                            onPersonaSelected(persona)
                        }
                    )
                }
            }
        }
    }
}



@Composable
fun DateandTime(
    biggestMealTime: String,
    onBiggestMealTimeChange: (String) -> Unit,
    sleepTime: String,
    onSleepTimeChange: (String) -> Unit,
    wakeUpTime: String,
    onWakeUpTimeChange: (String) -> Unit
) {
    val context = LocalContext.current

    Column {
        Text(
            text = "Timings",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TimeRow(
            label = "What time of day approx. do you normally eat your biggest meal?",
            timeValue = biggestMealTime,
            onTimeSelected = onBiggestMealTimeChange,
            context = context
        )

        TimeRow(
            label = "What time of day approx. do you go to sleep at night?",
            timeValue = sleepTime,
            onTimeSelected = onSleepTimeChange,
            context = context
        )

        TimeRow(
            label = "What time of day approx. do you wake up in the morning?",
            timeValue = wakeUpTime,
            onTimeSelected = onWakeUpTimeChange,
            context = context
        )
    }
}


@Composable
fun TimeRow(
    label: String,
    timeValue: String,
    onTimeSelected: (String) -> Unit,
    context: Context
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f)
        )
        Button(
            onClick = {
                showTimePicker(context, onTimeSelected)
            },
            modifier = Modifier.width(100.dp)
        ) {
            Text(if (timeValue.isBlank()) "00:00" else timeValue)
        }
    }
}


fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {
    // Create a TimePickerDialog instance
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            // Format the selected time as "HH:mm"
            val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)
            // Pass the formatted time string to the onTimeSelected callback
            onTimeSelected(timeString)
        },
        hour, // default hour
        minute, // default minute
        true // 24-hour format
    )

    // Show the time picker dialog
    timePickerDialog.show()
}
