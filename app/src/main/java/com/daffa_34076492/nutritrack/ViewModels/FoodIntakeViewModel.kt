package com.daffa_34076492.nutritrack.ViewModels

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.daffa_34076492.nutritrack.data.FoodIntakeRepository
import com.daffa_34076492.nutritrack.data.model.FoodIntake
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FoodIntakeViewModel(context: Context) : ViewModel() {

    private val repository = FoodIntakeRepository(context)

    private val _foodIntake = MutableStateFlow<FoodIntake?>(null)
    data class FoodIntakeFormState(
        val persona: String = "",
        val eatsFruits: Boolean = false,
        val eatsVegetables: Boolean = false,
        val eatsGrains: Boolean = false,
        val eatsRedMeat: Boolean = false,
        val eatsSeafood: Boolean = false,
        val eatsPoultry: Boolean = false,
        val eatsFish: Boolean = false,
        val eatsEggs: Boolean = false,
        val eatsNutsSeeds: Boolean = false,
        val biggestMealTime: String = "",
        val sleepTime: String = "",
        val wakeUpTime: String = ""
    )


    private val _formState = mutableStateOf(FoodIntakeFormState())
    val formState: State<FoodIntakeFormState> = _formState

    // Load from DB into both the internal state and UI state
    fun loadFoodIntake(userId: Int) {
        viewModelScope.launch {
            val intake = repository.getFoodIntakeForUser(userId)
            _foodIntake.value = intake

            intake?.let {
                _formState.value = FoodIntakeFormState(
                    persona = it.persona,
                    eatsFruits = it.eatsFruits,
                    eatsVegetables = it.eatsVegetables,
                    eatsGrains = it.eatsGrains,
                    eatsRedMeat = it.eatsRedMeat,
                    eatsSeafood = it.eatsSeafood,
                    eatsPoultry = it.eatsPoultry,
                    eatsFish = it.eatsFish,
                    eatsEggs = it.eatsEggs,
                    eatsNutsSeeds = it.eatsNutsSeeds,
                    biggestMealTime = it.biggestMealTime,
                    sleepTime = it.sleepTime,
                    wakeUpTime = it.wakeUpTime
                )
            }
        }
    }

    fun onPersonaChange(persona: String) {
        _formState.value = _formState.value.copy(persona = persona)
    }

    fun onFoodCategoryToggle(category: String, checked: Boolean) {
        _formState.value = when (category) {
            "Fruits" -> _formState.value.copy(eatsFruits = checked)
            "Vegetables" -> _formState.value.copy(eatsVegetables = checked)
            "Grains" -> _formState.value.copy(eatsGrains = checked)
            "Red Meat" -> _formState.value.copy(eatsRedMeat = checked)
            "Seafood" -> _formState.value.copy(eatsSeafood = checked)
            "Poultry" -> _formState.value.copy(eatsPoultry = checked)
            "Fish" -> _formState.value.copy(eatsFish = checked)
            "Eggs" -> _formState.value.copy(eatsEggs = checked)
            "Nuts/Seeds" -> _formState.value.copy(eatsNutsSeeds = checked)
            else -> _formState.value
        }
    }

    fun onTimeChange(
        biggestMeal: String? = null,
        sleep: String? = null,
        wakeUp: String? = null
    ) {
        _formState.value = _formState.value.copy(
            biggestMealTime = biggestMeal ?: _formState.value.biggestMealTime,
            sleepTime = sleep ?: _formState.value.sleepTime,
            wakeUpTime = wakeUp ?: _formState.value.wakeUpTime
        )
    }

    fun saveCurrentForm(userId: Int) {
        viewModelScope.launch {
            val form = _formState.value
            val updated = FoodIntake(
                userId = userId,
                persona = form.persona,
                eatsFruits = form.eatsFruits,
                eatsVegetables = form.eatsVegetables,
                eatsGrains = form.eatsGrains,
                eatsRedMeat = form.eatsRedMeat,
                eatsSeafood = form.eatsSeafood,
                eatsPoultry = form.eatsPoultry,
                eatsFish = form.eatsFish,
                eatsEggs = form.eatsEggs,
                eatsNutsSeeds = form.eatsNutsSeeds,
                biggestMealTime = form.biggestMealTime,
                sleepTime = form.sleepTime,
                wakeUpTime = form.wakeUpTime
            )
            repository.saveFoodIntake(updated)
        }
    }


    // Factory to construct ViewModel
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

