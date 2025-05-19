package com.daffa_34076492.nutritrack.ViewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.daffa_34076492.nutritrack.data.MotivationalMessageRepository
import com.daffa_34076492.nutritrack.model.MotivationalMessage
import com.yourpackage.data.api.FruityViceService
import com.yourpackage.data.model.FruitInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FruitViewModel(
    private val repository: MotivationalMessageRepository
) : ViewModel() {
    var selectedFruit = mutableStateOf<FruitInfo?>(null)
        private set

    var isLoading = mutableStateOf(false)
        private set

    var fruitErrorMessage = mutableStateOf<String?>(null)
        private set


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
    var searchQuery by mutableStateOf("")
        private set

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
    }


    init {
        loadDefaultFruit()
    }


    private val _motivationalMessage = MutableStateFlow<String?>(null)
    val motivationalMessage = _motivationalMessage.asStateFlow()

    private val _isLoadingMotivation = MutableStateFlow(false)
    val isLoadingMotivation = _isLoadingMotivation.asStateFlow()

    private val _motivationError = MutableStateFlow<String?>(null)
    val motivationError = _motivationError.asStateFlow()


    fun fetchMotivationalMessage() {
        viewModelScope.launch {
            _isLoadingMotivation.value = true
            _motivationError.value = null
            try {
                val promptOptions = listOf(
                    "Give me a short, powerful motivational quote to eat healthy. Output only the quote, no explanations.",
                    "Provide a short motivational message encouraging me to eat more fruits and vegetables. Just the message, no extra text.",
                    "Say something energizing to inspire healthy eating in one sentence. Output only the sentence.",
                    "Encourage me to make healthy food choices with a strong sentence. Only output the sentence, nothing else.",
                    "Give a motivational message about healthy eating. Do not explain—just the message.",
                    "What would a motivational coach say to encourage eating more fruits and veggies? Output only the motivational message.",

                )

                val prompt = promptOptions.random() + " (Make it new each time.)"

                val message = repository.getMotivationalMessage(prompt)

                _motivationalMessage.value = message
            } catch (e: Exception) {
                _motivationError.value = "❌ Failed to fetch motivational message."
                Log.e("FruitViewModel", "Motivation fetch failed", e)
            } finally {
                _isLoadingMotivation.value = false
            }
        }
    }


    fun saveMotivationalMessage(userId: Int, message: String) {
        viewModelScope.launch {
            repository.saveMessage(MotivationalMessage(
                userId = userId,
                message = message
            ))
        }
    }

    /**
     * Factory for creating FruitViewModel with a MotivationalMessageRepository.
     */
    class Factory(
        private val repository: MotivationalMessageRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FruitViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FruitViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
