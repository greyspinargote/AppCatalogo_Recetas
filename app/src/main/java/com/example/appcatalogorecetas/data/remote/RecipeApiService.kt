package com.example.appcatalogorecetas.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApiService {

    // Buscar recetas por nombre (ej: "chicken", "pasta")
    @GET("search.php")
    suspend fun buscarRecetas(
        @Query("s") query: String
    ): MealListResponse

    // Obtener el detalle completo de una receta por su id
    @GET("lookup.php")
    suspend fun obtenerDetalleReceta(
        @Query("i") id: String
    ): MealListResponse
}