# Notifications Context Implementation

## Overview
The Notifications bounded context handles all email notifications in the Autonexo application. It uses JavaMailSender (Spring Mail) to send HTML emails for various purposes including password reset, email verification, and workshop invitations.

## Architecture

### Domain Layer
- **EmailService**: Core domain service interface for sending emails
- **EmailTemplate**: Enum defining supported email template types

### Application Layer
- **EmailServiceImpl**: Implementation of EmailService using JavaMailSender
  - Uses EmailTemplateService to load and process HTML templates
  - Handles email formatting and error handling

### Infrastructure Layer
- **EmailProperties**: Configuration properties for email settings (from address, base URL)
- **EmailTemplateService**: Service for loading and processing email templates from resources
  - Templates are stored in `src/main/resources/templates/emails/`
  - Templates use placeholder syntax: `{variableName}`
  - Templates are cached in memory for performance

### Interfaces (ACL)
- **IamNotificationsFacade**: Implements IAM's NotificationService interface
  - Bridges IAM context with Notifications BC
  - Handles password reset and email verification emails
  
- **WorkshopNotificationsFacade**: Implements Workshop's NotificationService interface
  - Bridges Workshop context with Notifications BC
  - Handles workshop invitation emails

## Email Templates

Email templates are stored as HTML files in `src/main/resources/templates/emails/` directory. Templates use placeholder syntax `{variableName}` which are replaced at runtime.

### Template Files
- `password-reset.html` - Password reset email template
- `email-verification.html` - Email verification template
- `workshop-invitation.html` - Workshop invitation template
- `invitation-expired.html` - Invitation expired notification template

### Template Variables

#### Password Reset (`password-reset.html`)
- **Subject**: "Password Reset Request - Autonexo"
- **Variables**: `{resetUrl}`
- **Expiration**: 24 hours (handled by IAM context)

#### Email Verification (`email-verification.html`)
- **Subject**: "Verify Your Email Address - Autonexo"
- **Variables**: `{verificationUrl}`
- **Expiration**: 72 hours (handled by IAM context)

#### Workshop Invitation (`workshop-invitation.html`)
- **Subject**: "You've been invited to join {workshopName} - Autonexo"
- **Variables**: `{invitationCode}`, `{workshopName}`, `{invitationUrl}`
- **Expiration**: 7 days (handled by Workshop context)

#### Invitation Expired (`invitation-expired.html`)
- **Subject**: "Invitation Expired - {workshopName} - Autonexo"
- **Variables**: `{workshopName}`

## Configuration

### Email Server Configuration
Configure SMTP settings in `application-dev.properties`:

```properties
# Email configuration (JavaMailSender)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USERNAME:your-email@gmail.com}
spring.mail.password=${EMAIL_PASSWORD:your-app-password}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# Application email settings
autonexo.email.from=noreply@autonexo.com
autonexo.email.base-url=http://localhost:8080
```

### Gmail Setup
1. Enable 2-factor authentication on your Google account
2. Generate an "App Password" at https://myaccount.google.com/apppasswords
3. Use the app password in the `EMAIL_PASSWORD` environment variable
4. Set `EMAIL_USERNAME` to your Gmail address

### Other SMTP Providers

#### SendGrid
```properties
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=${SENDGRID_API_KEY}
```

#### AWS SES
```properties
spring.mail.host=email-smtp.{region}.amazonaws.com
spring.mail.port=587
spring.mail.username=${AWS_SES_USERNAME}
spring.mail.password=${AWS_SES_PASSWORD}
```

#### Custom SMTP
```properties
spring.mail.host=your-smtp-server.com
spring.mail.port=587
spring.mail.username=${SMTP_USERNAME}
spring.mail.password=${SMTP_PASSWORD}
```

## Integration Points

### IAM Context
- **Password Reset**: When a user requests password reset, IAM generates a token and calls `NotificationService.sendPasswordResetToken()`
- **Email Verification**: When a user signs up, IAM generates a verification token and calls `NotificationService.sendEmailVerificationToken()`

### Workshop Context
- **Staff Invitation**: When a workshop creates an invitation, it calls `NotificationService.sendInvitationEmail()`
- **Invitation Expired**: When an invitation expires, it calls `NotificationService.sendInvitationExpiredNotification()`

## Usage Example

```java
// In IAM context
@Autowired
private NotificationService notificationService;

public void handlePasswordReset(String email, String token) {
    notificationService.sendPasswordResetToken(email, token);
}

// In Workshop context
@Autowired
private NotificationService notificationService;

public void handleInvitation(String email, String code, String workshopName) {
    notificationService.sendInvitationEmail(email, code, workshopName);
}
```

## Error Handling

The EmailServiceImpl logs errors but does not throw exceptions that would break the calling context's transaction. This ensures that:
- Password reset requests succeed even if email fails
- User signups succeed even if verification email fails
- Invitation creation succeeds even if email fails

Errors are logged with full stack traces for debugging.

## Future Enhancements

1. **SMS Support**: Add SMS notification capabilities using Twilio or similar service
2. **Push Notifications**: Add push notification support for mobile apps
3. **Email Queue**: Implement async email queue for better performance
4. **Template Engine**: Use Thymeleaf or similar for more flexible email templates
5. **Email Tracking**: Track email opens and link clicks
6. **Notification Preferences**: Allow users to configure notification preferences

## Testing

For development/testing without a real SMTP server:
- Use a mock SMTP server like MailHog or MailCatcher
- Configure to use `localhost:1025` (MailHog default)
- View emails in the web UI at `http://localhost:8025`

## Files Structure

```
notifications/
├── domain/
│   ├── model/
│   │   └── valueobjects/
│   │       └── EmailTemplate.java
│   └── services/
│       └── EmailService.java
├── application/
│   └── internal/
│       └── commandservices/
│           └── EmailServiceImpl.java
├── infrastructure/
│   └── mail/
│       ├── EmailProperties.java
│       └── EmailTemplateService.java
└── interfaces/
    └── acl/
        ├── IamNotificationsFacade.java
        └── WorkshopNotificationsFacade.java

resources/
└── templates/
    └── emails/
        ├── password-reset.html
        ├── email-verification.html
        ├── workshop-invitation.html
        └── invitation-expired.html
```

## Customizing Email Templates

To customize email templates:
1. Edit the HTML files in `src/main/resources/templates/emails/`
2. Use placeholder syntax `{variableName}` for dynamic content
3. Templates are cached in memory - restart the application to see changes
4. For development, you can call `EmailTemplateService.clearCache()` to reload templates

