# Food Waste Collection & Redistribution System - Implementation Plan

**Plan Date:** 2026-10-21
**Team:** Senior Engineering Team
**Based On:** FEATURE_AUDIT.md
**Branch:** Pallavi-week-1 (94 modified files, uncommitted)

---

## Baseline Status

### Git Status
- **Branch:** Pallavi-week-1 (behind origin by 1 commit)
- **Modified Files:** 94 files (backend + frontend)
- **Deleted Files:** 6 (shared components with typos)
- **Untracked Files:** 5 (new DTO, services, shared components, audit, system guide)
- **Status:** All changes preserved, ready for implementation

### Build Status
- **Backend Maven Compile:** ✅ BUILD SUCCESS (96 source files)
- **Frontend Build:** ⚠️ Skipped (PowerShell execution policy issue - will resolve in Phase A)
- **Database:** MySQL 8.0.44, food_waste_db exists
- **Backend Runtime:** Spring Boot 4.1.1 on port 8080
- **Frontend Runtime:** Angular 21.2.0 on port 4200

### Verified Working Features (Preserve These)
✅ User registration and login with JWT
✅ Donor dashboard with donation listing
✅ Food donation creation form
✅ Donation history with filtering
✅ NGO dashboard with request listing
✅ Available food browsing
✅ Food request creation (with notification to donor)
✅ Volunteer dashboard with pickup listing
✅ Pickup status updates
✅ Admin dashboard with statistics
✅ User management (list, toggle, delete)
✅ NGO verification
✅ Donation, pickup, and request management
✅ Notification system (create, read, mark read)
✅ Real-time dashboard auto-refresh (10s polling)
✅ Auth interceptor (attaches JWT to requests)
✅ Route guards (exist, need enforcement)

---

## Implementation Phases

### Phase A: Foundation & Broken Functionality

**Goal:** Fix critical foundation issues, secure configuration, and ensure clean build

#### A1. Security Configuration (CRITICAL)
**Files to Modify:**
- `Backend/food-waste-backend/src/main/resources/application.properties`
- `Backend/food-waste-backend/src/main/resources/application-dev.properties` (new)
- `Backend/food-waste-backend/src/main/resources/application-prod.properties` (new)
- `.gitignore` (add secrets)

**Changes:**
1. Remove hardcoded database password from application.properties
2. Remove hardcoded JWT secret from application.properties
3. Create environment-specific configuration files
4. Add placeholders for environment variables: `${DB_PASSWORD}`, `${JWT_SECRET}`
5. Update .gitignore to prevent committing secrets

**Implementation:**
```properties
# application.properties (template)
spring.datasource.password=${DB_PASSWORD:default_password}
jwt.secret=${JWT_SECRET:default_secret_change_in_production}
```

**Deliverables:**
- Environment configuration files
- Updated .gitignore
- Example .env file (without secrets)
- Documentation for environment setup

#### A2. Frontend Build Configuration
**Files to Modify:**
- PowerShell execution policy or use node directly
- Verify Angular CLI configuration

**Changes:**
1. Resolve PowerShell execution policy for npm
2. Verify frontend builds successfully
3. Check for TypeScript compilation errors

**Deliverables:**
- Frontend builds successfully
- No TypeScript errors
- Production build verified

#### A3. Database Seed Data Fix
**Files to Modify:**
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/config/DatabaseConfig.java`

**Changes:**
1. Fix seed data guard logic - check for critical data (users, ngos, volunteers) not just donations
2. Ensure idempotent seed data
3. Add proper error handling

**Implementation:**
```java
// Instead of checking only donations
if (donationRepository.count() > 0) return;

// Check for all critical entities
if (userRepository.count() > 0 && ngoRepository.count() > 0 && volunteerRepository.count() > 0) return;
```

**Deliverables:**
- Reliable seed data that runs correctly
- All test users available for testing

#### A4. Route Guard Enforcement
**Files to Modify:**
- `Frontend/food-waste-frontend/src/app/app.routes.ts`
- `Frontend/food-waste-frontend/src/app/guards/*.ts`

**Changes:**
1. Verify all guards are properly implemented
2. Ensure guards are applied to all protected routes
3. Test guard redirects work correctly

**Deliverables:**
- All protected routes enforce authentication
- Role-specific routes enforce role checks
- Unauthorized users redirected appropriately

**Phase A Deliverables Summary:**
- ✅ Secrets removed from source control
- ✅ Environment configuration system
- ✅ Frontend builds successfully
- ✅ Reliable seed data
- ✅ Route guards enforced
- ✅ Documentation for setup

**Phase A Blockers:**
- None expected

---

### Phase B: Secure Authentication & Role-Based Access

**Goal:** Implement proper server-side authentication and authorization for all roles

#### B1. SecurityConfig Hardening
**Files to Modify:**
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/security/SecurityConfig.java`

**Changes:**
1. Remove `permitAll()` from most endpoints
2. Implement proper role-based access control:
   - `/api/auth/**` - permitAll (login, register)
   - `/api/admin/**` - hasRole('ADMIN')
   - `/api/donations/my` - authenticated
   - `/api/donations/**` - authenticated (except create may be public)
   - `/api/food-requests/**` - authenticated
   - `/api/pickups/**` - authenticated
   - `/api/ngos/**` - authenticated
   - `/api/volunteers/**` - authenticated
   - `/api/notifications/**` - authenticated
3. Add method-level security annotations on controllers

**Implementation:**
```java
http.csrf(AbstractHttpConfigurer::disable)
    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers("/api/admin/**").hasRole("ADMIN")
        .requestMatchers("/api/donations/my").authenticated()
        .requestMatchers("/api/donations").authenticated()
        .requestMatchers("/api/food-requests/**").authenticated()
        .requestMatchers("/api/pickups/**").authenticated()
        .requestMatchers("/api/ngos/**").authenticated()
        .requestMatchers("/api/volunteers/**").authenticated()
        .requestMatchers("/api/notifications/**").authenticated()
        .anyRequest().authenticated()
    )
```

**Deliverables:**
- Properly secured endpoints
- Role-based access control
- Security tests

#### B2. Input Validation
**Files to Modify:**
- All DTO files in `Backend/food-waste-backend/src/main/java/com/food_waste_backend/dto/`
- All controller files in `Backend/food-waste-backend/src/main/java/com/food_waste_backend/controller/`

**Changes:**
1. Add `@Valid` annotation to all `@RequestBody` parameters
2. Add validation annotations to DTO fields:
   - `@NotBlank` for required strings
   - `@Email` for email fields
   - `@Min`, `@Max` for numeric fields
   - `@Future` for date fields
   - `@Pattern` for phone numbers, registration numbers
3. Add `@NotNull` for required objects

**Deliverables:**
- All DTOs validated
- Controllers reject invalid input
- Validation error responses

#### B3. Password Strength Validation
**Files to Modify:**
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/dto/RegisterRequest.java`
- `Frontend/food-waste-frontend/src/app/components/register/register.ts`

**Changes:**
1. Add custom password validator:
   - Minimum 8 characters
   - At least one uppercase letter
   - At least one lowercase letter
   - At least one number
   - At least one special character
2. Implement validation on both frontend and backend

**Deliverables:**
- Strong password policy
- Frontend validation
- Backend validation
- User feedback

#### B4. Rate Limiting
**Files to Modify:**
- `Backend/food-waste-backend/pom.xml` (add bucket4j dependency)
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/config/RateLimitConfig.java` (new)
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/security/SecurityConfig.java`

**Changes:**
1. Add rate limiting library (bucket4j)
2. Configure rate limits:
   - Login: 5 attempts per 15 minutes
   - Register: 3 attempts per hour
   - General API: 100 requests per minute
3. Apply rate limiting to authentication endpoints

**Deliverables:**
- Rate limiting configured
- Brute force protection
- Rate limit error responses

**Phase B Deliverables Summary:**
- ✅ Properly secured endpoints
- ✅ Role-based access control
- ✅ Input validation on all endpoints
- ✅ Strong password policy
- ✅ Rate limiting on auth endpoints
- ✅ Security tests

**Phase B Blockers:**
- None expected

---

### Phase C: Donation & Request Workflows with Validation

**Goal:** Ensure donation listings and recipient requests work correctly with proper validation

#### C1. Food Expiry Validation
**Files to Modify:**
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/dto/DonationRequest.java`
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/service/impl/DonationServiceImpl.java`
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/controller/DonationController.java`

**Changes:**
1. Add `@Future` validation to expiryTime
2. Prevent creation of donations with past expiry dates
3. Add automatic expiry check on request
4. Prevent NGOs from requesting expired donations

**Implementation:**
```java
// In DonationServiceImpl
public DonationResponse createDonation(DonationRequest request) {
    if (request.getExpiryTime().isBefore(LocalDateTime.now())) {
        throw new ValidationException("Expiry time must be in the future");
    }
    // ... rest of implementation
}

// In FoodRequestServiceImpl
public FoodRequestResponse createFoodRequest(String email, FoodRequestDto request) {
    FoodDonation donation = foodDonationRepository.findById(request.getDonationId())
        .orElseThrow(() -> new ResourceNotFoundException("FoodDonation", request.getDonationId()));
    
    if (donation.getExpiryTime().isBefore(LocalDateTime.now())) {
        throw new ValidationException("Cannot request expired donation");
    }
    // ... rest of implementation
}
```

**Deliverables:**
- Cannot create expired donations
- Cannot request expired donations
- Validation error messages
- Tests for expiry validation

#### C2. Prevent Duplicate Requests
**Files to Modify:**
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/service/impl/FoodRequestServiceImpl.java`
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/repository/FoodRequestRepository.java`

**Changes:**
1. Add check for existing request on same donation
2. Prevent multiple NGOs from requesting same donation
3. Prevent same NGO from requesting same donation twice

**Implementation:**
```java
// In FoodRequestServiceImpl
public FoodRequestResponse createFoodRequest(String email, FoodRequestDto request) {
    // Check if donation already has an active request
    List<FoodRequest> existingRequests = foodRequestRepository
        .findByDonationIdAndStatusIn(request.getDonationId(), 
            Arrays.asList(RequestStatus.PENDING, RequestStatus.APPROVED));
    
    if (!existingRequests.isEmpty()) {
        throw new ValidationException("Donation already has an active request");
    }
    // ... rest of implementation
}
```

**Deliverables:**
- No duplicate requests
- Concurrency protection
- Tests for duplicate prevention

#### C3. Prevent Over-Allocation
**Files to Modify:**
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/service/impl/FoodRequestServiceImpl.java`
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/service/impl/PickupServiceImpl.java`

**Changes:**
1. Ensure donation status transitions are valid
2. Prevent multiple pickups for same donation
3. Ensure pickup only created for approved requests

**Implementation:**
```java
// In PickupServiceImpl
public PickupResponse createPickup(String email, PickupRequestDto request) {
    FoodDonation donation = foodDonationRepository.findById(request.getDonationId())
        .orElseThrow(() -> new ResourceNotFoundException("FoodDonation", request.getDonationId()));
    
    // Only allow pickup for accepted donations
    if (donation.getStatus() != DonationStatus.ACCEPTED) {
        throw new ValidationException("Pickup can only be created for accepted donations");
    }
    
    // Check if pickup already exists
    if (pickupRequestRepository.findByDonationId(request.getDonationId()).isPresent()) {
        throw new ValidationException("Pickup already exists for this donation");
    }
    // ... rest of implementation
}
```

**Deliverables:**
- No over-allocation
- Valid status transitions
- Tests for allocation logic

#### C4. Donor Profile Update Integration
**Files to Modify:**
- `Frontend/food-waste-frontend/src/app/components/donor-profile/donor-profile.ts`
- `Frontend/food-waste-frontend/src/app/services/user.ts`

**Changes:**
1. Connect donor profile edit form to backend API
2. Update localStorage after successful save
3. Add success/error messages

**Deliverables:**
- Donor profile updates working
- Real-time UI updates
- Tests for profile update

**Phase C Deliverables Summary:**
- ✅ Food expiry validation
- ✅ No duplicate requests
- ✅ No over-allocation
- ✅ Valid status transitions
- ✅ Donor profile update working
- ✅ Workflow tests

**Phase C Blockers:**
- None expected

---

### Phase D: Collection/Delivery Workflow with Concurrency

**Goal:** Implement robust pickup workflow with server-side validation and concurrency controls

#### D1. Valid Server-Side Status Transitions
**Files to Modify:**
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/service/impl/PickupServiceImpl.java`
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/service/impl/FoodRequestServiceImpl.java`
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/service/impl/DonationServiceImpl.java`

**Changes:**
1. Define valid status transition matrix
2. Implement state machine validation
3. Prevent invalid status changes

**Implementation:**
```java
// Valid transitions:
// Donation: PENDING -> ACCEPTED -> DELIVERED -> COMPLETED
// Pickup: SCHEDULED -> COLLECTED -> COMPLETED
// Request: PENDING -> APPROVED -> FULFILLED

private void validateDonationStatusTransition(DonationStatus current, DonationStatus newStatus) {
    Map<DonationStatus, List<DonationStatus>> validTransitions = Map.of(
        DonationStatus.PENDING, List.of(DonationStatus.ACCEPTED, DonationStatus.CANCELLED, DonationStatus.EXPIRED),
        DonationStatus.ACCEPTED, List.of(DonationStatus.DELIVERED, DonationStatus.CANCELLED),
        DonationStatus.DELIVERED, List.of(DonationStatus.COMPLETED),
        DonationStatus.COMPLETED, List.of(),
        DonationStatus.CANCELLED, List.of(),
        DonationStatus.EXPIRED, List.of()
    );
    
    if (!validTransitions.get(current).contains(newStatus)) {
        throw new ValidationException("Invalid status transition from " + current + " to " + newStatus);
    }
}
```

**Deliverables:**
- Valid status transitions only
- State machine validation
- Tests for all transitions

#### D2. Concurrency Controls
**Files to Modify:**
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/service/impl/FoodRequestServiceImpl.java`
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/service/impl/PickupServiceImpl.java`
- All service interfaces

**Changes:**
1. Add `@Transactional` to write operations
2. Add optimistic locking with `@Version` on entities
3. Implement retry logic for concurrent updates

**Implementation:**
```java
// Add @Version field to entities
@Version
private Long version;

// Add @Transactional to service methods
@Transactional
public FoodRequestResponse createFoodRequest(String email, FoodRequestDto request) {
    // ... implementation
}

// Handle OptimisticLockingFailureException
@Transactional
public PickupResponse updatePickupStatus(Long id, String status) {
    try {
        // ... implementation
    } catch (ObjectOptimisticLockingFailureException e) {
        throw new ConcurrentModificationException("Pickup was modified by another user");
    }
}
```

**Deliverables:**
- Transactional operations
- Optimistic locking
- Concurrency error handling
- Tests for concurrent operations

#### D3. Prevent Unauthorized Record Access
**Files to Modify:**
- All service implementations
- All controllers

**Changes:**
1. Verify user owns the record they're accessing
2. Add ownership checks in service methods
3. Return 403 for unauthorized access

**Implementation:**
```java
// In FoodRequestServiceImpl
public List<FoodRequestResponse> getMyRequests(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User", email));
    
    Ngo ngo = ngoRepository.findByUserId(user.getId())
        .orElseThrow(() -> new ResourceNotFoundException("NGO profile not found"));
    
    return foodRequestRepository.findByNgoId(ngo.getId())
        .stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
}

// In controllers
@GetMapping("/my")
public ResponseEntity<List<FoodRequestResponse>> getMyRequests(
    @RequestHeader("X-User-Email") String email) {
    return ResponseEntity.ok(foodRequestService.getMyRequests(email));
}
```

**Deliverables:**
- Ownership verification
- Access control
- Tests for authorization

#### D4. Arbitrary Status Change Prevention
**Files to Modify:**
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/service/impl/PickupServiceImpl.java`
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/service/impl/DonationServiceImpl.java`

**Changes:**
1. Only allow specific roles to change status
2. Add role-based authorization for status updates
3. Log all status changes

**Implementation:**
```java
// In PickupServiceImpl
public PickupResponse updatePickupStatus(Long id, String status, String email) {
    PickupRequest pickup = pickupRequestRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("PickupRequest", id));
    
    // Only assigned volunteer or admin can update status
    if (!pickup.getVolunteer().getUser().getEmail().equals(email) && 
        !pickup.getVolunteer().getUser().getRole() != UserRole.ADMIN) {
        throw new AccessDeniedException("Only assigned volunteer or admin can update pickup status");
    }
    
    // ... rest of implementation
}
```

**Deliverables:**
- Role-based status updates
- Audit logging
- Tests for authorization

**Phase D Deliverables Summary:**
- ✅ Valid status transitions
- ✅ Concurrency controls
- ✅ Transactional operations
- ✅ Ownership verification
- ✅ Role-based status updates
- ✅ Concurrency tests

**Phase D Blockers:**
- None expected

---

### Phase E: Dashboards, Admin Verification, Notifications

**Goal:** Complete real role-specific dashboards, admin verification workflow, and persistent notifications

#### E1. NGO Profile Component
**Files to Create:**
- `Frontend/food-waste-frontend/src/app/components/ngo-profile/ngo-profile.ts`
- `Frontend/food-waste-frontend/src/app/components/ngo-profile/ngo-profile.html`
- `Frontend/food-waste-frontend/src/app/components/ngo-profile/ngo-profile.css`

**Files to Modify:**
- `Frontend/food-waste-frontend/src/app/app.routes.ts`
- `Frontend/food-waste-frontend/src/app/shared/navbar/navbar.ts`

**Changes:**
1. Create NGO profile component with:
   - Read-only view of NGO details
   - Edit form for updating profile
   - Verification status display
   - Save to backend API
2. Add route `/ngo/profile`
3. Add link in navbar

**Deliverables:**
- NGO profile component
- Profile update working
- Tests for profile management

#### E2. Volunteer Profile Component
**Files to Create:**
- `Frontend/food-waste-frontend/src/app/components/volunteer-profile/volunteer-profile.ts`
- `Frontend/food-waste-frontend/src/app/components/volunteer-profile/volunteer-profile.html`
- `Frontend/food-waste-frontend/src/app/components/volunteer-profile/volunteer-profile.css`

**Files to Modify:**
- `Frontend/food-waste-frontend/src/app/app.routes.ts`
- `Frontend/food-waste-frontend/src/app/shared/navbar/navbar.ts`

**Changes:**
1. Create volunteer profile component with:
   - Read-only view of volunteer details
   - Edit form for updating profile
   - Availability toggle
   - Save to backend API
2. Add route `/volunteer/profile`
3. Add link in navbar

**Deliverables:**
- Volunteer profile component
- Profile update working
- Tests for profile management

#### E3. Admin Verification Workflow
**Files to Modify:**
- `Frontend/food-waste-frontend/src/app/components/ngo-management/ngo-management.ts`
- `Frontend/food-waste-frontend/src/app/components/ngo-management/ngo-management.html` (create if missing)
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/service/impl/FoodRequestServiceImpl.java`

**Changes:**
1. Enforce NGO verification in food request:
   - Only verified NGOs can request food
   - Show verification status in NGO management
   - Add verify/reject buttons for admin
2. Create NGO management HTML if missing
3. Implement verification UI

**Implementation:**
```java
// In FoodRequestServiceImpl
public FoodRequestResponse createFoodRequest(String email, FoodRequestDto request) {
    Ngo ngo = ngoRepository.findById(request.getNgoId())
        .orElseThrow(() -> new ResourceNotFoundException("NGO", request.getNgoId()));
    
    if (!ngo.getVerified()) {
        throw new AccessDeniedException("Only verified NGOs can request food");
    }
    // ... rest of implementation
}
```

**Deliverables:**
- NGO verification enforced
- Verification UI complete
- Tests for verification workflow

#### E4. Notification Persistence Improvements
**Files to Modify:**
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/service/impl/FoodRequestServiceImpl.java`
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/service/impl/PickupServiceImpl.java`
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/service/impl/DonationServiceImpl.java`

**Changes:**
1. Ensure all critical events create notifications
2. Add notifications for:
   - Donation created (NGO notification)
   - Request approved (NGO notification)
   - Request rejected (NGO notification)
   - Pickup assigned (Volunteer notification)
   - Pickup collected (Donor notification)
   - Pickup completed (Donor notification)
   - Donation expired (Donor notification)
3. Ensure notifications persist correctly

**Deliverables:**
- Complete notification coverage
- All critical events notified
- Tests for notification system

#### E5. Dashboard Accuracy
**Files to Modify:**
- All dashboard components
- All dashboard services

**Changes:**
1. Verify all dashboards use real backend data
2. Remove any mock data
3. Ensure proper error handling
4. Add loading states
5. Test dashboard accuracy

**Deliverables:**
- All dashboards use real data
- No mock data remaining
- Proper error handling
- Loading states
- Dashboard tests

**Phase E Deliverables Summary:**
- ✅ NGO profile component
- ✅ Volunteer profile component
- ✅ Admin verification workflow
- ✅ Complete notification coverage
- ✅ Accurate dashboards
- ✅ Dashboard tests

**Phase E Blockers:**
- None expected

---

### Phase F: UI Polish, Security, Testing, Documentation

**Goal:** Professional responsive UI, comprehensive security, test coverage, and documentation

#### F1. Professional Responsive UI
**Files to Modify:**
- All component CSS files
- All component HTML files

**Changes:**
1. Ensure responsive design for all components
2. Add consistent styling
3. Improve mobile experience
4. Add proper error states
5. Add loading spinners
6. Improve accessibility (ARIA labels, focus states)

**Deliverables:**
- Responsive design
- Consistent styling
- Mobile-friendly
- Accessible UI
- UI tests

#### F2. Password Reset Implementation
**Files to Create:**
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/dto/PasswordResetRequest.java`
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/dto/PasswordResetConfirm.java`
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/service/PasswordResetService.java` (interface)
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/service/impl/PasswordResetServiceImpl.java`
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/controller/PasswordResetController.java`

**Files to Modify:**
- `Frontend/food-waste-frontend/src/app/components/forgot-password/forgot-password.ts`
- `Frontend/food-waste-frontend/src/app/components/forgot-password/forgot-password.html`

**Changes:**
1. Implement password reset flow:
   - Request reset (send email - placeholder for real email service)
   - Confirm reset with token
   - Update password
2. Frontend form to request reset
3. Frontend form to confirm reset

**Note:** Email service will be configured as placeholder for honest external service integration

**Deliverables:**
- Password reset API
- Password reset UI
- Documentation for email service integration
- Tests for password reset

#### F3. Contact Form Backend
**Files to Create:**
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/dto/ContactRequest.java`
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/controller/ContactController.java`

**Files to Modify:**
- `Frontend/food-waste-frontend/src/app/components/contact/contact.ts`

**Changes:**
1. Implement contact form submission endpoint
2. Save contact messages to database (new Contact entity)
3. Send email notification to admin (placeholder)
4. Connect frontend form to backend

**Deliverables:**
- Contact form API
- Contact entity
- Contact form integration
- Tests for contact form

#### F4. Backend Unit Tests
**Files to Create:**
- Test files for all services in `Backend/food-waste-backend/src/test/java/`

**Changes:**
1. Write unit tests for all service methods
2. Write integration tests for all controllers
3. Write tests for security rules
4. Write tests for validation
5. Write tests for business logic

**Target Coverage:** 70% minimum

**Deliverables:**
- Service unit tests
- Controller integration tests
- Security tests
- Validation tests
- Coverage report

#### F5. Frontend Tests
**Files to Modify:**
- All component spec files

**Changes:**
1. Implement component tests
2. Implement service tests
3. Implement guard tests
4. Implement E2E tests for critical workflows

**Target Coverage:** 60% minimum

**Deliverables:**
- Component tests
- Service tests
- Guard tests
- E2E tests
- Coverage report

#### F6. Security Hardening (Final)
**Files to Modify:**
- `Backend/food-waste-backend/src/main/java/com/food_waste_backend/security/SecurityConfig.java`
- `Backend/food-waste-backend/pom.xml`

**Changes:**
1. Add HTTPS configuration
2. Add CORS configuration for production
3. Add security headers (X-Frame-Options, X-XSS-Protection, etc.)
4. Add request size limits
5. Add file upload validation

**Deliverables:**
- HTTPS configuration
- Security headers
- CORS configuration
- Security tests

#### F7. Documentation
**Files to Create:**
- `README.md` (root)
- `DEPLOYMENT.md`
- `API_DOCUMENTATION.md`
- `SETUP.md`
- `.env.example`

**Changes:**
1. Create comprehensive README
2. Create deployment guide
3. Create API documentation
4. Create setup guide
5. Create environment example file

**Deliverables:**
- Complete documentation
- Setup instructions
- Deployment guide
- API documentation
- Environment configuration guide

**Phase F Deliverables Summary:**
- ✅ Professional responsive UI
- ✅ Password reset implementation
- ✅ Contact form backend
- ✅ Backend unit tests (70% coverage)
- ✅ Frontend tests (60% coverage)
- ✅ Security hardening
- ✅ Complete documentation
- ✅ Test reports

**Phase F Blockers:**
- None expected

---

## Overall Deliverables

### Setup Instructions
- Environment configuration guide
- Database setup instructions
- Backend build and run instructions
- Frontend build and run instructions
- Example .env file (without secrets)

### Migration Instructions
- Database schema changes
- Seed data changes
- Environment variable changes

### Test Results
- Backend test report
- Frontend test report
- Coverage reports
- Security scan results

### Known Limitations
- Email service integration (placeholder)
- SMS service integration (not implemented)
- Payment gateway (not implemented)
- Maps API (not implemented)
- Real-time updates (polling instead of WebSocket)

### Completion Matrix
- Feature completion status
- Test coverage by module
- Security compliance status
- Documentation completeness

---

## Risk Assessment

### High Risk
- Security configuration changes (Phase A, B, F)
- Database schema changes (Phase C, D)
- Status transition logic (Phase D)

### Medium Risk
- Route guard enforcement (Phase A)
- Concurrency controls (Phase D)
- Test implementation (Phase F)

### Low Risk
- UI improvements (Phase F)
- Documentation (Phase F)
- Profile components (Phase E)

---

## Approval Gates

After each phase, review and approve before proceeding:
- Phase A: Foundation - Security config, build status, seed data
- Phase B: Authentication - Security rules, validation, rate limiting
- Phase C: Workflows - Expiry validation, duplicate prevention
- Phase D: Concurrency - Status transitions, concurrency controls
- Phase E: Dashboards - Profile components, verification, notifications
- Phase F: Polish - UI, tests, documentation, security hardening

---

## Next Steps

**Immediate Action:** Begin Phase A implementation

**Questions for Approval:**
1. Proceed with Phase A (Foundation & Security Configuration)?
2. Should we use environment variables or a secret management system?
3. Should we implement email service integration or keep as placeholder?
4. Should we implement WebSocket for real-time updates or keep polling?

**Awaiting approval to begin Phase A.**
