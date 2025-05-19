package com.yourpackage.data.model

data class FruitInfo(
    val name: String,
    val family: String,
    val nutritions: Nutrition
)

data class Nutrition(
    val calories: Float,
    val fat: Float,
    val sugar: Float,
    val carbohydrates: Float,
    val protein: Float
)
