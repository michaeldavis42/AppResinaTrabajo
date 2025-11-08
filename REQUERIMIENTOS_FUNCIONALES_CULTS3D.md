# 📋 Requerimientos Funcionales - AppResina (Inspirado en Cults3D)

## 📱 Descripción General

Este documento describe los requerimientos funcionales adicionales que deben implementarse en AppResina, inspirados en las funcionalidades principales de la plataforma Cults3D, adaptadas al contexto de gestión de productos de resina.

---

## 🎯 Funcionalidades Principales

### 1. 🏷️ Sistema de Categorías Mejorado

#### 1.1 Categorías Principales
- **Arte**: Productos relacionados con arte y decoración
- **Moda**: Accesorios y complementos de moda
- **Joyas**: Joyería y bisutería
- **Casa**: Artículos para el hogar
- **Arquitectura**: Elementos arquitectónicos y decorativos
- **Artilugios**: Gadgets y objetos útiles
- **Juegos**: Elementos de juego y entretenimiento
- **Herramientas**: Herramientas y útiles
- **Variado**: Otras categorías

#### 1.2 Subcategorías
- Cada categoría principal debe tener subcategorías específicas
- Ejemplo: Casa → Portavelas, Jarrones, Adornos, etc.

#### Requerimientos Técnicos:
- Nueva entidad `Categoria` en la base de datos
- Relación muchos-a-muchos entre `Producto` y `Categoria`
- Filtrado por categorías múltiples
- Navegación jerárquica de categorías

---

### 2. ⭐ Sistema de Valoraciones y Reseñas

#### 2.1 Valoraciones
- Sistema de estrellas (1-5 estrellas)
- Promedio de valoraciones por producto
- Número total de valoraciones
- Distribución de valoraciones (cuántas de cada estrella)

#### 2.2 Reseñas/Comentarios
- Usuarios pueden dejar comentarios sobre productos
- Fecha de la reseña
- Verificación de compra (opcional)
- Sistema de "útil/no útil" para reseñas
- Respuestas a comentarios (threading)

#### Requerimientos Técnicos:
- Nueva entidad `Valoracion` con campos:
  - `id`, `productoId`, `usuarioId`, `calificacion` (1-5), `comentario`, `fecha`
- Nueva entidad `Comentario` con campos:
  - `id`, `productoId`, `usuarioId`, `texto`, `fecha`, `padreId` (para respuestas)
- DAO para gestionar valoraciones y comentarios
- Cálculo automático de promedio de valoraciones

---

### 3. ❤️ Sistema de Favoritos y Seguimientos

#### 3.1 Favoritos
- Usuarios pueden marcar productos como favoritos
- Lista de favoritos del usuario
- Notificaciones cuando un producto favorito cambia de precio
- Compartir lista de favoritos

#### 3.2 Seguimientos
- Seguir a otros usuarios/creadores
- Ver productos de usuarios seguidos
- Feed de actividad de usuarios seguidos
- Perfil de creador con sus productos

#### Requerimientos Técnicos:
- Nueva entidad `Favorito` con campos:
  - `id`, `usuarioId`, `productoId`, `fechaAgregado`
- Nueva entidad `Seguimiento` con campos:
  - `id`, `seguidorId`, `seguidoId`, `fecha`
- Nueva entidad `Usuario` con campos:
  - `id`, `nombre`, `email`, `avatarUrl`, `biografia`, `fechaRegistro`
- Queries optimizadas para obtener favoritos y seguimientos

---

### 4. 📊 Sistema de Estadísticas y Métricas

#### 4.1 Métricas por Producto
- **Vistas**: Número de veces que se visualizó el producto
- **Descargas**: Número de veces que se descargó el archivo (si aplica)
- **Ventas**: Número de ventas realizadas
- **Favoritos**: Número de usuarios que marcaron como favorito
- **Tendencias**: Cálculo de tendencia basado en actividad reciente

#### 4.2 Rankings
- **Más vendidos**: Top productos por ventas
- **Más populares**: Top productos por vistas y favoritos
- **Más descargados**: Top productos por descargas
- **Tendencia**: Productos con mayor actividad reciente
- **Nuevos**: Productos agregados recientemente

#### Requerimientos Técnicos:
- Nueva entidad `EstadisticaProducto` con campos:
  - `id`, `productoId`, `vistas`, `descargas`, `ventas`, `favoritos`, `fechaUltimaActualizacion`
- Vista materializada o tabla para rankings
- Job scheduler para actualizar rankings periódicamente
- Cálculo de tendencia usando algoritmo de "hot score"

---

### 5. 🏷️ Sistema de Tags/Etiquetas

#### 5.1 Etiquetas
- Productos pueden tener múltiples etiquetas
- Etiquetas populares/trending
- Búsqueda por etiquetas
- Sugerencias de etiquetas similares
- Nube de etiquetas

#### Requerimientos Técnicos:
- Nueva entidad `Tag` con campos:
  - `id`, `nombre`, `color`, `usos`
- Relación muchos-a-muchos entre `Producto` y `Tag`
- Búsqueda por tags con autocompletado
- Vista de tags más usados

---

### 6. 📁 Sistema de Colecciones

#### 6.1 Colecciones de Productos
- Usuarios pueden crear colecciones personalizadas
- Ejemplos: "Portavelas", "Navidad", "Regalos", etc.
- Compartir colecciones públicas
- Colecciones destacadas/curated
- Agregar productos a múltiples colecciones

#### Requerimientos Técnicos:
- Nueva entidad `Coleccion` con campos:
  - `id`, `usuarioId`, `nombre`, `descripcion`, `imagenUrl`, `publica`, `fechaCreacion`
- Nueva entidad `ProductoColeccion` (tabla intermedia)
- Pantalla de creación y edición de colecciones
- Navegación por colecciones

---

### 7. 🔍 Sistema de Búsqueda Avanzada

#### 7.1 Filtros de Búsqueda
- Búsqueda por texto (nombre, descripción, tags)
- Filtro por categoría
- Filtro por rango de precio
- Filtro por tipo de resina
- Filtro por disponibilidad
- Filtro por calificación mínima
- Ordenamiento: relevancia, precio, fecha, popularidad, valoraciones

#### 7.2 Búsqueda Semántica
- Sugerencias de búsqueda mientras se escribe
- Búsquedas frecuentes
- Búsquedas relacionadas
- Historial de búsquedas del usuario

#### Requerimientos Técnicos:
- Índices full-text en base de datos
- Query builder para filtros dinámicos
- Algoritmo de relevancia para resultados
- Cache de resultados de búsqueda frecuentes

---

### 8. 💰 Sistema de Marketplace/E-commerce

#### 8.1 Compra/Venta
- Productos pueden ser gratuitos o de pago
- Carrito de compras
- Proceso de checkout
- Historial de compras
- Gestión de inventario
- Sistema de descuentos y promociones

#### 8.2 Gestión de Pagos
- Integración con sistemas de pago (PayPal, Stripe, etc.)
- Comisiones para la plataforma (ej: 20%)
- Pago a creadores (ej: 80%)
- Historial de transacciones
- Facturación

#### Requerimientos Técnicos:
- Nueva entidad `Compra` con campos:
  - `id`, `usuarioId`, `productoId`, `precio`, `fecha`, `estado`, `metodoPago`
- Nueva entidad `Carrito` con campos:
  - `id`, `usuarioId`, `productoId`, `cantidad`, `fechaAgregado`
- Integración con API de pagos
- Sistema de notificaciones de compra

---

### 9. 👤 Sistema de Perfiles de Usuario

#### 9.1 Perfil de Usuario
- Información personal (nombre, avatar, biografía)
- Productos creados/publicados
- Valoraciones recibidas
- Seguidores y seguidos
- Estadísticas del usuario (ventas, productos, etc.)
- Badges/insignias por logros

#### 9.2 Perfil de Creador
- Portafolio de productos
- Estadísticas de ventas
- Calificación promedio como creador
- Información de contacto
- Redes sociales

#### Requerimientos Técnicos:
- Expandir entidad `Usuario` con campos adicionales
- Pantalla de perfil editable
- Vista pública de perfil
- Dashboard de creador con estadísticas

---

### 10. 🎨 Sistema de Trending y Popularidad

#### 10.1 Algoritmo de Trending
- Cálculo basado en:
  - Actividad reciente (últimas 24-48 horas)
  - Número de vistas
  - Número de favoritos
  - Número de ventas/descargas
  - Valoraciones
  - Comentarios
- Score de trending actualizado periódicamente

#### Requerimientos Técnicos:
- Algoritmo de "hot score" tipo Reddit/Hacker News
- Job scheduler para recalcular trending
- Cache de productos trending

---

### 11. 🏆 Sistema de Concursos y Desafíos

#### 11.1 Concursos
- Crear concursos temáticos
- Usuarios pueden participar con productos
- Votación pública
- Premios y reconocimientos
- Fechas de inicio y fin

#### Requerimientos Técnicos:
- Nueva entidad `Concurso` con campos:
  - `id`, `nombre`, `descripcion`, `fechaInicio`, `fechaFin`, `tema`, `premio`
- Nueva entidad `ParticipacionConcurso`
- Sistema de votación
- Pantalla de concursos activos y pasados

---

### 12. 📸 Sistema de "Makes" (Proyectos Realizados)

#### 12.1 Makes
- Usuarios pueden mostrar proyectos realizados con productos
- Subir fotos del proyecto finalizado
- Descripción del proceso
- Etiquetas relacionadas
- Likes en makes

#### Requerimientos Técnicos:
- Nueva entidad `Make` con campos:
  - `id`, `usuarioId`, `productoId`, `titulo`, `descripcion`, `imagenes`, `fecha`, `likes`
- Galería de makes por producto
- Pantalla de makes destacados

---

### 13. 🔔 Sistema de Notificaciones

#### 13.1 Tipos de Notificaciones
- Nuevo producto de usuario seguido
- Cambio de precio en producto favorito
- Nueva valoración en producto
- Nuevo comentario
- Nuevo seguidor
- Mensaje directo
- Actualización de producto seguido

#### Requerimientos Técnicos:
- Nueva entidad `Notificacion` con campos:
  - `id`, `usuarioId`, `tipo`, `mensaje`, `fecha`, `leida`, `urlDestino`
- Sistema de push notifications
- Badge de notificaciones no leídas
- Centro de notificaciones

---

### 14. 📱 Sistema de Filtros Avanzados

#### 14.1 Filtros Disponibles
- **Más vendidos**: Ordenar por número de ventas
- **Más populares**: Ordenar por vistas y favoritos
- **Más descargados**: Ordenar por descargas
- **Mejor valorados**: Ordenar por calificación promedio
- **Más recientes**: Ordenar por fecha de creación
- **Tendencia**: Ordenar por score de trending
- **Precio**: Ordenar por precio (ascendente/descendente)
- **Gratis**: Filtrar solo productos gratuitos

#### Requerimientos Técnicos:
- Componente de filtros mejorado en HomeScreen
- Query parameters en navegación
- Persistencia de filtros seleccionados

---

### 15. 🔖 Sistema de Bookmarks y Listas

#### 15.1 Bookmarks
- Guardar productos para revisar después
- Organizar en listas personalizadas
- Compartir listas
- Notas privadas sobre productos

#### Requerimientos Técnicos:
- Entidad `Bookmark` similar a `Favorito` pero con listas
- Nueva entidad `ListaPersonalizada`
- Gestión de múltiples listas por usuario

---

## 🗄️ Modelo de Datos Propuesto

### Nuevas Entidades Principales

```kotlin
// Usuario
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val email: String,
    val avatarUrl: String = "",
    val biografia: String = "",
    val fechaRegistro: Long = System.currentTimeMillis(),
    val esCreador: Boolean = false
)

// Categoría
@Entity(tableName = "categorias")
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String = "",
    val icono: String = "",
    val padreId: Int? = null // Para subcategorías
)

// Valoración
@Entity(tableName = "valoraciones")
data class ValoracionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productoId: Int,
    val usuarioId: Int,
    val calificacion: Int, // 1-5
    val comentario: String = "",
    val fecha: Long = System.currentTimeMillis()
)

// Favorito
@Entity(tableName = "favoritos")
data class FavoritoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val productoId: Int,
    val fechaAgregado: Long = System.currentTimeMillis()
)

// Tag
@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val color: String = "#4CAF50",
    val usos: Int = 0
)

// Colección
@Entity(tableName = "colecciones")
data class ColeccionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val nombre: String,
    val descripcion: String = "",
    val imagenUrl: String = "",
    val publica: Boolean = false,
    val fechaCreacion: Long = System.currentTimeMillis()
)

// Estadísticas
@Entity(tableName = "estadisticas_producto")
data class EstadisticaProductoEntity(
    @PrimaryKey val productoId: Int,
    val vistas: Int = 0,
    val descargas: Int = 0,
    val ventas: Int = 0,
    val favoritos: Int = 0,
    val scoreTrending: Double = 0.0,
    val fechaUltimaActualizacion: Long = System.currentTimeMillis()
)

// Compra
@Entity(tableName = "compras")
data class CompraEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val productoId: Int,
    val precio: Double,
    val fecha: Long = System.currentTimeMillis(),
    val estado: String = "completada", // completada, pendiente, cancelada
    val metodoPago: String = ""
)

// Notificación
@Entity(tableName = "notificaciones")
data class NotificacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val tipo: String, // favorito, comentario, seguimiento, etc.
    val mensaje: String,
    val fecha: Long = System.currentTimeMillis(),
    val leida: Boolean = false,
    val urlDestino: String = ""
)
```

### Tablas de Relación

```kotlin
// Producto-Categoría (muchos-a-muchos)
@Entity(tableName = "producto_categoria")
data class ProductoCategoriaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productoId: Int,
    val categoriaId: Int
)

// Producto-Tag (muchos-a-muchos)
@Entity(tableName = "producto_tag")
data class ProductoTagEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productoId: Int,
    val tagId: Int
)

// Producto-Colección (muchos-a-muchos)
@Entity(tableName = "producto_coleccion")
data class ProductoColeccionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productoId: Int,
    val coleccionId: Int
)

// Seguimiento Usuario-Usuario
@Entity(tableName = "seguimientos")
data class SeguimientoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val seguidorId: Int,
    val seguidoId: Int,
    val fecha: Long = System.currentTimeMillis()
)
```

---

## 📱 Pantallas Nuevas Requeridas

### 1. Pantalla de Categorías
- Navegación jerárquica de categorías
- Productos por categoría
- Filtros dentro de categoría

### 2. Pantalla de Detalle Mejorada
- Valoraciones y reseñas
- Galería de imágenes (múltiples)
- Información del creador
- Productos relacionados
- Makes del producto
- Comentarios y respuestas

### 3. Pantalla de Perfil de Usuario
- Información del usuario
- Productos publicados
- Favoritos
- Colecciones
- Seguidores/Seguidos
- Estadísticas

### 4. Pantalla de Búsqueda Avanzada
- Barra de búsqueda con sugerencias
- Filtros múltiples
- Resultados ordenados
- Historial de búsquedas

### 5. Pantalla de Colecciones
- Lista de colecciones
- Crear/editar colección
- Productos en colección
- Colecciones destacadas

### 6. Pantalla de Marketplace/Carrito
- Carrito de compras
- Proceso de checkout
- Historial de compras
- Gestión de pagos

### 7. Pantalla de Concursos
- Concursos activos
- Participar en concurso
- Votar en productos
- Ganadores

### 8. Pantalla de Trending
- Productos en tendencia
- Explicación del algoritmo
- Filtros por período

### 9. Pantalla de Notificaciones
- Lista de notificaciones
- Marcar como leída
- Filtros por tipo

### 10. Pantalla de Estadísticas (Dashboard Creador)
- Gráficos de ventas
- Productos más populares
- Ingresos
- Métricas de engagement

---

## 🔄 Flujos de Usuario Principales

### Flujo 1: Buscar y Descargar Producto
1. Usuario busca producto
2. Ve resultados con filtros
3. Selecciona producto
4. Ve detalles y valoraciones
5. Agrega al carrito o descarga directamente
6. Completa compra si es de pago
7. Descarga archivo
8. Deja valoración y comentario

### Flujo 2: Crear y Publicar Producto
1. Usuario crea cuenta/entra
2. Sube producto con información
3. Selecciona categorías y tags
4. Establece precio (gratis o pago)
5. Publica producto
6. Gestiona producto desde dashboard
7. Ve estadísticas y comentarios

### Flujo 3: Explorar y Descubrir
1. Usuario entra a app
2. Ve productos trending/populares
3. Navega por categorías
4. Descubre colecciones
5. Sigue a creadores
6. Guarda favoritos
7. Recibe notificaciones personalizadas

---

## 🎨 Mejoras de UI/UX

### Componentes Nuevos Requeridos
1. **RatingBar**: Componente de estrellas para valoraciones
2. **CommentCard**: Tarjeta de comentario con respuestas
3. **CategoryChip**: Chip de categoría con icono
4. **TagChip**: Chip de tag con color
5. **ProductStatsCard**: Tarjeta con estadísticas del producto
6. **UserAvatar**: Avatar de usuario con badge
7. **TrendingBadge**: Badge de "En tendencia"
8. **FilterSheet**: Bottom sheet con filtros avanzados
9. **SearchBarAdvanced**: Barra de búsqueda con sugerencias
10. **NotificationBell**: Icono de notificaciones con badge

### Mejoras Visuales
- Animaciones de transición mejoradas
- Efectos de hover/tap más fluidos
- Loading states más informativos
- Empty states más atractivos
- Skeleton loaders para mejor UX

---

## 🔐 Seguridad y Privacidad

### Requerimientos de Seguridad
- Autenticación de usuarios
- Autorización para acciones (editar solo productos propios)
- Validación de datos de entrada
- Protección contra SQL injection
- Encriptación de datos sensibles
- Política de privacidad
- Términos y condiciones

---

## 📊 Priorización de Implementación

### Fase 1 (Alta Prioridad) - MVP Marketplace
1. ✅ Sistema de categorías mejorado
2. ✅ Sistema de valoraciones y reseñas
3. ✅ Sistema de favoritos
4. ✅ Sistema de estadísticas básicas
5. ✅ Filtros avanzados
6. ✅ Búsqueda mejorada

### Fase 2 (Media Prioridad) - Engagement
1. Sistema de tags
2. Sistema de colecciones
3. Sistema de perfiles de usuario
4. Sistema de trending
5. Sistema de notificaciones básicas

### Fase 3 (Baja Prioridad) - Características Avanzadas
1. Sistema de marketplace/e-commerce completo
2. Sistema de concursos
3. Sistema de makes
4. Dashboard de creador avanzado
5. Sistema de seguimientos

---

## 📝 Notas de Implementación

- Todas las nuevas entidades deben seguir el patrón Repository existente
- Se requiere migración de base de datos para nuevas tablas
- Los ViewModels deben manejar los nuevos estados de forma reactiva
- Considerar paginación para listas grandes
- Implementar caché para mejorar rendimiento
- Testing necesario para todas las nuevas funcionalidades

---

## 🎯 Métricas de Éxito

- Aumento en tiempo de uso de la aplicación
- Incremento en productos agregados
- Mayor engagement con valoraciones y comentarios
- Incremento en uso de favoritos y colecciones
- Mejora en tasa de conversión (si aplica e-commerce)

---

**Documento creado**: [Fecha]  
**Versión**: 1.0  
**Estado**: Propuesta inicial

