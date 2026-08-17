package com.example.appcatalogorecetas.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appcatalogorecetas.data.local.Receta
import com.example.appcatalogorecetas.data.local.RecetaDatabase
import com.example.appcatalogorecetas.data.remote.RetrofitClient
import com.example.appcatalogorecetas.data.repository.RecetaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecetaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecetaRepository

    // Favoritas guardadas en Room (se actualiza solo cuando cambia la BD)
    private val _recetasFavoritas = MutableStateFlow<List<Receta>>(emptyList())
    val recetasFavoritas: StateFlow<List<Receta>> = _recetasFavoritas.asStateFlow()

    // Resultados de la búsqueda en la API
    private val _recetasApi = MutableStateFlow<List<Receta>>(emptyList())
    val recetasApi: StateFlow<List<Receta>> = _recetasApi.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    // NUEVO: estado de error, para cumplir con el requisito de mostrar
    // "cargando / éxito / error" del profe
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        val dao = RecetaDatabase.getDatabase(application).recetaDao()
        repository = RecetaRepository(RetrofitClient.apiService, dao)

        viewModelScope.launch {
            repository.recetasFavoritas.collect { lista ->
                _recetasFavoritas.value = lista
            }
        }

        buscarRecetas("chicken")
    }

    fun buscarRecetas(query: String) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                val resultado = repository.buscarRecetasRemotas(query)
                if (resultado.isEmpty()) {
                    _error.value = "No se encontraron recetas."
                }
                _recetasApi.value = resultado
            } catch (e: Exception) {
                _error.value = "No se pudo conectar. Revisa tu internet."
            } finally {
                _cargando.value = false
            }
        }
    }

    fun guardarFavorito(receta: Receta) {
        viewModelScope.launch {
            repository.guardarFavorito(receta)
        }
    }

    fun eliminarFavorito(receta: Receta) {
        viewModelScope.launch {
            repository.eliminarFavorito(receta)
        }
    }

    suspend fun obtenerDetalle(id: String): Receta? {
        return repository.obtenerFavoritoPorId(id) ?: repository.obtenerDetalleRemoto(id)
    }
}