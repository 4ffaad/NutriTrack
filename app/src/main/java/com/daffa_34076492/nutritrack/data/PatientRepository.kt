package com.daffa_34076492.nutritrack.data

import android.content.Context
import androidx.lifecycle.LiveData
import com.daffa_34076492.nutritrack.data.local.AppDatabase
import com.daffa_34076492.nutritrack.model.Patient
import com.opencsv.CSVReader
import java.io.InputStreamReader

class PatientRepository(private val context: Context) {

    private val patientDao = AppDatabase.getDatabase(context).patientDao()


    // Returns the total number of patients in the database
    suspend fun getPatientCount(): Int {
        return patientDao.getPatientCount()
    }

    // Returns a LiveData list of all registered user IDs
    fun getUserIds(): LiveData<List<Int>> {
        return patientDao.getUserIds()
    }

    // Returns 1 if login credentials are valid, otherwise 0
    suspend fun verifyLogin(userId: Int, password: String): Int {
        return patientDao.verifyLogin(userId, password)
    }

    // Returns true if the user already has a password set (i.e., is registered)
    suspend fun isPasswordSet(userId: Int): Boolean {
        val patient = patientDao.getPatientById(userId)
        return patient?.password?.isNotEmpty() == true
    }

    suspend fun getPatientById(userId: Int): Patient? {
        return patientDao.getPatientById(userId)
    }

    // Handles the user registration by matching phone number and setting password
    suspend fun registerUser(userId: Int, phone: String, password: String, name: String): Boolean {
        val patient = patientDao.getPatientById(userId) ?: return false

        // Check phone number match
        if (patient.phoneNumber != phone) {
            return false
        }

        // Check if already registered
        if (!patient.password.isNullOrEmpty()) {
            return false
        }

        // Update password (registration step)
        return patientDao.registerPassword(userId, password, name) > 0
    }

    // Loads all patient records from CSV and inserts them into the database (only called on first launch)
    suspend fun populateDatabaseFromCSV() {
        val inputStream = context.assets.open("data.csv")
        val reader = CSVReader(InputStreamReader(inputStream))

        val rows = reader.readAll()

        // Skip header row and iterate through the rest
        rows.drop(1).forEach { row ->
            val sex = row[2]

            // Utility to select male or female value based on sex
            fun pick(maleIndex: Int, femaleIndex: Int): Double {
                return if (sex == "Male") row[maleIndex].toDoubleOrNull() ?: 0.0
                else row[femaleIndex].toDoubleOrNull() ?: 0.0
            }

            val patient = Patient(
                userId = row[1].toInt(),
                phoneNumber = row[0],
                sex = sex,
                password = "",
                name = "",
                HEIFATotalScore = pick(3, 4),
                discretionaryHEIFAScore = pick(5, 6),
                discretionaryServeSize = row[7].toDoubleOrNull() ?: 0.0,
                vegetablesHEIFAScore = pick(8, 9),
                vegetablesWithLegumesAllocatedServeSize = row[10].toDoubleOrNull() ?: 0.0,
                legumesAllocatedVegetables = row[11].toDoubleOrNull() ?: 0.0,
                vegetablesVariationsScore = row[12].toDoubleOrNull() ?: 0.0,
                vegetablesCruciferous = row[13].toDoubleOrNull() ?: 0.0,
                vegetablesTuberAndBulb = row[14].toDoubleOrNull() ?: 0.0,
                vegetablesOther = row[15].toDoubleOrNull() ?: 0.0,
                legumes = row[16].toDoubleOrNull() ?: 0.0,
                vegetablesGreen = row[17].toDoubleOrNull() ?: 0.0,
                vegetablesRedAndOrange = row[18].toDoubleOrNull() ?: 0.0,
                fruitHEIFAScore = pick(19, 20),
                fruitServeSize = row[21].toDoubleOrNull() ?: 0.0,
                fruitVariationsScore = row[22].toDoubleOrNull() ?: 0.0,
                fruitPome = row[23].toDoubleOrNull() ?: 0.0,
                fruitTropicalAndSubTropical = row[24].toDoubleOrNull() ?: 0.0,
                fruitBerry = row[25].toDoubleOrNull() ?: 0.0,
                fruitStone = row[26].toDoubleOrNull() ?: 0.0,
                fruitCitrus = row[27].toDoubleOrNull() ?: 0.0,
                fruitOther = row[28].toDoubleOrNull() ?: 0.0,
                grainsAndCerealsHEIFAScore = pick(29, 30),
                grainsAndCerealsServeSize = row[31].toDoubleOrNull() ?: 0.0,
                grainsAndCerealsNonWholeGrains = row[32].toDoubleOrNull() ?: 0.0,
                wholeGrainsHEIFAScore = pick(33, 34),
                wholeGrainsServeSize = row[35].toDoubleOrNull() ?: 0.0,
                meatAndAlternativesHEIFAScore = pick(36, 37),
                meatAndAlternativesWithLegumesAllocatedServeSize = row[38].toDoubleOrNull() ?: 0.0,
                legumesAllocatedMeatAndAlternatives = row[39].toDoubleOrNull() ?: 0.0,
                dairyAndAlternativesHEIFAScore = pick(40, 41),
                dairyAndAlternativesServeSize = row[42].toDoubleOrNull() ?: 0.0,
                sodiumHEIFAScore = pick(43, 44),
                sodiumMgMilligrams = row[45].toDoubleOrNull() ?: 0.0,
                alcoholHEIFAScore = pick(46, 47),
                alcoholStandardDrinks = row[48].toDoubleOrNull() ?: 0.0,
                waterHEIFAScore = pick(49, 50),
                water = row[51].toDoubleOrNull() ?: 0.0,
                waterTotalMl = row[52].toDoubleOrNull() ?: 0.0,
                beverageTotalMl = row[53].toDoubleOrNull() ?: 0.0,
                sugarHEIFAScore = pick(54, 55),
                sugar = row[56].toDoubleOrNull() ?: 0.0,
                saturatedFatHEIFAScore = pick(57, 58),
                saturatedFat = row[59].toDoubleOrNull() ?: 0.0,
                unsaturatedFatHEIFAScore = pick(60, 61),
                unsaturatedFatServeSize = row[62].toDoubleOrNull() ?: 0.0,
            )

            patientDao.insert(patient)
        }

        reader.close()
    }

    suspend fun verifyUser(userId: Int, phoneNumber: String): Boolean {
        return patientDao.verifyUserByIdAndPhone(userId, phoneNumber) > 0
    }

    suspend fun resetPassword(userId: Int, newPassword: String): Boolean {
        return patientDao.updatePassword(userId, newPassword) > 0
    }

    suspend fun getHEIFAScore(userId: Int): Double? {
        return patientDao.getHEIFAScore(userId)
    }


    suspend fun getPatientByUserId(userId: Int): Patient? {
        return patientDao.getFoodScoresByUserId(userId)
    }

    suspend fun isUserOptimal(userId: Int): Boolean {
        val patient = getPatientByUserId(userId) ?: return false
        val sex = patient.sex.lowercase()

        return if (sex == "male") {
            (patient.discretionaryServeSize < 3.0) &&
                    (patient.vegetablesWithLegumesAllocatedServeSize >= 6.0) &&
                    (patient.fruitServeSize >= 2.0 && patient.fruitVariationsScore >= 2.0) &&
                    (patient.grainsAndCerealsServeSize >= 6.0 && patient.wholeGrainsServeSize >= patient.grainsAndCerealsServeSize * 0.5) &&
                    (patient.meatAndAlternativesWithLegumesAllocatedServeSize >= 3.0) &&
                    (patient.dairyAndAlternativesServeSize >= 2.5) &&
                    (patient.waterTotalMl >= 1500 && patient.waterTotalMl >= 0.5 * patient.beverageTotalMl) &&
                    (patient.unsaturatedFatServeSize >= 4.0) &&
                    (patient.saturatedFat <= 10.0) &&
                    (patient.sodiumMgMilligrams <= 920.0) &&
                    (patient.sugar <= 15.0) &&
                    (patient.alcoholStandardDrinks <= 1.4)
        } else if (sex == "female") {
            (patient.discretionaryServeSize < 2.5) &&
                    (patient.vegetablesWithLegumesAllocatedServeSize >= 5.0) &&
                    (patient.fruitServeSize >= 2.0 && patient.fruitVariationsScore >= 2.0) &&
                    (patient.grainsAndCerealsServeSize >= 6.0 && patient.wholeGrainsServeSize >= patient.grainsAndCerealsServeSize * 0.5) &&
                    (patient.meatAndAlternativesWithLegumesAllocatedServeSize >= 2.5) &&
                    (patient.dairyAndAlternativesServeSize >= 2.5) &&
                    (patient.waterTotalMl >= 1500 && patient.waterTotalMl >= 0.5 * patient.beverageTotalMl) &&
                    (patient.unsaturatedFatServeSize >= 2.0) &&
                    (patient.saturatedFat <= 10.0) &&
                    (patient.sodiumMgMilligrams <= 920.0) &&
                    (patient.sugar <= 15.0) &&
                    (patient.alcoholStandardDrinks <= 1.4)
        } else {
            false
        }
    }

}
