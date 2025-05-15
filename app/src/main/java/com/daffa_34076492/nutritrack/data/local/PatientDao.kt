package com.daffa_34076492.nutritrack.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.daffa_34076492.nutritrack.model.Patient

@Dao
interface PatientDao {
    // Inserts a new patient into the database
    // Used during CSV population on first launch
    @Insert
    suspend fun insert(patient: Patient)

    // Checks if a user exists based on user ID
    // Used for user existence check during registration/login
    @Query("SELECT COUNT(*) FROM patient_table WHERE userId = :userId")
    suspend fun doesUserExist(userId: Int): Int

    // Fetches the full patient object for a given user ID
    // Used to validate phone number or check if password is already set
    @Query("SELECT * FROM patient_table WHERE userId = :userId LIMIT 1")
    suspend fun getPatientById(userId: Int): Patient?

    // Gets the total number of patients
    // Could be used for debugging, logging, or validation
    @Query("SELECT COUNT(*) FROM patient_table")
    suspend fun getPatientCount(): Int

    // Returns all user IDs as LiveData
    // Used in dropdown menus or selection lists
    @Query("SELECT userId FROM patient_table")
    fun getUserIds(): LiveData<List<Int>>

    // Verifies login credentials (user ID + password)
    // Used in the login screen logic
    @Query("SELECT COUNT(*) FROM patient_table WHERE userId = :userId AND password = :password")
    suspend fun verifyLogin(userId: Int, password: String): Int

    // Updates a user's password during registration
    // Called once the phone number is verified and password is being set
    @Query("UPDATE patient_table SET password = :newPassword WHERE userId = :userId")
    suspend fun updatePassword(userId: Int, newPassword: String): Int



}