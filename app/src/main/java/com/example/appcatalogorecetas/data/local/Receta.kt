package com.example.appcatalogorecetas.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recetas_favoritas")
data class Receta(
    @PrimaryKey
    val id: String,
    val nombre: String,
    val categoria: String?,
    val instrucciones: String?,
    val imagenUrl: String?,
    val fotoUri: String? = null // NUEVO: foto de la versión de la receta
)