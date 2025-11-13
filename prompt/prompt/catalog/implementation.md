# Catalog System Implementation

## Overview

The AutoNexo application uses a combination of database-driven and enum-based catalogs to provide standardized options for vehicle brands, models, services, and workshop capabilities. This ensures consistency across the application and provides a reliable data source for frontend applications.

## Architecture

### Catalog Types

1. **Database Catalogs** (Dynamic, Administrable):
   - Vehicle Brands (`VehicleBrand`)
   - Vehicle Models (`VehicleModel`)

2. **Enum Catalogs** (Static, Code-defined):
   - Service Catalog (`ServiceCatalog`) - ~60 services
   - Capability Tags (`CapabilityTag`) - ~50 tags

### Design Decision Rationale

**Why Database for Brands/Models:**
- Large number of combinations (thousands of models)
- Need for dynamic addition without code changes
- Require metadata (logos, country, years of production)
- Admin-manageable through API

**Why Enums for Services/Tags:**
- Relatively stable set of options
- Type-safety in Java code
- Better performance (no DB queries)
- Compiler-validated usage
- Easier to maintain business logic tied to specific services

## Database Entities

### VehicleBrand

**Location:** `shared/domain/model/entities/catalog/VehicleBrand.java`

**Fields:**
- `id` (Long) - Primary key
- `name` (String) - Brand name, unique, max 100 chars
- `logoUrl` (String) - Optional URL to brand logo
- `country` (String) - Country of origin
- `isActive` (boolean) - Soft delete flag
- `popular` (boolean) - Flag for filtering popular brands
- `createdAt`, `updatedAt` - Audit fields

**Business Methods:**
- `activate()` / `deactivate()` - Soft delete operations
- `markAsPopular()` / `unmarkAsPopular()` - Popularity management
- `update()` - Update brand information

### VehicleModel

**Location:** `shared/domain/model/entities/catalog/VehicleModel.java`

**Fields:**
- `id` (Long) - Primary key
- `brandId` (Long) - Foreign key to VehicleBrand
- `name` (String) - Model name, max 100 chars
- `startYear` (Integer) - Year production started (optional)
- `endYear` (Integer) - Year production ended (null if still produced)
- `isActive` (boolean) - Soft delete flag
- `createdAt`, `updatedAt` - Audit fields

**Business Methods:**
- `activate()` / `deactivate()` - Soft delete operations
- `update()` - Update model information

**Validation:**
- Years must be between 1900 and 2100
- `endYear` cannot be before `startYear`

## Repositories

### VehicleBrandRepository

**Location:** `shared/infrastructure/persistence/jpa/repositories/VehicleBrandRepository.java`

**Queries:**
- `findByIsActiveTrue()` - Get all active brands
- `findByIsActiveTrueAndPopularTrue()` - Get popular brands only
- `findByNameIgnoreCase(String)` - Find brand by name
- `existsByNameIgnoreCase(String)` - Check if brand exists

### VehicleModelRepository

**Location:** `shared/infrastructure/persistence/jpa/repositories/VehicleModelRepository.java`

**Queries:**
- `findByBrandIdAndIsActiveTrue(Long)` - Get active models for a brand
- `findByBrandId(Long)` - Get all models for a brand
- `existsByBrandIdAndNameIgnoreCase(Long, String)` - Check if model exists

## REST API Endpoints

### Public Catalog Controller

**Location:** `shared/interfaces/rest/PublicCatalogController.java`

**Base Path:** `/api/v1/catalog`

**Authentication:** None required (public endpoints)

#### Vehicle Brands and Models

```
GET /brands
  Query Params: popularOnly (boolean, default: false)
  Response: Array of VehicleBrandResource
  Example: [
    {
      "id": 1,
      "name": "Toyota",
      "logoUrl": null,
      "country": "Japan",
      "isActive": true,
      "popular": true
    }
  ]

GET /brands/{brandId}/models
  Path Param: brandId (Long)
  Response: Array of VehicleModelResource
  Example: [
    {
      "id": 15,
      "brandId": 1,
      "name": "Corolla",
      "startYear": 1966,
      "endYear": null,
      "isActive": true
    }
  ]
```

#### Service Catalog

```
GET /services
  Query Params: category (optional, e.g., "MAINTENANCE")
  Response: Array of service objects
  Example: [
    {
      "code": "OIL_CHANGE",
      "displayName": "Cambio de aceite",
      "description": "Cambio de aceite de motor y filtro",
      "category": "MAINTENANCE",
      "categoryDisplayName": "Mantenimiento"
    }
  ]

GET /services/categories
  Response: Array of category objects
  Example: [
    {
      "code": "MAINTENANCE",
      "displayName": "Mantenimiento"
    }
  ]
```

#### Capability Tags

```
GET /capability-tags
  Query Params: category (optional, e.g., "BRAND")
  Response: Array of tag objects
  Example: [
    {
      "code": "TOYOTA",
      "displayName": "Toyota",
      "category": "BRAND",
      "categoryDisplayName": "Marcas especializadas"
    }
  ]

GET /capability-tags/categories
  Response: Array of tag category objects
  Example: [
    {
      "code": "BRAND",
      "displayName": "Marcas especializadas"
    }
  ]
```

### Admin Catalog Controller

**Location:** `shared/interfaces/rest/CatalogAdminController.java`

**Base Path:** `/api/v1/admin/catalog`

**Authentication:** Required - `@PreAuthorize("hasRole('ADMIN')")`

#### Brand Management

```
POST /brands
  Body: CreateVehicleBrandResource
  Response: 201 Created with VehicleBrandResource

PUT /brands/{id}
  Path Param: id (Long)
  Body: UpdateVehicleBrandResource
  Response: 200 OK with VehicleBrandResource

DELETE /brands/{id}
  Path Param: id (Long)
  Response: 204 No Content
  Note: Soft delete (sets isActive = false)

GET /brands
  Response: Array of all brands (including inactive)

GET /brands/{id}
  Response: Single brand by ID
```

#### Model Management

```
POST /models
  Body: CreateVehicleModelResource
  Response: 201 Created with VehicleModelResource

PUT /models/{id}
  Path Param: id (Long)
  Body: UpdateVehicleModelResource
  Response: 200 OK with VehicleModelResource

DELETE /models/{id}
  Path Param: id (Long)
  Response: 204 No Content
  Note: Soft delete (sets isActive = false)

GET /brands/{brandId}/models
  Response: Array of all models for a brand (including inactive)

GET /models/{id}
  Response: Single model by ID
```

## Integration with Other Contexts

### Vehicle Context

**Impact:** Major change

**Changes Made:**
- `Vehicle` aggregate: `String brand` → `Long brandId`
- `CreateVehicleCommand`: Updated to accept `brandId`
- `CreateVehicleResource`: Updated to accept `brandId`
- `VehicleCommandServiceImpl`: Added validation of `brandId`

**Validation:**
```java
VehicleBrand brand = vehicleBrandRepository.findById(command.brandId())
    .filter(VehicleBrand::isActive)
    .orElseThrow(() -> new IllegalArgumentException(
        "Vehicle brand not found or inactive with ID: " + command.brandId()));
```

**Frontend Flow:**
1. GET `/api/v1/catalog/brands?popularOnly=true`
2. User selects brand (e.g., Toyota with id=1)
3. GET `/api/v1/catalog/brands/1/models`
4. User selects or types model name
5. POST `/api/v1/vehicles` with `brandId: 1, model: "Corolla 2020"`

### Workshop Context

**Impact:** Validation enhancement (no schema changes)

**Validation Points:**
1. **ServiceTemplate Creation:**
   - Validates `catalogService` against `ServiceCatalog` enum
   - Location: `WorkshopCommandServiceImpl.handle(AddServiceTemplateCommand)`
   - Already implemented

2. **CapabilityTag Addition:**
   - Validates tag against `CapabilityTag` enum
   - Location: `WorkshopController.addCapabilityTag()`
   - Already implemented

**Frontend Flow:**
1. GET `/api/v1/catalog/services?category=MAINTENANCE`
2. Workshop selects services (e.g., "OIL_CHANGE")
3. POST `/api/v1/workshops/service-templates` with `catalogService: "OIL_CHANGE"`
4. GET `/api/v1/catalog/capability-tags`
5. Workshop selects tags (e.g., "TOYOTA", "LIGHT_VEHICLES")
6. POST `/api/v1/workshops/tags?tag=TOYOTA`

### Matching Context

**Impact:** Validation enhancement (no schema changes)

**Validation Point:**
- `ServiceRequestCommandFromResourceAssembler`:
  - Validates requested services against `ServiceCatalog` enum
  - Uses `ServiceCatalog.fromString()` which throws exception if invalid
  - Location: Line 23 of assembler

**Frontend Flow:**
1. GET `/api/v1/catalog/services`
2. User selects needed services
3. POST `/api/v1/service-requests` with `requestedServices: ["OIL_CHANGE", "BRAKE_PAD_REPLACEMENT"]`
4. Matching system finds workshops offering those services

## Data Seeding

**Script Location:** `src/main/resources/db/seed/vehicle-brands-seed.sql`

**Contents:**
- 33 vehicle brands (20 marked as popular)
- ~200 vehicle models across major brands
- Covers Japanese, American, Korean, German, French, Italian, British, and Chinese brands

**Popular Brands Include:**
- Toyota, Honda, Nissan, Mazda, Suzuki (Japanese)
- Ford, Chevrolet, Jeep, Tesla (American)
- Hyundai, Kia (Korean)
- Volkswagen, BMW, Mercedes-Benz, Audi (German)
- Peugeot (French)
- Chery (Chinese)

**Execution:**
1. Reset database (if needed)
2. Run application to create tables
3. Execute seed script manually in MySQL Workbench/client
4. Verify: `SELECT COUNT(*) FROM vehicle_brand;` should return 33

## Error Handling

### Validation Errors

**Invalid Brand ID (Vehicle Creation):**
```json
{
  "status": 400,
  "message": "Vehicle brand not found or inactive with ID: 999"
}
```

**Invalid Service Catalog (Workshop/ServiceRequest):**
```json
{
  "status": 400,
  "message": "Invalid catalog service: INVALID_SERVICE"
}
```

**Invalid Capability Tag (Workshop):**
```json
{
  "status": 400,
  "message": "Invalid capability tag: INVALID_TAG"
}
```

### Admin Endpoints

**Duplicate Brand Name:**
```json
{
  "status": 409,
  "message": "Conflict"
}
```

**Brand Not Found:**
```json
{
  "status": 404,
  "message": "Not Found"
}
```

## Security

### Public Endpoints
- No authentication required
- Safe for direct frontend access
- Read-only operations

### Admin Endpoints
- Require `ADMIN` role
- JWT token with role validation
- Only accessible to system administrators

### Role Assignment
- ADMIN role must be manually assigned in database
- Not available through signup process
- Protected sensitive operations

## Performance Considerations

### Database Catalogs
- Small dataset (~33 brands, ~200 models)
- Indexed on `name` and `isActive`
- No caching needed for MVP
- Future: Consider Redis cache if needed

### Enum Catalogs
- Zero database overhead
- Compile-time type checking
- In-memory access
- Optimal performance

## Migration Guide

### Updating from String brand to Long brandId

**Required Steps:**
1. Backup existing vehicle data (if any)
2. Drop and recreate database (acceptable for demo)
3. Run seed script to populate brands
4. Update frontend to use new endpoints

**For Production (Future):**
1. Create migration script to map String brands to brand IDs
2. Add brandId column (nullable initially)
3. Populate brandId from existing brand strings
4. Make brandId NOT NULL
5. Drop brand string column

## Testing Checklist

- [ ] Can retrieve all active brands from `/api/v1/catalog/brands`
- [ ] Can filter popular brands with `?popularOnly=true`
- [ ] Can retrieve models for a specific brand
- [ ] Can create vehicle with valid brandId
- [ ] Cannot create vehicle with invalid brandId (returns 400)
- [ ] Can retrieve all services from `/api/v1/catalog/services`
- [ ] Can filter services by category
- [ ] Can retrieve all capability tags
- [ ] Can filter tags by category
- [ ] Workshop can only add valid services (invalid rejected)
- [ ] Workshop can only add valid tags (invalid rejected)
- [ ] ServiceRequest can only request valid services (invalid rejected)
- [ ] ADMIN can create new brands
- [ ] ADMIN can update existing brands
- [ ] ADMIN can deactivate brands (soft delete)
- [ ] Non-ADMIN users cannot access admin endpoints (403)

## Future Enhancements

1. **Brand Logo Integration:**
   - Upload logos via admin panel
   - Store in Cloudinary
   - Display in frontend

2. **Model Variants:**
   - Add trim levels (e.g., "Corolla LE", "Corolla SE")
   - Engine specifications
   - Transmission types

3. **Dynamic Service Catalog:**
   - Move ServiceCatalog to database
   - Allow workshops to suggest new services
   - Admin approval workflow

4. **Localization:**
   - Multi-language support for displayNames
   - Region-specific brands/models

5. **Caching Strategy:**
   - Redis cache for frequently accessed catalogs
   - Cache invalidation on admin updates

6. **Search Functionality:**
   - Fuzzy search for brand/model names
   - Autocomplete suggestions

7. **Analytics:**
   - Track most used brands/models
   - Popular service requests
   - Tag usage statistics

