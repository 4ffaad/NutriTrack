package com.daffa_34076492.nutritrack.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.daffa_34076492.nutritrack.data.model.FoodIntake
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodIntakeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(foodIntake: FoodIntake)

    @Query("SELECT * FROM food_intake WHERE userId = :userId LIMIT 1")
    suspend fun getFoodIntakeByUserId(userId: Int): FoodIntake?
}