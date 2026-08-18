package com.example.appcatalogorecetas.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Esto crea el archivo de preferencias una sola vez para toda la app
val Context.dataStore by preferencesDataStore(name = "preferencias_usuario")

class PreferenciasDataStore(private val context: Context) {

    companion object {
        private val MODO_OSCURO = booleanPreferencesKey("modo_oscuro")
        private val UNIDAD_MEDIDA = stringPreferencesKey("unidad_medida")
    }

    // MODO OSCURO
    val modoOscuro: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[MODO_OSCURO] ?: false
    }

    suspend fun guardarModoOscuro(activado: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[MODO_OSCURO] = activado
        }
    }

    // UNIDAD DE MEDIDA ("Métrica" o "Imperial")
    val unidadMedida: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[UNIDAD_MEDIDA] ?: "Métrica"
    }

    suspend fun guardarUnidadMedida(unidad: String) {
        context.dataStore.edit { prefs ->
            prefs[UNIDAD_MEDIDA] = unidad
        }
    }
}
