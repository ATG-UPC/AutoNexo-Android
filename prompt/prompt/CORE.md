# AutoNexo - Sistema Core

## 🚗 Descripción de la Aplicación

**AutoNexo** es una plataforma de intermediación digital diseñada para conectar a **Dueños de Vehículos** con **Talleres Mecánicos** de confianza. Su principal valor reside en:

- Simplificar la búsqueda de servicios de mantenimiento específicos
- Facilitar la comunicación entre propietarios y talleres
- Digitalizar y centralizar el historial de mantenimientos de cada vehículo
- Generar confianza través de un sistema de reputación bidireccional
- Gestionar suscripciones y pagos para talleres

Esto beneficia a los **dueños** al tener un registro claro de su vehículo y a los **mecánicos** al obtener información histórica valiosa para un diagnóstico y servicio más eficiente.

## 📱 Conexión con Aplicaciones Móviles

El backend de AutoNexo sirve como la fuente de datos (API) principal para dos aplicaciones móviles distintas:
- Una aplicación móvil **nativa de Android**
- Una aplicación móvil construida en **Flutter** (código cruzado)

Ambas apps consumen la misma API REST y comparten la misma lógica de negocio del backend.

## 🏗️ Arquitectura DDD - Bounded Contexts

La arquitectura se basa en **Domain-Driven Design (DDD)**, dividiendo la lógica de negocio en **7 contextos delimitados** (Bounded Contexts):

### 1. IAM Context (Identity and Access Management)

**Propósito**: Gestionar identidades, autenticación y autorización de usuarios.

**Responsabilidades**:
- Registro y autenticación de usuarios (Car Owners y Workshop Managers)
- Emisión y validación de tokens JWT
- Gestión de roles (`CAR_OWNER`, `WORKSHOP_MANAGER`, `WORKSHOP_WORKER`, `ADMIN`)
- Verificación de email
- Recuperación de contraseña
- Gestión de perfiles de usuario
- Trust Score de usuarios

**Entities Principales**:
- `User` (Aggregate Root)
- `PasswordResetToken`
- `EmailVerificationToken`
- `WorkshopReference` (asociación con talleres)

**Endpoints Clave**:
- `POST /api/v1/users/signup` - Registro
- `POST /api/v1/users/signin` - Login
- `POST /api/v1/users/request-password-reset` - Solicitar reset de contraseña
- `GET /api/v1/users/me` - Obtener perfil actual

**Integración**: Provee información de usuarios a todos los demás contextos a través de su facade ACL.

---

### 2. Workshop Context

**Propósito**: Gestionar el ciclo de vida y configuración de talleres mecánicos.

**Responsabilidades**:
- Registro y configuración de talleres
- Gestión de múltiples ubicaciones
- Definición de servicios ofrecidos (service templates)
- Gestión de personal (staff members)
- Capability tags (especialidades del taller)
- Sistema de invitaciones para añadir staff
- Gestión de suscripciones (FREE, BASIC, PREMIUM)
- Trust Score de talleres
- Subida de logos y fotos del taller

**Entities Principales**:
- `Workshop` (Aggregate Root)
- `Location`
- `ServiceTemplate`
- `StaffMember`
- `Invitation`

**Endpoints Clave**:
- `POST /api/v1/workshops` - Crear taller
- `GET /api/v1/workshops/my-workshop` - Ver mi taller
- `POST /api/v1/workshops/locations` - Añadir ubicación
- `POST /api/v1/workshops/staff` - Añadir miembro del personal
- `GET /api/v1/workshops/search` - Búsqueda pública de talleres
- `GET /api/v1/workshops/catalog/**` - Catálogos públicos

**Integración**: Expone información de talleres al Matching & Booking, Trust & Reputation, y Payment contexts.

---

### 3. Vehicle & Maintenance Context

**Propósito**: Gestionar vehículos de los usuarios y su historial de mantenimiento.

**Responsabilidades**:
- Registro de vehículos (marca, modelo, año, VIN, placa, color, kilometraje)
- Gestión de propiedad de vehículos
- Transferencia de propiedad (mantiene historial)
- Historial completo de mantenimientos
- Mantenimientos manuales (usuario) vs verificados (taller)
- Confirmación de mantenimientos creados por talleres
- Recordatorios de mantenimiento por email
- Gestión de imágenes de vehículos (Cloudinary)

**Entities Principales**:
- `Vehicle` (Aggregate Root)
- `Maintenance` (Aggregate Root)

**Endpoints Clave**:
- `POST /api/v1/vehicles` - Registrar vehículo
- `GET /api/v1/vehicles/my-vehicles` - Listar mis vehículos
- `POST /api/v1/vehicles/{id}/transfer` - Transferir propiedad
- `GET /api/v1/maintenances/vehicle/{vehicleId}` - Historial de un vehículo
- `POST /api/v1/maintenances/{id}/confirm` - Confirmar mantenimiento

**Integración**: Provee información de vehículos y permite creación de mantenimientos desde Matching & Booking.

---

### 4. Matching & Booking Context

**Propósito**: Conectar solicitudes de servicio de usuarios con talleres cercanos y gestionar reservas.

**Responsabilidades**:
- Creación de solicitudes de servicio por usuarios
- Matching geolocalizado de talleres (radio configurable 1-50km)
- Gestión de ofertas de talleres
- Aceptación/rechazo de ofertas
- Agendamiento de servicios (Service Booking)
- Estados: PENDING, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED, PICKED_UP
- Expiración automática de ofertas (3 días)
- Recordatorios de citas por email
- Cancelación por ambas partes con historial
- Confirmación de recogida del vehículo

**Entities Principales**:
- `ServiceRequest` (Aggregate Root)
- `Offer` (Entity)
- `ServiceBooking` (Aggregate Root)

**Endpoints Clave**:
- `POST /api/v1/service-requests` - Crear solicitud
- `GET /api/v1/service-requests/nearby` - Solicitudes cercanas (taller)
- `POST /api/v1/offers` - Crear oferta (taller)
- `POST /api/v1/offers/{id}/accept` - Aceptar oferta (usuario)
- `POST /api/v1/service-bookings/{id}/complete` - Marcar completado (taller)
- `POST /api/v1/service-bookings/{id}/confirm-pickup` - Confirmar recogida (usuario)

**Integración**: Coordina con Workshop, Vehicle, Notifications, y crea Maintenances tras completar servicios.

---

### 5. Trust & Reputation Context

**Propósito**: Sistema bidireccional de reseñas y puntuación de confianza.

**Responsabilidades**:
- Reseñas de usuarios hacia talleres
- Reseñas de talleres hacia usuarios
- Cálculo de Trust Score ponderado
- Ventana de 14 días para crear reseña
- Solo una reseña por servicio
- Sistema básico de reportes
- Recalculación periódica de trust scores

**Entities Principales**:
- `Review` (Aggregate Root)
- `ReviewReport` (Entity)

**Value Objects**:
- `Rating` (1-5 estrellas)
- `ReviewStatus`, `ReviewType`, `ReportReason`

**Endpoints Clave**:
- `POST /api/v1/reviews` - Crear reseña
- `GET /api/v1/reviews/workshop/{id}` - Reseñas de un taller
- `GET /api/v1/trust-scores/workshop/{id}` - Trust score de taller
- `POST /api/v1/reviews/{id}/report` - Reportar reseña

**Algoritmo Trust Score**:
- 50% promedio de ratings
- 25% número de reviews (normalizado)
- 15% recency (frescura de reviews)
- 10% ratio de respuestas del taller

**Integración**: Se sincroniza con IAM y Workshop para actualizar trust scores.

---

### 6. Notifications Context

**Propósito**: Envío centralizado de notificaciones por email.

**Responsabilidades**:
- Envío de emails transaccionales
- Templates HTML personalizados
- Verificación de email
- Recuperación de contraseña
- Invitaciones a talleres
- Notificaciones de ofertas y servicios
- Recordatorios de mantenimiento
- Sin almacenamiento de historial (stateless)

**Templates de Email**:
- Email verification
- Password reset
- Workshop invitation
- Invitation expired
- Offer received/accepted
- Service reminders
- Pickup confirmation
- Maintenance reminders

**Configuración**: JavaMailSender con SMTP configurable.

**Integración**: Expuesto solo via ACL facades, nunca llamado directamente por controllers.

---

### 7. Payment Context

**Propósito**: Gestión de pagos de suscripciones de talleres (demo, sin integración real).

**Responsabilidades**:
- Creación de pagos de suscripción
- Estados: PENDING, COMPLETED, FAILED, REFUNDED, CANCELLED
- Historial de pagos
- Tracking de próximas renovaciones
- Actualización automática de suscripciones tras pago exitoso
- Simulación de procesamiento de pagos

**Pricing**:
- FREE: $0.00 USD
- BASIC: $19.99 USD/mes
- PREMIUM: $49.99 USD/mes

**Entities Principales**:
- `Payment` (Aggregate Root)

**Endpoints Clave**:
- `POST /api/v1/payments/subscriptions` - Crear pago
- `GET /api/v1/payments/my-payments` - Historial de pagos
- `POST /api/v1/payments/{id}/complete` - Completar pago (simulado)
- `GET /api/v1/billing/history` - Historial de facturación

**Integración**: Actualiza suscripciones en Workshop Context tras pagos exitosos.

---

## 🔗 Anti-Corruption Layers (ACLs)

Los Bounded Contexts se comunican entre sí a través de **ACL Facades** para mantener las fronteras claras:

| Context Origen | Context Destino | Facade | Operaciones |
|----------------|-----------------|--------|-------------|
| Matching & Booking | Workshop | WorkshopContextFacade | Validar taller, obtener info |
| Matching & Booking | Vehicle | VehicleMaintenanceFacade | Validar vehículo, crear maintenance |
| Matching & Booking | Notifications | MatchingNotificationsFacade | Enviar notificaciones de ofertas |
| Trust & Reputation | Workshop | WorkshopContextFacade | Actualizar trust score |
| Trust & Reputation | IAM | IamFacade | Actualizar trust score de usuarios |
| Trust & Reputation | Matching & Booking | ServiceBookingFacade | Validar servicio completado |
| Payment | Workshop | WorkshopContextFacade | Actualizar suscripción |
| Vehicle | Notifications | VehicleNotificationsFacade | Recordatorios de mantenimiento |
| IAM | Workshop | WorkshopContextFacade | Asociar usuario con taller |
| IAM | Notifications | IamNotificationsFacade | Email verification, password reset |
| Workshop | Notifications | WorkshopNotificationsFacade | Invitaciones |

## ✅ Funcionalidades Principales por Rol

### 🚗 Car Owners (Dueños de Vehículos)

- ✅ Registrarse e iniciar sesión
- ✅ Verificar email
- ✅ Gestionar perfil
- ✅ Registrar y consultar vehículos
- ✅ Ver historial de mantenimientos
- ✅ Transferir propiedad de vehículos
- ✅ Buscar talleres cercanos
- ✅ Crear solicitudes de servicio
- ✅ Recibir y evaluar ofertas
- ✅ Aceptar ofertas y agendar
- ✅ Confirmar recogida de vehículo
- ✅ Dejar reseñas a talleres
- ✅ Ver trust score de talleres

### 🧑‍🔧 Workshop Managers (Administradores de Talleres)

- ✅ Registrarse e iniciar sesión
- ✅ Crear y configurar taller
- ✅ Añadir ubicaciones, servicios, staff
- ✅ Gestionar capability tags
- ✅ Invitar staff members
- ✅ Ver solicitudes cercanas
- ✅ Enviar ofertas a solicitudes
- ✅ Gestionar servicios agendados
- ✅ Marcar servicios como completados
- ✅ Crear registros de mantenimiento
- ✅ Dejar reseñas a clientes
- ✅ Ver y gestionar suscripción
- ✅ Realizar pagos de suscripción

### 👷 Workshop Workers (Personal del Taller)

- ✅ Iniciar sesión (invitados por manager)
- ✅ Ver solicitudes cercanas
- ✅ Ver servicios agendados
- ✅ Marcar servicios como completados
- ✅ Crear registros de mantenimiento

## 🛠️ Stack Tecnológico

- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Database**: MySQL 8+
- **ORM**: Spring Data JPA + Hibernate
- **Security**: Spring Security + JWT
- **Password Hashing**: BCrypt
- **Email**: JavaMailSender (SMTP)
- **Media Storage**: Cloudinary
- **API Documentation**: Swagger/OpenAPI 3
- **Build Tool**: Maven
- **Architecture**: DDD + CQRS patterns
- **Multitenancy**: ThreadLocal WorkshopContext

## 📊 Patrones y Prácticas

- **Domain-Driven Design (DDD)**: Separación en Bounded Contexts
- **CQRS**: Command/Query separation en services
- **Repository Pattern**: Abstracción de persistencia
- **ACL Pattern**: Anti-Corruption Layers entre contextos
- **Value Objects**: Inmutables para conceptos de dominio
- **Aggregates**: Entidades raíz con invariantes
- **Scheduled Tasks**: `@Scheduled` para tareas periódicas
- **Auditable Entities**: `createdAt`, `updatedAt` automáticos
- **Multitenancy**: Context-based para workshops

## 🔐 Seguridad

- **JWT Tokens**: Autenticación stateless
- **Role-Based Access Control**: 3 roles principales
- **Method Security**: `@PreAuthorize` en endpoints
- **Password Encryption**: BCrypt con salt
- **Email Verification**: Tokens de verificación
- **Password Reset**: Tokens de reset con expiración
- **CORS**: Configurado para permitir apps móviles
- **Public Endpoints**: Búsqueda y catálogos sin auth

## 📅 Tareas Programadas

| Servicio | Frecuencia | Propósito |
|----------|-----------|-----------|
| MaintenanceReminderService | Diario | Enviar recordatorios de mantenimiento |
| ExpiredOfferCleanupService | Cada hora | Marcar ofertas expiradas (>3 días) |
| UpcomingServiceReminderService | Cada hora | Recordar citas próximas (24h antes) |
| ReviewWindowExpirationService | Diario | Cerrar ventanas de review expiradas (>14 días) |
| TrustScoreRecalculationService | Semanal | Recalcular trust scores |

## 🌐 Endpoints Públicos (sin autenticación)

### Autenticación
- `POST /api/v1/users/signup`
- `POST /api/v1/users/signin`

### Talleres
- `GET /api/v1/workshops/search`
- `GET /api/v1/workshops/{id}/public`
- `GET /api/v1/workshops/{id}/services`

### Sistema de Catálogos ⭐ NUEVO
- `GET /api/v1/catalog/brands` - Marcas de vehículos (33 marcas)
- `GET /api/v1/catalog/brands/{brandId}/models` - Modelos por marca (~200 modelos)
- `GET /api/v1/catalog/services` - Servicios mecánicos (~60 servicios)
- `GET /api/v1/catalog/capability-tags` - Tags de capacidades (~50 tags)

### Endpoints Legacy (Deprecados)
- `GET /api/v1/workshops/catalog/**` - Usar `/api/v1/catalog/**` en su lugar

## 📝 Notas Importantes

### Decisiones de Diseño

1. **Flyway Deshabilitado**: Las migraciones se removieron, se usa `ddl-auto=create` en dev y `update` en prod.

2. **Workshop Multitenancy**: El contexto del taller se extrae del JWT y se almacena en ThreadLocal para que los Workers puedan operar en nombre del taller.

3. **Trust Score Bidireccional**: Tanto usuarios como talleres tienen trust score, promoviendo comportamiento profesional en ambos lados.

4. **Confirmación de Mantenimientos**: Los mantenimientos creados por talleres requieren confirmación del usuario para evitar fraudes.

5. **Transferencia de Vehículos**: Se mantiene el historial completo pero el propietario anterior pierde acceso.

6. **Payment Simulado**: El Payment Context no integra con pasarelas reales, es solo para demo.

7. **Notifications Stateless**: No se guarda historial de notificaciones enviadas.

8. **Sistema de Catálogos Híbrido**: ⭐ NUEVO
   - **Marcas/Modelos en Base de Datos**: Permite administración dinámica y escalabilidad
   - **Servicios/Tags como Enums**: Mayor performance, type-safety y validación en tiempo de compilación
   - Validación estricta en todos los puntos de entrada para garantizar consistencia de datos

### Limitaciones Actuales (Por Completar)

- Workshop schedule/agenda management (pendiente)
- CRUD completo para locations/templates/staff (solo CREATE implementado)
- Push notifications para apps móviles
- Integración con pasarela de pagos real
- Tests automatizados

## 📚 Documentación Adicional

Para información detallada de cada contexto, ver:
- [`prompt/catalog/implementation.md`](catalog/implementation.md) ⭐ NUEVO - Sistema de catálogos
- [`prompt/iam/improvements.md`](iam/improvements.md)
- [`prompt/workshop/improvements.md`](workshop/improvements.md)
- [`prompt/vehicle/implementation.md`](vehicle/implementation.md)
- [`prompt/matching/implementation.md`](matching/implementation.md)
- [`prompt/trust/implementation.md`](trust/implementation.md)
- [`prompt/notifications/implementation.md`](notifications/implementation.md)
- [`prompt/payment/implementation.md`](payment/implementation.md)

Para arquitectura y relaciones entre contextos, ver:
- [`prompt/OVERVIEW.md`](OVERVIEW.md)

Para lista completa de endpoints, ver:
- [`prompt/API_ENDPOINTS.md`](API_ENDPOINTS.md)

Para guía de deployment, ver:
- [`prompt/DEPLOYMENT.md`](DEPLOYMENT.md)
