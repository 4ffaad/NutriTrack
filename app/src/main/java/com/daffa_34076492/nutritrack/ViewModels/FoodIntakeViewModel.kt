package com.daffa_34076492.nutritrack.ViewModels

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.daffa_34076492.nutritrack.data.FoodIntakeRepository
import com.daffa_34076492.nutritrack.data.model.FoodIntake
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FoodIntakeViewModel(context: Context) : ViewModel() {

    private val repository = FoodIntakeRepository(context)

    private val _foodIntake = MutableStateFlow<FoodIntake?>(null)
    val foodIntake: StateFlow<FoodIntake?> = _foodIntake

    // Method to load food intake for a user
    fun loadFoodIntake(userId: Int) {
        viewModelScope.launch {
            val foodIntakeData = repository.getFoodIntakeForUser(userId)
            _foodIntake.value = foodIntakeData
        }
    }

    fun saveFoodIntake(foodIntake: FoodIntake) {
        viewModelScope.launch {
            repository.saveFoodIntake(foodIntake)
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FoodIntakeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FoodIntakeViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
