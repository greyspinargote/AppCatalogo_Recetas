package com.example.appcatalogorecetas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Receta::class], version = 1, exportSchema = false)
abstract class RecetaDatabase : RoomDatabase() {

    abstract fun recetaDao(): RecetaDao

    companion object {
        @Volatile
        private var INSTANCE: RecetaDatabase? = null

        fun getDatabase(context: Context): RecetaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecetaDatabase::class.java,
                    "receta_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}