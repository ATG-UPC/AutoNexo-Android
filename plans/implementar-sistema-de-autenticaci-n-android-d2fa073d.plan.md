<!-- d2fa073d-a0cc-4241-993f-191b023e34d3 6fa20d94-8fb1-4ea5-af3d-348e092bce24 -->
# Plan de Implementación: Sistema de Autenticación Android - Clean Architecture

## Contexto

- **Backend URL**: `https://autonexo-backend-akcsb5avacemdwh7.canadacentral-01.azurewebsites.net`
- **Arquitectura**: Clean Architecture estricta con capas **domain**, **data** y **presentation**
- **Tecnologías**: Retrofit, ViewModel, DataStore, Coroutines, Hilt, Compose

## Ejemplos JSON - Requests y Responses del Backend

### 1. POST /api/v1/users/signin

**Request:**

```json
{
  "email": "usuario@ejemplo.com",
  "password": "password123"
}
```

**Response (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 604800,
  "user": {
    "id": 1,
    "email": "usuario@ejemplo.com",
    "firstName": "Juan",
    "lastName": "Pérez",
    "phoneNumber": "987654321",
    "isVerified": true,
    "active": true,
    "roles": ["CAR_OWNER"],
    "workshopId": null,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
}
```

### 2. POST /api/v1/users/signup

**Request (CAR_OWNER o WORKSHOP_MANAGER):**

```json
{
  "email": "nuevo@ejemplo.com",
  "password": "password123",
  "firstName": "María",
  "lastName": "García",
  "phoneNumber": "987654321",
  "requestedRole": "CAR_OWNER",
  "invitationCode": null
}
```

**Request (WORKSHOP_EMPLOYEE - invitationCode requerido):**

```json
{
  "email": "empleado@ejemplo.com",
  "password": "password123",
  "firstName": "Carlos",
  "lastName": "López",
  "phoneNumber": "987654321",
  "requestedRole": "WORKSHOP_EMPLOYEE",
  "invitationCode": "ABC123XYZ"
}
```

**Response (201 Created):**

```json
"User registered successfully"
```

### 3. POST /api/v1/users/verify-email

**Request:**

```json
{
  "token": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

**Response (200 OK):**

```json
"Email has been verified successfully"
```

### 4. POST /api/v1/users/resend-verification

**Request:**

```json
{
  "email": "usuario@ejemplo.com"
}
```

**Response (200 OK):**

```json
"Verification email has been sent"
```

### 5. POST /api/v1/users/forgot-password

**Request:**

```json
{
  "email": "usuario@ejemplo.com"
}
```

**Response (200 OK):**

```json
"If an account exists with this email, a password reset link has been sent."
```

### 6. POST /api/v1/users/reset-password

**Request:**

```json
{
  "token": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "newPassword": "nuevaPassword123"
}
```

**Response (200 OK):**

```json
"Password has been reset successfully"
```

### 7. GET /api/v1/users/available-roles

**Response (200 OK):**

```json
["CAR_OWNER", "WORKSHOP_MANAGER", "WORKSHOP_EMPLOYEE"]
```

**⚠️ IMPORTANTE**: Los nombres de campos en los DTOs deben coincidir EXACTAMENTE con estos ejemplos JSON.

---

## Estructura Clean Architecture

### 📦 CAPA DOMAIN

**Ubicación**: `features/auth/domain/`

#### Modelos de Dominio

- `User.kt` - Modelo de usuario del dominio
- `Role.kt` - Enum: CAR_OWNER, WORKSHOP_MANAGER, WORKSHOP_EMPLOYEE
- `SignUpRequest.kt` - Request de registro

#### Repository Interface

- `AuthRepository.kt` - Interface con métodos suspend

#### Use Cases

- `SignInUseCase.kt`
- `SignUpUseCase.kt`
- `VerifyEmailUseCase.kt`
- `ResendVerificationUseCase.kt`
- `ForgotPasswordUseCase.kt`
- `ResetPasswordUseCase.kt`

### 📡 CAPA DATA

**Ubicación**: `features/auth/data/`

#### Configuración Core (Infraestructura Base)

- `AutoNexoApplication.kt` - Clase Application con @HiltAndroidApp
- `core/data/PreferencesDataStore.kt` - DataStore para token y sesión
- `core/data/UserPreferences.kt` - Wrapper con métodos isLoggedIn(), getToken(), etc.
- `core/network/NetworkModule.kt` - Retrofit con base URL e interceptors
- `core/navigation/Route.kt` - Rutas de navegación (sealed class)
- `core/navigation/AppNavigation.kt` - NavHost principal
- `core/di/CoreModule.kt` - Módulo Hilt para componentes core

#### DTOs (Nombres exactos del JSON)

- `SignInRequestDto.kt` - `email`, `password`
- `SignInResponseDto.kt` - `token`, `tokenType`, `expiresIn`, `user`
- `SignUpRequestDto.kt` - `email`, `password`, `firstName`, `lastName`, `phoneNumber`, `requestedRole`, `invitationCode`
- `VerifyEmailRequestDto.kt` - `token`
- `ResendVerificationRequestDto.kt` - `email`
- `ForgotPasswordRequestDto.kt` - `email`
- `ResetPasswordRequestDto.kt` - `token`, `newPassword`
- `UserDto.kt` - `id`, `email`, `firstName`, `lastName`, `phoneNumber`, `isVerified`, `active`, `roles`, `workshopId`, `createdAt`, `updatedAt`

#### Mappers

- `UserDtoMapper.kt` - Convierte UserDto → User (dominio)

#### API Service

- `AuthApiService.kt` - Interface Retrofit con todos los endpoints

#### Repository Implementation

- `AuthRepositoryImpl.kt` - Implementa AuthRepository

#### DI Module

- `DataModule.kt` - Módulo Hilt para capa data

### 🎨 CAPA PRESENTATION

**Ubicación**: `features/auth/presentation/`

#### Login

- `login/LoginUiState.kt`
- `login/LoginViewModel.kt`
- `login/LoginScreen.kt`

#### Register

- `register/RegisterUiState.kt`
- `register/RegisterViewModel.kt`
- `register/RegisterScreen.kt`

#### Email Verification

- `emailverification/EmailVerificationUiState.kt`
- `emailverification/EmailVerificationViewModel.kt`
- `emailverification/EmailVerificationScreen.kt`

#### Forgot Password

- `forgotpassword/ForgotPasswordUiState.kt`
- `forgotpassword/ForgotPasswordViewModel.kt`
- `forgotpassword/ForgotPasswordScreen.kt`

#### Reset Password

- `resetpassword/ResetPasswordUiState.kt`
- `resetpassword/ResetPasswordViewModel.kt`
- `resetpassword/ResetPasswordScreen.kt`

#### DI Module

- `di/PresentationModule.kt` - Módulo Hilt para ViewModels

---

## Archivos Esenciales a Crear (47 archivos)

### Infraestructura Base (7 archivos)

1. `AutoNexoApplication.kt` - Clase Application con @HiltAndroidApp
2. `core/data/PreferencesDataStore.kt` - DataStore para token y sesión
3. `core/data/UserPreferences.kt` - Wrapper para PreferencesDataStore con métodos de autenticación
4. `core/network/NetworkModule.kt` - Retrofit con base URL e interceptors
5. `core/navigation/Route.kt` - Objeto sealed class con todas las rutas de navegación
6. `core/navigation/AppNavigation.kt` - Composable de navegación principal
7. `core/di/CoreModule.kt` - Módulo Hilt para componentes core

### Domain Layer (10 archivos)

8. `features/auth/domain/models/User.kt`
9. `features/auth/domain/models/Role.kt`
10. `features/auth/domain/models/SignUpRequest.kt`
11. `features/auth/domain/repositories/AuthRepository.kt`
12. `features/auth/domain/usecases/SignInUseCase.kt`
13. `features/auth/domain/usecases/SignUpUseCase.kt`
14. `features/auth/domain/usecases/VerifyEmailUseCase.kt`
15. `features/auth/domain/usecases/ResendVerificationUseCase.kt`
16. `features/auth/domain/usecases/ForgotPasswordUseCase.kt`
17. `features/auth/domain/usecases/ResetPasswordUseCase.kt`

### Data Layer (14 archivos)

18. `features/auth/data/remote/models/SignInRequestDto.kt`
19. `features/auth/data/remote/models/SignInResponseDto.kt`
20. `features/auth/data/remote/models/SignUpRequestDto.kt`
21. `features/auth/data/remote/models/VerifyEmailRequestDto.kt`
22. `features/auth/data/remote/models/ResendVerificationRequestDto.kt`
23. `features/auth/data/remote/models/ForgotPasswordRequestDto.kt`
24. `features/auth/data/remote/models/ResetPasswordRequestDto.kt`
25. `features/auth/data/remote/models/UserDto.kt`
26. `features/auth/data/mappers/UserDtoMapper.kt`
27. `features/auth/data/remote/services/AuthApiService.kt`
28. `features/auth/data/repositories/AuthRepositoryImpl.kt`
29. `features/auth/data/di/DataModule.kt`

### Presentation Layer (16 archivos)

30. `features/auth/presentation/login/LoginUiState.kt`
31. `features/auth/presentation/login/LoginViewModel.kt`
32. `features/auth/presentation/login/LoginScreen.kt`
33. `features/auth/presentation/register/RegisterUiState.kt`
34. `features/auth/presentation/register/RegisterViewModel.kt`
35. `features/auth/presentation/register/RegisterScreen.kt`
36. `features/auth/presentation/emailverification/EmailVerificationUiState.kt`
37. `features/auth/presentation/emailverification/EmailVerificationViewModel.kt`
38. `features/auth/presentation/emailverification/EmailVerificationScreen.kt`
39. `features/auth/presentation/forgotpassword/ForgotPasswordUiState.kt`
40. `features/auth/presentation/forgotpassword/ForgotPasswordViewModel.kt`
41. `features/auth/presentation/forgotpassword/ForgotPasswordScreen.kt`
42. `features/auth/presentation/resetpassword/ResetPasswordUiState.kt`
43. `features/auth/presentation/resetpassword/ResetPasswordViewModel.kt`
44. `features/auth/presentation/resetpassword/ResetPasswordScreen.kt`
45. `features/auth/presentation/di/PresentationModule.kt`

---

## Orden de Implementación

1. **Infraestructura Base** (Application, DataStore, Network, Navegación)
2. **Domain Layer** (sin dependencias)
3. **Data Layer** (depende de Domain)
4. **Presentation Layer** (depende de Domain)
5. **Integración Final** (conectar todo)

### To-dos

#### Infraestructura Base
- [ ] Agregar DataStore a dependencias (libs.versions.toml y build.gradle.kts)
- [ ] Crear AutoNexoApplication.kt con @HiltAndroidApp
- [ ] Crear core/data/PreferencesDataStore.kt para persistencia de token y sesión
- [ ] Crear core/data/UserPreferences.kt wrapper con métodos isLoggedIn(), getToken(), etc.
- [ ] Crear core/network/NetworkModule.kt con Retrofit, base URL e interceptors
- [ ] Crear core/navigation/Route.kt con todas las rutas (Auth.Login, Auth.Register, etc.)
- [ ] Crear core/navigation/AppNavigation.kt con NavHost y rutas de auth
- [ ] Crear core/di/CoreModule.kt para inyección de componentes core

#### Domain Layer
- [ ] Crear modelos de dominio: User.kt, Role.kt, SignUpRequest.kt
- [ ] Crear AuthRepository interface en domain/repositories/
- [ ] Crear Use Cases: SignIn, SignUp, VerifyEmail, ResendVerification, ForgotPassword, ResetPassword

#### Data Layer
- [ ] Crear DTOs con nombres exactos del JSON: SignInRequestDto, SignInResponseDto, SignUpRequestDto, VerifyEmailRequestDto, ResendVerificationRequestDto, ForgotPasswordRequestDto, ResetPasswordRequestDto, UserDto
- [ ] Crear UserDtoMapper para convertir UserDto → User (dominio)
- [ ] Crear AuthApiService.kt con todos los endpoints del backend
- [ ] Implementar AuthRepositoryImpl conectando API service, DataStore y mappers
- [ ] Crear DataModule.kt para inyección de dependencias de la capa data

#### Presentation Layer
- [ ] Crear LoginUiState.kt, LoginViewModel.kt y LoginScreen.kt
- [ ] Crear RegisterUiState.kt, RegisterViewModel.kt y RegisterScreen.kt
- [ ] Crear EmailVerificationUiState.kt, EmailVerificationViewModel.kt y EmailVerificationScreen.kt
- [ ] Crear ForgotPasswordUiState.kt, ForgotPasswordViewModel.kt y ForgotPasswordScreen.kt
- [ ] Crear ResetPasswordUiState.kt, ResetPasswordViewModel.kt y ResetPasswordScreen.kt
- [ ] Crear PresentationModule.kt para inyección de ViewModels

#### Integración
- [ ] Verificar que todas las rutas estén conectadas en AppNavigation
- [ ] Probar flujo completo: registro → verificación → login → recuperación