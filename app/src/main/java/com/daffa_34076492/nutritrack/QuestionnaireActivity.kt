package com.daffa_34076492.nutritrack

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.core.content.edit
import kotlin.jvm.java

class QuestionnaireActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve userId from Intent
        val userId = intent.getStringExtra("userId") ?: ""
        setContent {
            NutriTrack_Daffa_34076492Theme {
                QuestionnaireScreen(userId)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireScreen(userId: String) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("Questionnaire", Context.MODE_PRIVATE)

    // Load previously saved values
    val savedTime = sharedPref.getString("selected_time", "No time selected") ?: "No time selected"
    val savedText = sharedPref.getString("user_text", "") ?: ""
    val savedCheckBox = sharedPref.getBoolean("checkbox_state", false)
    val savedPersona = sharedPref.getString("selected_persona", "") ?: ""
    val savedBiggestMeal = sharedPref.getString("biggest_meal_time", "00:00") ?: "00:00"
    val savedSleepTime = sharedPref.getString("sleep_time", "00:00") ?: "00:00"
    val savedWakeTime = sharedPref.getString("wake_up_time", "00:00") ?: "00:00"

    val foodCategories = listOf("Fruits", "Vegetables", "Grains", "Red Meat", "Seafood", "Poultry", "Fish", "Eggs", "Nuts/Seeds")
    val initialFoodCategories = remember {
        foodCategories.associateWith { category ->
            sharedPref.getBoolean(category, false)
        }
    }

    // Mutable states for UI updates
    val mTime = remember { mutableStateOf(savedTime) }
    val mTextFieldValue = remember { mutableStateOf(savedText) }
    var checkBoxState by remember { mutableStateOf(savedCheckBox) }
    var selectedPersona by remember { mutableStateOf(savedPersona) }
    var foodCategoriesState by remember { mutableStateOf(initialFoodCategories) }
    var biggestMealTime by remember { mutableStateOf(savedBiggestMeal) }
    var sleepTime by remember { mutableStateOf(savedSleepTime) }
    var wakeUpTime by remember { mutableStateOf(savedWakeTime) }

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
                FoodCategoryCheckboxes(
                    initialSelection = initialFoodCategories,
                    onSelectionChanged = { categories ->
                        foodCategoriesState = categories
                    }
                )

                PersonaModals()

                PersonaDropdown { persona ->
                    selectedPersona = persona
                }


                DateandTime(
                    biggestMealTime = biggestMealTime,
                    sleepTime = sleepTime,
                    wakeUpTime = wakeUpTime,
                    onBiggestMealTimeChange = { biggestMealTime = it },
                    onSleepTimeChange = { sleepTime = it },
                    onWakeUpTimeChange = { wakeUpTime = it }
                )

                // Save Button
                Button(
                    onClick = {
                        // Save to SharedPreferences
                        sharedPref.edit {
                            putString("selected_time", mTime.value)
                            putString("user_text", mTextFieldValue.value)
                            putBoolean("checkbox_state", checkBoxState)
                            putString("selected_persona", selectedPersona)
                            foodCategoriesState.forEach { (category, checked) ->
                                putBoolean(category, checked)
                            }
                            putString("biggest_meal_time", biggestMealTime)
                            putString("sleep_time", sleepTime)
                            putString("wake_up_time", wakeUpTime)
                        }
                        // Prepare the Intent to pass to HomeActivity
                        val intent = Intent(context, HomeActivity::class.java).apply {
                            putExtra("userId", userId)

                            // Add food categories if necessary
                            foodCategoriesState.forEach { (category, checked) ->
                                putExtra(category, checked)
                            }
                        }
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
    initialSelection: Map<String, Boolean>,
    onSelectionChanged: (Map<String, Boolean>) -> Unit
) {
    val foodCategories = listOf(
        "Fruits", "Vegetables", "Grains",
        "Red Meat", "Seafood", "Poultry",
        "Fish", "Eggs", "Nuts/Seeds"
    )

    val checkBoxStates = remember { mutableStateMapOf<String, Boolean>().apply {
        putAll(initialSelection)
    }}

    Column {
        Text(
            text = "Tick all the food categories you can eat",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val columns = List(3) { mutableListOf<String>() }
                foodCategories.forEachIndexed { index, category ->
                    columns[index % 3].add(category)
                }

                columns.forEach { columnItems ->
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        columnItems.forEach { category ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Checkbox(
                                    checked = checkBoxStates[category] == true,
                                    onCheckedChange = { isChecked ->
                                        checkBoxStates[category] = isChecked
                                        onSelectionChanged(checkBoxStates.toMap())
                                    }
                                )
                                Text(
                                    text = category,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    Spacer(Modifier.height(10.dp))
}
//Done
@Composable

fun PersonaModals() {
    var showHealthDevotee by remember { mutableStateOf(false) }
    var showMindfulEater by remember { mutableStateOf(false) }
    var showWellnessStriver by remember { mutableStateOf(false) }
    var showBalanceSeeker by remember { mutableStateOf(false) }
    var showHealthProcrastinator by remember { mutableStateOf(false) }
    var showFoodCarefree by remember { mutableStateOf(false) }

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

        // 3x2 Grid Layout
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { // Space between rows
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp) // Space between buttons
            ) {
                Button(
                    onClick = { showHealthDevotee = true },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Health Devotee", fontSize = 10.sp)
                }
                Button(
                    onClick = { showMindfulEater = true },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Mindful Eater", fontSize = 10.sp)
                }
                Button(
                    onClick = { showWellnessStriver = true },
                    modifier = Modifier.weight(1f).height(50.dp),
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
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Balance Seeker", fontSize = 10.sp)
                }
                Button(
                    onClick = { showHealthProcrastinator = true },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("Health Procrastinator", fontSize = 10.sp)
                }
                Button(
                    onClick = { showFoodCarefree = true },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Food Carefree", fontSize = 10.sp)
                }
            }
        }
    }

    // MODALS (Using if statements to display them when a button is clicked)
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
//Done
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
    onPersonaSelected: (String) -> Unit
) {
    val personas = listOf(
        "Health Devotee",
        "Mindful Eater",
        "Wellness Striver",
        "Balance Seeker",
        "Health Procrastinator",
        "Food Carefree"
    )

    var selectedPersona by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Text(
        text = "Which Persona best fits you?",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            value = if (selectedPersona.isEmpty()) "Select a field" else selectedPersona,
            onValueChange = { },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 1.dp)
                .menuAnchor(),
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
            personas.forEach { persona ->
                DropdownMenuItem(
                    text = { Text(persona) },
                    onClick = {
                        selectedPersona = persona
                        onPersonaSelected(persona)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DateandTime(
    biggestMealTime: String,
    sleepTime: String,
    wakeUpTime: String,
    onBiggestMealTimeChange: (String) -> Unit,
    onSleepTimeChange: (String) -> Unit,
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

        // Biggest meal time
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(
                text = "What time of day approx. do you normally eat your biggest meal?",
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    showTimePicker(context) { newTime ->
                        onBiggestMealTimeChange(newTime)
                    }
                },
                modifier = Modifier.width(100.dp)
            ) {
                Text(text = biggestMealTime)
            }
        }

        // Sleep time
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(
                text = "What time of day approx. do you go to sleep at night?",
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    showTimePicker(context) { newTime ->
                        onSleepTimeChange(newTime)
                    }
                },
                modifier = Modifier.width(100.dp)
            ) {
                Text(text = sleepTime)
            }
        }

        // Wake up time
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(
                text = "What time of day approx. do you wake up in the morning?",
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    showTimePicker(context) { newTime ->
                        onWakeUpTimeChange(newTime)
                    }
                },
                modifier = Modifier.width(100.dp)
            ) {
                Text(text = wakeUpTime)
            }
        }
    }
}

// Move these outside any composable functions
@SuppressLint("DefaultLocale")
fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {
    val mCalendar = Calendar.getInstance()
    val mHour = mCalendar.get(Calendar.HOUR_OF_DAY)
    val mMinute = mCalendar.get(Calendar.MINUTE)

    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            onTimeSelected(String.format("%02d:%02d", hourOfDay, minute))
        },
        mHour, mMinute, true
    ).show()
}

