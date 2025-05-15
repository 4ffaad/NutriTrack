package com.daffa_34076492.nutritrack.data

import android.content.Context
import com.daffa_34076492.nutritrack.data.local.AppDatabase
import com.daffa_34076492.nutritrack.data.model.FoodIntake

class FoodIntakeRepository(context: Context) {

    private val foodIntakeDao = AppDatabase.getDatabase(context).foodIntakeDao()

    suspend fun saveFoodIntake(foodIntake: FoodIntake) {
        foodIntakeDao.insertOrUpdate(foodIntake)
    }

    suspend fun getFoodIntakeForUser(userId: Int): FoodIntake? {
        return foodIntakeDao.getFoodIntakeByUserId(userId)
    }
}
