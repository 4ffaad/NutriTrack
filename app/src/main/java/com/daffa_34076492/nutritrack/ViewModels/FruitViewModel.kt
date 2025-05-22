package com.daffa_34076492.nutritrack.ViewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.daffa_34076492.nutritrack.data.MotivationalMessageRepository
import com.daffa_34076492.nutritrack.data.PatientRepository
import com.daffa_34076492.nutritrack.model.MotivationalMessage
import com.daffa_34076492.nutritrack.model.Patient
import com.yourpackage.data.api.FruityViceService
import com.yourpackage.data.model.FruitInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FruitViewModel(
    private val repository: MotivationalMessageRepository,
    private val patientRepo: PatientRepository
) : ViewModel() {

    var selectedFruit = mutableStateOf<FruitInfo?>(null)
        private set

    var isLoading = mutableStateOf(false)
        private set

    var fruitErrorMessage = mutableStateOf<String?>(null)
        private set

    var searchQuery by mutableStateOf("")
        private set

    private val _motivationalMessage = MutableStateFlow<String?>(null)
    val motivationalMessage = _motivationalMessage.asStateFlow()

    private val _isLoadingMotivation = MutableStateFlow(false)
    val isLoadingMotivation = _isLoadingMotivation.asStateFlow()

    private val _motivationError = MutableStateFlow<String?>(null)
    val motivationError = _motivationError.asStateFlow()

    private val _allMessages = MutableStateFlow<List<MotivationalMessage>>(emptyList())
    val allMessages: StateFlow<List<MotivationalMessage>> = _allMessages

    private val _patternInsights = MutableStateFlow<List<String>>(emptyList())
    val patternInsights: StateFlow<List<String>> = _patternInsights

    private val _isLoading = mutableStateOf(false)

    private val _isLoadingDataPatterns = MutableStateFlow(false)

    val isLoadingDataPatterns: StateFlow<Boolean> = _isLoadingDataPatterns


    init {
        loadDefaultFruit()
    }

    fun loadDefaultFruit() {
        viewModelScope.launch {
            isLoading.value = true
            fruitErrorMessage.value = null
            try {
                selectedFruit.value = FruityViceService.api.getFruitDetails("banana")
            } catch (e: Exception) {
                selectedFruit.value = null
                fruitErrorMessage.value = "Couldn't load default fruit."
            }
            isLoading.value = false
        }
    }

    fun searchFruit(name: String) {
        viewModelScope.launch {
            isLoading.value = true
            fruitErrorMessage.value = null
            try {
                println("Searching fruit: $name")
                val fruitDetails = FruityViceService.api.getFruitDetails(name.lowercase())
                println("Got fruit details: $fruitDetails")
                selectedFruit.value = fruitDetails
            } catch (e: Exception) {
                println("Error searching fruit: ${e.message}")
                selectedFruit.value = null
                fruitErrorMessage.value = "Fruit not found or network error."
            }
            isLoading.value = false
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
    }

    fun fetchMotivationalMessage(userId: Int) {
        if (_motivationalMessage.value != null) return // Already fetched

        viewModelScope.launch {
            _isLoadingMotivation.value = true
            _motivationError.value = null
            try {
                val patient = patientRepo.getPatientByUserId(userId)
                if (patient == null) {
                    _motivationError.value = "❌ Patient not found."
                    return@launch
                }

                val prompt = buildString {
                    append("Generate a motivational message for a patient with the following details:\n")
                    append("• Name: ${patient.name}\n")
                    append("• Gender: ${patient.sex}\n")
                    append("• HEIFA Score: ${patient.HEIFATotalScore}\n")
                    append("The message should be short, powerful, and encourage healthy eating. Output only the message, no explanations.")
                }

                val messageText = repository.getMotivationalMessage(prompt)
                _motivationalMessage.value = messageText

                val motivationalMessage = MotivationalMessage(
                    userId = userId,
                    message = messageText,
                    timestamp = System.currentTimeMillis()
                )
                repository.saveMessage(motivationalMessage)

            } catch (e: Exception) {
                _motivationError.value = "❌ Failed to fetch motivational message."
                Log.e("FruitViewModel", "Motivation fetch failed", e)
            } finally {
                _isLoadingMotivation.value = false
            }
        }
    }

    fun loadMessagesForUser(userId: Int) {
        viewModelScope.launch {
            _allMessages.value = repository.getMessagesForUser(userId)
        }
    }
    fun clearAllMessagesForUser(userId: Int) {
        viewModelScope.launch {
            repository.clearMessagesForUser(userId)
            loadMessagesForUser(userId)
        }
    }
    fun analyzeDataPatterns() {
        viewModelScope.launch {
            _isLoadingDataPatterns.value = true
            try {
                val patients = patientRepo.getAllPatients()
                val prompt = buildPromptFromPatients(patients)
                val patterns = repository.getDataPatterns(prompt)
                _patternInsights.value = patterns
            } catch (e: Exception) {
                Log.e("ClinicianViewModel", "Error analyzing patterns: ${e.message}", e)
            } finally {
                // Use the correct loading state variable here:
                _isLoadingDataPatterns.value = false
            }
        }
    }

    fun buildPromptFromPatients(patients: List<Patient>): String {
        val csvHeader = "Sex,HEIFATotal,Discretionary,Vegetables,Fruit,Grains,Cereals,WholeGrains,MeatAlt,Dairy,Sodium,Alcohol,Water,Sugar,Fat"
        val csvData = patients.joinToString("\n") { patient ->
            listOf(
                patient.sex,
                patient.HEIFATotalScore,
                patient.discretionaryHEIFAScore,
                patient.vegetablesHEIFAScore,
                patient.fruitHEIFAScore,
                patient.grainsAndCerealsHEIFAScore,
                patient.wholeGrainsHEIFAScore,
                patient.meatAndAlternativesHEIFAScore,
                patient.dairyAndAlternativesHEIFAScore,
                patient.sodiumHEIFAScore,
                patient.alcoholHEIFAScore,
                patient.waterHEIFAScore,
                patient.sugarHEIFAScore,
                patient.saturatedFatHEIFAScore
            ).joinToString(",")
        }

        return """
        Analyze the following patient data in CSV format:
        $csvHeader
        $csvData

        Based on the dataset, identify and explain 3 interesting data patterns.
        Format your response like:
        1. ...
        2. ...
        3. ...

        Focus on trends like gender differences, correlations between scores (e.g. fruit vs. vegetable), or general nutrition behavior and keep it short.
    """.trimIndent()
    }


}


/**
 * Factory for creating FruitViewModel with a MotivationalMessageRepository.
 */
class FruitViewModelFactory(
    private val messageRepo: MotivationalMessageRepository,
    private val patientRepo: PatientRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FruitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FruitViewModel(messageRepo, patientRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}




