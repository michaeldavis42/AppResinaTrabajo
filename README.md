# AppResina - Gestión de Productos de Resina

## 📱 Descripción del Proyecto

AppResina es una aplicación móvil desarrollada en Android Studio que permite gestionar un inventario de productos de resina. La aplicación está diseñada específicamente para usuarios que trabajan con resina y necesitan un sistema organizado para administrar sus productos.

## ✨ Características Principales

### 🎨 **Interfaz Visual**
- **Material Design 3**: Diseño moderno y consistente
- **Tema personalizado**: Colores verdes que reflejan la naturaleza de los productos de resina
- **Animaciones fluidas**: Transiciones suaves entre pantallas
- **Responsive**: Adaptable a diferentes tamaños de pantalla

### 📋 **Funcionalidades**
- **Gestión de Productos**: CRUD completo (Crear, Leer, Actualizar, Eliminar)
- **Formularios Validados**: Validación en tiempo real con retroalimentación visual
- **Filtrado por Tipo**: Filtros para diferentes tipos de resina (Epoxi, Poliuretano, Acrílica, UV, etc.)
- **Búsqueda**: Barra de búsqueda para encontrar productos rápidamente
- **Navegación Intuitiva**: Navegación clara entre pantallas

### 🔒 **Seguridad y Recursos Nativos**
- **Diálogo de Seguridad**: Recomendaciones de seguridad al iniciar la app
- **Acceso a Cámara**: Para tomar fotos de productos
- **Acceso a Almacenamiento**: Para acceder a la galería de imágenes
- **Gestión de Permisos**: Manejo seguro de permisos del sistema

### 💾 **Persistencia de Datos**
- **Room Database**: Base de datos local robusta
- **Repository Pattern**: Arquitectura limpia y mantenible
- **ViewModel**: Gestión de estado reactiva
- **Corrutinas**: Operaciones asíncronas eficientes

## 🏗️ Arquitectura del Proyecto

```
app/
├── src/main/java/com/example/appresina/
│   ├── data/                    # Capa de datos
│   │   ├── AppDatabase.kt       # Configuración de Room
│   │   ├── ProductoDao.kt       # Acceso a datos
│   │   ├── ProductoEntity.kt    # Entidad de base de datos
│   │   └── ProductoRepository.kt # Repositorio
│   ├── model/                   # Modelos de datos
│   │   └── Producto.kt          # Modelo de producto
│   ├── ui/                      # Interfaz de usuario
│   │   ├── components/          # Componentes reutilizables
│   │   │   ├── ProductoCard.kt
│   │   │   ├── SearchBar.kt
│   │   │   ├── ValidationTextField.kt
│   │   │   └── ImagePicker.kt
│   │   ├── navigation/          # Navegación
│   │   │   └── AppNavigation.kt
│   │   ├── screens/             # Pantallas
│   │   │   ├── HomeScreen.kt
│   │   │   ├── AddEditProductScreen.kt
│   │   │   ├── ProductDetailScreen.kt
│   │   │   ├── SettingsScreen.kt
│   │   │   └── SeguridadDialog.kt
│   │   └── theme/               # Tema y estilos
│   │       ├── Color.kt
│   │       ├── Theme.kt
│   │       └── Type.kt
│   ├── viewmodel/               # Lógica de negocio
│   │   └── ProductViewModel.kt
│   └── MainActivity.kt          # Actividad principal
```

## 🛠️ Tecnologías Utilizadas

- **Kotlin**: Lenguaje de programación principal
- **Jetpack Compose**: Framework de UI moderno
- **Material Design 3**: Sistema de diseño
- **Room**: Base de datos local
- **Navigation Compose**: Navegación entre pantallas
- **ViewModel**: Gestión de estado
- **Corrutinas**: Programación asíncrona
- **Accompanist Permissions**: Manejo de permisos

## 📋 Requisitos del Sistema

- **Android Studio**: Arctic Fox o superior
- **Kotlin**: 1.8.0 o superior
- **Compile SDK**: 34
- **Min SDK**: 30 (Android 11)
- **Target SDK**: 34 (Android 14)

## 🚀 Instalación y Configuración

1. **Clonar el repositorio**:
   ```bash
   git clone [URL_DEL_REPOSITORIO]
   cd AppResina
   ```

2. **Abrir en Android Studio**:
   - Abrir Android Studio
   - Seleccionar "Open an existing project"
   - Navegar a la carpeta del proyecto

3. **Sincronizar dependencias**:
   - Android Studio sincronizará automáticamente las dependencias
   - Si hay problemas, hacer clic en "Sync Now"

4. **Ejecutar la aplicación**:
   - Conectar dispositivo Android o iniciar emulador
   - Hacer clic en "Run" (▶️) o presionar Shift+F10

## 📱 Funcionalidades por Pantalla

### 🏠 **Pantalla Principal (HomeScreen)**
- Lista de productos con diseño de tarjetas
- Barra de búsqueda
- Filtros por tipo de resina
- Botones de acción (Agregar, Configuración)
- Diálogo de seguridad al iniciar

### ➕ **Pantalla Agregar/Editar Producto**
- Formulario validado con campos:
  - Nombre del producto
  - Tipo de resina (dropdown)
  - Precio (validación numérica)
  - Cantidad (validación numérica)
  - Descripción
- Selector de imagen (cámara/galería)
- Validación en tiempo real

### 📄 **Pantalla Detalle del Producto**
- Información completa del producto
- Visualización de imagen
- Precio y stock destacados
- Botones de acción (Editar, Compartir)

### ⚙️ **Pantalla de Configuración**
- Gestión de permisos (Cámara, Almacenamiento)
- Información de la aplicación
- Estado de recursos nativos

## 🎯 Cumplimiento de Requisitos de Evaluación

### ✅ **IL2.1 - Diseño de Interfaces**
- ✅ Interfaz estructurada con Material 3
- ✅ Principios de usabilidad aplicados
- ✅ Jerarquía visual clara
- ✅ Formularios validados con retroalimentación visual
- ✅ Navegación coherente entre componentes

### ✅ **IL2.2 - Funcionalidades Visuales**
- ✅ Estructuras de programación implementadas
- ✅ Lógica de control en ViewModel
- ✅ Gestión de estado reactiva
- ✅ Coherencia entre interacción y respuesta visual
- ✅ Animaciones y transiciones fluidas

### ✅ **IL2.3 - Almacenamiento y Arquitectura**
- ✅ Almacenamiento local con Room Database
- ✅ Patrón arquitectónico MVVM
- ✅ Repository pattern implementado
- ✅ Organización modular del código
- ✅ Mantenibilidad del código

### ✅ **IL2.4 - Recursos Nativos**
- ✅ Acceso a cámara implementado
- ✅ Acceso a almacenamiento implementado
- ✅ Gestión segura de permisos
- ✅ Integración en la UI

## 🔧 Configuración Adicional

### Permisos Requeridos
La aplicación requiere los siguientes permisos:
- `CAMERA`: Para tomar fotos de productos
- `READ_EXTERNAL_STORAGE`: Para acceder a la galería
- `WRITE_EXTERNAL_STORAGE`: Para guardar imágenes (Android < 10)
- `READ_MEDIA_IMAGES`: Para acceder a imágenes (Android 13+)

### Base de Datos
La aplicación utiliza Room Database con las siguientes características:
- **Nombre**: `appresina_database`
- **Versión**: 2
- **Migración**: Destructiva (para desarrollo)
- **Tabla**: `productos`

## 🎨 Personalización del Tema

Los colores del tema están definidos en `ui/theme/Color.kt`:
- **Primario**: Verde oscuro (#2E7D32)
- **Secundario**: Verde medio (#4CAF50)
- **Fondo**: Gris claro (#F8F9FA)
- **Superficie**: Blanco (#FFFFFF)

## 📊 Estructura de Datos

### Producto
```kotlin
data class Producto(
    val id: Int = 0,
    val nombre: String,
    val tipo: String,
    val precio: Double,
    val cantidad: Int,
    val descripcion: String,
    val imagenUrl: String = "",
    val fechaCreacion: Long = System.currentTimeMillis(),
    val disponible: Boolean = true
)
```

## 🚀 Próximas Mejoras

- [ ] Implementar sincronización en la nube
- [ ] Agregar notificaciones push
- [ ] Implementar modo offline
- [ ] Agregar reportes y estadísticas
- [ ] Implementar sistema de respaldos
- [ ] Agregar más tipos de validación

## 📋 Requerimientos Funcionales Adicionales (Inspirados en Cults3D)

Se han definido requerimientos funcionales adicionales para expandir las capacidades de la aplicación, inspirados en las funcionalidades principales de plataformas como Cults3D. Estos requerimientos están documentados en detalle en el archivo **[REQUERIMIENTOS_FUNCIONALES_CULTS3D.md](./REQUERIMIENTOS_FUNCIONALES_CULTS3D.md)**.

### 🎯 Funcionalidades Principales Propuestas:

#### Fase 1 - MVP Marketplace (Alta Prioridad)
- ✅ **Sistema de Categorías Mejorado**: Categorías principales y subcategorías jerárquicas
- ✅ **Sistema de Valoraciones y Reseñas**: Calificaciones con estrellas y comentarios
- ✅ **Sistema de Favoritos**: Guardar productos favoritos del usuario
- ✅ **Sistema de Estadísticas**: Métricas por producto (vistas, descargas, ventas)
- ✅ **Filtros Avanzados**: Más vendidos, populares, tendencia, mejor valorados
- ✅ **Búsqueda Mejorada**: Búsqueda avanzada con múltiples filtros

#### Fase 2 - Engagement (Media Prioridad)
- 🔄 **Sistema de Tags/Etiquetas**: Etiquetado de productos con búsqueda por tags
- 🔄 **Sistema de Colecciones**: Crear y gestionar colecciones personalizadas
- 🔄 **Sistema de Perfiles de Usuario**: Perfiles completos con estadísticas
- 🔄 **Sistema de Trending**: Algoritmo de productos en tendencia
- 🔄 **Sistema de Notificaciones**: Centro de notificaciones personalizadas

#### Fase 3 - Características Avanzadas (Baja Prioridad)
- ⏳ **Sistema de Marketplace/E-commerce**: Carrito de compras y pagos
- ⏳ **Sistema de Concursos**: Concursos temáticos con votación
- ⏳ **Sistema de Makes**: Proyectos realizados por usuarios
- ⏳ **Dashboard de Creador**: Panel avanzado con métricas detalladas
- ⏳ **Sistema de Seguimientos**: Seguir a otros usuarios/creadores

### 📊 Modelo de Datos Extendido

Los nuevos requerimientos incluyen las siguientes entidades principales:
- `Usuario`: Gestión de usuarios y creadores
- `Categoria`: Sistema de categorías jerárquico
- `Valoracion`: Sistema de calificaciones y reseñas
- `Favorito`: Productos favoritos del usuario
- `Tag`: Sistema de etiquetado
- `Coleccion`: Colecciones personalizadas
- `EstadisticaProducto`: Métricas y estadísticas
- `Compra`: Sistema de transacciones
- `Notificacion`: Centro de notificaciones

Ver el documento completo para detalles técnicos, relaciones de base de datos y especificaciones de implementación.

## 👥 Contribución

Este proyecto fue desarrollado como parte de una evaluación académica. Para contribuir:

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 📞 Contacto

Para preguntas sobre el proyecto, contactar a los desarrolladores del equipo.

---

**AppResina** - Gestión inteligente de productos de resina 🧪✨

