package com.example.appcatalogorecetas.data.repository

import com.example.appcatalogorecetas.data.local.Receta
import com.example.appcatalogorecetas.data.local.RecetaDao
import com.example.appcatalogorecetas.data.remote.RecipeApiService
import kotlinx.coroutines.flow.Flow

class RecetaRepository(
    private val apiService: RecipeApiService,
    private val recetaDao: RecetaDao
) {

    // 1. OPERACIONES LOCALES (ROOM)

    val recetasFavoritas: Flow<List<Receta>> = recetaDao.obtenerTodas()

    suspend fun guardarFavorito(receta: Receta) {
        recetaDao.insertar(receta)
    }

    suspend fun eliminarFavorito(receta: Receta) {
        recetaDao.eliminar(receta)
    }

    suspend fun obtenerFavoritoPorId(id: String): Receta? {
        return recetaDao.obtenerPorId(id)
    }

    // 2. OPERACIONES REMOTAS (TheMealDB)

    suspend fun buscarRecetasRemotas(query: String): List<Receta> {
        return try {
            val respuesta = apiService.buscarRecetas(query)
            respuesta.meals?.map { it.aReceta() } ?: emptyList()
        } catch (e: Exception) {
            android.util.Log.e("Recetario", "Error al buscar recetas: ${e.message}")
            emptyList()
        }
    }

    suspend fun obtenerDetalleRemoto(id: String): Receta? {
        return try {
            val respuesta = apiService.obtenerDetalleReceta(id)
            respuesta.meals?.firstOrNull()?.aReceta()
        } catch (e: Exception) {
            android.util.Log.e("Recetario", "Error al obtener detalle: ${e.message}")
            null
        }
    }
}

// Función de mapeo: convierte el DTO de la API en la entidad de Room
private fun com.example.appcatalogorecetas.data.remote.MealDto.aReceta(): Receta {
    return Receta(
        id = idMeal,
        nombre = strMeal,
        categoria = strCategory,
        instrucciones = strInstructions,
        imagenUrl = strMealThumb
    )
}