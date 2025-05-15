package com.daffa_34076492.nutritrack.data.model

import android.R
import androidx.room.*
import com.daffa_34076492.nutritrack.model.Patient

@Entity(
    tableName = "food_intake",
    foreignKeys = [ForeignKey(
        entity = Patient::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("userId")]
)
data class FoodIntake(
    @PrimaryKey
    val userId: Int,
    val persona: String,
    val biggestMealTime: String,
    val sleepTime: String,
    val wakeUpTime: String,
    val eatsFruits: Boolean,
    val eatsVegetables: Boolean,
    val eatsGrains: Boolean,
    val eatsRedMeat: Boolean,
    val eatsSeafood: Boolean,
    val eatsPoultry: Boolean,
    val eatsFish: Boolean,
    val eatsEggs: Boolean,
    val eatsNutsSeeds: Boolean
)
