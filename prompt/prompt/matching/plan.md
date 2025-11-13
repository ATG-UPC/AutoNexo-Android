# Plan de Implementación: Matching & Booking Context

## 📋 Resumen Ejecutivo

El Matching & Booking Context es el núcleo de la plataforma Autonexo, conectando a dueños de vehículos con talleres mecánicos. Maneja el flujo completo desde la creación de solicitudes de servicio hasta la finalización y registro de mantenimientos.

## 🎯 Funcionalidades Principales

### Para Dueños de Vehículos (Car Owners)
- Crear solicitudes de servicio con ubicación y radio de búsqueda
- Ver ofertas recibidas de talleres cercanos
- Aceptar/rechazar ofertas
- Negociar fecha/hora de servicio (mediación)
- Confirmar recogida del vehículo después del servicio
- Cancelar solicitudes o servicios agendados
- Ver historial de solicitudes y servicios

### Para Talleres Mecánicos (Workshops)
- Recibir notificaciones de solicitudes cercanas (futuro: push notifications)
- Ver solicitudes disponibles en su área
- Enviar ofertas con precio y fecha/hora propuesta
- Negociar fecha/hora con el cliente
- Ver servicios agendados (upcoming services)
- Marcar servicio como completo
- Cancelar ofertas o servicios agendados

## 🏗️ Arquitectura del Dominio

### Aggregates

#### 1. ServiceRequest (Aggregate Root)
Representa una solicitud de servicio creada por un dueño de vehículo.

**Campos:**
- `id` (Long)
- `userId` (UserId) - Dueño que crea la solicitud
- `vehicleId` (Long) - Vehículo que necesita servicio
- `requestedServices` (List<ServiceCatalog>) - Servicios solicitados
- `description` (String) - Descripción adicional
- `userLocation` (Coordinates) - Ubicación del usuario para matching
- `searchRadiusKm` (Integer) - Radio de búsqueda (1-50 km)
- `status` (ServiceRequestStatus) - Estado actual
- `createdAt` (LocalDateTime)
- `cancelledAt` (LocalDateTime) - Si fue cancelada
- `rejectedByWorkshops` (Set<WorkshopId>) - Talleres que rechazaron
- `offers` (List<Offer>) - Ofertas recibidas (relación OneToMany)

**Estados (ServiceRequestStatus):**
- `PENDING` - Esperando ofertas
- `CANCELLED` - Cancelada por el usuario
- `COMPLETED` - Convertida en ServiceBooking y completada
- `REJECTED` - Rechazada por un taller (solo afecta visibilidad para ese taller)

**Reglas de Negocio:**
- Solo el dueño puede cancelar una solicitud PENDING
- Una solicitud REJECTED por un taller sigue visible para otros talleres
- Al aceptar una oferta, la solicitud se convierte en ServiceBooking
- Una solicitud puede tener múltiples ofertas simultáneas

#### 2. Offer (Entity dentro de ServiceRequest)
Representa una oferta enviada por un taller.

**Campos:**
- `id` (Long)
- `serviceRequestId` (Long) - Solicitud asociada
- `workshopId` (WorkshopId) - Taller que envía la oferta
- `proposedPrice` (Money) - Precio propuesto
- `proposedDate` (LocalDateTime) - Fecha/hora propuesta
- `status` (OfferStatus) - Estado actual
- `message` (String) - Mensaje opcional del taller
- `createdAt` (LocalDateTime)
- `expiresAt` (LocalDateTime) - 3 días después de creación
- `acceptedAt` (LocalDateTime) - Si fue aceptada
- `withdrawnAt` (LocalDateTime) - Si fue retirada

**Estados (OfferStatus):**
- `PENDING` - Esperando respuesta
- `ACCEPTED` - Aceptada por el usuario (convierte a ServiceBooking)
- `REJECTED` - Rechazada por el usuario
- `EXPIRED` - Expirada automáticamente (3 días)
- `WITHDRAWN` - Retirada por el taller

**Reglas de Negocio:**
- Expira automáticamente después de 3 días
- Solo el taller puede retirar una oferta PENDING
- Al aceptar una oferta, se crea un ServiceBooking y se rechazan automáticamente las demás ofertas de la misma solicitud
- Una oferta aceptada no puede ser cancelada (debe cancelarse el ServiceBooking)

#### 3. ServiceBooking (Aggregate Root)
Representa un servicio agendado después de aceptar una oferta.

**Campos:**
- `id` (Long)
- `serviceRequestId` (Long) - Solicitud original
- `offerId` (Long) - Oferta aceptada
- `userId` (UserId) - Dueño del vehículo
- `vehicleId` (Long) - Vehículo a servicio
- `workshopId` (WorkshopId) - Taller que realizará el servicio
- `scheduledDate` (LocalDateTime) - Fecha/hora acordada
- `proposedPrice` (Money) - Precio acordado
- `finalPrice` (Money) - Precio final (puede diferir si hubo cambios)
- `status` (ServiceBookingStatus) - Estado actual
- `servicesToPerform` (List<ServiceCatalog>) - Servicios a realizar
- `description` (String) - Descripción del servicio
- `createdAt` (LocalDateTime)
- `completedAt` (LocalDateTime) - Cuando el taller marca como completo
- `pickedUpAt` (LocalDateTime) - Cuando el usuario confirma recogida
- `cancelledAt` (LocalDateTime) - Si fue cancelado
- `cancelledBy` (UserId) - Quién canceló (usuario o taller)
- `cancellationReason` (String) - Razón de cancelación

**Estados (ServiceBookingStatus):**
- `PENDING_SCHEDULE` - Oferta aceptada, negociando fecha/hora
- `SCHEDULED` - Fecha/hora confirmada
- `IN_PROGRESS` - Servicio en progreso (opcional, puede saltarse)
- `COMPLETED` - Taller marcó como completo
- `PENDING_PICKUP` - Esperando confirmación de recogida del usuario
- `PICKED_UP` - Usuario confirmó recogida (final)
- `CANCELLED` - Cancelado por usuario o taller

**Reglas de Negocio:**
- Ambos (usuario y taller) pueden cancelar en cualquier momento antes de PICKED_UP
- Solo el taller puede marcar como COMPLETED
- Solo el usuario puede confirmar PICKED_UP
- Al marcar como COMPLETED, se crea automáticamente un registro de mantenimiento (PENDING_CONFIRMATION) via ACL
- Una vez PICKED_UP, no se puede cancelar

### Value Objects

#### ServiceRequestStatus (Enum)
```java
PENDING, CANCELLED, COMPLETED, REJECTED
```

#### OfferStatus (Enum)
```java
PENDING, ACCEPTED, REJECTED, EXPIRED, WITHDRAWN
```

#### ServiceBookingStatus (Enum)
```java
PENDING_SCHEDULE, SCHEDULED, IN_PROGRESS, COMPLETED, PENDING_PICKUP, PICKED_UP, CANCELLED
```

#### SearchRadius (Value Object)
- Validación: 1-50 km
- Método para calcular si un taller está dentro del radio

### Commands

#### ServiceRequest Commands
- `CreateServiceRequestCommand` - Crear nueva solicitud
- `CancelServiceRequestCommand` - Cancelar solicitud
- `RejectServiceRequestCommand` - Taller rechaza solicitud

#### Offer Commands
- `CreateOfferCommand` - Taller crea oferta
- `WithdrawOfferCommand` - Taller retira oferta
- `AcceptOfferCommand` - Usuario acepta oferta (crea ServiceBooking)
- `RejectOfferCommand` - Usuario rechaza oferta

#### ServiceBooking Commands
- `ConfirmScheduleCommand` - Confirmar fecha/hora acordada
- `ProposeScheduleChangeCommand` - Proponer cambio de fecha/hora (mediación)
- `MarkCompletedCommand` - Taller marca como completo
- `ConfirmPickupCommand` - Usuario confirma recogida
- `CancelServiceBookingCommand` - Cancelar servicio agendado

### Queries

#### ServiceRequest Queries
- `GetServiceRequestByIdQuery` - Obtener solicitud por ID
- `GetUserServiceRequestsQuery` - Obtener solicitudes de un usuario
- `GetWorkshopAvailableRequestsQuery` - Obtener solicitudes disponibles para un taller (matching)
- `GetWorkshopReceivedRequestsQuery` - Obtener solicitudes recibidas por un taller

#### Offer Queries
- `GetOffersByServiceRequestQuery` - Obtener ofertas de una solicitud
- `GetWorkshopOffersQuery` - Obtener ofertas enviadas por un taller
- `GetUserOffersQuery` - Obtener ofertas recibidas por un usuario

#### ServiceBooking Queries
- `GetServiceBookingByIdQuery` - Obtener servicio agendado por ID
- `GetUserServiceBookingsQuery` - Obtener servicios agendados de un usuario
- `GetWorkshopServiceBookingsQuery` - Obtener servicios agendados de un taller
- `GetUpcomingServiceBookingsQuery` - Obtener servicios próximos (para calendario)

## 🔄 Flujo de Proceso

### Flujo Principal: Crear Solicitud → Recibir Ofertas → Aceptar → Agendar → Completar

1. **Usuario crea ServiceRequest**
   - Especifica vehículo, servicios, ubicación, radio de búsqueda
   - Estado: PENDING

2. **Sistema ejecuta Matching**
   - Busca talleres dentro del radio
   - Filtra por servicios ofrecidos, disponibilidad, rating
   - Retorna lista de talleres candidatos
   - **Nota:** Por ahora no envía notificaciones push, pero prepara la estructura

3. **Talleres ven solicitudes disponibles**
   - Query: `GetWorkshopAvailableRequestsQuery`
   - Filtrado por distancia, servicios, rating

4. **Taller envía Offer**
   - Propone precio y fecha/hora
   - Estado: PENDING
   - Expira en 3 días

5. **Usuario recibe ofertas**
   - Query: `GetOffersByServiceRequestQuery`
   - Puede aceptar o rechazar

6. **Usuario acepta Offer**
   - Se crea ServiceBooking
   - Estado inicial: PENDING_SCHEDULE
   - Otras ofertas de la misma solicitud se rechazan automáticamente
   - **Notificación:** Email al taller (oferta aceptada)

7. **Negociación de fecha/hora (opcional)**
   - Usuario o taller pueden proponer cambios
   - Comando: `ProposeScheduleChangeCommand`
   - Una vez acordado: `ConfirmScheduleCommand`
   - Estado: SCHEDULED

8. **Taller marca como COMPLETED**
   - Comando: `MarkCompletedCommand`
   - Estado: COMPLETED → PENDING_PICKUP
   - Se crea registro de mantenimiento (PENDING_CONFIRMATION) via ACL
   - **Notificación:** Email al usuario (servicio completado)

9. **Usuario confirma recogida**
   - Comando: `ConfirmPickupCommand`
   - Estado: PICKED_UP (final)
   - **Notificación:** Email al taller (confirmación de recogida)

10. **Recordatorio de cita próxima**
    - Scheduled task diario
    - Envía email 24 horas antes de la fecha agendada
    - Solo para servicios SCHEDULED

## 🧮 Servicio de Matching

### MatchingService (Domain Service)

**Método principal:**
```java
List<WorkshopMatchResult> findMatchingWorkshops(
    Coordinates userLocation,
    Integer searchRadiusKm,
    List<ServiceCatalog> requestedServices,
    Optional<Double> minRating
)
```

**Algoritmo de Matching:**
1. Obtener todos los talleres activos con ubicaciones
2. Filtrar por distancia (dentro del radio)
3. Filtrar por servicios ofrecidos (ServiceTemplate con ServiceCatalog matching)
4. Filtrar por rating mínimo (si se especifica)
5. Calcular score de matching:
   - Distancia (más cercano = mayor score)
   - Rating del taller (mayor rating = mayor score)
   - Servicios matching (más servicios = mayor score)
6. Ordenar por score descendente
7. Retornar top N resultados (ej: 20)

**WorkshopMatchResult:**
- `workshopId` (WorkshopId)
- `workshopName` (String)
- `distanceKm` (Double)
- `rating` (Double)
- `matchingServices` (List<ServiceCatalog>)
- `matchScore` (Double)

## 🔌 ACLs (Anti-Corruption Layers)

### 1. WorkshopFacade (para obtener información de talleres)
```java
public interface WorkshopFacade {
    WorkshopInfo getWorkshopInfo(WorkshopId workshopId);
    List<LocationInfo> getWorkshopLocations(WorkshopId workshopId);
    List<ServiceCatalog> getWorkshopServices(WorkshopId workshopId);
    Double getWorkshopRating(WorkshopId workshopId);
}

record WorkshopInfo(
    WorkshopId id,
    String name,
    Coordinates primaryLocation,
    Double rating,
    boolean active
) {}

record LocationInfo(
    Long locationId,
    Coordinates coordinates,
    boolean active
) {}
```

### 2. VehicleFacade (para validar vehículos)
```java
public interface VehicleFacade {
    VehicleInfo getVehicleInfo(Long vehicleId, UserId userId);
    boolean userOwnsVehicle(Long vehicleId, UserId userId);
}

record VehicleInfo(
    Long id,
    String brand,
    String model,
    Integer year,
    UserId ownerId
) {}
```

### 3. VehicleMaintenanceFacade (ya existe)
- Usar para crear mantenimiento cuando se completa servicio
- Método: `createMaintenanceFromCompletedService`

### 4. NotificationFacade (para enviar notificaciones)
```java
public interface NotificationFacade {
    void notifyOfferReceived(Long serviceRequestId, Long offerId, String userEmail);
    void notifyOfferAccepted(Long offerId, String workshopEmail);
    void notifyOfferRejected(Long offerId, String workshopEmail);
    void notifyServiceCompleted(Long serviceBookingId, String userEmail);
    void notifyPickupConfirmed(Long serviceBookingId, String workshopEmail);
    void notifyUpcomingService(Long serviceBookingId, String userEmail, String workshopEmail);
}
```

## 📅 Scheduled Tasks

### UpcomingServiceReminderService
- Ejecuta diariamente a las 8:00 AM
- Busca servicios con `scheduledDate` en las próximas 24 horas
- Estado: SCHEDULED
- Envía email de recordatorio a usuario y taller

### ExpiredOfferCleanupService
- Ejecuta diariamente a las 2:00 AM
- Busca ofertas con `expiresAt < now()` y estado PENDING
- Cambia estado a EXPIRED
- **Notificación:** Email al taller (oferta expirada)

## 🗄️ Estructura de Base de Datos

### Tabla: service_requests
- `id` BIGINT PRIMARY KEY
- `user_id` BIGINT NOT NULL
- `vehicle_id` BIGINT NOT NULL
- `requested_services` JSON (List<ServiceCatalog>)
- `description` TEXT
- `user_latitude` DOUBLE NOT NULL
- `user_longitude` DOUBLE NOT NULL
- `search_radius_km` INTEGER NOT NULL (CHECK: 1-50)
- `status` VARCHAR(20) NOT NULL
- `created_at` TIMESTAMP NOT NULL
- `cancelled_at` TIMESTAMP
- `rejected_by_workshops` JSON (Set<WorkshopId>)

### Tabla: offers
- `id` BIGINT PRIMARY KEY
- `service_request_id` BIGINT NOT NULL (FK)
- `workshop_id` BIGINT NOT NULL
- `proposed_price_amount` DECIMAL(10,2)
- `proposed_price_currency` VARCHAR(3)
- `proposed_date` TIMESTAMP
- `status` VARCHAR(20) NOT NULL
- `message` TEXT
- `created_at` TIMESTAMP NOT NULL
- `expires_at` TIMESTAMP NOT NULL
- `accepted_at` TIMESTAMP
- `withdrawn_at` TIMESTAMP

### Tabla: service_bookings
- `id` BIGINT PRIMARY KEY
- `service_request_id` BIGINT NOT NULL (FK)
- `offer_id` BIGINT NOT NULL (FK)
- `user_id` BIGINT NOT NULL
- `vehicle_id` BIGINT NOT NULL
- `workshop_id` BIGINT NOT NULL
- `scheduled_date` TIMESTAMP
- `proposed_price_amount` DECIMAL(10,2)
- `proposed_price_currency` VARCHAR(3)
- `final_price_amount` DECIMAL(10,2)
- `final_price_currency` VARCHAR(3)
- `status` VARCHAR(20) NOT NULL
- `services_to_perform` JSON (List<ServiceCatalog>)
- `description` TEXT
- `created_at` TIMESTAMP NOT NULL
- `completed_at` TIMESTAMP
- `picked_up_at` TIMESTAMP
- `cancelled_at` TIMESTAMP
- `cancelled_by_user_id` BIGINT
- `cancellation_reason` TEXT

## 🔐 Seguridad y Autorización

### Endpoints de Usuario (CAR_OWNER)
- Crear solicitud: Solo para sus propios vehículos
- Ver sus solicitudes: Solo propias
- Ver ofertas: Solo de sus solicitudes
- Aceptar/rechazar ofertas: Solo de sus solicitudes
- Confirmar recogida: Solo de sus servicios

### Endpoints de Taller (WORKSHOP_MANAGER, WORKSHOP_EMPLOYEE)
- Ver solicitudes disponibles: Solo talleres activos
- Crear oferta: Solo para su taller
- Ver sus ofertas: Solo de su taller
- Ver servicios agendados: Solo de su taller
- Marcar como completo: Solo de su taller
- Cancelar servicio: Solo de su taller

## 📝 Endpoints REST

### ServiceRequestController

#### POST /api/service-requests
Crear nueva solicitud
- Body: `CreateServiceRequestResource`
- Response: `ServiceRequestResource`

#### GET /api/service-requests
Obtener solicitudes del usuario actual
- Query params: `status` (opcional)
- Response: `List<ServiceRequestResource>`

#### GET /api/service-requests/{id}
Obtener solicitud por ID
- Response: `ServiceRequestResource`

#### DELETE /api/service-requests/{id}
Cancelar solicitud
- Response: 204 No Content

#### POST /api/service-requests/{id}/reject
Taller rechaza solicitud
- Response: 204 No Content

### OfferController

#### POST /api/offers
Taller crea oferta
- Body: `CreateOfferResource`
- Response: `OfferResource`

#### GET /api/service-requests/{requestId}/offers
Obtener ofertas de una solicitud
- Response: `List<OfferResource>`

#### GET /api/offers/my-workshop
Obtener ofertas enviadas por el taller actual
- Query params: `status` (opcional)
- Response: `List<OfferResource>`

#### GET /api/offers/my-requests
Obtener ofertas recibidas por el usuario actual
- Query params: `status` (opcional)
- Response: `List<OfferResource>`

#### POST /api/offers/{id}/accept
Usuario acepta oferta
- Response: `ServiceBookingResource`

#### POST /api/offers/{id}/reject
Usuario rechaza oferta
- Response: 204 No Content

#### DELETE /api/offers/{id}
Taller retira oferta
- Response: 204 No Content

### ServiceBookingController

#### GET /api/service-bookings
Obtener servicios agendados del usuario/taller actual
- Query params: `status`, `upcoming` (boolean)
- Response: `List<ServiceBookingResource>`

#### GET /api/service-bookings/{id}
Obtener servicio agendado por ID
- Response: `ServiceBookingResource`

#### POST /api/service-bookings/{id}/confirm-schedule
Confirmar fecha/hora acordada
- Body: `ConfirmScheduleResource`
- Response: `ServiceBookingResource`

#### POST /api/service-bookings/{id}/propose-change
Proponer cambio de fecha/hora
- Body: `ProposeScheduleChangeResource`
- Response: `ServiceBookingResource`

#### POST /api/service-bookings/{id}/complete
Taller marca como completo
- Body: `MarkCompletedResource`
- Response: `ServiceBookingResource`

#### POST /api/service-bookings/{id}/confirm-pickup
Usuario confirma recogida
- Response: `ServiceBookingResource`

#### DELETE /api/service-bookings/{id}
Cancelar servicio agendado
- Body: `CancelServiceBookingResource`
- Response: 204 No Content

### MatchingController

#### GET /api/matching/workshops
Buscar talleres para una solicitud (matching)
- Query params: `latitude`, `longitude`, `radiusKm`, `services` (comma-separated), `minRating`
- Response: `List<WorkshopMatchResultResource>`

## 🔔 Notificaciones

### Eventos que requieren notificación:

1. **Nueva oferta recibida** (b - Email al usuario)
   - Cuando un taller envía una oferta
   - Template: `new-offer.html`

2. **Oferta aceptada** (c - Email al taller)
   - Cuando usuario acepta una oferta
   - Template: `offer-accepted.html`

3. **Oferta rechazada** (c - Email al taller)
   - Cuando usuario rechaza una oferta
   - Template: `offer-rejected.html`

4. **Recordatorio de cita próxima** (d - Email a ambos)
   - 24 horas antes de la fecha agendada
   - Template: `upcoming-service.html`

5. **Servicio completado** (e - Email al usuario)
   - Cuando taller marca como completo
   - Template: `service-completed.html`

6. **Recogida confirmada** (e - Email al taller)
   - Cuando usuario confirma recogida
   - Template: `pickup-confirmed.html`

## 📦 Dependencias entre BCs

### Matching & Booking → Workshop
- Obtener información de talleres (WorkshopFacade)
- Obtener ubicaciones y coordenadas
- Obtener servicios ofrecidos
- Obtener rating del taller

### Matching & Booking → Vehicle & Maintenance
- Validar vehículo del usuario (VehicleFacade)
- Crear registro de mantenimiento al completar servicio (VehicleMaintenanceFacade)

### Matching & Booking → Notifications
- Enviar notificaciones de eventos (NotificationFacade)

### Matching & Booking → IAM
- Validar usuario autenticado (via SecurityContext)
- Obtener información básica del usuario si es necesario

## ✅ Checklist de Implementación

### Fase 1: Estructura Base
- [ ] Crear estructura de paquetes del BC
- [ ] Crear value objects (estados, SearchRadius)
- [ ] Crear entidades (Offer)
- [ ] Crear aggregates (ServiceRequest, ServiceBooking)
- [ ] Crear excepciones de dominio
- [ ] Crear commands y queries

### Fase 2: Servicios de Dominio
- [ ] Implementar ServiceRequestCommandService
- [ ] Implementar ServiceRequestQueryService
- [ ] Implementar OfferCommandService
- [ ] Implementar OfferQueryService
- [ ] Implementar ServiceBookingCommandService
- [ ] Implementar ServiceBookingQueryService
- [ ] Implementar MatchingService

### Fase 3: Infraestructura
- [ ] Crear JPA repositories
- [ ] Crear ACL facades (WorkshopFacade, VehicleFacade, NotificationFacade)
- [ ] Implementar ACL facades usando repositorios de otros BCs
- [ ] Configurar entidades JPA

### Fase 4: Controllers REST
- [ ] ServiceRequestController
- [ ] OfferController
- [ ] ServiceBookingController
- [ ] MatchingController
- [ ] Crear resources y assemblers

### Fase 5: Scheduled Tasks
- [ ] UpcomingServiceReminderService
- [ ] ExpiredOfferCleanupService
- [ ] Configurar @EnableScheduling si no está

### Fase 6: Notificaciones
- [ ] Crear templates de email
- [ ] Integrar con NotificationFacade
- [ ] Enviar notificaciones en eventos correspondientes

### Fase 7: Testing y Documentación
- [ ] Crear documentación en `prompt/matching/implementation.md`
- [ ] Verificar integración con otros BCs
- [ ] Probar flujos completos

## 🎯 Consideraciones Especiales

1. **Performance**: El matching puede ser costoso si hay muchos talleres. Por ahora, buscar en todos los registros está bien para el curso, pero considerar índices en coordenadas.

2. **Concurrencia**: Múltiples talleres pueden enviar ofertas simultáneamente. Usar transacciones adecuadas.

3. **Cancelaciones**: Las cancelaciones afectan Trust & Reputation (futuro BC). Por ahora, solo registrar la cancelación.

4. **Mediación de fecha/hora**: Implementar un sistema simple de propuestas. El último que confirma establece la fecha final.

5. **Expiración de ofertas**: Usar scheduled task para cambiar estado automáticamente. No depender solo de queries.

6. **Historial**: Mantener historial completo de solicitudes, ofertas y servicios para reportes futuros.

