# Android Workshop App Scope Document

This document defines the scope, boundaries, and technical requirements for the **Android native mobile application** designed exclusively for workshop users (mechanics) in the AutoNexo platform.

## 1. Target Roles and Users

### IN SCOPE: Workshop Users Only

This Android application is **EXCLUSIVELY** designed for users with the following roles:

- **`WORKSHOP_MANAGER`**: Workshop administrators who can:
  - Create and manage workshop profiles
  - Configure workshop settings (locations, services, staff, subscriptions)
  - View nearby service requests
  - Create offers for service requests
  - Manage service bookings
  - Handle subscription payments
  - Invite staff members

- **`WORKSHOP_WORKER`**: Workshop staff members who can:
  - View nearby service requests
  - View service bookings for their workshop
  - Create offers for service requests
  - Mark services as completed
  - Create maintenance records
  - View workshop information (read-only for most settings)

### OUT OF SCOPE: Explicitly Excluded Roles

The following roles and their associated flows are **NOT** part of this application:

- **`CAR_OWNER`**: All vehicle owner functionality is OUT OF SCOPE:
  - ❌ Vehicle registration and management
  - ❌ Service request creation
  - ❌ Offer acceptance/rejection
  - ❌ Vehicle pickup confirmation
  - ❌ Maintenance history viewing (as owner)
  - ❌ Manual maintenance record creation

- **`ADMIN`**: All administrative functionality is OUT OF SCOPE:
  - ❌ Catalog administration (brands, models)
  - ❌ Review report management
  - ❌ System-wide administration features

**Important**: The app should **assume** that all authenticated users are either `WORKSHOP_MANAGER` or `WORKSHOP_WORKER`. No UI flows, screens, or API calls should be implemented for `CAR_OWNER` or `ADMIN` roles.

---

## 2. Bounded Contexts Used by This App

The Android workshop app interacts with the following bounded contexts from the AutoNexo backend. Each context is described from the **workshop user's perspective**:

### 2.1. IAM Context (Identity & Access Management)

**Purpose**: User authentication, authorization, and profile management for workshop users.

**Workshop Perspective**:
- Workshop managers register and create accounts
- Workshop workers are invited and accept invitations to join workshops
- Both roles authenticate using JWT tokens
- Users can manage their personal profiles (name, email, phone)
- Password management (change password, reset password)
- Email verification for new accounts

**Key Features for Workshop App**:
- User registration (for new workshop managers)
- Login/logout functionality
- Profile viewing and editing
- Password change
- Email verification flow

---

### 2.2. Workshop Context

**Purpose**: Core workshop lifecycle and configuration management.

**Workshop Perspective**:
- **Workshop Profile**: Create, view, and update workshop information (name, description, contact info)
- **Locations**: Add and manage multiple workshop locations with geolocation data
- **Service Templates**: Define services offered by the workshop (e.g., "Oil Change", "Brake Repair") with pricing
- **Staff Management**: Add staff members and manage their access
- **Capability Tags**: Configure workshop specializations (e.g., "BMW Specialist", "Electric Vehicles")
- **Media Management**: Upload workshop logo and photos
- **Subscriptions**: View and manage subscription tier (FREE, BASIC, PREMIUM)
- **Invitations**: Create and manage invitations for new staff members

**Key Features for Workshop App**:
- Complete workshop profile management
- Multi-location support
- Service catalog configuration
- Staff member management
- Subscription status and upgrade flows
- Invitation system for onboarding workers

---

### 2.3. Matching & Booking Context

**Purpose**: Connect service requests from vehicle owners with workshops and manage service bookings.

**Workshop Perspective**:
- **View Nearby Requests**: See service requests from vehicle owners within a configurable radius (1-50km)
- **Create Offers**: Submit offers for service requests with pricing and estimated completion time
- **Manage Offers**: View, update, and cancel offers before they're accepted
- **Service Bookings**: View all bookings for the workshop (pending, in-progress, completed)
- **Booking Management**: 
  - Confirm scheduled appointments
  - Start services (mark as IN_PROGRESS)
  - Complete services (mark as COMPLETED)
  - Cancel bookings (with proper workflow)
- **Service Completion**: When marking a service as completed, the system automatically creates a maintenance record for the vehicle

**Key Features for Workshop App**:
- Map/list view of nearby service requests
- Offer creation and management interface
- Booking calendar/list view
- Service workflow management (start, complete, cancel)
- Integration with maintenance record creation

---

### 2.4. Trust & Reputation Context

**Purpose**: Bidirectional review and trust scoring system.

**Workshop Perspective**:
- **View Reviews**: See reviews that customers have left for the workshop
- **View Trust Score**: See the workshop's current trust score (calculated from reviews)
- **Create Reviews**: Leave reviews for customers after service completion (within 14-day window)
- **Review Management**: View review details and respond to customer reviews (if supported)

**Key Features for Workshop App**:
- Review dashboard showing customer feedback
- Trust score display
- Review creation flow for completed services
- Review history and details

---

### 2.5. Payment Context

**Purpose**: Workshop subscription payment management.

**Workshop Perspective**:
- **Subscription Payments**: Create and process payments for subscription upgrades (FREE → BASIC → PREMIUM)
- **Payment History**: View all past subscription payments
- **Billing Information**: View upcoming renewals and billing history
- **Payment Status**: Track payment status (PENDING, COMPLETED, FAILED, REFUNDED)

**Key Features for Workshop App**:
- Subscription upgrade/payment flow
- Payment history view
- Billing information dashboard
- Payment status tracking

**Note**: The payment system is currently simulated/demo mode. No real payment gateway integration exists.

---

### 2.6. Catalog System

**Purpose**: Reference data for services and capability tags.

**Workshop Perspective**:
- **Services Catalog**: Browse available service types to configure workshop offerings
- **Capability Tags**: Browse available specialization tags to add to workshop profile
- **Read-Only Access**: The catalog is reference data only (managed by ADMIN, not workshops)

**Key Features for Workshop App**:
- Service catalog browser (for selecting services when creating service templates)
- Capability tags browser (for selecting tags when configuring workshop)
- Category filtering and search

---

### 2.7. Bounded Contexts NOT Used

The following bounded contexts are **OUT OF SCOPE** for this application:

- **Vehicle & Maintenance Context**: Entirely focused on vehicle owners. Workshops create maintenance records through the Matching & Booking context when completing services, but do not directly manage vehicles or view maintenance history.

- **Notifications Context**: Backend-only context with no direct API endpoints. Notifications are sent via email by the backend automatically.

---

## 3. Allowed API Endpoints (Workshop/Manager App)

This section lists all API endpoints that the Android workshop app **SHOULD** use, organized by bounded context. Endpoints marked with `[AUTH]` require JWT authentication. Endpoints marked with `[WORKSHOP_MANAGER]` or `[WORKSHOP_WORKER]` have role-specific restrictions.

### 3.1. IAM Context Endpoints

#### IN SCOPE: Authentication & Profile Management

- `POST /api/v1/users/signup` - Register new workshop manager (public)
- `POST /api/v1/users/signin` - User login (returns JWT) (public)
- `GET /api/v1/users/available-roles` - Get available roles (public, for registration)
- `GET /api/v1/users/me` - Get current user profile [AUTH]
- `PUT /api/v1/users/me` - Update user profile [AUTH]
- `POST /api/v1/users/change-password` - Change password [AUTH]
- `DELETE /api/v1/users/me` - Deactivate account [AUTH]

#### IN SCOPE: Email Verification

- `POST /api/v1/users/request-email-verification` - Request verification email [AUTH]
- `POST /api/v1/users/verify-email` - Verify email with token [AUTH]

#### IN SCOPE: Password Recovery

- `POST /api/v1/users/request-password-reset` - Request password reset (public)
- `POST /api/v1/users/reset-password` - Reset password with token (public)

---

### 3.2. Workshop Context Endpoints

#### IN SCOPE: Workshop Management

- `POST /api/v1/workshops` - Create workshop [WORKSHOP_MANAGER]
- `GET /api/v1/workshops/my-workshop` - Get own workshop [WORKSHOP_*]
- `GET /api/v1/workshops/{id}` - Get workshop by ID [AUTH]
- `PUT /api/v1/workshops` - Update workshop [WORKSHOP_MANAGER]

#### IN SCOPE: Location Management

- `POST /api/v1/workshops/locations` - Add location [WORKSHOP_MANAGER]

#### IN SCOPE: Staff Management

- `POST /api/v1/workshops/staff` - Add staff member [WORKSHOP_MANAGER]

#### IN SCOPE: Service Templates

- `POST /api/v1/workshops/service-templates` - Add service template [WORKSHOP_MANAGER]

#### IN SCOPE: Capability Tags

- `POST /api/v1/workshops/tags` - Add capability tag [WORKSHOP_MANAGER]
- `PUT /api/v1/workshops/tags` - Update capability tags [WORKSHOP_MANAGER]

#### IN SCOPE: Media Management

- `POST /api/v1/workshops/logo` - Upload logo [WORKSHOP_MANAGER]
- `POST /api/v1/workshops/photos` - Add photo [WORKSHOP_MANAGER]
- `DELETE /api/v1/workshops/photos/{index}` - Delete photo [WORKSHOP_MANAGER]

#### IN SCOPE: Subscription Management

- `GET /api/v1/workshops/my-workshop/subscription` - Get subscription status [WORKSHOP_MANAGER]
- `PUT /api/v1/workshops/my-workshop/subscription` - Update subscription [WORKSHOP_MANAGER]

#### IN SCOPE: Invitation Management

- `POST /api/v1/invitations` - Create invitation [WORKSHOP_MANAGER]
- `POST /api/v1/invitations/accept` - Accept invitation (public with code)
- `GET /api/v1/invitations/{code}` - Get invitation by code (public)
- `GET /api/v1/invitations` - Get workshop invitations [WORKSHOP_MANAGER]

#### IN SCOPE: Public Workshop Search (Reference)

- `GET /api/v1/workshops/search` - Search workshops (public, for reference)
- `GET /api/v1/workshops/{id}/public` - Get public workshop info (public, for reference)
- `GET /api/v1/workshops/{id}/services` - Get workshop services (public, for reference)

---

### 3.3. Matching & Booking Context Endpoints

#### IN SCOPE: Service Requests (Workshop View)

- `GET /api/v1/service-requests/nearby` - Get nearby requests [WORKSHOP_*]
  - Query params: `latitude`, `longitude`, `radiusKm` (default: 50), `page`, `size`
- `GET /api/v1/service-requests/{id}` - Get request details [WORKSHOP_*]

#### OUT OF SCOPE: Service Requests (Car Owner Actions)

- ❌ `POST /api/v1/service-requests` - Create service request [CAR_OWNER only]
- ❌ `GET /api/v1/service-requests/my-requests` - Get own requests [CAR_OWNER only]
- ❌ `POST /api/v1/service-requests/{id}/cancel` - Cancel request [CAR_OWNER only]

#### IN SCOPE: Offers (Workshop Actions)

- `POST /api/v1/offers` - Create offer [WORKSHOP_*]
- `GET /api/v1/offers/my-offers` - Get workshop offers [WORKSHOP_*]
- `GET /api/v1/offers/{id}` - Get offer details [WORKSHOP_*]
- `POST /api/v1/offers/{id}/cancel` - Cancel offer [WORKSHOP_*]

#### OUT OF SCOPE: Offers (Car Owner Actions)

- ❌ `GET /api/v1/offers/for-request/{requestId}` - Get offers for request [CAR_OWNER only]
- ❌ `POST /api/v1/offers/{id}/accept` - Accept offer [CAR_OWNER only]
- ❌ `POST /api/v1/offers/{id}/reject` - Reject offer [CAR_OWNER only]

#### IN SCOPE: Service Bookings (Workshop Actions)

- `GET /api/v1/service-bookings/my-bookings` - Get workshop bookings [WORKSHOP_*]
- `GET /api/v1/service-bookings/{id}` - Get booking details [WORKSHOP_*]
- `POST /api/v1/service-bookings/{id}/confirm-schedule` - Confirm schedule [WORKSHOP_*]
- `POST /api/v1/service-bookings/{id}/start` - Start service [WORKSHOP_*]
- `POST /api/v1/service-bookings/{id}/complete` - Complete service [WORKSHOP_*]
- `POST /api/v1/service-bookings/{id}/cancel` - Cancel booking [WORKSHOP_*]

#### OUT OF SCOPE: Service Bookings (Car Owner Actions)

- ❌ `POST /api/v1/service-bookings/{id}/confirm-pickup` - Confirm pickup [CAR_OWNER only]

---

### 3.4. Trust & Reputation Context Endpoints

#### IN SCOPE: Reviews

- `POST /api/v1/reviews` - Create review [AUTH]
  - Workshop users can create reviews for customers after service completion
- `GET /api/v1/reviews/{id}` - Get review details [AUTH]
- `GET /api/v1/reviews/workshop/{workshopId}` - Get workshop reviews [AUTH]
- `GET /api/v1/reviews/service-booking/{bookingId}` - Get booking reviews [AUTH]
- `POST /api/v1/reviews/{id}/report` - Report review [AUTH]

#### IN SCOPE: Review Window

- `GET /api/v1/reviews/window-status/{bookingId}` - Check review window [AUTH]
  - Check if a review can still be created (14-day window)

#### IN SCOPE: Trust Scores

- `GET /api/v1/trust-scores/workshop/{workshopId}` - Get workshop trust score [AUTH]
- `GET /api/v1/trust-scores/me` - Get own trust score [AUTH]

#### OUT OF SCOPE: User Reviews (Car Owner Focus)

- ❌ `GET /api/v1/reviews/user/{userId}` - Get user reviews [AUTH] (not primary use case for workshop app)
- ❌ `GET /api/v1/trust-scores/user/{userId}` - Get user trust score [AUTH] (not primary use case)

#### OUT OF SCOPE: Admin Reports

- ❌ `GET /api/v1/reviews/reports` - Get all reports [ADMIN only]

---

### 3.5. Payment Context Endpoints

#### IN SCOPE: Payments

- `POST /api/v1/payments/subscriptions` - Create subscription payment [WORKSHOP_MANAGER]
- `GET /api/v1/payments/{id}` - Get payment details [WORKSHOP_MANAGER]
- `GET /api/v1/payments/my-payments` - Get own payments [WORKSHOP_MANAGER]
- `POST /api/v1/payments/{id}/complete` - Complete payment (simulated) [WORKSHOP_MANAGER]
- `POST /api/v1/payments/{id}/cancel` - Cancel payment [WORKSHOP_MANAGER]

#### IN SCOPE: Billing

- `GET /api/v1/billing/upcoming-renewals` - Get upcoming renewals [WORKSHOP_MANAGER]
- `GET /api/v1/billing/history` - Get billing history [WORKSHOP_MANAGER]

#### OUT OF SCOPE: Admin Payment Actions

- ❌ `POST /api/v1/payments/{id}/fail` - Fail payment (testing) [ADMIN only]
- ❌ `POST /api/v1/payments/{id}/refund` - Refund payment [ADMIN only] (workshop managers may have this, but typically admin function)

---

### 3.6. Catalog System Endpoints

#### IN SCOPE: Read-Only Catalog Access

- `GET /api/v1/catalog/brands` - Get vehicle brands (optional: `?popularOnly=true`) (public)
- `GET /api/v1/catalog/brands/{brandId}/models` - Get models for a brand (public)
- `GET /api/v1/catalog/services` - Get service catalog (optional: `?category=MAINTENANCE`) (public)
- `GET /api/v1/catalog/services/categories` - Get service categories (public)
- `GET /api/v1/catalog/capability-tags` - Get capability tags (optional: `?category=BRAND`) (public)
- `GET /api/v1/catalog/capability-tags/categories` - Get tag categories (public)

#### OUT OF SCOPE: Catalog Administration

- ❌ All `/api/v1/admin/catalog/**` endpoints [ADMIN only]

---

### 3.7. Vehicle & Maintenance Context Endpoints

#### OUT OF SCOPE: All Vehicle Endpoints

- ❌ `POST /api/v1/vehicles` - Register vehicle [CAR_OWNER only]
- ❌ `GET /api/v1/vehicles/my-vehicles` - Get own vehicles [CAR_OWNER only]
- ❌ `GET /api/v1/vehicles/{id}` - Get vehicle details [CAR_OWNER only]
- ❌ `PUT /api/v1/vehicles/{id}` - Update vehicle [CAR_OWNER only]
- ❌ `POST /api/v1/vehicles/{id}/transfer` - Transfer ownership [CAR_OWNER only]
- ❌ `DELETE /api/v1/vehicles/{id}` - Deactivate vehicle [CAR_OWNER only]
- ❌ `POST /api/v1/vehicles/{id}/photos` - Upload vehicle photo [CAR_OWNER only]

#### OUT OF SCOPE: All Maintenance Endpoints

- ❌ `GET /api/v1/maintenances/vehicle/{vehicleId}` - Get vehicle history [CAR_OWNER only]
- ❌ `GET /api/v1/maintenances/{id}` - Get maintenance details [CAR_OWNER only]
- ❌ `POST /api/v1/maintenances/manual` - Add manual maintenance [CAR_OWNER only]
- ❌ `POST /api/v1/maintenances/{id}/confirm` - Confirm workshop maintenance [CAR_OWNER only]
- ❌ `POST /api/v1/maintenances/{id}/reject` - Reject maintenance record [CAR_OWNER only]

**Note**: Maintenance records are automatically created by the backend when a workshop marks a service booking as `COMPLETED`. The workshop app does not need to directly call maintenance endpoints.

---

## 4. General Rules for the Android Workshop App

This section defines general architectural rules, patterns, and constraints that must be followed when developing the Android workshop app.

### 4.1. User Role Assumptions

- **Always assume workshop context**: The app should assume that all authenticated users are either `WORKSHOP_MANAGER` or `WORKSHOP_WORKER`.
- **No CAR_OWNER flows**: Do not implement any screens, UI components, or API calls related to:
  - Vehicle registration or management
  - Service request creation (as a vehicle owner)
  - Offer acceptance/rejection (as a vehicle owner)
  - Vehicle pickup confirmation
  - Manual maintenance record creation
  - Viewing "my vehicles" or "my service requests"
- **No ADMIN flows**: Do not implement any administrative features or screens.

### 4.2. Authentication & Authorization

- **JWT Token Management**:
  - All endpoints marked with `[AUTH]` require JWT authentication
  - The app must store the JWT token securely after successful login
  - All authenticated API calls must include the header: `Authorization: Bearer <token>`
  - Implement token refresh logic if the backend supports it (check token expiration)
  - Handle 401 Unauthorized responses by redirecting to login

- **Role-Based Access Control**:
  - Some endpoints are restricted to `[WORKSHOP_MANAGER]` only
  - The app should check user role and conditionally show/hide features:
    - `WORKSHOP_MANAGER`: Full access to all workshop management features
    - `WORKSHOP_WORKER`: Limited access (read-only for most settings, can create offers and manage bookings)
  - Handle 403 Forbidden responses appropriately

- **Workshop Context**:
  - The backend automatically extracts `workshopId` from the JWT for `WORKSHOP_*` roles
  - The app does not need to manually pass `workshopId` in requests
  - The backend uses ThreadLocal-based multitenancy to scope operations to the correct workshop

### 4.3. Connection Pattern Architecture

The Android app **MUST** follow the connection pattern described in `prompt/CONNECTION_PATTERN_EASYSHOP.md`. Key requirements:

- **Clean Architecture with Feature Modules**:
  - Organize code by feature (e.g., `auth/`, `workshop/`, `bookings/`, `payments/`)
  - Each feature has three layers: `data/`, `domain/`, `presentation/`

- **Data Layer** (`data/`):
  - `remote/services/`: Retrofit service interfaces for API calls
  - `remote/models/`: DTOs (Data Transfer Objects) matching backend JSON structure
  - `repositories/`: Repository implementations that call services and map DTOs to domain models
  - `di/DataModule.kt`: Dependency injection for Retrofit, repositories, etc.

- **Domain Layer** (`domain/`):
  - `repositories/`: Repository interfaces (contracts)
  - `models/`: Domain models (business objects, independent of backend structure)

- **Presentation Layer** (`presentation/`):
  - `[screen]/`: Screen-specific Composables and ViewModels
  - `di/PresentationModule.kt`: Dependency injection for ViewModels

- **Technology Stack**:
  - **Retrofit 3.0+**: For HTTP client and API service definitions
  - **Gson Converter**: For JSON serialization/deserialization
  - **Coroutines**: For asynchronous operations (`suspend` functions)
  - **StateFlow/LiveData**: For reactive state management in ViewModels
  - **Jetpack Compose**: For UI (if using Compose)

- **Data Flow**:
  ```
  UI (Composable) 
    → ViewModel 
    → Repository (interface) 
    → RepositoryImpl 
    → Service (Retrofit) 
    → Backend API
  ```

- **Response Flow**:
  ```
  Backend API 
    → JSON 
    → DTO (via Gson) 
    → Domain Model (via mapper in RepositoryImpl) 
    → ViewModel 
    → UI (via StateFlow)
  ```

- **DTO to Domain Mapping**:
  - Mapping happens **inside** `RepositoryImpl`, not in separate mapper classes
  - Use inline mapping with `.map { }` for collections
  - Handle nullable fields with elvis operator (`?:`)
  - Extract only necessary fields from DTOs

### 4.4. Error Handling

- **Network Errors**: Handle network connectivity issues gracefully
- **HTTP Status Codes**:
  - `200 OK`: Success
  - `201 Created`: Resource created successfully
  - `400 Bad Request`: Invalid request data (show validation errors)
  - `401 Unauthorized`: Token expired or invalid (redirect to login)
  - `403 Forbidden`: Insufficient permissions (show error message)
  - `404 Not Found`: Resource not found
  - `409 Conflict`: Business rule violation (show specific error message)
  - `500 Internal Server Error`: Server error (show generic error, log details)

- **Error Propagation**: Consider using `Result<T>` or sealed classes to represent success/error states
- **User Feedback**: Always provide user-friendly error messages

### 4.5. Pagination

- Many list endpoints support pagination with query parameters:
  - `page` (default: 0): Page number
  - `size` (default: 20): Page size
- Implement pagination in list screens (e.g., infinite scroll, "Load More" button)

### 4.6. Geolocation

- The app should request location permissions for:
  - Finding nearby service requests (`GET /api/v1/service-requests/nearby`)
  - Workshop location management
- Handle cases where location is unavailable or permission is denied

### 4.7. Media Upload

- Workshop logo and photos are uploaded to Cloudinary via the backend
- Use multipart form data for image uploads
- Compress images before upload to reduce payload size
- Show upload progress indicators

### 4.8. Subscription & Payment Flow

- **Payment Simulation**: The payment system is currently simulated (no real payment gateway)
- **Payment Flow**:
  1. User selects subscription tier (BASIC or PREMIUM)
  2. Create payment via `POST /api/v1/payments/subscriptions`
  3. Complete payment via `POST /api/v1/payments/{id}/complete` (simulated)
  4. Backend automatically updates workshop subscription
- **Billing History**: Display past payments and upcoming renewals

### 4.9. Service Booking Workflow

The app should support the complete service booking lifecycle:

1. **View Nearby Requests**: Display service requests within radius
2. **Create Offer**: Submit offer with price and estimated time
3. **Wait for Acceptance**: Offer is accepted by vehicle owner (backend handles this)
4. **View Booking**: Booking appears in "my bookings" list
5. **Confirm Schedule**: Confirm the scheduled appointment time
6. **Start Service**: Mark service as IN_PROGRESS
7. **Complete Service**: Mark service as COMPLETED (creates maintenance record automatically)
8. **Create Review**: Optionally create review for customer (within 14 days)

### 4.10. Testing Considerations

- **Unit Tests**: Test ViewModels, Repository implementations, and mappers
- **Integration Tests**: Test API service calls with mock backend
- **UI Tests**: Test critical user flows (login, create offer, complete service)

### 4.11. Security Best Practices

- **Secure Token Storage**: Use Android Keystore or EncryptedSharedPreferences for JWT storage
- **HTTPS Only**: Ensure all API calls use HTTPS (not HTTP)
- **Certificate Pinning**: Consider implementing certificate pinning for production
- **Input Validation**: Validate user inputs before sending to backend
- **Sensitive Data**: Never log JWT tokens or sensitive user data

### 4.12. Performance Considerations

- **Image Loading**: Use image loading libraries (e.g., Coil, Glide) with caching
- **API Caching**: Consider caching catalog data (services, tags) since it's relatively static
- **Background Sync**: Consider background sync for service requests and bookings
- **Offline Support**: Handle offline scenarios gracefully (show cached data, queue actions)

---

## Summary

This Android workshop app is a **focused, role-specific application** that enables workshop managers and workers to:

1. Manage their workshop profile and settings
2. Find and respond to nearby service requests
3. Manage service bookings and complete services
4. Handle subscription payments
5. View and create reviews
6. Manage staff and invitations

The app **explicitly excludes** all vehicle owner functionality and administrative features, ensuring a clean, focused user experience for workshop users.

All development should follow the connection pattern described in `prompt/CONNECTION_PATTERN_EASYSHOP.md`, maintaining clean architecture principles with clear separation between data, domain, and presentation layers.

