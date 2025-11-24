package com.example.appresina.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context

@Database(
    entities = [
        ProductoEntity::class,
        CategoriaEntity::class,
        UsuarioEntity::class,
        ValoracionEntity::class,
        FavoritoEntity::class,
        EstadisticaProductoEntity::class,
        ProductoCategoriaEntity::class
    ],
    version = 5, // <-- He incrementado la versión a 5
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun valoracionDao(): ValoracionDao
    abstract fun favoritoDao(): FavoritoDao
    abstract fun estadisticaProductoDao(): EstadisticaProductoDao
    abstract fun productoCategoriaDao(): ProductoCategoriaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "appresina_database"
                )
                .fallbackToDestructiveMigration() // Borra y recrea si no hay migración
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}