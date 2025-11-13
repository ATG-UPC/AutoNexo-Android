# Workshop Context Improvements

## Overview
This document summarizes all enhancements made to the Workshop bounded context.

## Implemented Features

### 1. Subscription Management
- **Subscription Status**: Enum (ACTIVE, EXPIRED, TRIAL, CANCELLED)
- **Subscription Tier**: Enum (FREE, BASIC, PREMIUM)
- **Subscription Fields**: Added to Workshop aggregate:
  - `subscriptionStatus` (default: TRIAL)
  - `subscriptionTier` (default: FREE)
  - `subscriptionExpiresAt` (nullable)
- **Business Logic**:
  - `isSubscriptionActive()` - Checks if subscription is currently active
  - `canAccessPremiumFeatures()` - Checks if workshop can access premium features
  - `updateSubscription()` - Updates subscription information
- **Endpoints**:
  - `GET /api/v1/workshops/my-workshop/subscription` - Get subscription status
  - `PUT /api/v1/workshops/my-workshop/subscription` - Update subscription (called by Payment BC)

**Files Created:**
- `domain/model/valueobjects/SubscriptionStatus.java`
- `domain/model/valueobjects/SubscriptionTier.java`
- `domain/model/commands/UpdateSubscriptionCommand.java`
- `domain/model/queries/GetWorkshopSubscriptionQuery.java`
- `interfaces/rest/resources/SubscriptionResource.java`
- `interfaces/rest/resources/UpdateSubscriptionResource.java`

**Files Modified:**
- `domain/model/aggregates/Workshop.java` - Added subscription fields and methods
- `domain/services/WorkshopCommandService.java` - Added UpdateSubscriptionCommand handler
- `application/internal/commandservices/WorkshopCommandServiceImpl.java` - Implemented handler
- `interfaces/rest/WorkshopController.java` - Added subscription endpoints

## Database Schema Changes
When recreating the database, ensure the `workshop` table includes these columns:
- `subscription_status` VARCHAR(20) NOT NULL DEFAULT 'TRIAL'
- `subscription_tier` VARCHAR(20) NOT NULL DEFAULT 'FREE'
- `subscription_expires_at` DATETIME NULL

## API Endpoints Summary

### Authenticated Endpoints (require JWT token with workshop context)
- `GET /api/v1/workshops/my-workshop/subscription` - Get subscription status
- `PUT /api/v1/workshops/my-workshop/subscription` - Update subscription

## Integration Points

### With Payment BC (Pending)
- Payment BC will call `PUT /api/v1/workshops/my-workshop/subscription` to update subscription status after payment processing

## Business Rules
- New workshops start with TRIAL status and FREE tier
- Subscription is considered active if:
  - Status is ACTIVE or TRIAL
  - Status is not CANCELLED or EXPIRED
  - Expiration date (if set) is in the future
- Premium features are accessible if:
  - Subscription is active
  - Tier is BASIC or PREMIUM

## Future Enhancements (Pending)
- Workshop schedule/agenda management (for Matching & Booking BC integration)
- Enhanced CRUD operations (update/delete for locations, service templates, staff members)
- Staff permissions system

