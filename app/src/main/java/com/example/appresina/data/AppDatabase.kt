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
    version = 3,
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

        // Migración de versión 2 a 3
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crear tabla de categorías
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS categorias (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL,
                        descripcion TEXT NOT NULL,
                        icono TEXT NOT NULL,
                        padreId INTEGER
                    )
                """.trimIndent())

                // Crear tabla de usuarios
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS usuarios (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL,
                        email TEXT NOT NULL,
                        avatarUrl TEXT NOT NULL,
                        biografia TEXT NOT NULL,
                        fechaRegistro INTEGER NOT NULL,
                        esCreador INTEGER NOT NULL
                    )
                """.trimIndent())

                // Crear tabla de valoraciones
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS valoraciones (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        productoId INTEGER NOT NULL,
                        usuarioId INTEGER NOT NULL,
                        calificacion INTEGER NOT NULL,
                        comentario TEXT NOT NULL,
                        fecha INTEGER NOT NULL
                    )
                """.trimIndent())

                // Crear tabla de favoritos
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS favoritos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        usuarioId INTEGER NOT NULL,
                        productoId INTEGER NOT NULL,
                        fechaAgregado INTEGER NOT NULL
                    )
                """.trimIndent())

                // Crear tabla de estadísticas
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS estadisticas_producto (
                        productoId INTEGER PRIMARY KEY NOT NULL,
                        vistas INTEGER NOT NULL,
                        descargas INTEGER NOT NULL,
                        ventas INTEGER NOT NULL,
                        favoritos INTEGER NOT NULL,
                        scoreTrending REAL NOT NULL,
                        fechaUltimaActualizacion INTEGER NOT NULL
                    )
                """.trimIndent())

                // Crear tabla de relación producto-categoría
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS producto_categoria (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        productoId INTEGER NOT NULL,
                        categoriaId INTEGER NOT NULL
                    )
                """.trimIndent())

                // Insertar categorías iniciales
                database.execSQL("""
                    INSERT INTO categorias (nombre, descripcion, icono, padreId) VALUES
                    ('Arte', 'Productos relacionados con arte y decoración', '🎨', NULL),
                    ('Moda', 'Accesorios y complementos de moda', '👗', NULL),
                    ('Joyas', 'Joyería y bisutería', '💍', NULL),
                    ('Casa', 'Artículos para el hogar', '🏠', NULL),
                    ('Arquitectura', 'Elementos arquitectónicos y decorativos', '🏛️', NULL),
                    ('Artilugios', 'Gadgets y objetos útiles', '📱', NULL),
                    ('Juegos', 'Elementos de juego y entretenimiento', '🎲', NULL),
                    ('Herramientas', 'Herramientas y útiles', '🔧', NULL),
                    ('Variado', 'Otras categorías', '👽', NULL)
                """.trimIndent())

                // Crear usuario por defecto
                database.execSQL("""
                    INSERT INTO usuarios (nombre, email, avatarUrl, biografia, fechaRegistro, esCreador) VALUES
                    ('Usuario Invitado', 'invitado@appresina.com', '', 'Usuario invitado', ${System.currentTimeMillis()}, 0)
                """.trimIndent())

                // Inicializar estadísticas para productos existentes
                database.execSQL("""
                    INSERT INTO estadisticas_producto (productoId, vistas, descargas, ventas, favoritos, scoreTrending, fechaUltimaActualizacion)
                    SELECT id, 0, 0, 0, 0, 0.0, ${System.currentTimeMillis()}
                    FROM productos
                    WHERE id NOT IN (SELECT productoId FROM estadisticas_producto)
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "appresina_database"
                )
                .addMigrations(MIGRATION_2_3)
                .fallbackToDestructiveMigration() // Solo para desarrollo
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}