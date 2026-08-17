package com.example.appcatalogorecetas.data.remote

import com.google.gson.annotations.SerializedName

// Respuesta principal: TheMealDB manda la lista dentro de "meals"
data class MealListResponse(
    @SerializedName("meals")
    val meals: List<MealDto>?
)

// Modelo de CADA receta que viene de internet
data class MealDto(
    @SerializedName("idMeal") val idMeal: String,
    @SerializedName("strMeal") val strMeal: String,
    @SerializedName("strCategory") val strCategory: String?,
    @SerializedName("strInstructions") val strInstructions: String?,
    @SerializedName("strMealThumb") val strMealThumb: String?
)