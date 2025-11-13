# Patrón de Conexión Frontend-Backend al Estilo EasyShop

Este documento describe la arquitectura y organización del código que utiliza EasyShop para conectar el frontend (Android con Jetpack Compose) con el backend. El enfoque está en la **estructura de capas, ubicación de componentes y flujo de datos**, no en las reglas de negocio específicas.

## Tabla de Contenidos

1. [Librerías Utilizadas](#librerías-utilizadas)
2. [Configuración del BaseUrl](#configuración-del-baseurl)
3. [Organización de Capas](#organización-de-capas)
4. [Ubicación de Componentes](#ubicación-de-componentes)
5. [Flujo de Información](#flujo-de-información)
6. [Manejo de Errores](#manejo-de-errores)
7. [Nota sobre Autenticación](#nota-sobre-autenticación)

---

## Librerías Utilizadas

EasyShop utiliza las siguientes librerías para la comunicación con el backend:

### Retrofit 3.0.0
- **Propósito**: Cliente HTTP para Android que facilita la creación de servicios REST
- **Uso**: Define interfaces de servicios que mapean endpoints del backend a funciones Kotlin suspend
- **Configuración**: Definida en `gradle/libs.versions.toml` y referenciada en `app/build.gradle.kts`

### Gson Converter
- **Propósito**: Convierte automáticamente JSON a objetos Kotlin (DTOs) y viceversa
- **Uso**: Se configura en Retrofit mediante `GsonConverterFactory`
- **Configuración**: `retrofit2.converter.gson` en `app/build.gradle.kts`

### OkHttp (implícito)
- **Propósito**: Cliente HTTP subyacente que Retrofit utiliza internamente
- **Uso**: Maneja las peticiones HTTP reales, timeouts, interceptores, etc.
- **Nota**: Aunque no se configura explícitamente en el código, Retrofit lo utiliza automáticamente

### Room Database
- **Propósito**: Base de datos local para caché y persistencia
- **Uso**: Almacena datos localmente (ej: productos favoritos en el módulo `home`)
- **Configuración**: Definida en `gradle/libs.versions.toml` y utilizada solo en features que requieren persistencia local

---

## Configuración del BaseUrl

### Patrón: No Centralizado

El `baseUrl` del backend **NO está centralizado**. Cada feature tiene su propio `DataModule` que configura una instancia independiente de Retrofit.

### Ubicación

Cada feature define su Retrofit en:
- `features/home/data/di/DataModule.kt`
- `features/auth/data/di/DataModule.kt`

### Estructura de Configuración

Cada `DataModule` contiene una función `getRetrofit()` que:
1. Crea una instancia de `Retrofit.Builder()`
2. Define el `baseUrl` (actualmente hardcodeado: `"https://dummyjson.com/"`)
3. Agrega el `GsonConverterFactory` para serialización JSON
4. Construye y retorna la instancia de Retrofit

### Características

- **BaseUrl actual**: `"https://dummyjson.com/"`
- **Configuración**: Hardcodeada en cada `DataModule`
- **Ventaja**: Cada feature puede tener su propio backend si es necesario
- **Desventaja**: Duplicación de código de configuración

---

## Organización de Capas

EasyShop sigue una **arquitectura limpia (Clean Architecture)** con separación clara de responsabilidades por feature.

### Estructura General

```
features/
└── [feature]/
    ├── data/          # Capa de datos
    ├── domain/        # Capa de dominio
    └── presentation/  # Capa de presentación
```

Cada feature es independiente y contiene sus propias tres capas.

### Capa de Datos (`data/`)

**Responsabilidad**: Comunicación con fuentes externas (API REST, base de datos local)

#### Estructura de Carpetas

```
data/
├── di/
│   └── DataModule.kt              # Factory de dependencias (Retrofit, Repositories, DAOs)
├── remote/                        # Comunicación con API
│   ├── models/
│   │   └── [Nombre]Dto.kt         # DTOs que representan la estructura JSON del backend
│   └── services/
│       └── [Nombre]Service.kt     # Interfaces Retrofit con anotaciones HTTP
├── repositories/
│   └── [Nombre]RepositoryImpl.kt  # Implementación concreta del repositorio
└── local/                         # Persistencia local (solo si se requiere)
    ├── dao/
    │   └── [Nombre]Dao.kt         # Interfaces Room para acceso a base de datos
    ├── database/
    │   └── AppDatabase.kt         # Configuración de Room
    └── models/
        └── [Nombre]Entity.kt      # Entidades Room para persistencia
```

**Componentes clave**:
- **DataModule**: Factory object que crea instancias de servicios, repositorios y DAOs
- **Services**: Interfaces Retrofit que definen los endpoints del backend
- **DTOs**: Modelos de datos que representan exactamente la estructura JSON del backend
- **RepositoryImpl**: Implementación concreta que orquesta llamadas a servicios y DAOs
- **Local** (opcional): Componentes de Room para persistencia local

### Capa de Dominio (`domain/`)

**Responsabilidad**: Contratos y modelos de negocio independientes de la fuente de datos

#### Estructura de Carpetas

```
domain/
├── repositories/
│   └── [Nombre]Repository.kt      # Interfaz que define las operaciones disponibles
└── models/
    └── [Nombre].kt                # Modelos de dominio (objetos de negocio)
```

**Componentes clave**:
- **Interfaces de Repositorios**: Contratos que definen qué operaciones están disponibles, sin importar cómo se implementen
- **Modelos de Dominio**: Objetos de negocio que representan entidades del dominio, independientes de la estructura del backend

**Nota**: Esta capa NO tiene dependencias de frameworks externos (Retrofit, Room, etc.)

### Capa de Presentación (`presentation/`)

**Responsabilidad**: UI y lógica de presentación

#### Estructura de Carpetas

```
presentation/
├── di/
│   └── PresentationModule.kt      # Factory de ViewModels
└── [screen]/                      # Agrupación por pantalla/feature de UI
    ├── [Screen]ViewModel.kt       # ViewModel que maneja estado y coordina con repositorios
    ├── [Screen].kt                # Composable principal de la pantalla
    └── [Component].kt             # Componentes UI reutilizables de la pantalla
```

**Componentes clave**:
- **PresentationModule**: Factory object que crea instancias de ViewModels inyectando los repositorios
- **ViewModels**: Manejan el estado de la UI y coordinan con los repositorios mediante coroutines
- **Composables**: Componentes de UI con Jetpack Compose que observan el estado del ViewModel

---

## Ubicación de Componentes

### Servicios HTTP

**Ubicación**: `features/[feature]/data/remote/services/`

**Ejemplo**: `features/home/data/remote/services/ProductService.kt`

**Características**:
- Son interfaces (no clases)
- Utilizan anotaciones Retrofit: `@GET`, `@POST`, `@PUT`, `@DELETE`, etc.
- Los métodos son `suspend fun` para operaciones asíncronas
- Retornan `Response<Dto>` donde `Dto` es el modelo de datos esperado del backend
- Los parámetros de ruta se definen con `@Path`, los de query con `@Query`, y el body con `@Body`

### DTOs (Data Transfer Objects)

**Ubicación**: `features/[feature]/data/remote/models/`

**Ejemplos**:
- `features/home/data/remote/models/ProductDto.kt`
- `features/home/data/remote/models/ProductsWrapperDto.kt`
- `features/auth/data/remote/models/LoginRequestDto.kt`
- `features/auth/data/remote/models/LoginResponseDto.kt`

**Características**:
- Son `data class` en Kotlin
- Todos los campos son **nullable** (`?`) para manejar respuestas incompletas o campos opcionales
- La estructura coincide exactamente con el JSON que retorna el backend
- Pueden contener DTOs anidados (ej: `ProductDto` contiene `DimensionsDto`, `MetaDto`, `ReviewDto`)

### Mappers DTO → Domain Model

**Ubicación**: **DENTRO** de `RepositoryImpl`, no en clases separadas

**Ejemplo**: La transformación de `ProductDto` a `Product` ocurre dentro de `ProductRepositoryImpl.getAllProducts()`

**Patrón**:
- El mapper es **inline** dentro de la función del repositorio
- Utiliza funciones de transformación como `.map { }` para listas
- Extrae solo los campos necesarios del DTO
- Proporciona valores por defecto usando el operador elvis (`?:`) cuando los campos son null
- Puede combinar datos del API con datos locales (ej: verificar si un producto es favorito consultando la base de datos local)

**Ejemplo de patrón**:
```kotlin
productsDto.map { productDto ->
    Product(
        name = productDto.title ?: "",
        price = productDto.price ?: 0.0,
        image = productDto.thumbnail ?: "",
        id = productDto.id ?: 0
    )
}
```

### Repositories

#### Interfaces de Repositorios

**Ubicación**: `features/[feature]/domain/repositories/`

**Ejemplo**: `features/home/domain/repositories/ProductRepository.kt`

**Características**:
- Son interfaces (no clases)
- Definen métodos `suspend fun` que retornan modelos de dominio (no DTOs)
- Son parte de la capa de dominio, por lo que NO tienen dependencias de frameworks externos

#### Implementaciones de Repositorios

**Ubicación**: `features/[feature]/data/repositories/`

**Ejemplo**: `features/home/data/repositories/ProductRepositoryImpl.kt`

**Características**:
- Son clases que implementan la interfaz del repositorio
- Reciben en el constructor:
  - El `Service` (interfaz Retrofit) para llamadas al backend
  - Opcionalmente, el `Dao` (interfaz Room) para persistencia local
- Utilizan `withContext(Dispatchers.IO)` para ejecutar operaciones de red/BD en el hilo apropiado
- Contienen la lógica de mapeo DTO → Domain Model
- Orquestan llamadas a servicios y DAOs según sea necesario

---

## Flujo de Información

### Flujo hacia el Backend (Request)

```
UI (Composable)
  ↓
  Llama función del ViewModel (ej: viewModel.getAllProducts())
  ↓
ViewModel (ej: HomeViewModel)
  ↓
  Llama método del Repository (ej: repository.getAllProducts())
  ↓
Repository (interfaz en domain)
  ↓
  Implementado por
  ↓
RepositoryImpl (en data)
  ↓
  Ejecuta llamada al Service (ej: service.getAllProducts())
  ↓
Service (interfaz Retrofit)
  ↓
  Retrofit ejecuta la petición HTTP
  ↓
Backend (API REST)
```

**Características del flujo**:
- El ViewModel utiliza `viewModelScope.launch` para ejecutar operaciones asíncronas
- El RepositoryImpl utiliza `withContext(Dispatchers.IO)` para operaciones de red/BD
- No hay capa de UseCases: el flujo es directo ViewModel → Repository

### Flujo de Respuesta (Response)

```
Backend (API REST)
  ↓
  Retorna JSON
  ↓
Retrofit + Gson Converter
  ↓
  Convierte JSON automáticamente a DTO
  ↓
DTO (ej: ProductDto, ProductsWrapperDto)
  ↓
  Mapper inline en RepositoryImpl
  ↓
Domain Model (ej: Product)
  ↓
  Retorna a través de la interfaz Repository
  ↓
RepositoryImpl → Repository (interfaz)
  ↓
  Retorna al ViewModel
  ↓
ViewModel
  ↓
  Actualiza StateFlow (ej: _products.value = ...)
  ↓
UI (Composable)
  ↓
  Observa StateFlow con collectAsState()
  ↓
  Recompone automáticamente con los nuevos datos
```

**Características del flujo**:
- La conversión JSON → DTO es automática gracias a Gson
- El mapeo DTO → Domain Model ocurre en el RepositoryImpl
- El ViewModel expone el estado mediante `StateFlow` o `LiveData`
- La UI observa el estado y se actualiza reactivamente

### Ejemplo Completo: Obtener Lista de Productos

1. **UI**: El Composable `Home` llama a `viewModel.getAllProducts()` (o se ejecuta en `init`)
2. **ViewModel**: `HomeViewModel.getAllProducts()` ejecuta `repository.getAllProducts()` dentro de `viewModelScope.launch`
3. **Repository**: `ProductRepositoryImpl.getAllProducts()` ejecuta `service.getAllProducts()` dentro de `withContext(Dispatchers.IO)`
4. **Service**: Retrofit ejecuta la petición HTTP GET a `https://dummyjson.com/products`
5. **Backend**: Retorna JSON con la lista de productos
6. **Retrofit + Gson**: Convierte el JSON a `ProductsWrapperDto`
7. **RepositoryImpl**: Mapea `List<ProductDto>` a `List<Product>` usando `.map { }`
8. **RepositoryImpl**: Retorna `List<Product>` al ViewModel
9. **ViewModel**: Actualiza `_products.value = repository.getAllProducts()`
10. **UI**: El Composable observa `viewModel.products.collectAsState()` y se recompone con los nuevos datos

---

## Manejo de Errores

### Patrón Actual

**Ubicación**: Dentro de `RepositoryImpl`, en cada método que realiza una petición HTTP

**Estrategia**: Verificación simple de `response.isSuccessful`

**Comportamiento**:
- Si `response.isSuccessful == true`: procesa `response.body()`, mapea a Domain Model y retorna
- Si `response.isSuccessful == false`: retorna `null` o `emptyList()` según el tipo de retorno

**Características**:
- NO utiliza `Result<T>` o sealed classes para representar estados de éxito/error
- NO diferencia entre tipos de errores (red, servidor, autenticación, etc.)
- NO propaga mensajes de error al ViewModel o UI
- NO utiliza try-catch explícito para manejar excepciones de red

**Ejemplo de patrón**:
```kotlin
val response = service.getAllProducts()

if (response.isSuccessful) {
    response.body()?.let { dto ->
        // mapear y retornar Domain Model
        return@withContext mappedData
    }
}

return@withContext emptyList() // o null
```

### Propagación de Errores

**Nivel actual**: Los errores se "silencian" en el repositorio retornando valores por defecto (`null`, `emptyList()`)

**Hacia arriba**: El ViewModel recibe estos valores por defecto sin información sobre qué salió mal

**Hacia la UI**: La UI simplemente muestra una lista vacía o no muestra datos, sin indicar al usuario que hubo un error

### Nota sobre Mejoras

Este patrón es funcional pero limitado. Para proyectos más robustos, se recomendaría:
- Usar `Result<T>` o sealed classes para representar estados
- Diferenciar tipos de errores (red, servidor, autenticación, etc.)
- Propagar mensajes de error al ViewModel y luego a la UI
- Implementar manejo de excepciones con try-catch para errores de red

Sin embargo, este documento se enfoca en documentar el patrón actual de EasyShop, no en recomendar mejoras.

---

## Nota sobre Autenticación

### Estructura de Autenticación en EasyShop

EasyShop incluye un módulo de autenticación (`features/auth/`) que sigue la misma estructura de capas descrita en este documento:

- **Service**: `features/auth/data/remote/services/AuthService.kt` - Interfaz Retrofit para login
- **DTOs**: `features/auth/data/remote/models/LoginRequestDto.kt` y `LoginResponseDto.kt`
- **Repository**: `features/auth/domain/repositories/AuthRepository.kt` (interfaz) y `features/auth/data/repositories/AuthRepositoryImpl.kt` (implementación)
- **ViewModel**: `features/auth/presentation/login/LoginViewModel.kt`
- **UI**: `features/auth/presentation/login/Login.kt`

### Advertencia Importante

**La implementación de autenticación de EasyShop es específica de este proyecto y puede estar incompleta o ser mejorable.**

Este documento describe la **estructura arquitectónica** de cómo EasyShop organiza su código para conectar frontend y backend. Sin embargo:

- ❌ **NO se debe reutilizar** la lógica específica de autenticación (manejo de tokens, almacenamiento, interceptores, etc.)
- ❌ **NO se debe asumir** que la implementación actual de login/JWT/registro es correcta o completa
- ✅ **SÍ se puede reutilizar** la estructura de capas (data/domain/presentation)
- ✅ **SÍ se puede reutilizar** el patrón de organización de carpetas y flujo de datos

Para otros proyectos (como AutoNexo), se recomienda:
1. Adoptar la **estructura de capas** y organización de código descrita en este documento
2. Implementar la **lógica de autenticación** según las necesidades específicas del proyecto, siguiendo mejores prácticas de seguridad

---

## Resumen del Patrón

### Principios Clave

1. **Separación por Feature**: Cada feature es independiente con sus propias capas
2. **Arquitectura Limpia**: Separación clara entre data, domain y presentation
3. **Dependencias hacia adentro**: Domain no depende de Data ni Presentation
4. **DTOs separados de Domain Models**: Permite cambios en el backend sin afectar la lógica de negocio
5. **Repositorios como abstracción**: Facilita testing y cambios de implementación
6. **Coroutines para asincronía**: Uso de `suspend` functions y `Dispatchers.IO`

### Flujo Simplificado

```
UI → ViewModel → Repository (interfaz) → RepositoryImpl → Service (Retrofit) → Backend
                                                                                    ↓
UI ← ViewModel ← Repository (interfaz) ← RepositoryImpl ← DTO ← JSON ← Backend
```

### Componentes por Capa

- **Presentation**: ViewModels, Composables, PresentationModule
- **Domain**: Interfaces de Repositorios, Modelos de Dominio
- **Data**: Services (Retrofit), DTOs, RepositoryImpl, DataModule, DAOs (opcional), Entities (opcional)

Este patrón proporciona una base sólida y escalable para conectar el frontend Android con APIs REST, manteniendo el código organizado y fácil de mantener.

⚠️ Nota importante para AutoNexo

Este documento se utiliza solo como referencia de arquitectura y organización (capas, módulos, uso de Retrofit, Repositories, ViewModels, etc.).

La implementación actual de autenticación en EasyShop NO debe copiarse tal cual en AutoNexo. En particular:
No se debe replicar el flujo de login tal como está aquí.
El manejo correcto de JWT (login, registro, almacenamiento del token y envío del header Authorization: Bearer <token>) debe seguir la documentación del backend de AutoNexo (CORE.md, API_ENDPOINTS.md, ANDROID_WORKSHOP_APP_SCOPE.md).
Para cualquier nueva app (como la Android de taller), usa este documento solo como guía de estructura, nunca como fuente de verdad para endpoints o reglas de autenticación.