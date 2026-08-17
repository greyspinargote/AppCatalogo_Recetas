package com.example.appcatalogorecetas.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecetaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(receta: Receta)

    @Query("SELECT * FROM recetas_favoritas")
    fun obtenerTodas(): Flow<List<Receta>>

    @Query("SELECT * FROM recetas_favoritas WHERE id = :idBuscado")
    suspend fun obtenerPorId(idBuscado: String): Receta?

    @Delete
    suspend fun eliminar(receta: Receta)

    @Query("DELETE FROM recetas_favoritas")
    suspend fun borrarTodas()
}
