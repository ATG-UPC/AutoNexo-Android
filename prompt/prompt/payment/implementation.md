# Payment Context Implementation

## Overview

The **Payment Context** is a lightweight bounded context responsible for managing **workshop subscription payments** within the AutoNexo platform. It handles the creation, processing, and tracking of payments for subscription tiers (BASIC, PREMIUM, ENTERPRISE), maintaining a complete payment history for each workshop.

**Important Note:** This context is designed for **demo purposes** and does not integrate with real payment gateways. All payment processing is simulated.

## Scope

- **In Scope:**
  - Subscription payment creation and processing
  - Payment status management (PENDING, COMPLETED, FAILED, REFUNDED, CANCELLED)
  - Payment history and billing information
  - Workshop subscription updates after successful payments
  - Upcoming renewal tracking

- **Out of Scope:**
  - Service-based payments between workshops and car owners
  - Integration with real payment gateways (Stripe, PayPal, etc.)
  - Automatic recurring billing
  - Discount codes or promotional pricing
  - Trial periods
  - Prorated charges for plan changes

## Architecture

### Domain Model

#### Value Objects

1. **PaymentMethod**
   - Enum: `CREDIT_CARD`, `DEBIT_CARD`, `BANK_TRANSFER`, `DIGITAL_WALLET`
   - Represents the payment method used for a transaction

2. **PaymentStatus**
   - Enum: `PENDING`, `COMPLETED`, `FAILED`, `REFUNDED`, `CANCELLED`
   - Tracks the lifecycle of a payment

3. **SubscriptionPaymentType**
   - Enum: `NEW_SUBSCRIPTION`, `RENEWAL`, `UPGRADE`, `DOWNGRADE`
   - Categorizes the type of subscription payment

#### Aggregates

**Payment (Aggregate Root)**

Represents a subscription payment made by a workshop.

**Attributes:**
- `id`: Primary key
- `workshopId`: Workshop identifier
- `subscriptionTier`: Target subscription tier
- `amount`: Payment amount (Money value object)
- `paymentMethod`: Method used for payment
- `paymentType`: Type of payment (new, renewal, upgrade, downgrade)
- `status`: Current payment status
- `paymentDate`: Date when payment was processed
- `transactionId`: Unique transaction identifier (generated)
- `description`: Payment description
- `billingPeriodStart`: Start date of billing period
- `billingPeriodEnd`: End date of billing period
- `nextBillingDate`: Date of next billing (typically end of current period)
- `invoiceUrl`: URL to invoice (optional, for future use)
- `createdAt`: Creation timestamp
- `updatedAt`: Last update timestamp

**Business Rules:**
1. Payments are created in `PENDING` status
2. Only `COMPLETED` payments update workshop subscriptions
3. Only `PENDING` payments can be completed or failed
4. Only `COMPLETED` payments can be refunded
5. Only `PENDING` payments can be cancelled
6. Each payment generates a unique transaction ID

**Key Methods:**
- `complete()`: Mark payment as completed and update subscription
- `fail()`: Mark payment as failed
- `refund()`: Refund a completed payment
- `cancel()`: Cancel a pending payment

### Application Layer

#### Command Services

**PaymentCommandService**

Handles all payment state-changing operations:
- `handle(CreateSubscriptionPaymentCommand)`: Create new payment with calculated pricing
- `handle(CompletePaymentCommand)`: Complete payment and trigger subscription update
- `handle(FailPaymentCommand)`: Mark payment as failed (for testing)
- `handle(RefundPaymentCommand)`: Refund completed payment
- `handle(CancelPaymentCommand)`: Cancel pending payment

**Key Logic:**
- Automatically calculates subscription price based on tier:
  - BASIC: $0.00 USD
  - PREMIUM: $29.99 USD
  - ENTERPRISE: $99.99 USD
- Generates billing period (1 month from creation)
- Creates unique transaction ID
- Updates workshop subscription via ACL after successful payment

#### Query Services

**PaymentQueryService**

Handles all payment data retrieval operations:
- `handle(GetPaymentByIdQuery)`: Get payment details
- `handle(GetWorkshopPaymentsQuery)`: Get paginated payment history for a workshop
- `handle(GetPendingPaymentsQuery)`: Get all pending payments (admin use)
- `handle(GetUpcomingRenewalsQuery)`: Get upcoming renewals within date range

### Infrastructure Layer

#### Repositories

**PaymentRepository**

JPA repository with custom queries:
- `findByWorkshopId(workshopId, pageable)`: Paginated workshop payments
- `findByStatus(status)`: Find payments by status
- `findUpcomingRenewals(from, to)`: Find renewals in date range
- `findByWorkshopIdAndStatus(workshopId, status, pageable)`: Filter by workshop and status
- `findLatestCompletedPaymentByWorkshopId(workshopId)`: Get most recent completed payment

**Database Schema:**

```sql
CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    workshop_id BIGINT NOT NULL,
    subscription_tier VARCHAR(20) NOT NULL,
    amount_value DECIMAL(10,2) NOT NULL,
    amount_currency VARCHAR(3) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    payment_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    payment_date TIMESTAMP NULL,
    transaction_id VARCHAR(100) NULL,
    description VARCHAR(500) NULL,
    billing_period_start DATE NOT NULL,
    billing_period_end DATE NOT NULL,
    next_billing_date DATE NULL,
    invoice_url VARCHAR(500) NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    
    INDEX idx_workshop_id (workshop_id),
    INDEX idx_status (status),
    INDEX idx_next_billing_date (next_billing_date)
);
```

### Interface Layer

#### REST Controllers

**PaymentController** (`/api/v1/payments`)

Manages payment operations:

1. **POST /subscriptions**
   - Create new subscription payment
   - Auth: `WORKSHOP_MANAGER`
   - Body: `CreateSubscriptionPaymentResource`
   - Returns: `PaymentResource` (HTTP 201)

2. **GET /{id}**
   - Get payment details
   - Auth: `WORKSHOP_MANAGER`, `ADMIN`
   - Returns: `PaymentResource`

3. **GET /my-payments**
   - Get paginated payment history for current workshop
   - Auth: `WORKSHOP_MANAGER`
   - Params: `page`, `size`
   - Returns: `Page<PaymentResource>`

4. **POST /{id}/complete**
   - Complete pending payment (simulated)
   - Auth: `WORKSHOP_MANAGER`, `ADMIN`
   - Returns: `PaymentResource`

5. **POST /{id}/fail**
   - Mark payment as failed (testing only)
   - Auth: `ADMIN`
   - Returns: `PaymentResource`

6. **POST /{id}/refund**
   - Refund completed payment
   - Auth: `WORKSHOP_MANAGER`, `ADMIN`
   - Params: `reason` (optional)
   - Returns: `PaymentResource`

7. **POST /{id}/cancel**
   - Cancel pending payment
   - Auth: `WORKSHOP_MANAGER`, `ADMIN`
   - Returns: `PaymentResource`

**SubscriptionBillingController** (`/api/v1/billing`)

Manages billing information:

1. **GET /upcoming-renewals**
   - Get upcoming subscription renewals
   - Auth: `WORKSHOP_MANAGER`, `ADMIN`
   - Params: `daysAhead` (default: 7)
   - Returns: `List<UpcomingRenewalResource>`

2. **GET /history**
   - Get billing history for current workshop
   - Auth: `WORKSHOP_MANAGER`
   - Params: `page`, `size`
   - Returns: `Page<PaymentSummaryResource>`

#### REST Resources

1. **CreateSubscriptionPaymentResource**: Input for creating payments
2. **PaymentResource**: Complete payment information
3. **PaymentSummaryResource**: Summarized payment view
4. **UpcomingRenewalResource**: Renewal information with days until renewal

### ACL (Anti-Corruption Layer)

**WorkshopContextFacade**

New method added for payment integration:
- `updateSubscription(workshopId, tier, status, expiresAt)`: Updates workshop subscription after successful payment

This method is called automatically when a payment is completed, ensuring the workshop's subscription tier, status, and expiration date are updated accordingly.

## Key Business Flows

### 1. Create Subscription Payment Flow

```
Workshop Manager → POST /api/v1/payments/subscriptions
    ↓
PaymentCommandService.handle(CreateSubscriptionPaymentCommand)
    ↓
Calculate subscription price based on tier
    ↓
Generate billing period (1 month from today)
    ↓
Create Payment aggregate (status: PENDING)
    ↓
Generate unique transaction ID
    ↓
Save payment to repository
    ↓
Return PaymentResource (HTTP 201)
```

### 2. Complete Payment Flow

```
User → POST /api/v1/payments/{id}/complete
    ↓
PaymentCommandService.handle(CompletePaymentCommand)
    ↓
Load Payment aggregate
    ↓
Validate status is PENDING
    ↓
Mark as COMPLETED
    ↓
Update payment date
    ↓
Save payment
    ↓
Call WorkshopContextFacade.updateSubscription()
    ↓
Update workshop tier, status, expiration date
    ↓
Return PaymentResource
```

### 3. Refund Payment Flow

```
User → POST /api/v1/payments/{id}/refund
    ↓
PaymentCommandService.handle(RefundPaymentCommand)
    ↓
Load Payment aggregate
    ↓
Validate status is COMPLETED
    ↓
Mark as REFUNDED
    ↓
Save payment
    ↓
(Optional: Downgrade subscription)
    ↓
Return PaymentResource
```

### 4. Upcoming Renewals Flow

```
User → GET /api/v1/billing/upcoming-renewals?daysAhead=7
    ↓
PaymentQueryService.handle(GetUpcomingRenewalsQuery)
    ↓
Query payments with nextBillingDate in range
    ↓
Filter by status = COMPLETED
    ↓
Calculate days until renewal for each
    ↓
Return List<UpcomingRenewalResource>
```

## Pricing Structure

The following simulated pricing is used for subscription tiers:

| Tier       | Monthly Price | Description                    |
|------------|---------------|--------------------------------|
| BASIC      | $0.00 USD     | Free tier with basic features  |
| PREMIUM    | $29.99 USD    | Enhanced features for SMBs     |
| ENTERPRISE | $99.99 USD    | Full feature set for large ops |

All amounts are stored in the `Money` value object with currency code "USD".

## Demo Simulation

Since this is a demo implementation without real payment gateway integration:

1. **Instant Processing**: Payments can be completed immediately via the `/complete` endpoint
2. **Manual Control**: Payments can be marked as failed for testing scenarios
3. **Transaction IDs**: Generated in format `TXN-XXXXXXXX` (8 random uppercase hex characters)
4. **No Webhooks**: No external callbacks or webhook handling
5. **No Recurring Billing**: Renewals must be manually created

## Security Considerations

- All payment endpoints require authentication
- Workshop managers can only access their own payment data
- Admin role required for testing operations (fail payment)
- Payment completion triggers automatic subscription update
- Multitenancy ensured via `WorkshopContext`

## Future Enhancements (Out of Current Scope)

1. **Real Payment Gateway Integration**
   - Integrate with Stripe, PayPal, or similar
   - Webhook handling for payment confirmations
   - Secure tokenization of payment methods

2. **Automatic Recurring Billing**
   - Scheduled jobs to create renewal payments
   - Automatic charge attempts on due dates
   - Email notifications for upcoming charges

3. **Advanced Pricing Features**
   - Discount codes and promotions
   - Trial periods for new workshops
   - Prorated charges for mid-month upgrades/downgrades
   - Annual billing options with discounts

4. **Invoice Generation**
   - PDF invoice generation
   - Automatic invoice delivery via email
   - Tax calculation and compliance

5. **Payment Analytics**
   - Revenue reports and dashboards
   - Churn analysis
   - Subscription metrics (MRR, LTV, etc.)

## Testing Considerations

For testing purposes, the following endpoints allow manual control:

1. **Complete Payment**: Simulate successful payment processing
2. **Fail Payment**: Simulate payment failure scenarios
3. **Refund Payment**: Test refund flows and subscription adjustments
4. **Cancel Payment**: Test cancellation logic

## Related Contexts

- **Workshop Context**: Receives subscription updates after successful payments
- **IAM Context**: Authentication and authorization for payment operations
- **Notifications Context** (Future): Email notifications for payment events

## API Examples

### Create Payment

```http
POST /api/v1/payments/subscriptions
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "workshopId": 1,
  "subscriptionTier": "PREMIUM",
  "paymentMethod": "CREDIT_CARD",
  "paymentType": "NEW_SUBSCRIPTION",
  "description": "Premium subscription for Workshop ABC"
}
```

### Complete Payment (Simulated)

```http
POST /api/v1/payments/123/complete
Authorization: Bearer {jwt_token}
```

### Get Billing History

```http
GET /api/v1/billing/history?page=0&size=10
Authorization: Bearer {jwt_token}
```

### Get Upcoming Renewals

```http
GET /api/v1/billing/upcoming-renewals?daysAhead=30
Authorization: Bearer {jwt_token}
```

## Conclusion

The Payment Context provides a foundational subscription payment system for the AutoNexo platform. While designed for demo purposes without real payment gateway integration, its architecture is clean and extensible, allowing for future integration with actual payment processors when needed.

The context maintains clear boundaries with other bounded contexts through the ACL pattern, ensuring that payment processing logic remains isolated and that subscription updates are properly propagated to the Workshop context.

