# AutoNexo Deployment Guide

## Prerequisites

- **Java**: JDK 17 or higher
- **MySQL**: 8.0 or higher
- **Maven**: 3.6 or higher
- **Cloudinary Account**: For media storage
- **SMTP Service**: For email notifications (Gmail, SendGrid, etc.)

## Environment Variables

### Required Variables

```bash
# Database Configuration
DB_HOST=your_mysql_host
DB_PORT=3306
DB_NAME=autonexo_db
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password

# Optional Database Connection Pool
DB_POOL_SIZE=20
DB_POOL_MIN_IDLE=5
DB_CONNECTION_TIMEOUT=30000
DB_IDLE_TIMEOUT=600000
DB_MAX_LIFETIME=1800000

# JWT Configuration
JWT_SECRET=your_super_secret_jwt_key_minimum_256_bits
JWT_EXPIRATION_DAYS=7

# Cloudinary Configuration
CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret
CLOUDINARY_BASE_URL=autonexo

# Email Configuration (SMTP)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
EMAIL_USERNAME=your_email@gmail.com
EMAIL_PASSWORD=your_app_password
EMAIL_FROM=noreply@autonexo.com
APP_BASE_URL=https://autonexo.com

# Optional Server Configuration
PORT=8080
LOG_FILE=logs/autonexo.log

# Optional Flyway
FLYWAY_ENABLED=false
```

## Development Setup

### 1. Clone Repository

```bash
git clone https://github.com/your-org/autonexo-backend.git
cd autonexo-backend
```

### 2. Setup MySQL Database

```sql
CREATE DATABASE autonexo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configure Environment Variables

Create `.env` file or set environment variables:

```bash
export DB_HOST=localhost
export DB_NAME=autonexo_db
export DB_USERNAME=root
export DB_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret_key_here
export CLOUDINARY_CLOUD_NAME=your_cloud_name
export CLOUDINARY_API_KEY=your_api_key
export CLOUDINARY_API_SECRET=your_api_secret
export EMAIL_USERNAME=your_email@gmail.com
export EMAIL_PASSWORD=your_app_password
```

### 4. Build and Run

```bash
# Build
mvn clean install -DskipTests

# Run with dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Application will start on `http://localhost:8080`

### 5. Access API Documentation

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI Spec: http://localhost:8080/v3/api-docs

## Production Deployment

### 1. Build Production JAR

```bash
mvn clean package -DskipTests -Pprod
```

This creates `target/backend-0.0.1-SNAPSHOT.jar`

### 2. Configure Production Environment

Set all required environment variables in your production environment.

### 3. Run Application

```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 4. Using Docker (Optional)

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
```

Build and run:

```bash
docker build -t autonexo-backend .
docker run -p 8080:8080 --env-file .env autonexo-backend
```

## Cloud Deployment

### AWS Elastic Beanstalk

1. Create Elastic Beanstalk application
2. Upload JAR file
3. Configure environment variables in EB console
4. Deploy

### Heroku

1. Create Heroku app: `heroku create autonexo-backend`
2. Add ClearDB MySQL addon: `heroku addons:create cleardb:ignite`
3. Set environment variables: `heroku config:set JWT_SECRET=...`
4. Deploy: `git push heroku main`

### Azure App Service

1. Create App Service (Java 17)
2. Configure database connection
3. Set application settings (environment variables)
4. Deploy via Maven plugin or Azure CLI

## Database Configuration

### Development
- **Profile**: `dev`
- **DDL Auto**: `create` (recreates schema on startup)
- **Show SQL**: `true`

### Production
- **Profile**: `prod`
- **DDL Auto**: `update` (preserves data)
- **Show SQL**: `false`
- **Connection Pool**: HikariCP with optimized settings

### Initial Setup

On first run, tables are created automatically. No manual migration needed.

## Cloudinary Setup

1. Sign up at https://cloudinary.com
2. Get Cloud Name, API Key, and API Secret from dashboard
3. Set environment variables
4. Create folder structure (optional):
   - `/autonexo/workshops/logos`
   - `/autonexo/workshops/photos`
   - `/autonexo/vehicles/photos`

## Email Configuration

### Gmail Setup

1. Enable 2-Factor Authentication
2. Generate App Password:
   - Go to Google Account → Security → App Passwords
   - Create password for "Mail"
3. Use app password as `EMAIL_PASSWORD`

### SendGrid (Alternative)

```bash
SMTP_HOST=smtp.sendgrid.net
SMTP_PORT=587
EMAIL_USERNAME=apikey
EMAIL_PASSWORD=your_sendgrid_api_key
```

## Security Considerations

### Production Checklist

- ✅ Use strong JWT secret (minimum 256 bits)
- ✅ Enable HTTPS/TLS
- ✅ Secure database credentials
- ✅ Use environment variables (never commit secrets)
- ✅ Configure CORS for specific origins
- ✅ Enable database connection encryption
- ✅ Set up firewall rules
- ✅ Regular security updates
- ✅ Monitor logs for suspicious activity

### CORS Configuration

Edit `WebSecurityConfiguration.java` to restrict allowed origins:

```java
cors.setAllowedOrigins(List.of(
    "https://yourdomain.com",
    "https://app.yourdomain.com"
));
```

## Monitoring & Logging

### Application Logs

Logs are written to:
- Console (stdout)
- File: `logs/autonexo.log` (configurable)

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

Response:
```json
{
  "status": "UP"
}
```

### Metrics (Optional)

Enable more actuator endpoints in `application-prod.properties`:

```properties
management.endpoints.web.exposure.include=health,info,metrics
```

## Backup & Restore

### Database Backup

```bash
mysqldump -u username -p autonexo_db > backup.sql
```

### Database Restore

```bash
mysql -u username -p autonexo_db < backup.sql
```

### Cloudinary Backup

Cloudinary automatically backs up media. Download via API if needed.

## Troubleshooting

### Database Connection Issues

```
Check:
- MySQL is running
- Database exists
- Credentials are correct
- Firewall allows connection
- Connection pool settings
```

### JWT Issues

```
Check:
- JWT_SECRET is set
- Token hasn't expired
- Authorization header format: "Bearer <token>"
```

### Email Not Sending

```
Check:
- SMTP credentials
- App password (not regular password for Gmail)
- Firewall allows SMTP port
- Email templates exist in resources/templates/emails/
```

### Cloudinary Upload Fails

```
Check:
- API credentials are correct
- File size within limits
- Internet connectivity
- Cloudinary quota not exceeded
```

## Performance Tuning

### JVM Options

```bash
java -Xms512m -Xmx2g -XX:+UseG1GC -jar app.jar
```

### Database Optimization

- Add indexes for frequently queried columns
- Optimize connection pool size based on load
- Enable query caching (if needed)
- Use read replicas for read-heavy workloads

### Caching (Future Enhancement)

Consider adding Redis for:
- Session storage
- API response caching
- Workshop search results

## Scaling

### Horizontal Scaling

- Deploy multiple instances behind load balancer
- Use shared MySQL database
- Ensure stateless operation (JWT handles auth)
- Consider Redis for distributed caching

### Vertical Scaling

- Increase JVM heap size
- Increase database connection pool
- Optimize MySQL configuration
- Use faster hardware

## Maintenance

### Regular Tasks

- Monitor disk space for logs
- Rotate logs periodically
- Update dependencies (security patches)
- Backup database regularly
- Monitor Cloudinary usage/quota
- Review and clean expired tokens

### Scheduled Tasks

System runs these automatically:
- Maintenance reminders (daily)
- Offer expiration (hourly)
- Service reminders (hourly)
- Review window expiration (daily)
- Trust score recalculation (weekly)

No manual intervention needed.

## Support & Contact

For deployment issues, consult:
- `prompt/CORE.md` - System overview
- `prompt/OVERVIEW.md` - Architecture details
- `prompt/API_ENDPOINTS.md` - API reference
- Individual BC documentation in `prompt/<context>/`

## License

[Your License Here]

