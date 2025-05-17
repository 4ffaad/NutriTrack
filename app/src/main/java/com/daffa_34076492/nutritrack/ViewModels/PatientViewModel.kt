package com.daffa_34076492.nutritrack.ViewModels

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.daffa_34076492.nutritrack.auth.AuthManager.userId
import com.daffa_34076492.nutritrack.data.PatientRepository
import com.daffa_34076492.nutritrack.model.Patient
import kotlinx.coroutines.launch

/**
 * ViewModel for handling patient authentication and registration logic.
 */
class PatientViewModel(context: Context) : ViewModel() {

    private val patientRepo = PatientRepository(context)

    var userIdInput = mutableStateOf("")
    var phoneNumberInput = mutableStateOf("")
    var newPasswordInput = mutableStateOf("")
    var confirmPasswordInput = mutableStateOf("")
    var resetMessage = mutableStateOf<String?>(null)

    fun onUserIdChange(newVal: String) { userIdInput.value = newVal }
    fun onPhoneNumberChange(newVal: String) { phoneNumberInput.value = newVal }
    fun onNewPasswordChange(newVal: String) { newPasswordInput.value = newVal }
    fun onConfirmPasswordChange(newVal: String) { confirmPasswordInput.value = newVal }

    // LiveData for user IDs
    val userIds: LiveData<List<Int>> = patientRepo.getUserIds()

    init {
        viewModelScope.launch {
            if (patientRepo.getPatientCount() == 0) {
                patientRepo.populateDatabaseFromCSV()
            }
        }
    }

    fun checkIfPasswordSet(userId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            onResult(patientRepo.isPasswordSet(userId))
        }
    }

    fun validatePassword(password: String, confirmPassword: String): String {
        return when {
            password.length < 8 -> "Password must be at least 8 characters long"
            password != confirmPassword -> "Passwords do not match"
            else -> ""
        }
    }

    fun verifyLogin(userId: Int, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            onResult(patientRepo.verifyLogin(userId, password) != 0)
        }
    }

    fun registerUser(userId: Int, phone: String, password: String, name: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val patient = patientRepo.getPatientById(userId)
            if (patient == null || patient.phoneNumber != phone || !patient.password.isNullOrEmpty()) {
                callback(false)
                return@launch
            }
            callback(patientRepo.registerUser(userId, phone, password, name))
        }
    }

    private val _heifaScore = mutableStateOf<Double?>(null)
    val heifaScore: State<Double?> = _heifaScore

    fun loadHEIFAScore(userId: Int) {
        viewModelScope.launch {
            _heifaScore.value = patientRepo.getHEIFAScore(userId)
        }
    }

    private val _patientData = MutableLiveData<Patient?>()
    val patientData: LiveData<Patient?> = _patientData

    fun loadPatientData(userId: Int) {
        viewModelScope.launch {
            _patientData.postValue(patientRepo.getFoodScoresByUserId(userId))
        }
    }

    // Clinician authentication
    var passphraseInput by mutableStateOf("")
    var isAuthenticated by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val correctPassphrase = "dollar-entry-apples"

    fun authenticate() {
        isAuthenticated = passphraseInput == correctPassphrase
        errorMessage = if (!isAuthenticated) "Incorrect passphrase" else null
    }

    // Phone verification
    var verificationPassed by mutableStateOf(false)
    var verificationError by mutableStateOf<String?>(null)

    fun verifyUserByPhone(userId: Int, phoneNumber: String) {
        viewModelScope.launch {
            try {
                val isValid = patientRepo.verifyUser(userId, phoneNumber)
                verificationPassed = isValid
                verificationError = if (isValid) null else "No matching user found."
            } catch (e: Exception) {
                verificationError = "Verification failed: ${e.message}"
            }
        }
    }
    suspend fun resetPassword(): Boolean {
        val userId = userIdInput.value.toIntOrNull()
        val phone = phoneNumberInput.value
        val newPass = newPasswordInput.value
        val confirmPass = confirmPasswordInput.value

        if (userId == null || phone.isBlank() || newPass.isBlank() || confirmPass.isBlank()) {
            resetMessage.value = "All fields must be filled in."
            return false
        }

        if (newPass != confirmPass) {
            resetMessage.value = "Passwords do not match."
            return false
        }

        return try {
            val verified = patientRepo.verifyUser(userId, phone)
            if (verified) {
                patientRepo.resetPassword(userId, newPass)
                resetMessage.value = "Password reset successful!"
                true
            } else {
                resetMessage.value = "Invalid user ID or phone number."
                false
            }
        } catch (e: Exception) {
            resetMessage.value = "Error: ${e.message}"
            false
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