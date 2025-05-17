package com.daffa_34076492.nutritrack.data

import android.content.Context
import com.daffa_34076492.nutritrack.data.local.AppDatabase
import com.daffa_34076492.nutritrack.data.model.FoodIntake

class FoodIntakeRepository(context: Context) {
    /**
     * DAO instance to access FoodIntake data from the Room database.
     */
    private val foodIntakeDao = AppDatabase.getDatabase(context).foodIntakeDao()

    /**
     * Saves a FoodIntake record to the database.
     * If a record for the same user already exists, it will be updated.
     *
     * This is a suspend function and should be called from a coroutine or another suspend function.
     *
     * @param foodIntake The FoodIntake object containing food data to save or update.
     */
    suspend fun saveFoodIntake(foodIntake: FoodIntake) {
        foodIntakeDao.insertOrUpdate(foodIntake)
    }

    /**
     * Retrieves the FoodIntake record for a specific user by their user ID.
     *
     * This is a suspend function and should be called from a coroutine or another suspend function.
     *
     * @param userId The ID of the user whose food intake data is to be fetched.
     * @return The FoodIntake object for the given user, or null if no record is found.
     */
    suspend fun getFoodIntakeForUser(userId: Int): FoodIntake? {
        return foodIntakeDao.getFoodIntakeByUserId(userId)
    }

}
