# AutoNexo API Endpoints

Complete listing of all REST endpoints organized by Bounded Context.

**Note on API Versioning**: This API uses inconsistent versioning. Some contexts use `/api/v1/` while others use `/api/` (no version). This is documented as-is to reflect the actual implementation.

## Authentication

All authenticated endpoints require `Authorization: Bearer <JWT_TOKEN>` header.

## Public Endpoints (No Authentication Required)

### Authentication & User
- `POST /api/v1/users/signup` - Register new user
- `POST /api/v1/users/signin` - User login (returns JWT)
- `GET /api/v1/users/available-roles` - Get available roles
- `POST /api/v1/users/forgot-password` - Request password reset
- `POST /api/v1/users/reset-password` - Reset password with token
- `POST /api/v1/users/resend-verification` - Resend email verification
- `POST /api/v1/users/verify-email` - Verify email with token
- `GET /api/v1/users/verification-status?email={email}` - Check verification status

### Workshops
- `GET /api/v1/workshops/search` - Search workshops (with filters: latitude, longitude, radiusKm, services, tags, minRating)
- `GET /api/v1/workshops/{id}/public` - Get public workshop profile
- `GET /api/v1/workshops/{id}/services` - Get workshop services

### Catalog System
- `GET /api/v1/catalog/brands` - Get vehicle brands (optional: ?popularOnly=true)
- `GET /api/v1/catalog/brands/{brandId}/models` - Get models for a brand
- `GET /api/v1/catalog/services` - Get service catalog (optional: ?category=MAINTENANCE)
- `GET /api/v1/catalog/services/categories` - Get service categories
- `GET /api/v1/catalog/capability-tags` - Get capability tags (optional: ?category=BRAND)
- `GET /api/v1/catalog/capability-tags/categories` - Get tag categories

### Invitations
- `POST /api/v1/invitations/accept` - Accept invitation (public with code)
- `GET /api/v1/invitations/{code}` - Get invitation by code

### Legacy Catalog Endpoints (Deprecated)
- `GET /api/v1/workshops/catalog/categories` - **DEPRECATED**: Use /api/v1/catalog/services/categories instead
- `GET /api/v1/workshops/catalog/services` - **DEPRECATED**: Use /api/v1/catalog/services instead
- `GET /api/v1/workshops/catalog/capability-tags` - **DEPRECATED**: Use /api/v1/catalog/capability-tags instead

## 1. IAM Context

**Base Path**: `/api/v1/users`

### User Management
- `POST /api/v1/users/signup` - Register new user (public)
- `POST /api/v1/users/signin` - User login (returns JWT) (public)
- `GET /api/v1/users/available-roles` - Get available roles (public)
- `GET /api/v1/users` - Get all users [AUTH]
- `GET /api/v1/users/by-email?email={email}` - Get user by email [AUTH]
- `GET /api/v1/users/me` - Get current user profile [AUTH]
- `PUT /api/v1/users/me` - Update user profile [AUTH]
- `DELETE /api/v1/users/me` - Deactivate account [AUTH]

### Email Verification
- `POST /api/v1/users/resend-verification` - Resend verification email (public)
- `POST /api/v1/users/verify-email` - Verify email with token (public)
- `GET /api/v1/users/verification-status?email={email}` - Get verification status (public)

### Password Management
- `POST /api/v1/users/forgot-password` - Request password reset (public)
- `POST /api/v1/users/reset-password` - Reset password with token (public)
- `PUT /api/v1/users/me/password` - Change password [AUTH]

## 2. Workshop Context

**Base Path**: `/api/v1/workshops`

### Workshop Management
- `POST /api/v1/workshops` - Create workshop [WORKSHOP_MANAGER]
- `GET /api/v1/workshops/my-workshop` - Get own workshop [WORKSHOP_*]
- `GET /api/v1/workshops/{id}` - Get workshop by ID [AUTH]
- `GET /api/v1/workshops/by-owner/{userId}` - Get workshop by owner user ID [AUTH]
- `GET /api/v1/workshops` - Get all workshops [AUTH]
- `GET /api/v1/workshops/by-tag?tag={tag}` - Search workshops by capability tag [AUTH]
- `PUT /api/v1/workshops` - Update workshop [WORKSHOP_MANAGER]
- `GET /api/v1/workshops/my-workshop/available-requests` - Get available service requests for workshop [WORKSHOP_*]

### Location Management
- `POST /api/v1/workshops/locations` - Add location [WORKSHOP_MANAGER]
- `GET /api/v1/workshops/my-workshop/locations` - Get all locations for my workshop [WORKSHOP_*]
- `GET /api/v1/workshops/my-workshop/locations/{id}` - Get location by ID [WORKSHOP_*]
- `PUT /api/v1/workshops/my-workshop/locations/{id}` - Update location [WORKSHOP_MANAGER]
- `DELETE /api/v1/workshops/my-workshop/locations/{id}` - Delete location [WORKSHOP_MANAGER]

### Staff Management
- `POST /api/v1/workshops/staff` - Add staff member [WORKSHOP_MANAGER] **DEPRECATED** - Use invitation flow instead

### Service Templates
- `POST /api/v1/workshops/service-templates` - Add service template [WORKSHOP_MANAGER]

### Capability Tags
- `POST /api/v1/workshops/tags?tag={tag}` - Add capability tag [WORKSHOP_MANAGER]
- `PUT /api/v1/workshops/tags` - Update capability tags (replaces all) [WORKSHOP_MANAGER]

### Media Management
- `POST /api/v1/workshops/logo` - Upload logo (multipart/form-data) [WORKSHOP_MANAGER]
- `POST /api/v1/workshops/photos` - Add photo (multipart/form-data) [WORKSHOP_MANAGER]
- `DELETE /api/v1/workshops/photos/{photoIndex}` - Delete photo by index [WORKSHOP_MANAGER]

### Subscription Management
- `GET /api/v1/workshops/my-workshop/subscription` - Get subscription status [WORKSHOP_MANAGER]
- `PUT /api/v1/workshops/my-workshop/subscription` - Update subscription [WORKSHOP_MANAGER]

### Invitation Management

**Base Path**: `/api/v1/invitations`

- `POST /api/v1/invitations` - Create invitation [WORKSHOP_MANAGER]
- `GET /api/v1/invitations` - Get workshop invitations [WORKSHOP_MANAGER]

## 3. Vehicle & Maintenance Context

**Base Path**: `/api/v1/vehicles` and `/api/v1`

### Vehicle Management
- `POST /api/v1/vehicles` - Register vehicle [CAR_OWNER]
- `GET /api/v1/vehicles` - Get own vehicles [CAR_OWNER]
- `GET /api/v1/vehicles/{id}` - Get vehicle details [CAR_OWNER]
- `PUT /api/v1/vehicles/{id}/mileage` - Update vehicle mileage [CAR_OWNER]
- `PUT /api/v1/vehicles/{id}/transfer` - Transfer ownership [CAR_OWNER]
- `DELETE /api/v1/vehicles/{id}` - Deactivate vehicle [CAR_OWNER]
- `POST /api/v1/vehicles/{id}/images` - Upload vehicle image (multipart/form-data) [CAR_OWNER]
- `POST /api/v1/vehicles/{id}/authorized-users` - Add authorized user [CAR_OWNER]
- `DELETE /api/v1/vehicles/{id}/authorized-users/{userId}` - Remove authorized user [CAR_OWNER]

### Maintenance History
- `POST /api/v1/vehicles/{vehicleId}/maintenances` - Create manual maintenance [CAR_OWNER]
- `GET /api/v1/vehicles/{vehicleId}/maintenances` - Get vehicle maintenance history [CAR_OWNER]
- `GET /api/v1/maintenances/{id}` - Get maintenance details [CAR_OWNER]
- `GET /api/v1/maintenances/pending` - Get pending maintenances [CAR_OWNER]
- `PUT /api/v1/maintenances/{id}/confirm` - Confirm workshop maintenance [CAR_OWNER]
- `PUT /api/v1/maintenances/{id}/reject` - Reject maintenance record [CAR_OWNER]

## 4. Matching & Booking Context

**Base Path**: `/api/` (no version prefix)

### Service Requests

**Base Path**: `/api/service-requests`

#### Car Owners
- `POST /api/service-requests` - Create service request [CAR_OWNER]
- `GET /api/service-requests?status={status}` - Get own requests (optional status filter) [CAR_OWNER]
- `GET /api/service-requests/{id}` - Get request details [CAR_OWNER]
- `DELETE /api/service-requests/{id}` - Cancel request [CAR_OWNER]

#### Workshops
- `GET /api/service-requests/{id}` - Get request details [WORKSHOP_*]
- `POST /api/service-requests/{id}/reject` - Reject service request [WORKSHOP_*]

### Offers

**Base Path**: `/api/offers`

#### Workshops
- `POST /api/offers` - Create offer [WORKSHOP_*]
- `GET /api/offers/my-workshop?status={status}` - Get workshop offers (optional status filter) [WORKSHOP_*]
- `GET /api/offers/{id}` - Get offer details [WORKSHOP_*]
- `DELETE /api/offers/{id}` - Withdraw offer [WORKSHOP_*]

#### Car Owners
- `GET /api/offers/service-requests/{requestId}` - Get offers for request [CAR_OWNER]
- `GET /api/offers/my-requests?status={status}` - Get offers for my requests (optional status filter) [CAR_OWNER]
- `POST /api/offers/{id}/accept` - Accept offer [CAR_OWNER]
- `POST /api/offers/{id}/reject` - Reject offer [CAR_OWNER]

### Service Bookings

**Base Path**: `/api/service-bookings`

#### Workshops
- `GET /api/service-bookings?status={status}&upcoming={upcoming}` - Get workshop bookings (optional filters) [WORKSHOP_*]
- `GET /api/service-bookings/{id}` - Get booking details [WORKSHOP_*]
- `POST /api/service-bookings/{id}/confirm-schedule` - Confirm schedule [WORKSHOP_*]
- `POST /api/service-bookings/{id}/propose-change` - Propose schedule change [WORKSHOP_*]
- `POST /api/service-bookings/{id}/complete` - Complete service [WORKSHOP_*]
- `DELETE /api/service-bookings/{id}` - Cancel booking [WORKSHOP_*]

#### Car Owners
- `GET /api/service-bookings?status={status}&upcoming={upcoming}` - Get own bookings (optional filters) [CAR_OWNER]
- `GET /api/service-bookings/{id}` - Get booking details [CAR_OWNER]
- `POST /api/service-bookings/{id}/confirm-pickup` - Confirm pickup [CAR_OWNER]
- `DELETE /api/service-bookings/{id}` - Cancel booking [CAR_OWNER]

### Matching

**Base Path**: `/api/matching`

- `GET /api/matching/workshops?latitude={lat}&longitude={lon}&radiusKm={km}&services={services}&minRating={rating}` - Find matching workshops [AUTH]

## 5. Trust & Reputation Context

**Base Path**: `/api/` (no version prefix)

### Reviews

**Base Path**: `/api/reviews`

- `POST /api/reviews` - Create review [AUTH - CAR_OWNER, WORKSHOP_MANAGER, WORKSHOP_WORKER]
- `GET /api/reviews/service-bookings/{serviceBookingId}` - Get reviews for a service booking [AUTH]
- `GET /api/reviews/my-reviews?page={page}&size={size}` - Get my reviews (reviews I've written) [AUTH]
- `GET /api/reviews/received/workshops/{workshopId}?status={status}&page={page}&size={size}` - Get reviews received by workshop [WORKSHOP_MANAGER, WORKSHOP_WORKER]
- `GET /api/reviews/received/users/{userId}?status={status}&page={page}&size={size}` - Get reviews received by user [CAR_OWNER]
- `GET /api/reviews/{reviewId}/reports` - Get reports for a review [WORKSHOP_MANAGER, WORKSHOP_WORKER]
- `GET /api/reviews/window-status?serviceBookingId={id}` - Check review window status [AUTH]
- `POST /api/reviews/{reviewId}/report` - Report a review [AUTH]

### Trust Scores

**Base Path**: `/api/trust-score`

- `GET /api/trust-score/workshops/{workshopId}` - Get workshop trust score (public)
- `GET /api/trust-score/users/{userId}` - Get user trust score (public)
- `GET /api/trust-score/my-score` - Get own trust score [AUTH]

## 6. Payment Context

**Base Path**: `/api/v1/payments` and `/api/v1/billing`

### Payments
- `POST /api/v1/payments/subscriptions` - Create subscription payment [WORKSHOP_MANAGER]
- `GET /api/v1/payments/{id}` - Get payment details [WORKSHOP_MANAGER, ADMIN]
- `GET /api/v1/payments/my-payments?page={page}&size={size}` - Get own payments [WORKSHOP_MANAGER]
- `POST /api/v1/payments/{id}/complete` - Complete payment (simulated) [WORKSHOP_MANAGER, ADMIN]
- `POST /api/v1/payments/{id}/fail` - Fail payment (testing) [ADMIN]
- `POST /api/v1/payments/{id}/refund?reason={reason}` - Refund payment [WORKSHOP_MANAGER, ADMIN]
- `POST /api/v1/payments/{id}/cancel` - Cancel payment [WORKSHOP_MANAGER, ADMIN]

### Billing
- `GET /api/v1/billing/upcoming-renewals?daysAhead={days}` - Get upcoming renewals [WORKSHOP_MANAGER, ADMIN]
- `GET /api/v1/billing/history?page={page}&size={size}` - Get billing history [WORKSHOP_MANAGER]

## 7. Catalog Administration (ADMIN Only)

**Base Path**: `/api/v1/admin/catalog`

### Brand Management
- `GET /api/v1/admin/catalog/brands` - Get all brands (including inactive) [ADMIN]
- `GET /api/v1/admin/catalog/brands/{id}` - Get brand by ID [ADMIN]
- `POST /api/v1/admin/catalog/brands` - Create new brand [ADMIN]
- `PUT /api/v1/admin/catalog/brands/{id}` - Update brand [ADMIN]
- `DELETE /api/v1/admin/catalog/brands/{id}` - Deactivate brand (soft delete) [ADMIN]

### Model Management
- `GET /api/v1/admin/catalog/brands/{brandId}/models` - Get all models for a brand [ADMIN]
- `GET /api/v1/admin/catalog/models/{id}` - Get model by ID [ADMIN]
- `POST /api/v1/admin/catalog/models` - Create new model [ADMIN]
- `PUT /api/v1/admin/catalog/models/{id}` - Update model [ADMIN]
- `DELETE /api/v1/admin/catalog/models/{id}` - Deactivate model (soft delete) [ADMIN]

## Common Query Parameters

### Pagination
- `page` (default: 0) - Page number
- `size` (default: 20) - Page size

### Sorting
- `sort` - Sort criteria (e.g., `createdAt,desc`)

### Workshop Search
- `latitude` - User latitude for distance calculation
- `longitude` - User longitude
- `radiusKm` (default: 50) - Search radius in kilometers
- `services` - Comma-separated list of services
- `tags` - Comma-separated capability tags
- `minRating` - Minimum trust score

### Status Filters
- `status` - Filter by status (varies by endpoint: PENDING, ACCEPTED, COMPLETED, etc.)

## Common Response Codes

- `200 OK` - Successful GET/PUT
- `201 Created` - Successful POST
- `204 No Content` - Successful DELETE
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Missing or invalid JWT
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Business rule violation
- `500 Internal Server Error` - Server error

## Role-Based Access Control

### Role Definitions
- **CAR_OWNER**: Vehicle owners who request services
- **WORKSHOP_MANAGER**: Workshop administrators (full workshop management access)
- **WORKSHOP_WORKER**: Workshop staff members (limited access, uses WorkshopContext)
- **ADMIN**: System administrators (catalog management and testing)

### Authorization Notes
- `[AUTH]` - Requires any authenticated user
- `[WORKSHOP_*]` - Requires WORKSHOP_MANAGER or WORKSHOP_WORKER role
- Workshop context is automatically extracted from JWT for WORKSHOP_* roles
- Some endpoints use WorkshopContext (ThreadLocal) for multitenancy
- Endpoints without explicit role annotation may still require authentication based on implementation

## API Documentation

Live API documentation available at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8080/v3/api-docs`

## Notes

1. **API Versioning**: The API uses inconsistent versioning. Matching & Booking and Trust & Reputation contexts use `/api/` while others use `/api/v1/`. This reflects the actual implementation.

2. **Workshop Context**: For WORKSHOP_MANAGER and WORKSHOP_WORKER roles, the workshop ID is automatically extracted from the JWT token and stored in WorkshopContext (ThreadLocal). Endpoints that use `[WORKSHOP_*]` automatically operate within the user's workshop context.

3. **Deprecated Endpoints**: Some endpoints are marked as deprecated but still functional. Use the recommended alternatives when available.

4. **Multipart Uploads**: Media upload endpoints (logo, photos, vehicle images) require `multipart/form-data` content type.

5. **Query Parameters**: Many list endpoints support optional query parameters for filtering and pagination. Check individual endpoint descriptions for available parameters.
