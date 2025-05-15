package com.daffa_34076492.nutritrack.ViewModels

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.daffa_34076492.nutritrack.data.PatientRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for handling patient authentication and registration logic.
 */
class PatientViewModel(context: Context) : ViewModel() {

    private val patientRepo = PatientRepository(context)

    // Private mutable states
    private val _passwordError = mutableStateOf("")
    val passwordError: State<String> get() = _passwordError

    private val _userAlreadyRegisteredMessage = mutableStateOf("")
    val userAlreadyRegisteredMessage: State<String> get() = _userAlreadyRegisteredMessage

    // LiveData for observing available user IDs
    val userIds: LiveData<List<Int>> = patientRepo.getUserIds()

    init {
        // Pre-populate database from CSV on first launch if empty
        viewModelScope.launch {
            if (isDatabaseEmpty()) {
                patientRepo.populateDatabaseFromCSV()
            }
        }
    }

    /**
     * Updates password for the given userId.
     * @return Number of rows updated.
     */
    suspend fun updatePassword(userId: Int, password: String): Int {
        return patientRepo.updatePassword(userId, password)
    }

    /**
     * Checks if the patient table in the database is empty.
     */
    private suspend fun isDatabaseEmpty(): Boolean {
        return patientRepo.getPatientCount() == 0
    }

    /**
     * Updates password error state based on current input.
     */
    fun onPasswordChange(password: String, confirmPassword: String) {
        _passwordError.value = when {
            password.length < 8 -> "Password must be at least 8 characters"
            confirmPassword.isNotEmpty() && password != confirmPassword -> "Passwords do not match"
            else -> ""
        }
    }

    /**
     * Checks if the password is already set for the user.
     */
    fun checkIfPasswordSet(userId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isSet = patientRepo.isPasswordSet(userId)
            onResult(isSet)
        }
    }

    /**
     * Validates password and confirmPassword input.
     * @return Error message or empty string if valid.
     */
    fun validatePassword(password: String, confirmPassword: String): String {
        return when {
            password.length < 8 -> "Password must be at least 8 characters long"
            password != confirmPassword -> "Passwords do not match"
            else -> ""
        }
    }

    /**
     * Verifies login credentials.
     * @param onResult Callback with success status.
     */
    fun verifyLogin(userId: Int, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isValid = patientRepo.verifyLogin(userId, password) != 0
            onResult(isValid)
        }
    }

    /**
     * Registers user by setting phone and password if patient exists and hasn't registered.
     * @param callback Returns success/failure.
     */
    fun registerUser(userId: Int, phone: String, password: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val patient = patientRepo.getPatientById(userId)

            if (patient == null || patient.phoneNumber != phone || !patient.password.isNullOrEmpty()) {
                callback(false)
                return@launch
            }

            val updated = patientRepo.registerUser(userId, phone, password)
            callback(updated)
        }
    }

    /**
     * Factory for instantiating the ViewModel with a context.
     */
    class PatientViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PatientViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PatientViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}