# Matching & Booking Context - Implementation Documentation

## Overview

The Matching & Booking Context is one of the most critical bounded contexts in the Autonexo application. It handles the entire flow from service request creation to service completion, including matching workshops to user requests, managing offers, scheduling services, and tracking service completion.

## Domain Model

### Value Objects

- **ServiceRequestStatus**: Enum representing the status of a service request
  - `PENDING`: Waiting for offers
  - `CANCELLED`: Cancelled by the user
  - `COMPLETED`: Converted to ServiceBooking and completed
  - `REJECTED`: Rejected by a workshop (only affects visibility for that workshop)

- **OfferStatus**: Enum representing the status of an offer
  - `PENDING`: Waiting for response
  - `ACCEPTED`: Accepted by the user (converts to ServiceBooking)
  - `REJECTED`: Rejected by the user
  - `EXPIRED`: Automatically expired after 3 days
  - `WITHDRAWN`: Withdrawn by the workshop

- **ServiceBookingStatus**: Enum representing the status of a service booking
  - `PENDING_SCHEDULE`: Offer accepted, negotiating date/time
  - `SCHEDULED`: Date/time confirmed
  - `IN_PROGRESS`: Service in progress (optional, can be skipped)
  - `COMPLETED`: Workshop marked as complete
  - `PENDING_PICKUP`: Waiting for user pickup confirmation
  - `PICKED_UP`: User confirmed pickup (final state)
  - `CANCELLED`: Cancelled by user or workshop

- **SearchRadius**: Value object representing search radius in kilometers (1-50 km)

### Aggregates

#### ServiceRequest
Represents a service request created by a vehicle owner.

**Fields:**
- `userId`: User who created the request
- `vehicleId`: Vehicle requiring service
- `requestedServices`: List of services requested (ServiceCatalog)
- `description`: Optional description
- `userLocation`: User's location (Coordinates)
- `searchRadius`: Search radius in km (1-50)
- `status`: Current status
- `rejectedByWorkshops`: Set of workshop IDs that rejected this request
- `offers`: List of offers received

**Business Rules:**
- Only PENDING requests can accept offers
- Workshops that rejected a request cannot send offers
- When an offer is accepted, the request is marked as COMPLETED

#### ServiceBooking
Represents a scheduled service after an offer has been accepted.

**Fields:**
- `serviceRequestId`: Original service request
- `offerId`: Accepted offer
- `userId`: Vehicle owner
- `vehicleId`: Vehicle being serviced
- `workshopId`: Workshop performing the service
- `scheduledDate`: Confirmed date/time
- `proposedPrice`: Price from the offer
- `finalPrice`: Final price (may differ from proposed)
- `status`: Current status
- `servicesToPerform`: List of services to perform
- `description`: Service description

**Business Rules:**
- Schedule must be confirmed before service can be marked as completed
- Service completion creates a maintenance record (PENDING_CONFIRMATION)
- User must confirm pickup after service completion
- Cannot cancel after pickup is confirmed

#### Offer (Entity)
Represents an offer sent by a workshop for a service request.

**Fields:**
- `serviceRequestId`: Service request this offer is for
- `workshopId`: Workshop sending the offer
- `proposedPrice`: Proposed price (Money)
- `proposedDate`: Proposed date/time
- `status`: Current status
- `message`: Optional message from workshop
- `expiresAt`: Expiration date (3 days from creation)
- `acceptedAt`: When offer was accepted
- `withdrawnAt`: When offer was withdrawn

**Business Rules:**
- Offers expire automatically after 3 days
- Only PENDING offers can be accepted
- Only PENDING offers can be withdrawn

## Services

### ServiceRequestCommandService
Handles write operations for service requests.

**Methods:**
- `handle(CreateServiceRequestCommand)`: Creates a new service request
- `handle(CancelServiceRequestCommand)`: Cancels a service request
- `handle(RejectServiceRequestCommand)`: Rejects a request (workshop perspective)

### ServiceRequestQueryService
Handles read operations for service requests.

**Methods:**
- `handle(GetServiceRequestByIdQuery)`: Gets a request by ID
- `handle(GetUserServiceRequestsQuery)`: Gets requests for a user
- `handle(GetWorkshopAvailableRequestsQuery)`: Gets available requests for matching
- `handle(GetWorkshopReceivedRequestsQuery)`: Gets requests a workshop has sent offers to

### OfferCommandService
Handles write operations for offers.

**Methods:**
- `handle(CreateOfferCommand)`: Creates a new offer
- `handle(WithdrawOfferCommand)`: Withdraws an offer
- `handle(AcceptOfferCommand)`: Accepts an offer (creates ServiceBooking)
- `handle(RejectOfferCommand)`: Rejects an offer

**Business Logic:**
- When an offer is accepted:
  - Creates a ServiceBooking
  - Rejects all other pending offers for the same request
  - Marks the service request as COMPLETED
  - Sends notification to workshop

### OfferQueryService
Handles read operations for offers.

**Methods:**
- `handle(GetOffersByServiceRequestQuery)`: Gets all offers for a request
- `handle(GetWorkshopOffersQuery)`: Gets offers sent by a workshop
- `handle(GetUserOffersQuery)`: Gets offers received by a user

### ServiceBookingCommandService
Handles write operations for service bookings.

**Methods:**
- `handle(ConfirmScheduleCommand)`: Confirms the scheduled date/time
- `handle(ProposeScheduleChangeCommand)`: Proposes a schedule change
- `handle(MarkCompletedCommand)`: Marks service as completed (creates maintenance record)
- `handle(ConfirmPickupCommand)`: Confirms vehicle pickup
- `handle(CancelServiceBookingCommand)`: Cancels a booking

**Business Logic:**
- When service is marked as completed:
  - Creates a maintenance record via VehicleMaintenanceFacade (PENDING_CONFIRMATION)
  - Transitions to PENDING_PICKUP status
  - Sends notification to user

### ServiceBookingQueryService
Handles read operations for service bookings.

**Methods:**
- `handle(GetServiceBookingByIdQuery)`: Gets a booking by ID
- `handle(GetUserServiceBookingsQuery)`: Gets bookings for a user
- `handle(GetWorkshopServiceBookingsQuery)`: Gets bookings for a workshop
- `handle(GetUpcomingServiceBookingsQuery)`: Gets upcoming bookings (for calendar)

### MatchingService
Finds matching workshops for service requests.

**Method:**
- `findMatchingWorkshops(Coordinates, Integer, List<ServiceCatalog>, Optional<Double>)`: Finds matching workshops

**Matching Algorithm:**
1. Gets all active workshops
2. Filters by distance (Haversine formula) within search radius
3. Filters by services offered (must match at least one requested service)
4. Filters by rating (if minRating specified)
5. Calculates match score:
   - Distance score: 40% (closer = higher score)
   - Rating score: 30% (higher rating = higher score)
   - Service matching score: 30% (more matching services = higher score)
6. Returns top 20 results ordered by match score

## REST API Endpoints

### Service Requests

- `POST /api/service-requests`: Create a new service request
- `GET /api/service-requests`: Get my service requests (with optional status filter)
- `GET /api/service-requests/{id}`: Get a service request by ID
- `DELETE /api/service-requests/{id}`: Cancel a service request
- `POST /api/service-requests/{id}/reject`: Reject a service request (workshop)

### Offers

- `POST /api/offers`: Create an offer (workshop)
- `GET /api/offers/service-requests/{requestId}`: Get offers for a service request
- `GET /api/offers/my-workshop`: Get offers sent by my workshop (with optional status filter)
- `GET /api/offers/my-requests`: Get offers received for my requests (with optional status filter)
- `POST /api/offers/{id}/accept`: Accept an offer (user)
- `POST /api/offers/{id}/reject`: Reject an offer (user)
- `DELETE /api/offers/{id}`: Withdraw an offer (workshop)

### Service Bookings

- `GET /api/service-bookings`: Get my service bookings (user or workshop, with optional status filter)
- `GET /api/service-bookings/{id}`: Get a service booking by ID
- `POST /api/service-bookings/{id}/confirm-schedule`: Confirm scheduled date/time
- `POST /api/service-bookings/{id}/propose-change`: Propose schedule change
- `POST /api/service-bookings/{id}/complete`: Mark service as completed (workshop)
- `POST /api/service-bookings/{id}/confirm-pickup`: Confirm vehicle pickup (user)
- `DELETE /api/service-bookings/{id}`: Cancel a service booking

### Matching

- `GET /api/matching/workshops`: Find matching workshops
  - Query params: `latitude`, `longitude`, `radiusKm`, `services` (comma-separated), `minRating` (optional)

## ACL Facades

### WorkshopFacade
Provides access to workshop information for matching.

**Methods:**
- `getWorkshopInfo(WorkshopId)`: Gets basic workshop information
- `getWorkshopLocations(WorkshopId)`: Gets all workshop locations
- `getWorkshopServices(WorkshopId)`: Gets services offered (from ServiceTemplates linked to ServiceCatalog)
- `getWorkshopRating(WorkshopId)`: Gets workshop rating/trust score
- `getAllActiveWorkshops()`: Gets all active workshops

### VehicleFacade
Provides access to vehicle information for validation.

**Methods:**
- `getVehicleInfo(Long, UserId)`: Gets vehicle information
- `userOwnsVehicle(Long, UserId)`: Checks if user owns a vehicle

### NotificationFacade
Provides notification capabilities for Matching & Booking events.

**Methods:**
- `notifyOfferReceived(Long, Long, String)`: Notifies user when they receive a new offer
- `notifyOfferAccepted(Long, String)`: Notifies workshop when their offer is accepted
- `notifyOfferRejected(Long, String)`: Notifies workshop when their offer is rejected
- `notifyServiceCompleted(Long, String)`: Notifies user when service is completed
- `notifyPickupConfirmed(Long, String)`: Notifies workshop when pickup is confirmed
- `notifyUpcomingService(Long, String, String)`: Notifies both parties about upcoming service (24h reminder)

## Scheduled Tasks

### ExpiredOfferCleanupService
Runs daily at 2 AM to mark expired offers as EXPIRED.

**Cron:** `0 0 2 * * ?`

### UpcomingServiceReminderService
Runs daily at 8 AM to send reminders for services scheduled in the next 24 hours.

**Cron:** `0 0 8 * * ?`

## Email Templates

All email templates are stored in `src/main/resources/templates/emails/`:

- `new-offer.html`: Notification when user receives a new offer
- `offer-accepted.html`: Notification when workshop's offer is accepted
- `offer-rejected.html`: Notification when workshop's offer is rejected
- `service-completed.html`: Notification when service is completed
- `pickup-confirmed.html`: Notification when pickup is confirmed
- `upcoming-service.html`: Reminder for upcoming service (24h before)

## Integration Points

### Vehicle & Maintenance Context
- Uses `VehicleMaintenanceFacade` to create maintenance records when services are completed
- Maintenance records are created with `PENDING_CONFIRMATION` status

### Notifications Context
- Uses `EmailService` to send notifications for various events
- All notifications are sent via the `NotificationFacade` ACL

### Workshop Context
- Uses `WorkshopFacade` to get workshop information, locations, services, and ratings for matching
- Accesses workshop data through ACL to maintain bounded context boundaries

### IAM Context
- Uses `UserRepository` to get user emails for notifications
- Validates user ownership through `VehicleFacade`

## Database Schema

### service_requests
- `id`: Primary key
- `user_id`: User who created the request
- `vehicle_id`: Vehicle requiring service
- `description`: Optional description
- `user_latitude`: User's latitude
- `user_longitude`: User's longitude
- `search_radius_km`: Search radius (1-50)
- `status`: Request status
- `cancelled_at`: Cancellation timestamp
- `created_at`, `updated_at`: Audit fields

### service_request_services
- `service_request_id`: Foreign key to service_requests
- `service`: ServiceCatalog enum value

### service_request_rejected_workshops
- `service_request_id`: Foreign key to service_requests
- `workshop_id`: Workshop ID that rejected

### offers
- `id`: Primary key
- `service_request_id`: Foreign key to service_requests
- `workshop_id`: Workshop sending the offer
- `proposed_price_amount`: Proposed price amount
- `proposed_price_currency`: Currency code
- `proposed_date`: Proposed date/time
- `status`: Offer status
- `message`: Optional message
- `expires_at`: Expiration timestamp
- `accepted_at`: Acceptance timestamp
- `withdrawn_at`: Withdrawal timestamp
- `created_at`, `updated_at`: Audit fields

### service_bookings
- `id`: Primary key
- `service_request_id`: Foreign key to service_requests
- `offer_id`: Foreign key to offers
- `user_id`: Vehicle owner
- `vehicle_id`: Vehicle being serviced
- `workshop_id`: Workshop performing service
- `scheduled_date`: Confirmed date/time
- `proposed_price_amount`: Proposed price amount
- `proposed_price_currency`: Currency code
- `final_price_amount`: Final price amount
- `final_price_currency`: Currency code
- `status`: Booking status
- `description`: Service description
- `completed_at`: Completion timestamp
- `picked_up_at`: Pickup confirmation timestamp
- `cancelled_at`: Cancellation timestamp
- `cancelled_by_id`: User who cancelled
- `cancellation_reason`: Cancellation reason
- `created_at`, `updated_at`: Audit fields

### service_booking_services
- `service_booking_id`: Foreign key to service_bookings
- `service`: ServiceCatalog enum value

## Key Flows

### 1. Service Request Flow
1. User creates a service request with location, radius, and requested services
2. System finds matching workshops using MatchingService
3. Workshops can view available requests and send offers
4. User receives offers and can accept/reject them
5. When an offer is accepted, a ServiceBooking is created

### 2. Offer Flow
1. Workshop creates an offer with proposed price and date
2. Offer expires automatically after 3 days if not accepted
3. User can accept or reject the offer
4. When accepted, other pending offers are automatically rejected
5. Notifications are sent to both parties

### 3. Service Booking Flow
1. After offer acceptance, schedule must be confirmed
2. Both parties can propose schedule changes (mediación)
3. Workshop marks service as completed
4. Maintenance record is created (PENDING_CONFIRMATION)
5. User confirms pickup
6. Booking is marked as PICKED_UP (final state)

### 4. Matching Flow
1. User provides location, radius, and requested services
2. System finds all active workshops
3. Filters by distance (within radius)
4. Filters by services offered
5. Filters by rating (if specified)
6. Calculates match score
7. Returns top 20 results ordered by score

## Security Considerations

- Users can only access their own service requests and bookings
- Workshops can only access their own offers and bookings
- Vehicle ownership is validated through VehicleFacade
- Workshop context is validated through WorkshopContext (multitenancy)

## Error Handling

- `ServiceRequestNotFoundException`: When a service request is not found
- `OfferNotFoundException`: When an offer is not found
- `ServiceBookingNotFoundException`: When a service booking is not found
- `InvalidOfferStatusException`: When an operation is attempted on an offer with invalid status
- `InvalidServiceRequestStatusException`: When an operation is attempted on a request with invalid status

## Future Enhancements

- Real-time notifications (push notifications)
- Advanced matching algorithms (machine learning)
- Service request cancellation fees
- Workshop availability calendar integration
- Service rating and review system
- Payment integration for service completion

