# Vehicle & Maintenance Context Implementation

## Overview
The Vehicle & Maintenance bounded context manages vehicle lifecycle, shared ownership, and digitalized maintenance history. It provides a complete solution for vehicle registration, maintenance tracking, and automated reminders.

## Architecture

### Domain Layer
- **Vehicle**: Aggregate root representing a vehicle with ownership and images
- **Maintenance**: Aggregate root representing maintenance records with services performed
- **VehicleOwnership**: Entity tracking ownership relationships (PRIMARY, AUTHORIZED)
- **ServicePerformed**: Entity representing individual services within a maintenance record

### Value Objects
- **OwnershipType**: PRIMARY, AUTHORIZED
- **MaintenanceStatus**: PENDING_CONFIRMATION, CONFIRMED, REJECTED, MANUAL
- **LicensePlate**: Validated license plate format
- **VIN**: Optional 17-character Vehicle Identification Number
- **Mileage**: Non-negative integer with validation

### Application Layer
- **VehicleCommandServiceImpl**: Handles vehicle write operations
- **VehicleQueryServiceImpl**: Handles vehicle read operations
- **MaintenanceCommandServiceImpl**: Handles maintenance write operations
- **MaintenanceQueryServiceImpl**: Handles maintenance read operations
- **MaintenanceReminderService**: Scheduled service for sending maintenance reminders

### Infrastructure Layer
- **JPA Repositories**: VehicleRepository, MaintenanceRepository, VehicleOwnershipRepository, ServicePerformedRepository
- **Cloudinary Integration**: Reuses existing CloudinaryService for image uploads

### Interfaces (ACL)
- **VehicleMaintenanceFacade**: Interface for Matching & Booking context to create maintenance records
- **VehicleMaintenanceFacadeImpl**: Implementation that creates PENDING_CONFIRMATION maintenance records

## Key Features

### 1. Vehicle Registration
- Register vehicles with brand, model, year, license plate, VIN (optional), color, initial mileage
- Upload vehicle images (up to 10 images per vehicle)
- Automatic primary ownership assignment

### 2. Shared Ownership
- **Primary Owner**: Full control - can modify vehicle, add/remove authorized users, transfer ownership
- **Authorized Users**: Can view history and request services, but cannot modify vehicle info
- Add authorized users by email (must be existing users)
- Transfer ownership to another user (maintains full maintenance history)

### 3. Maintenance History
- **Manual Entries**: Users can add historical maintenance records (immediately CONFIRMED)
- **Workshop-Created**: Workshops create maintenance records via ACL (PENDING_CONFIRMATION)
- **Confirmation Flow**: Users must confirm/reject workshop-created maintenance
- **Services Tracking**: Each maintenance can include multiple services with individual costs
- **Images Support**: Upload before/after photos for maintenance records

### 4. Maintenance Reminders
- **Mileage-Based**: Reminds when approaching 5,000 km intervals (500 km before due)
- **Time-Based**: Reminds 14 days before 6-month maintenance due date
- **Email Notifications**: Sent via Notifications BC
- **Scheduled Task**: Runs daily at 8 AM

## API Endpoints

### Vehicle Management
- `POST /api/v1/vehicles` - Register new vehicle
- `GET /api/v1/vehicles` - List current user's vehicles
- `GET /api/v1/vehicles/{id}` - Get vehicle details
- `PUT /api/v1/vehicles/{id}/mileage` - Update current mileage
- `POST /api/v1/vehicles/{id}/images` - Upload vehicle image
- `POST /api/v1/vehicles/{id}/authorized-users` - Add authorized user (primary owner only)
- `DELETE /api/v1/vehicles/{id}/authorized-users/{userId}` - Remove authorized user (primary owner only)
- `PUT /api/v1/vehicles/{id}/transfer` - Transfer ownership
- `DELETE /api/v1/vehicles/{id}` - Deactivate vehicle

### Maintenance Management
- `POST /api/v1/vehicles/{vehicleId}/maintenances` - Create manual maintenance record
- `GET /api/v1/vehicles/{vehicleId}/maintenances` - Get maintenance history
- `GET /api/v1/maintenances/{id}` - Get maintenance details
- `PUT /api/v1/maintenances/{id}/confirm` - Confirm workshop-created maintenance
- `PUT /api/v1/maintenances/{id}/reject` - Reject workshop-created maintenance
- `GET /api/v1/maintenances/pending` - Get pending maintenance confirmations

## Business Rules

### Ownership
1. Every vehicle must have exactly one PRIMARY owner
2. A vehicle can have multiple AUTHORIZED users
3. Only PRIMARY owner can add/remove authorized users
4. Only PRIMARY owner can transfer ownership
5. When ownership is transferred, previous owner loses all access
6. Authorized users can view history and request services, but cannot modify vehicle info

### Maintenance Records
1. Workshop-created maintenance starts as PENDING_CONFIRMATION
2. Only vehicle owners (primary or authorized) can confirm/reject
3. Manual maintenance is immediately CONFIRMED (MANUAL status)
4. Once confirmed, maintenance cannot be modified (immutable for audit trail)
5. Images are optional but recommended
6. Mileage must be >= vehicle's current mileage at time of maintenance

### Reminders
1. Suggest maintenance every 5,000 km for regular services
2. Suggest maintenance every 6 months for time-based services
3. Send reminder emails 500 km before or 14 days before due date
4. Uses Notifications BC for email delivery

## Integration Points

### With IAM Context
- Get current user ID from JWT token
- Verify user roles (CAR_OWNER)
- Fetch user email for maintenance reminders
- UserRepository for email lookup

### With Workshop Context
- Get workshop information for maintenance records
- Link maintenance to specific workshop via WorkshopId

### With Matching & Booking Context (Future)
- **ACL**: `VehicleMaintenanceFacade.createMaintenanceFromCompletedService()`
- Creates PENDING_CONFIRMATION maintenance when service is completed
- Links maintenance to completed booking

### With Notifications Context
- Send maintenance reminder emails
- Email template: `maintenance-reminder.html`
- Notify user when workshop creates maintenance record (pending confirmation)

## Database Schema

### Vehicle Table
- `id` (PK)
- `brand`, `model`, `year`
- `license_plate` (embedded)
- `vin` (embedded, nullable)
- `color`
- `current_mileage` (embedded)
- `primary_owner_id` (embedded UserId)
- `image_urls` (collection)
- `active` (boolean)
- `created_at`, `updated_at` (auditing)

### VehicleOwnership Table
- `id` (PK)
- `user_id` (embedded UserId)
- `ownership_type` (PRIMARY, AUTHORIZED)
- `vehicle_id` (FK)
- `added_at`
- `created_at`, `updated_at` (auditing)

### Maintenance Table
- `id` (PK)
- `vehicle_id` (FK)
- `maintenance_date`
- `mileage` (embedded)
- `workshop_id` (embedded WorkshopId, nullable)
- `created_by_workshop` (boolean)
- `status` (PENDING_CONFIRMATION, CONFIRMED, REJECTED, MANUAL)
- `observations`
- `image_urls` (collection)
- `created_at`, `updated_at` (auditing)

### ServicePerformed Table
- `id` (PK)
- `service_type` (ServiceCatalog enum)
- `description`
- `cost` (BigDecimal)
- `maintenance_id` (FK)
- `created_at`, `updated_at` (auditing)

## Files Structure

```
vehicle/
├── domain/
│   ├── model/
│   │   ├── aggregates/
│   │   │   ├── Vehicle.java
│   │   │   └── Maintenance.java
│   │   ├── entities/
│   │   │   ├── VehicleOwnership.java
│   │   │   └── ServicePerformed.java
│   │   ├── valueobjects/
│   │   │   ├── OwnershipType.java
│   │   │   ├── MaintenanceStatus.java
│   │   │   ├── LicensePlate.java
│   │   │   ├── VIN.java
│   │   │   └── Mileage.java
│   │   ├── commands/
│   │   │   ├── CreateVehicleCommand.java
│   │   │   ├── UpdateVehicleMileageCommand.java
│   │   │   ├── AddAuthorizedUserCommand.java
│   │   │   ├── RemoveAuthorizedUserCommand.java
│   │   │   ├── TransferOwnershipCommand.java
│   │   │   ├── CreateMaintenanceCommand.java
│   │   │   ├── ConfirmMaintenanceCommand.java
│   │   │   └── RejectMaintenanceCommand.java
│   │   └── queries/
│   │       ├── GetUserVehiclesQuery.java
│   │       ├── GetVehicleByIdQuery.java
│   │       ├── GetVehicleMaintenanceHistoryQuery.java
│   │       ├── GetMaintenanceByIdQuery.java
│   │       └── GetPendingMaintenancesQuery.java
│   ├── services/
│   │   ├── VehicleCommandService.java
│   │   ├── VehicleQueryService.java
│   │   ├── MaintenanceCommandService.java
│   │   └── MaintenanceQueryService.java
│   └── exceptions/
│       ├── VehicleNotFoundException.java
│       ├── MaintenanceNotFoundException.java
│       ├── UnauthorizedVehicleAccessException.java
│       └── OnlyPrimaryOwnerException.java
├── application/
│   ├── internal/
│   │   ├── commandservices/
│   │   │   ├── VehicleCommandServiceImpl.java
│   │   │   └── MaintenanceCommandServiceImpl.java
│   │   ├── queryservices/
│   │   │   ├── VehicleQueryServiceImpl.java
│   │   │   └── MaintenanceQueryServiceImpl.java
│   │   └── services/
│   │       └── MaintenanceReminderService.java
│   └── acl/
│       └── VehicleMaintenanceFacadeImpl.java
├── infrastructure/
│   └── persistence/
│       └── jpa/
│           └── repositories/
│               ├── VehicleRepository.java
│               ├── VehicleOwnershipRepository.java
│               ├── MaintenanceRepository.java
│               └── ServicePerformedRepository.java
└── interfaces/
    ├── rest/
    │   ├── VehicleController.java
    │   ├── MaintenanceController.java
    │   ├── resources/
    │   │   ├── CreateVehicleResource.java
    │   │   ├── VehicleResource.java
    │   │   ├── UpdateMileageResource.java
    │   │   ├── TransferOwnershipResource.java
    │   │   ├── AddAuthorizedUserResource.java
    │   │   ├── CreateMaintenanceResource.java
    │   │   └── MaintenanceResource.java
    │   └── transform/
    │       ├── VehicleCommandFromResourceAssembler.java
    │       └── VehicleResourceFromEntityAssembler.java
    └── acl/
        └── VehicleMaintenanceFacade.java
```

## Security & Authorization

- All endpoints require authentication (JWT token)
- User ID extracted from SecurityContext
- Authorization checks:
  - Vehicle operations: User must be owner or authorized
  - Ownership management: Only primary owner
  - Maintenance confirmation: Vehicle owner (primary or authorized)

## Future Enhancements

1. Push notifications for mobile apps
2. Maintenance cost analytics and trends
3. Vehicle value estimation based on maintenance history
4. Predictive maintenance suggestions using ML
5. Integration with vehicle manufacturer APIs for recalls/updates
6. Export maintenance history to PDF
7. Maintenance calendar view
8. Service type-specific reminder intervals

