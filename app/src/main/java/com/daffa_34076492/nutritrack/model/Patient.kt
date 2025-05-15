

package com.daffa_34076492.nutritrack.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patient_table")
data class Patient(
    @PrimaryKey
    val userId: Int = 0,
    val phoneNumber: String,
    val sex: String,
    val HEIFATotalScore: Double,
    val discretionaryHEIFAScore: Double,
    val discretionaryServeSize: Double,
    val vegetablesHEIFAScore: Double,
    val vegetablesWithLegumesAllocatedServeSize: Double,
    val legumesAllocatedVegetables: Double,
    val vegetablesVariationsScore: Double,
    val vegetablesCruciferous: Double,
    val vegetablesTuberAndBulb: Double,
    val vegetablesOther: Double,
    val legumes: Double,
    val vegetablesGreen: Double,
    val vegetablesRedAndOrange: Double,
    val fruitHEIFAScore: Double,
    val fruitServeSize: Double,
    val fruitVariationsScore: Double,
    val fruitPome: Double,
    val fruitTropicalAndSubTropical: Double,
    val fruitBerry: Double,
    val fruitStone: Double,
    val fruitCitrus: Double,
    val fruitOther: Double,
    val grainsAndCerealsHEIFAScore: Double,
    val grainsAndCerealsServeSize: Double,
    val grainsAndCerealsNonWholeGrains: Double,
    val wholeGrainsHEIFAScore: Double,
    val wholeGrainsServeSize: Double,
    val meatAndAlternativesHEIFAScore: Double,
    val meatAndAlternativesWithLegumesAllocatedServeSize: Double,
    val legumesAllocatedMeatAndAlternatives: Double,
    val dairyAndAlternativesHEIFAScore: Double,
    val dairyAndAlternativesServeSize: Double,
    val sodiumHEIFAScore: Double,
    val sodiumMgMilligrams: Double,
    val alcoholHEIFAScore: Double,
    val alcoholStandardDrinks: Double,
    val waterHEIFAScore: Double,
    val water: Double,
    val waterTotalMl: Double,
    val beverageTotalMl: Double,
    val sugarHEIFAScore: Double,
    val sugar: Double,
    val saturatedFatHEIFAScore: Double,
    val saturatedFat: Double,
    val unsaturatedFatHEIFAScore: Double,
    val unsaturatedFatServeSize: Double,
    val password: String?
)