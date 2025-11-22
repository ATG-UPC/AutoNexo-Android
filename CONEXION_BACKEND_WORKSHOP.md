# Guía Paso a Paso: Conexión del Módulo Workshop con el Backend

## Tabla de Contenidos
1. [Introducción](#introducción)
2. [Arquitectura General](#arquitectura-general)
3. [Configuración Inicial](#configuración-inicial)
4. [Flujo Completo de Datos](#flujo-completo-de-datos)
5. [Paso 1: Capa Presentation (UI)](#paso-1-capa-presentation-ui)
6. [Paso 2: ViewModel](#paso-2-viewmodel)
7. [Paso 3: UseCase](#paso-3-usecase)
8. [Paso 4: Repository Interface](#paso-4-repository-interface)
9. [Paso 5: Repository Implementation](#paso-5-repository-implementation)
10. [Paso 6: API Service (Retrofit)](#paso-6-api-service-retrofit)
11. [Paso 7: DTOs y Mappers](#paso-7-dtos-y-mappers)
12. [Paso 8: Configuración de Red](#paso-8-configuración-de-red)
13. [Paso 9: Inyección de Dependencias](#paso-9-inyección-de-dependencias)
14. [Ejemplo Completo: Crear Workshop](#ejemplo-completo-crear-workshop)
15. [Manejo de Errores](#manejo-de-errores)
16. [Preguntas Frecuentes](#preguntas-frecuentes)

---

## Introducción

Esta guía explica **paso a paso** cómo se conecta el módulo `workshop` con el backend usando **Clean Architecture** y **Retrofit**. Cada paso está explicado en detalle para que entiendas exactamente qué sucede en cada capa.

---

## Arquitectura General

El módulo `workshop` sigue **Clean Architecture** con 3 capas principales:

```
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION                         │
│  (UI - Screen, ViewModel, UiState)                      │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────┐
│                      DOMAIN                             │
│  (Lógica de Negocio - UseCases, Models, Repositories)   │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────┐
│                       DATA                              │
│  (Acceso a Datos - DTOs, API Services, Repositories)   │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ▼
              ┌─────────┐
              │ BACKEND │
              └─────────┘
```

**Regla de Dependencias**: Las capas solo pueden depender de capas internas. Presentation → Domain → Data → Backend

---

## Configuración Inicial

### 1. URL Base del Backend

La URL base se configura en `core/network/NetworkModule.kt`:

```kotlin
private const val BASE_URL = "https://autonexo-backend-akcsb5avacemdwh7.canadacentral-01.azurewebsites.net/"
```

### 2. Configuración de Retrofit

Retrofit se configura en el mismo archivo y se encarga de:
- Convertir objetos Kotlin a JSON (y viceversa)
- Manejar peticiones HTTP
- Agregar headers automáticamente (como el token de autenticación)

---

## Flujo Completo de Datos

Cuando el usuario presiona "Siguiente" en la pantalla de crear workshop, esto es lo que sucede:

```
1. Usuario presiona botón "Siguiente"
   ↓
2. BasicInfoScreen llama a viewModel.createWorkshop()
   ↓
3. BasicInfoViewModel ejecuta createWorkshopUseCase()
   ↓
4. CreateWorkshopUseCase llama a repository.createWorkshop()
   ↓
5. WorkshopRepositoryImpl convierte CreateWorkshopRequest → CreateWorkshopRequestDto
   ↓
6. WorkshopRepositoryImpl llama a apiService.createWorkshop(dto)
   ↓
7. Retrofit convierte el DTO a JSON y hace la petición HTTP POST
   ↓
8. Backend procesa la petición y devuelve WorkshopResponseDto (JSON)
   ↓
9. Retrofit convierte el JSON a WorkshopResponseDto
   ↓
10. WorkshopRepositoryImpl convierte WorkshopResponseDto → Workshop (domain model)
   ↓
11. El resultado fluye de vuelta: Repository → UseCase → ViewModel → Screen
   ↓
12. La UI se actualiza con el resultado
```

---

## Paso 1: Capa Presentation (UI)

### Ubicación
`features/workshop/presentation/registration/basicinfo/BasicInfoScreen.kt`

### ¿Qué hace?
La pantalla muestra los campos del formulario y captura la interacción del usuario.

### Código Clave

```kotlin
@Composable
fun BasicInfoScreen(
    viewModel: BasicInfoViewModel = hiltViewModel(),
    onNext: (workshopId: Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Cuando el usuario presiona "Siguiente"
    Button(
        onClick = { viewModel.createWorkshop(onNext) },
        enabled = !uiState.isLoading && /* validaciones */
    ) {
        Text("Siguiente")
    }
}
```

### Explicación
- `viewModel.uiState.collectAsState()`: Observa el estado del ViewModel. Cuando cambia, la UI se actualiza automáticamente.
- `viewModel.createWorkshop(onNext)`: Llama al ViewModel para crear el workshop.
- `onNext`: Callback que se ejecuta cuando el workshop se crea exitosamente.

---

## Paso 2: ViewModel

### Ubicación
`features/workshop/presentation/registration/basicinfo/BasicInfoViewModel.kt`

### ¿Qué hace?
- Maneja el estado de la UI (`BasicInfoUiState`)
- Ejecuta los UseCases
- Maneja la lógica de presentación (validaciones, callbacks)

### Código Clave

```kotlin
@HiltViewModel
class BasicInfoViewModel @Inject constructor(
    private val createWorkshopUseCase: CreateWorkshopUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BasicInfoUiState())
    val uiState: StateFlow<BasicInfoUiState> = _uiState.asStateFlow()

    fun createWorkshop(onSuccess: (workshopId: Long) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // 1. Primero obtener el usuario actual para obtener su ID
            getCurrentUserUseCase()
                .onSuccess { user ->
                    // 2. Crear el request con el ownerUserId
                    val request = CreateWorkshopRequest(
                        ownerUserId = user.id,
                        name = currentState.name.trim(),
                        shortDescription = currentState.shortDescription.trim().takeIf { it.isNotBlank() },
                        legalName = currentState.legalName.trim().takeIf { it.isNotBlank() },
                        ruc = currentState.ruc.trim().takeIf { it.isNotBlank() }
                    )
                    
                    // 3. Ejecutar el UseCase
                    createWorkshopUseCase(request)
                        .onSuccess { workshop ->
                            _uiState.value = _uiState.value.copy(isLoading = false)
                            onSuccess(workshop.id) // Navegar a la siguiente pantalla
                        }
                        .onFailure { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = exception.message
                            )
                        }
                }
        }
    }
}
```

### Explicación Detallada

1. **`@HiltViewModel`**: Anotación que permite a Hilt inyectar dependencias automáticamente.
2. **`@Inject constructor(...)`**: Hilt inyecta los UseCases automáticamente.
3. **`viewModelScope.launch`**: Crea una corrutina que se cancela cuando el ViewModel se destruye.
4. **`_uiState.value = ...`**: Actualiza el estado. La UI observa `uiState` y se actualiza automáticamente.
5. **`.onSuccess { }`**: Se ejecuta si la operación fue exitosa.
6. **`.onFailure { }`**: Se ejecuta si hubo un error.

---

## Paso 3: UseCase

### Ubicación
`features/workshop/domain/usecases/CreateWorkshopUseCase.kt`

### ¿Qué hace?
Encapsula una única operación de negocio. En este caso: crear un workshop.

### Código Completo

```kotlin
class CreateWorkshopUseCase @Inject constructor(
    private val repository: WorkshopRepository
) {
    suspend operator fun invoke(request: CreateWorkshopRequest) = 
        repository.createWorkshop(request)
}
```

### Explicación

- **`suspend`**: Función que puede suspenderse (operación asíncrona).
- **`operator fun invoke`**: Permite llamar al UseCase como si fuera una función: `useCase(request)`.
- **`repository.createWorkshop(request)`**: Delega la operación al repositorio.

### ¿Por qué usar UseCase?
- **Separación de responsabilidades**: Cada UseCase hace una sola cosa.
- **Reutilización**: Puedes usar el mismo UseCase desde diferentes ViewModels.
- **Testing**: Es fácil testear la lógica de negocio.

---

## Paso 4: Repository Interface

### Ubicación
`features/workshop/domain/repositories/WorkshopRepository.kt`

### ¿Qué hace?
Define el **contrato** (interfaz) de qué operaciones puede realizar el repositorio. No tiene implementación, solo define qué métodos debe tener.

### Código Clave

```kotlin
interface WorkshopRepository {
    suspend fun createWorkshop(request: CreateWorkshopRequest): Result<Workshop>
    suspend fun getWorkshopById(workshopId: Long): Result<Workshop>
    suspend fun getMyWorkshop(): Result<Workshop>
    // ... más métodos
}
```

### Explicación

- **`interface`**: Define un contrato. Cualquier clase que implemente esta interfaz debe tener estos métodos.
- **`Result<Workshop>`**: Tipo que puede ser `Result.success(workshop)` o `Result.failure(exception)`.
- **`suspend`**: Todas las operaciones son asíncronas.

### ¿Por qué una interfaz?
- **Desacoplamiento**: El Domain no depende de la implementación concreta.
- **Testing**: Puedes crear un repositorio "mock" para pruebas.
- **Flexibilidad**: Puedes cambiar la implementación sin afectar el Domain.

---

## Paso 5: Repository Implementation

### Ubicación
`features/workshop/data/repositories/WorkshopRepositoryImpl.kt`

### ¿Qué hace?
Implementa la interfaz `WorkshopRepository`. Aquí es donde realmente se hace la llamada al backend.

### Código Clave (Método createWorkshop)

```kotlin
class WorkshopRepositoryImpl @Inject constructor(
    private val apiService: WorkshopApiService,
    private val gson: Gson,
    private val context: Context
) : WorkshopRepository {

    override suspend fun createWorkshop(request: CreateWorkshopRequest): Result<Workshop> {
        return try {
            // PASO 1: Convertir el modelo de dominio a DTO
            val dto = CreateWorkshopRequestDto(
                ownerUserId = request.ownerUserId,
                name = request.name,
                shortDescription = request.shortDescription,
                legalName = request.legalName,
                ruc = request.ruc
            )
            
            // PASO 2: Llamar al API Service (Retrofit)
            val response = apiService.createWorkshop(dto)
            
            // PASO 3: Verificar si la respuesta fue exitosa
            if (response.isSuccessful && response.body() != null) {
                // PASO 4: Convertir el DTO de respuesta a modelo de dominio
                val workshop = response.body()!!.toDomain()
                Result.success(workshop)
            } else {
                // PASO 5: Manejar errores HTTP
                Result.failure(Exception(parseError(response, "Error al crear workshop")))
            }
        } catch (e: HttpException) {
            // Manejar excepciones HTTP específicas
            Result.failure(Exception(parseHttpException(e, "Error al crear workshop")))
        } catch (e: IOException) {
            // Manejar errores de conexión
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            // Manejar cualquier otro error
            Result.failure(e)
        }
    }
}
```

### Explicación Paso a Paso

#### PASO 1: Conversión Domain → DTO
```kotlin
val dto = CreateWorkshopRequestDto(
    ownerUserId = request.ownerUserId,
    name = request.name,
    // ...
)
```
- **¿Por qué?** El backend espera un formato específico (DTO). El modelo de dominio puede tener una estructura diferente.
- **Ejemplo**: El dominio puede tener `ownerUserId`, pero el backend espera `ownerUserId` en el JSON.

#### PASO 2: Llamada al API Service
```kotlin
val response = apiService.createWorkshop(dto)
```
- **¿Qué hace?** Retrofit convierte el DTO a JSON y hace una petición HTTP POST al backend.
- **¿Cómo funciona?** Retrofit usa la interfaz `WorkshopApiService` para saber:
  - URL: `api/v1/workshops`
  - Método: `POST`
  - Body: El DTO convertido a JSON

#### PASO 3: Verificar Respuesta
```kotlin
if (response.isSuccessful && response.body() != null)
```
- **`response.isSuccessful`**: Verifica que el código HTTP sea 200-299.
- **`response.body() != null`**: Verifica que el backend devolvió datos.

#### PASO 4: Conversión DTO → Domain
```kotlin
val workshop = response.body()!!.toDomain()
```
- **`.toDomain()`**: Función de extensión que convierte `WorkshopResponseDto` a `Workshop`.
- **`!!`**: Operador de aserción no-null (sabemos que no es null porque lo verificamos antes).

#### PASO 5: Manejo de Errores
- **`HttpException`**: Errores HTTP (400, 401, 500, etc.)
- **`IOException`**: Errores de conexión (sin internet, timeout, etc.)
- **`Exception`**: Cualquier otro error inesperado

---

## Paso 6: API Service (Retrofit)

### Ubicación
`features/workshop/data/remote/services/WorkshopApiService.kt`

### ¿Qué hace?
Define las **interfaces** de los endpoints del backend usando anotaciones de Retrofit.

### Código Clave

```kotlin
interface WorkshopApiService {
    
    @POST("api/v1/workshops")
    suspend fun createWorkshop(@Body request: CreateWorkshopRequestDto): Response<WorkshopResponseDto>
    
    @GET("api/v1/workshops/{workshopId}")
    suspend fun getWorkshopById(@Path("workshopId") workshopId: Long): Response<WorkshopResponseDto>
    
    @Multipart
    @POST("api/v1/workshops/logo")
    suspend fun uploadLogo(@Part file: MultipartBody.Part): Response<UploadResponseDto>
}
```

### Explicación de Anotaciones

#### `@POST("api/v1/workshops")`
- **Método HTTP**: POST
- **Ruta**: Se concatena con `BASE_URL` → `https://.../api/v1/workshops`

#### `@Body request: CreateWorkshopRequestDto`
- **Body de la petición**: Retrofit convierte el DTO a JSON automáticamente.
- **Content-Type**: Se establece como `application/json`.

#### `@Path("workshopId")`
- **Parámetro de ruta**: Reemplaza `{workshopId}` en la URL.
- **Ejemplo**: `@GET("api/v1/workshops/{workshopId}")` con `workshopId = 123` → `/api/v1/workshops/123`

#### `@Query("tag")`
- **Parámetro de query**: Se agrega como `?tag=valor` en la URL.
- **Ejemplo**: `@GET("api/v1/workshops/by-tag")` con `tag = "HEAVY_VEHICLES"` → `/api/v1/workshops/by-tag?tag=HEAVY_VEHICLES`

#### `@Multipart` y `@Part`
- **Para subir archivos**: Se usa `multipart/form-data`.
- **Ejemplo**: Subir logo o fotos.

### ¿Cómo Retrofit Convierte a JSON?

Retrofit usa **Gson** (configurado en `NetworkModule`) para convertir:
- **Kotlin Object → JSON**: Cuando envías datos al backend.
- **JSON → Kotlin Object**: Cuando recibes datos del backend.

---

## Paso 7: DTOs y Mappers

### DTOs (Data Transfer Objects)

#### Ubicación
`features/workshop/data/remote/models/CreateWorkshopRequestDto.kt`

#### ¿Qué es un DTO?
Un objeto que representa exactamente cómo el backend espera recibir/enviar los datos.

#### Código

```kotlin
data class CreateWorkshopRequestDto(
    @SerializedName("ownerUserId")
    val ownerUserId: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("shortDescription")
    val shortDescription: String? = null,
    // ...
)
```

#### Explicación

- **`@SerializedName("ownerUserId")`**: Le dice a Gson cómo nombrar el campo en el JSON.
- **¿Por qué?** En Kotlin usamos `camelCase`, pero el backend puede usar `snake_case` o cualquier otro formato.
- **Ejemplo**: Si el backend espera `owner_user_id`, usarías `@SerializedName("owner_user_id")`.

### Mappers

#### Ubicación
`features/workshop/data/mappers/WorkshopDtoMapper.kt`

#### ¿Qué hace un Mapper?
Convierte entre modelos de dominio y DTOs.

#### Código

```kotlin
// Conversión: DTO → Domain (cuando recibes datos del backend)
fun WorkshopResponseDto.toDomain(): Workshop {
    return Workshop(
        id = id,
        ownerUserId = ownerUserId,
        name = name,
        // ...
    )
}

// Conversión: Domain → DTO (cuando envías datos al backend)
fun CreateWorkshopRequest.toDto(): CreateWorkshopRequestDto {
    return CreateWorkshopRequestDto(
        ownerUserId = ownerUserId,
        name = name,
        // ...
    )
}
```

#### Explicación

- **Función de extensión**: `fun Tipo.toOtroTipo()` permite hacer `dto.toDomain()`.
- **¿Por qué separar?** Los modelos de dominio pueden tener lógica de negocio, los DTOs solo representan datos.

---

## Paso 8: Configuración de Red

### Ubicación
`core/network/NetworkModule.kt`

### Componentes Principales

#### 1. OkHttpClient

```kotlin
fun provideOkHttpClient(preferencesDataStore: PreferencesDataStore): OkHttpClient {
    // Interceptor de autenticación
    val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = runBlocking {
            preferencesDataStore.token.first()
        }
        
        val requestBuilder = originalRequest.newBuilder()
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        
        chain.proceed(requestBuilder.build())
    }
    
    // Interceptor de logging
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    return OkHttpClient.Builder()
        .addInterceptor(authInterceptor)  // Agrega token automáticamente
        .addInterceptor(loggingInterceptor)  // Log de todas las peticiones
        .build()
}
```

#### Explicación

- **`authInterceptor`**: Agrega automáticamente el header `Authorization: Bearer <token>` a todas las peticiones.
- **`loggingInterceptor`**: Registra todas las peticiones y respuestas en Logcat (útil para debugging).

#### 2. Gson

```kotlin
fun provideGson(): Gson {
    return GsonBuilder()
        .serializeNulls()  // Incluye campos null en el JSON
        .setLenient()
        .create()
}
```

#### Explicación

- **`serializeNulls()`**: Si un campo es `null`, lo incluye en el JSON como `null` (requerido por el backend).
- **`setLenient()`**: Permite JSON más flexible (útil para debugging).

#### 3. Retrofit

```kotlin
fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}
```

#### Explicación

- **`baseUrl`**: URL base del backend.
- **`client`**: OkHttpClient configurado con interceptores.
- **`addConverterFactory`**: Convierte objetos Kotlin ↔ JSON usando Gson.

---

## Paso 9: Inyección de Dependencias

### Ubicación
`features/workshop/data/di/DataModule.kt`

### ¿Qué hace?
Le dice a Hilt cómo crear las dependencias de la capa Data.

### Código

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideWorkshopApiService(retrofit: Retrofit): WorkshopApiService {
        return retrofit.create(WorkshopApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWorkshopRepository(
        workshopApiService: WorkshopApiService,
        gson: Gson,
        @ApplicationContext context: Context
    ): WorkshopRepository {
        return WorkshopRepositoryImpl(workshopApiService, gson, context)
    }
}
```

### Explicación

- **`@Module`**: Marca esta clase como un módulo de Hilt.
- **`@InstallIn(SingletonComponent::class)`**: Las dependencias se crean una sola vez y se reutilizan.
- **`@Provides`**: Le dice a Hilt cómo crear esta dependencia.
- **`@Singleton`**: Solo existe una instancia en toda la aplicación.

### Flujo de Inyección

```
1. Hilt necesita WorkshopRepository
   ↓
2. Busca en DataModule cómo crearlo
   ↓
3. Ve que necesita WorkshopApiService, Gson, Context
   ↓
4. Busca cómo crear WorkshopApiService
   ↓
5. Ve que necesita Retrofit
   ↓
6. Busca cómo crear Retrofit (en NetworkModule)
   ↓
7. Ve que necesita OkHttpClient y Gson
   ↓
8. Crea todas las dependencias en orden
   ↓
9. Inyecta WorkshopRepository donde se necesite
```

---

## Ejemplo Completo: Crear Workshop

Vamos a seguir el flujo completo desde que el usuario presiona "Siguiente" hasta que recibe la respuesta.

### 1. Usuario Presiona "Siguiente"

**Archivo**: `BasicInfoScreen.kt`

```kotlin
Button(
    onClick = { viewModel.createWorkshop(onNext) }
) {
    Text("Siguiente")
}
```

### 2. ViewModel Ejecuta el UseCase

**Archivo**: `BasicInfoViewModel.kt`

```kotlin
fun createWorkshop(onSuccess: (workshopId: Long) -> Unit) {
    viewModelScope.launch {
        // Obtener usuario actual
        getCurrentUserUseCase()
            .onSuccess { user ->
                // Crear request
                val request = CreateWorkshopRequest(
                    ownerUserId = user.id,
                    name = currentState.name.trim(),
                    // ...
                )
                
                // Ejecutar UseCase
                createWorkshopUseCase(request)
                    .onSuccess { workshop ->
                        onSuccess(workshop.id)
                    }
            }
    }
}
```

### 3. UseCase Llama al Repository

**Archivo**: `CreateWorkshopUseCase.kt`

```kotlin
suspend operator fun invoke(request: CreateWorkshopRequest) = 
    repository.createWorkshop(request)
```

### 4. Repository Convierte a DTO y Llama al API

**Archivo**: `WorkshopRepositoryImpl.kt`

```kotlin
override suspend fun createWorkshop(request: CreateWorkshopRequest): Result<Workshop> {
    // Convertir Domain → DTO
    val dto = CreateWorkshopRequestDto(
        ownerUserId = request.ownerUserId,
        name = request.name,
        // ...
    )
    
    // Llamar al API Service
    val response = apiService.createWorkshop(dto)
    
    // Convertir DTO → Domain
    val workshop = response.body()!!.toDomain()
    return Result.success(workshop)
}
```

### 5. Retrofit Hace la Petición HTTP

**Archivo**: `WorkshopApiService.kt` (interfaz)

```kotlin
@POST("api/v1/workshops")
suspend fun createWorkshop(@Body request: CreateWorkshopRequestDto): Response<WorkshopResponseDto>
```

**Lo que hace Retrofit internamente**:

1. Toma el DTO y lo convierte a JSON usando Gson:
```json
{
  "ownerUserId": 23,
  "name": "Mi Workshop",
  "shortDescription": "Descripción",
  "legalName": null,
  "ruc": null
}
```

2. Crea una petición HTTP POST:
```
POST https://autonexo-backend.../api/v1/workshops
Headers:
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
  Content-Type: application/json
Body: { "ownerUserId": 23, "name": "Mi Workshop", ... }
```

3. Envía la petición al backend.

### 6. Backend Responde

El backend procesa la petición y responde con:

```json
{
  "id": 12,
  "ownerUserId": 23,
  "name": "Mi Workshop",
  "shortDescription": "Descripción",
  "legalName": null,
  "ruc": null,
  "rucVerified": false,
  "trustScore": null,
  "active": true,
  "deletedAt": null,
  "logoUrl": null,
  "photoUrls": [],
  "capabilityTags": [],
  "createdAt": "2025-11-20T14:18:35.777+00:00",
  "updatedAt": "2025-11-20T14:18:35.777+00:00"
}
```

### 7. Retrofit Convierte JSON a DTO

Retrofit recibe el JSON y lo convierte automáticamente a `WorkshopResponseDto` usando Gson.

### 8. Repository Convierte DTO a Domain Model

**Archivo**: `WorkshopDtoMapper.kt`

```kotlin
fun WorkshopResponseDto.toDomain(): Workshop {
    return Workshop(
        id = id,
        ownerUserId = ownerUserId,
        name = name,
        // ...
    )
}
```

### 9. El Resultado Fluye de Vuelta

```
Repository → UseCase → ViewModel → Screen
```

**En el ViewModel**:
```kotlin
createWorkshopUseCase(request)
    .onSuccess { workshop ->
        _uiState.value = _uiState.value.copy(isLoading = false)
        onSuccess(workshop.id)  // Navega a la siguiente pantalla
    }
```

**En la Screen**:
```kotlin
viewModel.createWorkshop { workshopId ->
    onNext(workshopId)  // Navega a TagsScreen
}
```

---

## Manejo de Errores

### Tipos de Errores

#### 1. Errores HTTP (400, 401, 500, etc.)

```kotlin
catch (e: HttpException) {
    val errorBodyString = e.response()?.errorBody()?.string()
    
    // Intentar parsear el error del backend
    val errorResponse = gson.fromJson(errorBodyString, ErrorResponseDto::class.java)
    val errorMessage = errorResponse.message ?: "Error desconocido"
    
    Result.failure(Exception(errorMessage))
}
```

#### 2. Errores de Conexión

```kotlin
catch (e: IOException) {
    Result.failure(Exception("Error de conexión: ${e.message}"))
}
```

#### 3. Errores de Parsing JSON

```kotlin
catch (e: JsonSyntaxException) {
    Result.failure(Exception("Error al parsear respuesta: ${e.message}"))
}
```

### Flujo de Manejo de Errores

```
Backend devuelve error HTTP 400
   ↓
Retrofit lanza HttpException
   ↓
Repository lo captura en catch
   ↓
Parsea el errorBody para obtener el mensaje
   ↓
Retorna Result.failure(Exception(mensaje))
   ↓
UseCase retorna el mismo Result.failure
   ↓
ViewModel recibe el error en .onFailure
   ↓
Actualiza _uiState con errorMessage
   ↓
Screen muestra el error al usuario
```

---

## Preguntas Frecuentes

### ¿Por qué usar 3 capas?

**Respuesta**: Separación de responsabilidades. Cada capa tiene un propósito específico:
- **Presentation**: Solo se preocupa de la UI.
- **Domain**: Contiene la lógica de negocio pura (sin dependencias de Android o red).
- **Data**: Se encarga de obtener datos (API, base de datos, etc.).

### ¿Por qué usar DTOs en lugar de los modelos de dominio directamente?

**Respuesta**: 
- Los modelos de dominio pueden cambiar sin afectar cómo se comunica con el backend.
- El backend puede esperar campos diferentes o adicionales.
- Los DTOs representan exactamente el contrato con el backend.

### ¿Cómo funciona la inyección de dependencias?

**Respuesta**: Hilt crea automáticamente las dependencias cuando las necesitas. Solo necesitas:
1. Anotar con `@Inject constructor(...)`
2. Proporcionar las dependencias en módulos (`@Provides`)
3. Hilt hace el resto automáticamente.

### ¿Qué pasa si el backend cambia un campo?

**Respuesta**: Solo necesitas actualizar:
1. El DTO correspondiente
2. El mapper (si es necesario)
3. El resto del código sigue funcionando igual.

### ¿Cómo agrego un nuevo endpoint?

**Pasos**:
1. Agregar el método en `WorkshopApiService` con la anotación correspondiente.
2. Agregar el método en `WorkshopRepository` (interfaz).
3. Implementar el método en `WorkshopRepositoryImpl`.
4. Crear el UseCase correspondiente.
5. Usar el UseCase en el ViewModel.

### ¿Cómo funciona el token de autenticación?

**Respuesta**: El `authInterceptor` en `NetworkModule` agrega automáticamente el token a todas las peticiones:
1. Obtiene el token de `PreferencesDataStore`.
2. Lo agrega como header: `Authorization: Bearer <token>`.
3. Retrofit lo incluye en cada petición automáticamente.

---

## Resumen del Flujo

```
┌─────────────────────────────────────────────────────────────┐
│ PASO 1: Usuario interactúa con la UI                       │
│   BasicInfoScreen → viewModel.createWorkshop()              │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ PASO 2: ViewModel ejecuta UseCase                           │
│   BasicInfoViewModel → createWorkshopUseCase(request)       │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ PASO 3: UseCase llama al Repository                          │
│   CreateWorkshopUseCase → repository.createWorkshop()       │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ PASO 4: Repository convierte Domain → DTO                    │
│   CreateWorkshopRequest → CreateWorkshopRequestDto          │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ PASO 5: Repository llama al API Service                     │
│   WorkshopRepositoryImpl → apiService.createWorkshop(dto)   │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ PASO 6: Retrofit convierte DTO → JSON y hace HTTP POST      │
│   Retrofit → POST /api/v1/workshops                          │
│   Headers: Authorization: Bearer <token>                    │
│   Body: JSON del DTO                                         │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ PASO 7: Backend procesa y responde                          │
│   Backend → HTTP 200 OK + JSON WorkshopResponseDto          │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ PASO 8: Retrofit convierte JSON → DTO                       │
│   JSON → WorkshopResponseDto                                │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ PASO 9: Repository convierte DTO → Domain                   │
│   WorkshopResponseDto → Workshop (usando mapper)             │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ PASO 10: Result fluye de vuelta                             │
│   Repository → UseCase → ViewModel → Screen                 │
│   Result.success(workshop)                                   │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ PASO 11: UI se actualiza                                     │
│   Screen muestra éxito y navega a la siguiente pantalla     │
└─────────────────────────────────────────────────────────────┘
```

---

## Conclusión

Esta arquitectura garantiza:
- **Separación de responsabilidades**: Cada capa tiene un propósito claro.
- **Testabilidad**: Puedes testear cada capa independientemente.
- **Mantenibilidad**: Cambios en una capa no afectan las otras.
- **Escalabilidad**: Fácil agregar nuevas funcionalidades.

Si tienes dudas sobre algún paso específico, revisa el código correspondiente en el proyecto.

