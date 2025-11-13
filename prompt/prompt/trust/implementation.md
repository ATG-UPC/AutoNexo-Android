# Trust & Reputation Context - Implementation Documentation

## Overview

The Trust & Reputation Context implements a bidirectional review system between car owners and workshops, with weighted trust score calculation, time-limited review windows, and a basic reporting system for moderation.

## Domain Model

### Value Objects

1. **Rating**: Represents a 1-5 star rating
   - Validates range (1-5)
   - Immutable record
   
2. **ReviewStatus**: Enum for review lifecycle
   - `PENDING_WINDOW`: Service not yet completed
   - `AVAILABLE`: Review can be submitted (within 14 days)
   - `EXPIRED`: Review window has passed
   - `SUBMITTED`: Review has been submitted

3. **ReviewType**: Enum for review direction
   - `USER_TO_WORKSHOP`: Car owner reviewing workshop
   - `WORKSHOP_TO_USER`: Workshop reviewing car owner

4. **ReportReason**: Enum for report categories
   - `INAPPROPRIATE_CONTENT`
   - `SPAM`
   - `FAKE_REVIEW`
   - `OFFENSIVE_LANGUAGE`
   - `OTHER`

5. **ReportStatus**: Enum for report lifecycle
   - `PENDING`: Awaiting moderation
   - `REVIEWED`: Acted upon
   - `DISMISSED`: Deemed invalid

### Aggregates

#### Review (Aggregate Root)
The main aggregate representing a review for a service booking.

**Attributes:**
- `serviceBookingId`: Reference to the service booking
- `reviewerId`: User who wrote the review
- `revieweeUserId`: User being reviewed (nullable)
- `revieweeWorkshopId`: Workshop being reviewed (nullable)
- `reviewType`: Direction of the review
- `rating`: 1-5 star rating
- `comment`: Optional text comment (max 1000 chars)
- `reviewStatus`: Current status
- `submittedAt`: When review was submitted
- `windowExpiresAt`: When review window expires (14 days)
- `reports`: Collection of reports filed against this review

**Business Rules:**
1. Review can only be created for COMPLETED or CANCELLED services
2. Only one review per reviewer per service
3. Review must be submitted within 14 days of service completion
4. Rating is mandatory, comment is optional
5. Window expiration is automatic via scheduled task

**Methods:**
- `submit(rating, comment)`: Submit the review with rating and comment
- `expire()`: Mark review as expired
- `addReport(reporterId, reason, description)`: Add a report to this review

#### ReviewReport (Entity)
Represents a report filed against a review.

**Attributes:**
- `reviewId`: Reference to the review
- `reporterId`: User who filed the report
- `reason`: Reason for the report
- `description`: Optional detailed description
- `status`: Current status
- `reportedAt`: When report was filed

## Application Layer

### Services

#### ReviewCommandService
Handles all command operations for reviews.

**Operations:**
1. `handle(CreateReviewCommand)`:
   - Validates service booking status
   - Checks for duplicate reviews
   - Validates review window
   - Detects review type (user→workshop or workshop→user)
   - Creates and submits review
   - Triggers trust score recalculation

2. `handle(ReportReviewCommand)`:
   - Validates review exists
   - Creates report for review
   - No automatic action (for demo purposes)

3. `expireAvailableReviews()`:
   - Finds all available reviews past expiration
   - Marks them as expired
   - Called by scheduled task

#### ReviewQueryService
Handles all query operations for reviews.

**Operations:**
1. `handle(GetReviewByServiceBookingAndReviewerQuery)`: Find specific review
2. `handle(GetWorkshopReviewsQuery)`: Get paginated workshop reviews
3. `handle(GetUserReviewsQuery)`: Get paginated user reviews
4. `handle(GetServiceBookingReviewsQuery)`: Get both reviews for a service
5. `handle(GetReviewWindowStatusQuery)`: Check if user can create review
6. `handle(GetReviewReportsQuery)`: Get reports for a review

#### TrustScoreService
Handles trust score calculation and management.

**Trust Score Algorithm:**
The weighted algorithm assigns different weights based on review age:
- 0-6 months: 100% weight (1.0)
- 6-12 months: 70% weight (0.7)
- 12-24 months: 30% weight (0.3)
- >24 months: 10% weight (0.1)

Formula:
```
Trust Score = Σ(rating × weight) / Σ(weight)
```

**Requirements:**
- Minimum 3 reviews to display trust score
- Score range: 0.0 to 5.0
- Recalculated weekly + on each new review

**Operations:**
1. `calculateAndUpdateWorkshopTrustScore(workshopId)`: Calculate and persist workshop score
2. `calculateAndUpdateUserTrustScore(userId)`: Calculate and persist user score
3. `recalculateAllWorkshopTrustScores()`: Batch recalculation (scheduled)
4. `recalculateAllUserTrustScores()`: Batch recalculation (scheduled)

## Infrastructure Layer

### Repositories

#### ReviewRepository
JPA repository with custom queries:
- `findByServiceBookingIdAndReviewerId`: Check for existing review
- `findByRevieweeWorkshopId`: Get workshop reviews (paginated)
- `findByRevieweeUserId`: Get user reviews (paginated)
- `findByServiceBookingId`: Get both reviews for a service
- `findRecentByRevieweeWorkshopId`: Get reviews within time range
- `findRecentByRevieweeUserId`: Get reviews within time range
- `findExpiredAvailableReviews`: Find reviews to expire
- `findAllWorkshopIdsWithReviews`: For batch recalculation
- `findAllUserIdsWithReviews`: For batch recalculation

**Performance Considerations:**
- Indexed on `serviceBookingId`, `reviewerId`
- Indexed on `revieweeWorkshopId`, `revieweeUserId`
- Indexed on `windowExpiresAt` for scheduled task
- Indexed on `submittedAt` for weighted calculations

## Interface Layer

### REST Controllers

#### ReviewController
Endpoints for review management:

1. `POST /api/reviews`: Create a review
   - Body: `{ serviceBookingId, rating, comment? }`
   - Auto-detects review type based on current user
   - Returns: Created review

2. `GET /api/reviews/service-bookings/{id}`: Get both reviews for a service
   - Returns: List of reviews (0-2)

3. `GET /api/reviews/my-reviews`: Get reviews I've written
   - Params: `page`, `size`
   - Returns: Paginated reviews

4. `GET /api/reviews/received/workshops/{id}`: Get workshop reviews
   - Params: `status?`, `page`, `size`
   - Returns: Paginated reviews

5. `GET /api/reviews/received/users/{id}`: Get user reviews
   - Params: `status?`, `page`, `size`
   - Returns: Paginated reviews

6. `GET /api/reviews/window-status`: Check review window status
   - Params: `serviceBookingId`
   - Returns: Window status info

7. `POST /api/reviews/{id}/report`: Report a review
   - Body: `{ reason, description? }`
   - Returns: Created report

8. `GET /api/reviews/{id}/reports`: Get reports for a review
   - Returns: List of reports

#### TrustScoreController
Endpoints for trust score queries:

1. `GET /api/trust-score/workshops/{id}`: Get workshop trust score
   - Returns: `{ trustScore, totalReviews, recentReviews, averageRating, hasMinimumReviews }`

2. `GET /api/trust-score/users/{id}`: Get user trust score
   - Returns: Same structure

3. `GET /api/trust-score/my-score`: Get current user's trust score
   - Auto-detects if user or workshop
   - Returns: Same structure

## Scheduled Tasks

### ReviewWindowExpirationService
- **Schedule:** Daily at 3:00 AM
- **Purpose:** Expire reviews that have passed their 14-day window
- **Logic:**
  1. Find all reviews with status `AVAILABLE` and `windowExpiresAt < now`
  2. Call `expire()` on each review
  3. Persist changes
  4. Log count of expired reviews

### TrustScoreRecalculationService
- **Schedule:** Weekly on Sundays at 2:00 AM
- **Purpose:** Recalculate all trust scores to account for time-based weight changes
- **Logic:**
  1. Get all workshop IDs with reviews
  2. Recalculate trust score for each workshop
  3. Get all user IDs with reviews
  4. Recalculate trust score for each user
  5. Log counts of updated entities

## Anti-Corruption Layers (ACL)

### ServiceBookingFacade
Exposes Matching & Booking operations to Trust context:
- `getServiceBookingInfo(serviceBookingId)`: Get booking details
- `validateUserCanReview(serviceBookingId, userId)`: Check user authorization
- `validateWorkshopCanReview(serviceBookingId, workshopId)`: Check workshop authorization

### WorkshopContextFacade
Updated to support trust score:
- `updateWorkshopTrustScore(workshopId, trustScore)`: Update workshop's trust score
- `workshopExists(workshopId)`: Check workshop existence

### IamFacade
New facade for IAM operations:
- `updateUserTrustScore(userId, trustScore)`: Update user's trust score
- `getUserTrustScore(userId)`: Get current trust score
- `userExists(userId)`: Check user existence

## Database Schema

### reviews
```sql
CREATE TABLE reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    service_booking_id BIGINT NOT NULL,
    reviewer_user_id BIGINT NOT NULL,
    reviewee_user_id BIGINT NULL,
    reviewee_workshop_id BIGINT NULL,
    review_type VARCHAR(30) NOT NULL,
    rating INTEGER NULL,
    comment VARCHAR(1000) NULL,
    review_status VARCHAR(20) NOT NULL,
    submitted_at TIMESTAMP NULL,
    window_expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    
    INDEX idx_service_booking_reviewer (service_booking_id, reviewer_user_id),
    INDEX idx_reviewee_workshop (reviewee_workshop_id),
    INDEX idx_reviewee_user (reviewee_user_id),
    INDEX idx_window_expires (window_expires_at),
    INDEX idx_submitted_at (submitted_at)
);
```

### review_reports
```sql
CREATE TABLE review_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    review_id BIGINT NOT NULL,
    reporter_user_id BIGINT NOT NULL,
    reason VARCHAR(30) NOT NULL,
    description VARCHAR(500) NULL,
    status VARCHAR(20) NOT NULL,
    reported_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    
    FOREIGN KEY (review_id) REFERENCES reviews(id),
    INDEX idx_review (review_id)
);
```

### users (updated)
Added field:
```sql
ALTER TABLE users ADD COLUMN trust_score FLOAT NULL;
```

### workshops (already had this field)
```sql
trust_score FLOAT NULL
```

## Key Business Flows

### Create Review Flow
1. User/workshop submits review via REST API
2. System validates service booking status (must be COMPLETED or CANCELLED)
3. System checks for duplicate review (only one per reviewer per service)
4. System validates review window (must be within 14 days)
5. System determines review type based on current user's role
6. System creates Review aggregate with AVAILABLE status
7. System immediately submits review with rating and comment
8. Review status changes to SUBMITTED
9. System triggers trust score recalculation for reviewee
10. System returns created review to client

### Trust Score Calculation Flow
1. Triggered by new review or weekly scheduled task
2. System fetches all submitted reviews for entity (workshop or user)
3. System filters reviews from last 3 years
4. For each review:
   - Calculate age in months
   - Assign weight based on age (100%, 70%, 30%, or 10%)
   - Multiply rating by weight
5. Calculate weighted average: Σ(rating × weight) / Σ(weight)
6. If less than 3 reviews, set trust score to null
7. Persist trust score to entity (Workshop or User)
8. Return trust score result with statistics

### Review Expiration Flow
1. Scheduled task runs daily at 3 AM
2. System finds all reviews with:
   - Status = AVAILABLE
   - windowExpiresAt < now
3. For each review:
   - Call `expire()` method
   - Status changes to EXPIRED
4. System persists changes
5. System logs count of expired reviews

## Testing Considerations

### Unit Tests
1. Review aggregate business rules:
   - Cannot submit expired review
   - Cannot submit twice
   - Rating validation
   
2. Trust score calculation:
   - Weighted average algorithm
   - Edge cases (no reviews, 1-2 reviews)
   - Time-based weight decay

3. Review window validation:
   - Within window
   - Past window
   - Service not completed

### Integration Tests
1. End-to-end review creation flow
2. Bidirectional reviews (both user and workshop)
3. Trust score recalculation after new review
4. Scheduled task execution
5. ACL facade interactions

## Security Considerations

1. **Authorization:**
   - Users can only review services they participated in
   - Workshops can only review services they provided
   - Reports can be filed by any authenticated user

2. **Validation:**
   - Service booking ownership verified via ACL
   - Review window enforced at domain level
   - Duplicate reviews prevented

3. **Data Privacy:**
   - Reviews are public within the platform
   - Reports are visible only to moderators (future feature)

## Performance Optimizations

1. **Database Indexes:**
   - Composite index on (service_booking_id, reviewer_user_id) for duplicate check
   - Index on reviewee IDs for trust score queries
   - Index on window_expires_at for scheduled task

2. **Caching Strategy:**
   - Trust scores are denormalized in Workshop and User entities
   - Recalculated weekly + on new review
   - No real-time calculation on read

3. **Batch Operations:**
   - Scheduled tasks process in batches
   - Error handling per entity (one failure doesn't stop batch)

## Future Enhancements

1. **Moderation System:**
   - Admin dashboard for review reports
   - Automated spam detection
   - User/workshop suspension for abuse

2. **Enhanced Algorithm:**
   - Consider cancellation rate in workshop trust score
   - Penalize workshops with many rejected offers
   - Bonus for consistent positive reviews

3. **Notifications:**
   - Email when review is received
   - Reminder to review after service completion
   - Alert when trust score changes significantly

4. **Analytics:**
   - Trust score trends over time
   - Review response rates
   - Common report reasons

## Related Contexts

- **Matching & Booking:** Source of ServiceBooking data for review validation
- **Workshop:** Stores workshop trust scores
- **IAM:** Stores user trust scores
- **Notifications:** Could trigger email notifications (future)

## API Examples

### Create Review
```http
POST /api/reviews
Content-Type: application/json
Authorization: Bearer {token}

{
  "serviceBookingId": 123,
  "rating": 5,
  "comment": "Excellent service! Very professional."
}

Response 201:
{
  "id": 456,
  "serviceBookingId": 123,
  "reviewerId": 789,
  "revieweeWorkshopId": 12,
  "reviewType": "USER_TO_WORKSHOP",
  "rating": 5,
  "comment": "Excellent service! Very professional.",
  "status": "SUBMITTED",
  "submittedAt": "2024-01-15T10:30:00",
  "windowExpiresAt": "2024-01-29T10:00:00",
  "reportsCount": 0,
  "createdAt": "2024-01-15T10:30:00"
}
```

### Get Trust Score
```http
GET /api/trust-score/workshops/12
Authorization: Bearer {token}

Response 200:
{
  "trustScore": 4.75,
  "totalReviews": 24,
  "recentReviews": 8,
  "averageRating": 4.75,
  "hasMinimumReviews": true
}
```

### Report Review
```http
POST /api/reviews/456/report
Content-Type: application/json
Authorization: Bearer {token}

{
  "reason": "SPAM",
  "description": "This review appears to be fake"
}

Response 201:
{
  "id": 789,
  "reviewId": 456,
  "reporterId": 111,
  "reason": "SPAM",
  "description": "This review appears to be fake",
  "status": "PENDING",
  "reportedAt": "2024-01-16T14:20:00"
}
```

## Conclusion

The Trust & Reputation Context provides a complete bidirectional review system with intelligent trust score calculation. The weighted algorithm ensures recent reviews have more impact, while the 14-day window balances recency with user convenience. The reporting system lays the foundation for future moderation features, and the scheduled tasks ensure data stays fresh and accurate.

