# IAM Context Improvements

## Overview
This document summarizes all enhancements made to the IAM (Identity and Access Management) bounded context.

## Implemented Features

### 1. Password Recovery System
- **Password Reset Tokens**: Entity to store reset tokens with expiration (24 hours)
- **Request Password Reset**: Endpoint `POST /api/v1/users/forgot-password`
- **Reset Password**: Endpoint `POST /api/v1/users/reset-password`
- **Security**: Prevents email enumeration by always returning success message
- **Integration**: Uses NotificationService interface (mock implementation for now)

**Files Created:**
- `domain/model/entities/PasswordResetToken.java`
- `domain/model/commands/RequestPasswordResetCommand.java`
- `domain/model/commands/ResetPasswordCommand.java`
- `domain/services/PasswordResetService.java`
- `application/internal/commandservices/PasswordResetServiceImpl.java`
- `infrastructure/persistence/jpa/repositories/PasswordResetTokenRepository.java`
- `interfaces/rest/resources/RequestPasswordResetResource.java`
- `interfaces/rest/resources/ResetPasswordResource.java`

### 2. Email Verification System
- **Email Verification Tokens**: Entity to store verification tokens with expiration (72 hours)
- **Automatic Token Generation**: Tokens generated automatically on user signup
- **Resend Verification**: Endpoint `POST /api/v1/users/resend-verification`
- **Verify Email**: Endpoint `POST /api/v1/users/verify-email`
- **Check Status**: Endpoint `GET /api/v1/users/verification-status?email={email}`
- **Integration**: Uses NotificationService interface (mock implementation for now)

**Files Created:**
- `domain/model/entities/EmailVerificationToken.java`
- `domain/model/commands/ResendVerificationCommand.java`
- `domain/model/commands/VerifyEmailCommand.java`
- `domain/services/EmailVerificationService.java`
- `application/internal/commandservices/EmailVerificationServiceImpl.java`
- `infrastructure/persistence/jpa/repositories/EmailVerificationTokenRepository.java`
- `interfaces/rest/resources/ResendVerificationResource.java`
- `interfaces/rest/resources/VerifyEmailResource.java`

### 3. User Profile Management
- **Get Current User**: Endpoint `GET /api/v1/users/me` (requires authentication)
- **Update Profile**: Endpoint `PUT /api/v1/users/me` (requires authentication)
- **Change Password**: Endpoint `PUT /api/v1/users/me/password` (requires authentication)
- **Deactivate Account**: Endpoint `DELETE /api/v1/users/me` (requires authentication)
- **Security**: All endpoints extract user ID from JWT token

**Files Created:**
- `domain/model/commands/UpdateUserProfileCommand.java`
- `domain/model/commands/ChangePasswordCommand.java`
- `domain/model/commands/DeactivateUserCommand.java`
- `domain/model/queries/GetCurrentUserQuery.java`
- `interfaces/rest/resources/UpdateUserProfileResource.java`
- `interfaces/rest/resources/ChangePasswordResource.java`

**Files Modified:**
- `domain/services/UserCommandService.java` - Added new command handlers
- `domain/services/UserQueryService.java` - Added GetCurrentUserQuery handler
- `application/internal/commandservices/UserCommandServiceImpl.java` - Implemented handlers
- `application/internal/queryservices/UserQueryServiceImpl.java` - Implemented query handler
- `interfaces/UsersController.java` - Added new endpoints and helper method `getCurrentUserId()`

### 4. Notification Service Integration
- **Interface**: `application/internal/outboundservices/notifications/NotificationService.java`
- **Mock Implementation**: `application/internal/outboundservices/notifications/MockNotificationServiceImpl.java`
- **Methods**:
  - `sendPasswordResetToken(email, token)`
  - `sendEmailVerificationToken(email, token)`
- **Note**: Mock implementation logs to console. Will be replaced by Notifications BC implementation.

## Database Schema Changes
When recreating the database, ensure these tables are created:
- `password_reset_token` - Stores password reset tokens (see `PasswordResetToken` entity)
- `email_verification_token` - Stores email verification tokens (see `EmailVerificationToken` entity)

## API Endpoints Summary

### Public Endpoints
- `POST /api/v1/users/forgot-password` - Request password reset
- `POST /api/v1/users/reset-password` - Reset password with token
- `POST /api/v1/users/resend-verification` - Resend verification email
- `POST /api/v1/users/verify-email` - Verify email with token
- `GET /api/v1/users/verification-status?email={email}` - Check verification status

### Authenticated Endpoints (require JWT token)
- `GET /api/v1/users/me` - Get current user profile
- `PUT /api/v1/users/me` - Update user profile
- `PUT /api/v1/users/me/password` - Change password
- `DELETE /api/v1/users/me` - Deactivate account

## Integration Points

### With Notifications BC (Pending)
- Password reset token sending
- Email verification token sending

### With Workshop Context
- User association with workshop (already implemented)
- Workshop context extraction from JWT token (already implemented)

## Security Considerations
- Password reset tokens expire after 24 hours
- Email verification tokens expire after 72 hours
- Tokens are single-use (marked as used after consumption)
- Password reset prevents email enumeration
- All authenticated endpoints validate JWT token
- Password change requires current password verification

