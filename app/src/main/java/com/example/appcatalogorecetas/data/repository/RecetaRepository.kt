package com.example.appcatalogorecetas.data.repository

import com.example.appcatalogorecetas.data.local.Receta
import com.example.appcatalogorecetas.data.local.RecetaDao
import com.example.appcatalogorecetas.data.remote.MealDto
import com.example.appcatalogorecetas.data.remote.RecipeApiService
import com.example.appcatalogorecetas.data.remote.TranslateApiService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

class RecetaRepository(
    private val apiService: RecipeApiService,
    private val translateApiService: TranslateApiService,
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

    // 2. OPERACIONES REMOTAS (TheMealDB) — ya traducidas al español

    suspend fun buscarRecetasRemotas(query: String): List<Receta> {
        return try {
            val respuesta = apiService.buscarRecetas(query)
            val recetasEnIngles = respuesta.meals?.map { it.aReceta() } ?: emptyList()

            coroutineScope {
                val diferidas: List<Deferred<Receta>> = recetasEnIngles.map { receta ->
                    async {
                        receta.copy(
                            nombre = traducirTexto(receta.nombre),
                            categoria = traducirCategoria(receta.categoria)
                        )
                    }
                }
                diferidas.map { it.await() }
            }
        } catch (e: Exception) {
            android.util.Log.e("Recetario", "Error al buscar recetas: ${e.message}")
            emptyList()
        }
    }

    suspend fun obtenerDetalleRemoto(id: String): Receta? {
        return try {
            val respuesta = apiService.obtenerDetalleReceta(id)
            val recetaEnIngles = respuesta.meals?.firstOrNull()?.aReceta() ?: return null

            recetaEnIngles.copy(
                nombre = traducirTexto(recetaEnIngles.nombre),
                categoria = traducirCategoria(recetaEnIngles.categoria),
                instrucciones = traducirTexto(recetaEnIngles.instrucciones ?: "")
            )
        } catch (e: Exception) {
            android.util.Log.e("Recetario", "Error al obtener detalle: ${e.message}")
            null
        }
    }

    // 3. TRADUCCIÓN

    private suspend fun traducirTexto(texto: String): String {
        if (texto.isBlank()) return texto
        return try {
            val fragmentos = dividirEnFragmentos(texto, 450)
            val traducidos = fragmentos.map { fragmento ->
                translateApiService.traducir(fragmento).responseData.translatedText
            }
            traducidos.joinToString(" ")
        } catch (e: Exception) {
            android.util.Log.e("Recetario", "Error al traducir: ${e.message}")
            texto
        }
    }

    private fun dividirEnFragmentos(texto: String, maxLength: Int): List<String> {
        val oraciones = texto.split(". ")
        val fragmentos = mutableListOf<String>()
        var actual = StringBuilder()
        for (oracion in oraciones) {
            if (actual.length + oracion.length + 2 > maxLength) {
                if (actual.isNotEmpty()) fragmentos.add(actual.toString())
                actual = StringBuilder()
            }
            if (actual.isNotEmpty()) actual.append(". ")
            actual.append(oracion)
        }
        if (actual.isNotEmpty()) fragmentos.add(actual.toString())
        return fragmentos
    }

    private val categoriasTraducidas = mapOf(
        "Beef" to "Res",
        "Chicken" to "Pollo",
        "Dessert" to "Postre",
        "Lamb" to "Cordero",
        "Miscellaneous" to "Varios",
        "Pasta" to "Pasta",
        "Pork" to "Cerdo",
        "Seafood" to "Mariscos",
        "Side" to "Acompañamiento",
        "Starter" to "Entrada",
        "Vegan" to "Vegano",
        "Vegetarian" to "Vegetariano",
        "Breakfast" to "Desayuno",
        "Goat" to "Cabra"
    )

    private fun traducirCategoria(categoria: String?): String? {
        if (categoria == null) return null
        return categoriasTraducidas[categoria] ?: categoria
    }
}

private fun MealDto.aReceta(): Receta {
    return Receta(
        id = idMeal,
        nombre = strMeal,
        categoria = strCategory,
        instrucciones = strInstructions,
        imagenUrl = strMealThumb
    )
}