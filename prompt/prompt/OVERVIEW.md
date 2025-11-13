# AutoNexo - System Overview & Architecture

## Executive Summary

AutoNexo is a comprehensive platform connecting vehicle owners with trusted mechanical workshops, featuring:
- 7 Bounded Contexts following DDD principles
- RESTful API serving Android and Flutter mobile apps
- JWT-based authentication with role-based access control
- Bidirectional trust and reputation system
- Geolocation-based workshop matching
- Complete digital vehicle maintenance history

## System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Mobile Applications                       │
│  ┌──────────────────────┐     ┌────────────────────────┐   │
│  │   Android Native     │     │   Flutter (Cross-plat) │   │
│  └──────────────────────┘     └────────────────────────┘   │
└────────────────────────┬────────────────────────────────────┘
                         │
                    REST API (HTTPS + JWT)
                         │
┌────────────────────────┴────────────────────────────────────┐
│               Spring Boot Backend (Java 17+)                 │
│                                                              │
│  ┌───────────────────────────────────────────────────────┐ │
│  │              7 Bounded Contexts (DDD)                  │ │
│  ├───────────────────────────────────────────────────────┤ │
│  │ IAM │ Workshop │ Vehicle │ Matching │ Trust │ Notif │ Pay│
│  └───────────────────────────────────────────────────────┘ │
│                                                              │
│  ┌───────────────────────────────────────────────────────┐ │
│  │         ACL Facades (Anti-Corruption Layers)           │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                              │
│  ┌───────────────────────────────────────────────────────┐ │
│  │     Spring Security + JWT + BCrypt + WorkshopContext  │ │
│  └───────────────────────────────────────────────────────┘ │
└────────────────────────┬────────────────────────────────────┘
                         │
        ┌────────────────┼────────────────┬──────────────────┐
        │                │                │                   │
     MySQL 8+      Cloudinary         SMTP Server      Swagger UI
   (Persistence)   (Media Storage)   (Notifications)   (API Docs)
```

## Bounded Contexts & Responsibilities

### 1. IAM Context (Identity & Access Management)
**Core Responsibility**: User authentication, authorization, and profile management

**Key Components**:
- User registration and login (JWT tokens)
- Role management (CAR_OWNER, WORKSHOP_MANAGER, WORKSHOP_WORKER)
- Email verification tokens
- Password reset tokens
- User profiles and trust scores

**External Dependencies**: None (core context)
**Dependent Contexts**: All others depend on IAM for authentication

---

### 2. Workshop Context
**Core Responsibility**: Workshop lifecycle and configuration

**Key Components**:
- Workshop registration and profiles
- Multiple locations management
- Service templates definition
- Staff member management
- Capability tags
- Subscription tiers (FREE, BASIC, PREMIUM)
- Workshop trust scores
- Public search and catalog endpoints

**External Dependencies**: IAM (user association)
**Dependent Contexts**: Matching & Booking, Trust & Reputation, Payment

---

### 3. Vehicle & Maintenance Context
**Core Responsibility**: Vehicle registration and maintenance history

**Key Components**:
- Vehicle CRUD operations
- Ownership management and transfers
- Digital maintenance history
- Manual vs workshop-verified maintenances
- Maintenance confirmation workflow
- Maintenance reminders (scheduled)
- Vehicle photo management

**External Dependencies**: IAM (owner validation)
**Dependent Contexts**: Matching & Booking (service requests)

---

### 4. Matching & Booking Context
**Core Responsibility**: Connect service requests with workshops

**Key Components**:
- Service request creation
- Geolocation-based matching (1-50km radius)
- Offer management
- Service booking workflow
- Appointment scheduling
- Service completion and pickup confirmation
- Offer expiration (3 days)
- Service reminders (scheduled)

**External Dependencies**: 
- Workshop (validate and search)
- Vehicle (validate and create maintenance records)
- Notifications (email alerts)

**Dependent Contexts**: Trust & Reputation (reviews require completed bookings)

---

### 5. Trust & Reputation Context
**Core Responsibility**: Bidirectional review and trust scoring system

**Key Components**:
- User → Workshop reviews
- Workshop → User reviews
- Weighted trust score algorithm
- 14-day review window
- One review per service
- Review reporting system
- Trust score recalculation (scheduled)

**External Dependencies**:
- Matching & Booking (validate service completion)
- IAM (update user trust scores)
- Workshop (update workshop trust scores)

**Dependent Contexts**: None

---

### 6. Notifications Context
**Core Responsibility**: Centralized email notifications

**Key Components**:
- HTML email templates
- JavaMailSender integration
- Transactional emails only (stateless)
- Email types:
  - Verification & password reset
  - Workshop invitations
  - Offer notifications
  - Service reminders
  - Maintenance reminders

**External Dependencies**: SMTP Server
**Dependent Contexts**: None (called via ACL only)

---

### 7. Payment Context
**Core Responsibility**: Workshop subscription payments (demo/simulated)

**Key Components**:
- Subscription payment creation
- Payment status tracking
- Billing history
- Renewal tracking
- Automatic subscription updates
- Simulated payment processing

**External Dependencies**: Workshop (update subscriptions)
**Dependent Contexts**: None

## Anti-Corruption Layers (ACL)

### ACL Communication Matrix

| Source Context | Target Context | Facade Interface | Key Operations |
|----------------|----------------|------------------|----------------|
| IAM | Workshop | WorkshopContextFacade | processInvitationForNewUser, associateUserWithWorkshop |
| IAM | Notifications | IamNotificationsFacade | sendEmailVerification, sendPasswordReset |
| Workshop | Notifications | WorkshopNotificationsFacade | sendWorkshopInvitation |
| Workshop | IAM | (Reverse) | getUserInfo, validateUser |
| Matching & Booking | Workshop | WorkshopContextFacade | getWorkshopInfo, validateWorkshop |
| Matching & Booking | Vehicle | VehicleMaintenanceFacade | createMaintenanceRecord, validateVehicle |
| Matching & Booking | Notifications | MatchingNotificationsFacade | sendOfferNotifications, sendServiceReminders |
| Vehicle | Notifications | VehicleNotificationsFacade | sendMaintenanceReminders |
| Trust & Reputation | Matching & Booking | ServiceBookingFacade | validateServiceBookingForReview |
| Trust & Reputation | Workshop | WorkshopContextFacade | updateWorkshopTrustScore |
| Trust & Reputation | IAM | IamFacade | updateUserTrustScore |
| Payment | Workshop | WorkshopContextFacade | updateSubscription |

### ACL Design Principles

1. **Unidirectional Dependencies**: ACLs enforce one-way dependencies between contexts
2. **Translation Layer**: Each ACL translates domain concepts between contexts
3. **Loose Coupling**: Changes in one context don't break others
4. **Interface Stability**: ACL interfaces are stable contracts
5. **No Direct Repository Access**: Contexts never directly access other contexts' repositories

## Key Business Flows

### Flow 1: User Registration & Workshop Creation

```
1. User → POST /api/v1/users/signup
2. IAM creates User (WORKSHOP_MANAGER role)
3. IAM → Notifications (sendEmailVerification)
4. User verifies email
5. User → POST /api/v1/workshops
6. Workshop creates Workshop aggregate
7. Workshop → IAM (associateUserWithWorkshop)
```

### Flow 2: Service Request & Matching

```
1. User → POST /api/v1/service-requests
2. Matching creates ServiceRequest
3. Matching validates Vehicle (VehicleMaintenanceFacade)
4. Nearby workshops see request (geolocation query)
5. Workshop → POST /api/v1/offers
6. Matching creates Offer
7. Matching → Notifications (sendOfferNotification)
8. User → POST /api/v1/offers/{id}/accept
9. Matching creates ServiceBooking
10. Matching → Notifications (sendBookingConfirmation)
```

### Flow 3: Service Completion & Review

```
1. Workshop → POST /api/v1/service-bookings/{id}/complete
2. Matching updates ServiceBooking to COMPLETED
3. Matching → Vehicle (createMaintenanceRecord with PENDING_CONFIRMATION)
4. Matching → Notifications (sendServiceCompletedEmail)
5. User → POST /api/v1/service-bookings/{id}/confirm-pickup
6. Matching updates to PICKED_UP
7. User → POST /api/v1/maintenances/{id}/confirm
8. Vehicle confirms maintenance
9. User → POST /api/v1/reviews (within 14 days)
10. Trust creates Review
11. Trust → IAM/Workshop (updateTrustScore)
```

### Flow 4: Subscription Payment

```
1. Workshop → POST /api/v1/payments/subscriptions
2. Payment creates Payment (PENDING)
3. Workshop → POST /api/v1/payments/{id}/complete
4. Payment updates to COMPLETED
5. Payment → Workshop (updateSubscription)
6. Workshop updates tier, status, expiresAt
```

## Data Flow & State Management

### Authentication Flow

```
┌────────┐                ┌─────────┐              ┌──────────┐
│ Client │                │   IAM   │              │  Other   │
│  App   │                │ Context │              │ Contexts │
└───┬────┘                └────┬────┘              └────┬─────┘
    │                          │                        │
    │  POST /signin            │                        │
    ├─────────────────────────>│                        │
    │                          │                        │
    │  JWT Token               │                        │
    │<─────────────────────────┤                        │
    │                          │                        │
    │  Authenticated Request   │                        │
    │  (Authorization: Bearer) │                        │
    ├──────────────────────────┼───────────────────────>│
    │                          │                        │
    │         BearerAuthorizationRequestFilter          │
    │              validates JWT & sets SecurityContext │
    │                          │                        │
    │         WorkshopExtractionFilter (if WORKSHOP_*)  │
    │              extracts workshopId to ThreadLocal   │
    │                          │                        │
    │                          │      Response          │
    │<──────────────────────────────────────────────────┤
    │                          │                        │
```

### Workshop Multitenancy

The system uses **ThreadLocal-based multitenancy** for workshop operations:

```java
// WorkshopContext (ThreadLocal storage)
public class WorkshopContext {
    private static final ThreadLocal<WorkshopId> CURRENT_WORKSHOP_ID = new ThreadLocal<>();
    
    public static void setCurrentWorkshopId(WorkshopId workshopId) { ... }
    public static WorkshopId getCurrentWorkshopId() { ... }
    public static Long getCurrentWorkshopIdAsLong() { ... }
    public static void clear() { ... }
}

// WorkshopExtractionFilter
// Extracts workshopId from JWT for WORKSHOP_MANAGER and WORKSHOP_WORKER roles
// Sets it in WorkshopContext for the duration of the request
```

This allows workshop operations to be scoped correctly without passing workshopId explicitly everywhere.

## Technology Stack Details

### Core Framework
- **Spring Boot 3.x**: Main application framework
- **Spring Security**: Authentication & authorization
- **Spring Data JPA**: ORM and repository pattern
- **Hibernate**: JPA implementation

### Database & Persistence
- **MySQL 8+**: Primary database
- **HikariCP**: Connection pooling
- **JPA Auditing**: Automatic createdAt/updatedAt

### Security & Authentication
- **JWT (io.jsonwebtoken)**: Stateless authentication
- **BCrypt**: Password hashing
- **Method Security**: `@PreAuthorize` annotations

### External Integrations
- **Cloudinary**: Media storage for logos, photos, vehicle images
- **JavaMailSender**: SMTP email sending
- **Swagger/OpenAPI**: API documentation

### Scheduled Tasks
- **Spring `@Scheduled`**: Cron-like task execution
- **ThreadPoolTaskScheduler**: Concurrent task execution

### Development Tools
- **Maven**: Build and dependency management
- **Spring Boot DevTools**: Hot reload in development
- **Lombok**: Boilerplate reduction

## Design Patterns & Principles

### 1. Domain-Driven Design (DDD)
- **Bounded Contexts**: Clear separation of concerns
- **Aggregates**: Entity clusters with invariants
- **Value Objects**: Immutable domain concepts
- **Domain Events**: (Potential future enhancement)

### 2. CQRS (Command Query Responsibility Segregation)
- **Command Services**: Handle state changes
- **Query Services**: Handle read operations
- **Separation**: Clear distinction in service interfaces

```java
// Command Service
public interface VehicleCommandService {
    Vehicle handle(CreateVehicleCommand command);
    void handle(TransferVehicleOwnershipCommand command);
}

// Query Service
public interface VehicleQueryService {
    Optional<Vehicle> handle(GetVehicleByIdQuery query);
    List<Vehicle> handle(GetVehiclesByOwnerQuery query);
}
```

### 3. Repository Pattern
- **Abstraction**: Hide persistence details
- **JPA**: Spring Data JPA repositories
- **Custom Queries**: `@Query` for complex operations

### 4. Anti-Corruption Layer
- **Facades**: Clean interfaces between contexts
- **Translation**: Domain model translation
- **Isolation**: Protect domain integrity

### 5. Dependency Injection
- **Constructor Injection**: Preferred for required dependencies
- **Interface-based**: Program to interfaces, not implementations

### 6. REST API Design
- **Resource-Oriented**: URLs represent resources
- **HTTP Methods**: Proper use of GET, POST, PUT, DELETE
- **Status Codes**: Meaningful HTTP status codes
- **HATEOAS**: (Not fully implemented, potential enhancement)

## Security Architecture

### Authentication Flow
1. User provides credentials
2. IAM validates credentials
3. JWT token issued (expires in 7 days configurable)
4. Client includes token in `Authorization: Bearer <token>` header
5. `BearerAuthorizationRequestFilter` validates token
6. `WorkshopExtractionFilter` sets workshop context (if applicable)
7. Request proceeds with `SecurityContext` populated

### Authorization Levels
- **Public**: No authentication required (search, catalogs)
- **Authenticated**: Any logged-in user
- **Role-Based**: Specific roles (CAR_OWNER, WORKSHOP_MANAGER, WORKSHOP_WORKER)
- **Owner-Based**: User can only access their own resources
- **Workshop-Based**: Workshop context enforced via ThreadLocal

### Security Layers
1. **Network**: HTTPS (in production)
2. **Authentication**: JWT validation
3. **Authorization**: Role-based access control
4. **Method**: `@PreAuthorize` on endpoints
5. **Data**: Owner/workshop validation in services

## Deployment Architecture

### Development
- Local MySQL instance
- Cloudinary test account
- Gmail SMTP (app password)
- `application-dev.properties`
- DDL auto: `create` (recreates DB on start)

### Production
- MySQL 8+ (cloud or dedicated)
- Cloudinary production account
- Professional SMTP service
- `application-prod.properties`
- DDL auto: `update` (preserves data)
- Connection pooling optimized
- Logging configured for production
- Compression enabled

### Environment Variables (Required)
```
# Database
DB_HOST=
DB_PORT=3306
DB_NAME=
DB_USERNAME=
DB_PASSWORD=

# JWT
JWT_SECRET=
JWT_EXPIRATION_DAYS=7

# Cloudinary
CLOUDINARY_CLOUD_NAME=
CLOUDINARY_API_KEY=
CLOUDINARY_API_SECRET=

# Email
EMAIL_USERNAME=
EMAIL_PASSWORD=
EMAIL_FROM=
APP_BASE_URL=
```

## Performance Considerations

### Database Optimization
- **Indexes**: On frequently queried columns (workshopId, userId, status, location coordinates)
- **Connection Pool**: HikariCP with configurable size
- **Lazy Loading**: Relationships loaded on demand
- **Batch Operations**: Hibernate batch inserts/updates

### API Optimization
- **Pagination**: All list endpoints support pagination
- **Compression**: Response compression enabled in production
- **Caching**: (Potential enhancement for catalog data)

### Scheduled Tasks
- **Thread Pool**: Configurable pool size for concurrent execution
- **Non-Blocking**: Scheduled tasks don't block request threads

## Future Enhancements

### Short Term
- Complete workshop schedule/agenda management
- CRUD operations for workshop entities (update/delete)
- Enhanced search filters
- Push notifications for mobile apps

### Medium Term
- Real payment gateway integration (Stripe/PayPal)
- WebSocket for real-time notifications
- Advanced caching strategy
- Automated testing suite

### Long Term
- GraphQL API option
- Event Sourcing for audit trail
- Microservices migration (if scale requires)
- Machine learning for workshop recommendations

## Conclusion

AutoNexo's architecture provides a solid foundation for a scalable, maintainable vehicle-workshop platform. The use of DDD with Bounded Contexts ensures clear separation of concerns, while ACLs maintain loose coupling between contexts. The system is ready for mobile app integration and can scale to handle growing user and workshop bases.

For detailed information on specific contexts, see their respective documentation files in the `prompt/` directory.

