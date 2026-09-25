# Food Waste Collection & Redistribution System — Backend
## Intern Task Guide (30 Days)

---

## Project Overview

This is a Spring Boot REST API backend for a food waste collection and 
redistribution platform. The system connects food donors (restaurants, 
hotels, individuals) with NGOs and volunteers to reduce food waste.

**Tech Stack:** Java 17, Spring Boot 4.x, Spring Security, 
Spring Data JPA, MySQL, Lombok, JWT Authentication

**Base Package:** `com.food_waste_backend`

**All source files are located under:**
`Backend/food-waste-backend/src/main/java/com/food_waste_backend/`

---

## How the Architecture Works

The project follows a standard layered architecture. Always work in this order:

```
Controller → Service Interface → ServiceImpl → Repository → Database
     ↑                                                          ↑
   DTOs                                                     Entities
```

- **Entity** — A Java class that maps directly to a database table. Annotated with `@Entity`.
- **Repository** — An interface that handles all database queries. Extends `JpaRepository`.
- **Service (interface)** — Declares what operations the feature supports. No logic here.
- **ServiceImpl** — The actual business logic. Implements the Service interface.
- **Controller** — Handles HTTP requests and responses. Calls service methods.
- **DTO** — Data Transfer Objects. Used for request input and API response output. Never 
expose Entity objects directly from controllers.
- **Enum** — Fixed constant values used across the system (e.g., status values, roles).

---

## Project Setup (Do This First)

### Prerequisites
- Java 17 installed
- Maven installed
- MySQL 8+ installed and running
- An IDE (IntelliJ IDEA recommended)
- Postman for API testing

### Steps
1. Open the project in your IDE from the `Backend/food-waste-backend/` folder
2. Create a MySQL database named `food_waste_db`
3. Add the following configuration to `src/main/resources/application.properties`:

```properties
spring.application.name=food-waste-backend

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/food_waste_db
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# JWT
jwt.secret=your_super_secret_key_minimum_32_characters_long
jwt.expiration=86400000

# File Upload
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
```

4. Add the following JWT dependencies to `pom.xml` inside the `<dependencies>` block:

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

5. Run `mvn clean install` to download all dependencies
6. Run the application using `mvn spring-boot:run` — it should start on port 8080
 and connect to MySQL

**Done when:** Application starts without errors and you see Hibernate SQL logs 
in the console.

---

## Important Rules (Read Before Coding)

- Never return Entity objects from controllers — always convert to DTO first
- Never store plain-text passwords — always encode using `BCryptPasswordEncoder`
- All business logic goes in `ServiceImpl` classes — controllers must only call 
service methods
- Use `@Transactional` annotation on any service method that writes or modifies data
- When fetching from a repository, use `Optional.orElseThrow()` instead of null checks — 
throw `ResourceNotFoundException` if not found
- Never hardcode passwords, JWT secrets, or database credentials in Java files — always
 read from `application.properties`
- Add `@Valid` annotation on `@RequestBody` parameters to trigger input validation

---

## WEEK 1 (Days 1–7): Foundation

---

### Task 1 — Enums
**Package:** `enums/`
**Files:** `UserRole.java`, `DonationStatus.java`, `FoodCategory.java`, `PickupStatus.java`,
 `RequestStatus.java`, `NotificationType.java`
**Estimated Time:** 2 hours

Enums are fixed sets of constants used throughout the project. All these files are already
 created — you need to add the constant values inside each enum.

**What to add in each file:**

- `UserRole.java` — Represents the role of a user in the system. Values: `ADMIN`, `DONOR`,
 `VOLUNTEER`, `NGO`, `USER`

- `DonationStatus.java` — Tracks the lifecycle of a food donation. Values: `PENDING`,
 `ACCEPTED`, `PICKED_UP`, `DELIVERED`, `CANCELLED`, `EXPIRED`

- `FoodCategory.java` — Type of food being donated. Values: `COOKED_FOOD`, `RAW_VEGETABLES`,
 `FRUITS`, `BAKERY`, `DAIRY`, `PACKAGED`, `OTHER`

- `PickupStatus.java` — Status of a volunteer pickup. Values: `SCHEDULED`, `IN_PROGRESS`,
 `COMPLETED`, `FAILED`, `CANCELLED`

- `RequestStatus.java` — Status of an NGO's food request. Values: `PENDING`, `APPROVED`,
 `REJECTED`, `FULFILLED`, `CANCELLED`

- `NotificationType.java` — Type of notification sent to a user. Values: `DONATION_CREATED`,
 `PICKUP_ASSIGNED`, `REQUEST_APPROVED`, `REQUEST_REJECTED`, `GENERAL`

**Done when:** All enum files compile with no errors.

---

### Task 2 — Entity: User
**File:** `Entity/User.java`
**Estimated Time:** 2 hours

This is the most important entity in the project. Every person using the system 
(admin, donor, volunteer, NGO) is stored as a User. The role field determines what they can do.

**Class-level annotations to add:** `@Entity`, `@Table(name = "users")`, `@Data`, 
`@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`

**Fields to add:**
- `id` — Primary key, auto-generated (Long)
- `name` — Full name of the user, cannot be null
- `email` — Unique email address, cannot be null — used for login
- `password` — Stored as a BCrypt hash, never plain text, cannot be null
- `phone` — Contact number (optional)
- `role` — The UserRole enum value — determines access permissions
- `enabled` — Boolean flag to enable/disable the account, default true
- `createdAt` — Timestamp set automatically when the record is first saved
- `updatedAt` — Timestamp updated automatically every time the record is modified

**Lifecycle methods to add:**
- Add a method annotated with `@PrePersist` that sets `createdAt` and `updatedAt` 
to the current date-time before the first save
- Add a method annotated with `@PreUpdate` that updates `updatedAt` to the 
current date-time on every update

**Done when:** App starts and Hibernate auto-creates the `users` table 
in MySQL.

---

### Task 3 — Entity: Address
**File:** `Entity/Address.java`
**Estimated Time:** 1 hour

Stores the address of a user. Each user has one address (one-to-one relationship).

**Class-level annotations:** `@Entity`, `@Table(name = "addresses")`, 
`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`

**Fields to add:**
- `id` — Primary key, auto-generated
- `street` — Street name/number
- `city` — City
- `state` — State
- `pincode` — Postal/ZIP code
- `country` — Country name
- `user` — Reference to the User entity. Use `@OneToOne` with `@JoinColumn
(name = "user_id")`. This creates a `user_id` foreign key column in the 
addresses table.

**Done when:** Hibernate creates the `addresses` table with a `user_id` 
foreign key column.

---

### Task 4 — Entity: Donor
**File:** `Entity/Donor.java`
**Note:** There is also an old `Donar.java` (typo) in the Entity folder — 
delete that file. Use only `Donor.java`.
**Estimated Time:** 1 hour

Stores extra information about users who are donors. Every Donor must 
have an associated User record.

**Class-level annotations:** `@Entity`, `@Table(name = "donors")`, 
`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`

**Fields to add:**
- `id` — Primary key, auto-generated
- `user` — `@OneToOne` relationship with User, cannot be null
- `organizationName` — Optional, for restaurants or hotels donating food
- `donorType` — UserRole enum, identifies this as a DONOR type
- `description` — Short description about the donor
- `createdAt` — Timestamp of registration, set via `@PrePersist`

**Done when:** Hibernate creates the `donors` table.

---

## WEEK 2 (Days 8–14): Remaining Entities + Repositories

---

### Task 5 — Entity: Role
**File:** `Entity/Role.java`
**Estimated Time:** 30 minutes

Simple entity that stores role definitions in the database. Mainly used 
for Spring Security role management.

**Fields:** `id` (primary key), `name` (UserRole enum, unique, not null)

**Annotations:** `@Entity`, `@Table(name = "roles")`, Lombok annotations

---

### Task 6 — Entity: FoodDonation
**File:** `Entity/FoodDonation.java`
**Estimated Time:** 1.5 hours

The central entity of the system. Represents a food donation created by a donor.

**Class-level annotations:** `@Entity`, `@Table(name = "food_donations")`,
 Lombok annotations

**Fields:**
- `id` — Primary key
- `donor` — `@ManyToOne` link to Donor (many donations can belong to one donor).
 Not null.
- `title` — Short title of the donation (e.g. "50 meals from wedding")
- `description` — Detailed description
- `quantity` — Amount of food (numeric)
- `quantityUnit` — Unit of measurement — "kg", "servings", "packets"
- `category` — FoodCategory enum
- `status` — DonationStatus enum, set to PENDING automatically on creation via
 `@PrePersist`
- `expiryTime` — When the food expires and can no longer be picked up
- `createdAt` — Set via `@PrePersist`
- `updatedAt` — Updated via `@PreUpdate`
- `items` — `@OneToMany` list of FoodItem objects linked to this donation

---

### Task 7 — Entity: FoodItem
**File:** `Entity/FoodItem.java`
**Estimated Time:** 45 minutes

Represents individual food items within a donation. A single donation can have
 multiple items.

**Fields:**
- `id` — Primary key
- `name` — Name of the food item (e.g. "Rice", "Dal")
- `quantity` — Amount
- `unit` — Unit of measurement
- `category` — FoodCategory enum
- `donation` — `@ManyToOne` link back to FoodDonation

---

### Task 8 — Entity: Volunteer
**File:** `Entity/Volunteer.java`
**Estimated Time:** 1 hour

Stores volunteer profile information. Volunteers pick up food from donors and 
deliver to NGOs.

**Fields:**
- `id` — Primary key
- `user` — `@OneToOne` with User, not null
- `available` — Boolean, whether the volunteer is currently available for pickup.
 Default true.
- `vehicleType` — "bike", "car", or "none"
- `operatingArea` — The city or area they can cover
- `joinedAt` — Set via `@PrePersist`

---

### Task 9 — Entity: Ngo
**File:** `Entity/Ngo.java`
**Estimated Time:** 1 hour

Stores NGO profile information. NGOs request food from available donations.

**Fields:**
- `id` — Primary key
- `user` — `@OneToOne` with User, not null
- `ngoName` — Name of the NGO
- `registrationNumber` — Official registration number
- `description` — What the NGO does
- `contactPerson` — Name of the contact person
- `verified` — Boolean, set to false by default. Admin can verify an NGO. Only 
verified NGOs should be allowed to make food requests.
- `registeredAt` — Set via `@PrePersist`

---

### Task 10 — Entity: FoodRequest
**File:** `Entity/FoodRequest.java`
**Estimated Time:** 1 hour

Represents an NGO's request for a specific food donation.

**Fields:**
- `id` — Primary key
- `ngo` — `@ManyToOne` link to Ngo
- `donation` — `@ManyToOne` link to FoodDonation
- `status` — RequestStatus enum, set to PENDING via `@PrePersist`
- `notes` — Any notes from the NGO regarding the request
- `requestedAt` — Set via `@PrePersist`
- `updatedAt` — Updated via `@PreUpdate`

---

### Task 11 — Entity: PickupRequest
**File:** `Entity/PickupRequest.java`
**Estimated Time:** 1 hour

Represents a pickup task assigned to a volunteer for a donation.

**Fields:**
- `id` — Primary key
- `donation` — `@ManyToOne` link to FoodDonation
- `volunteer` — `@ManyToOne` link to Volunteer
- `status` — PickupStatus enum, set to SCHEDULED via `@PrePersist`
- `scheduledTime` — When the pickup is planned
- `completedTime` — When the pickup was actually completed (set when status 
becomes COMPLETED)
- `createdAt` — Set via `@PrePersist`

---

### Task 12 — Entity: Notification
**File:** `Entity/Notification.java`
**Estimated Time:** 45 minutes

Stores in-app notifications sent to users.

**Fields:**
- `id` — Primary key
- `user` — `@ManyToOne` link to User (recipient)
- `title` — Short notification heading
- `message` — Full notification message
- `type` — NotificationType enum
- `isRead` — Boolean, default false. Set to true when user reads it.
- `createdAt` — Set via `@PrePersist`

**Done when (Tasks 5–12):** All remaining tables are auto-created by 
Hibernate on app start.

---

### Task 13 — All Repositories
**Package:** `repository/`
**Estimated Time:** Half day

Each repository is an interface that extends `JpaRepository<Entity, Long>`. 
Spring Data JPA automatically provides basic CRUD operations — you don't need
 to write any SQL for them. You only need to declare custom query methods by name.

Work through each file and add the correct custom methods:

- `UserRepository.java`
  - Find a user by their email address (returns Optional)
  - Check if a user with a given email already exists (returns boolean) — 
used during registration to prevent duplicates

- `DonorRepository.java`
  - Find a donor profile by their user ID (returns Optional)

- `FoodDonationRepository.java`
  - Find all donations belonging to a specific donor ID
  - Find all donations by their status (used to show PENDING donations to NGOs)

- `FoodItemRepository.java`
  - Find all food items belonging to a specific donation ID

- `PickupRequestRepository.java`
  - Find all pickup requests assigned to a specific volunteer ID
  - Find all pickup requests for a specific donation ID

- `VolunteerRepository.java`
  - Find a volunteer profile by their user ID (returns Optional)
  - Find all volunteers where `available` is true (used to assign pickups)

- `NgoRepository.java`
  - Find an NGO profile by their user ID (returns Optional)
  - Find all NGOs where `verified` is true

- `FoodRequestRepository.java`
  - Find all food requests made by a specific NGO ID
  - Find all food requests for a specific donation ID
  - Find all food requests by their status

- `NotificationRepository.java`
  - Find all notifications for a user ID, ordered by `createdAt` 
descending (newest first)
  - Find all unread notifications for a user ID (where `isRead` is false)

- `AddressRepository.java`
  - Find the address for a specific user ID (returns Optional)

**How to name methods:** Spring Data JPA generates queries from method 
names. Example: to find by `userId`, write `findByUserId(Long userId)`.
 To find by `available = true`, write `findByAvailableTrue()`. No SQL 
or `@Query` annotation needed for these simple cases.

**Done when:** No compilation errors and app starts cleanly.

---

## WEEK 3 (Days 15–21): Security + DTOs + Service Layer

---

### Task 14 — Security Layer
**Package:** `security/`
**Files:** `UserPrincipal.java`, `CustomUserDetailsService.java`,
 `JwtService.java`, `JwtAuthenticationFilter.java`, `SecurityConfig.java`
**Estimated Time:** 2 days

This is the most complex part of the project. Work through the files 
in this exact order:

#### Step 1: `UserPrincipal.java`
This class wraps your `User` entity so Spring Security can understand it.
 It must implement the `UserDetails` interface from Spring Security.

- Add a static factory method `create(User user)` that takes a User 
entity and returns a `UserPrincipal` instance
- Store the user's `id`, `email` (used as username), and `password`
- Build the list of authorities using the user's role — prefix the 
role name with `"ROLE_"` (e.g., `"ROLE_ADMIN"`)
- Implement all methods from `UserDetails`:
  - `getUsername()` should return the email
  - `getPassword()` should return the stored password
  - `getAuthorities()` should return the list of roles
  - All boolean methods (`isAccountNonExpired`, `isAccountNonLocked`,
 `isCredentialsNonExpired`, `isEnabled`) should return `true`

#### Step 2: `CustomUserDetailsService.java`
This class tells Spring Security how to load a user during authentication.
 Annotate it with `@Service`. It must implement `UserDetailsService`.

- Inject `UserRepository`
- Implement the `loadUserByUsername(String email)` method
- Inside this method, look up the user by email using the repository
- If not found, throw `UsernameNotFoundException`
- If found, call `UserPrincipal.create(user)` and return the result

#### Step 3: `JwtService.java`
This class handles all JWT token operations. Annotate with `@Service`.

- Read the `jwt.secret` and `jwt.expiration` values from
 `application.properties` using `@Value`
- Write a method to generate a JWT token — takes `UserDetails`
 as input, returns a signed token string
- Write a method to extract the username (email) from a token
- Write a method to check if a token is valid — compare the username in the 
token with the given `UserDetails` and ensure the token is not expired
- Use the JJWT library (already added to pom.xml) for all token operations
- The signing key should be created from the secret using HMAC-SHA algorithm

#### Step 4: `JwtAuthenticationFilter.java`
This filter runs on every HTTP request before it reaches the controller.
 It reads the JWT from the request header and sets up the security context 
if the token is valid. Extend `OncePerRequestFilter`.

- Inject `JwtService` and `CustomUserDetailsService`
- In the `doFilterInternal` method:
  1. Read the `Authorization` header from the request
  2. Check if it starts with `"Bearer "` — if not, continue the filter 
chain without doing anything
  3. Extract the token by removing the `"Bearer "` prefix
  4. Extract the username (email) from the token using `JwtService`
  5. Load the user details using `CustomUserDetailsService`
  6. Validate the token using `JwtService`
  7. If valid, create a `UsernamePasswordAuthenticationToken` and set
 it in the `SecurityContextHolder`
  8. Continue the filter chain

#### Step 5: `SecurityConfig.java`
This is the main Spring Security configuration. Annotate with `@Configuration`
 and `@EnableWebSecurity`.

- Inject `JwtAuthenticationFilter` and `CustomUserDetailsService`
- Define a `SecurityFilterChain` bean:
  - Disable CSRF (since this is a stateless REST API)
  - Allow unauthenticated access to `/api/auth/**` (login and register)
  - Restrict `/api/admin/**` to only users with the `ADMIN` role
  - Require authentication for all other requests
  - Set session management to `STATELESS` (no server-side sessions — JWT 
handles everything)
  - Add the `JwtAuthenticationFilter` before the default `UsernamePassword
AuthenticationFilter`
- Define a `PasswordEncoder` bean — use `BCryptPasswordEncoder`
- Define an `AuthenticationManager` bean — get it from `AuthenticationConfiguration`

**Done when:** App starts with security active. Requests to `/api/auth/`
 work without a token. Requests to other endpoints return `401 Unauthorized`
 when no token is provided.

---

### Task 15 — DTOs
**Package:** `dto/`
**Estimated Time:** Half day

All DTO files are already created. Add fields to each one. Use Lombok annotations
 (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`) on every DTO class.

DTOs should only contain fields — no business logic, no JPA annotations.

| File | Purpose | Fields to Add |
|------|---------|---------------|
| `LoginRequest.java` | Input for login | email (String), password (String) |
| `LoginResponse.java` | Response after successful login | token (String), 
type (String, default "Bearer"), userId (Long), name (String), email (String), 
role (String) |
| `RegisterRequest.java` | Input for registration | name (String), email 
(String), password (String), phone (String), role (String) |
| `UserResponse.java` | User info returned in responses | id (Long), name
 (String), email (String), phone (String), role (String), createdAt
 (LocalDateTime) |
| `DonationRequest.java` | Input to create a donation | title, description,
 quantity (Integer), quantityUnit, category, expiryTime (LocalDateTime) |
| `DonationResponse.java` | Donation info in responses |id,title,description,
 quantity, quantityUnit, category, status, expiryTime, donorId (Long),
 donorName (String), createdAt |
| `FoodRequestDto.java` | Input for NGO to request a donation | donationId
 (Long), notes (String) |
| `FoodResponse.java` | Food item info in responses | id, name, quantity, 
unit, category |
| `PickupRequestDto.java` | Input to schedule a pickup | donationId 
(Long), scheduledTime (LocalDateTime) |
| `PickupResponse.java` | Pickup info in responses | id, donationTitle 
(String), volunteerName (String), status, scheduledTime, completedTime |
| `NgoRequest.java` | Input to register/update NGO profile | ngoName, 
registrationNumber, description, contactPerson |
| `NgoResponse.java` | NGO info in responses | id, ngoName, 
registrationNumber, verified (boolean), contactPerson, registeredAt |
| `VolunteerRequest.java` | Input to register/update volunteer profile |
 vehicleType, operatingArea |
| `VolunteerResponse.java` | Volunteer info in responses | id, volunteerName
 (String), available (boolean), vehicleType, operatingArea |
| `NotificationResponse.java` | Notification info in responses | id, title,
 message, type, isRead (boolean), createdAt |
| `ReportResponse.java` | Dashboard stats | totalDonations (Long), 
totalPickups (Long), totalRequests (Long), activeVolunteers (Long), 
activeNgos (Long) |

---

### Task 16 — Exception Classes
**Package:** `exception/`
**Estimated Time:** 1 hour

#### `ResourceNotFoundException.java`
Thrown when a requested entity (user, donation, etc.) is not found in the 
database.
- Extend `RuntimeException`
- Add a constructor that accepts a `String message`
- Add a second constructor that accepts a `String resourceName` and a 
`Long id` — builds the message like "Donation not found with id: 5"

#### `BadRequestException.java`
Thrown when the input data is invalid (e.g., email already registered,
 invalid status transition).
- Extend `RuntimeException`
- Constructor accepting a `String message`

#### `UnauthorizedException.java`
Thrown when a user tries to do something they don't have permission for.
- Extend `RuntimeException`
- Constructor accepting a `String message`

#### `GlobalExceptionHandler.java`
This class intercepts all exceptions thrown anywhere in the application 
and returns a proper JSON error response. Annotate with `@RestControllerAdvice`.

- Add a handler method for `ResourceNotFoundException` — return HTTP 
404 with the error message
- Add a handler method for `BadRequestException` — return HTTP 400 
with the error message
- Add a handler method for `UnauthorizedException` — return HTTP 401
 with the error message
- Add a catch-all handler for `Exception` — return HTTP 500 with a
 generic error message

Use `ResponseUtil.error(message)` to build the response body in all
 handlers.

---

### Task 17 — Utility Classes
**Package:** `util/`
**Estimated Time:** 1 hour

#### `ResponseUtil.java`
A helper class for building consistent API responses across all controllers.
- Write a static `success(data, message)` method that returns a Map with keys:
 `success` (true), `message`, `data`
- Write a static `error(message)` method that returns a Map with keys: 
`success` (false), `message`
- All controllers should use this to build their responses

#### `DateUtil.java`
A helper class for date/time operations.
- Write a static method `isExpired(LocalDateTime expiryTime)` — returns
 true if the current time is after the expiry time
- Write a static method `formatDateTime(LocalDateTime dateTime)` — formats 
a date-time to a readable string like `"dd-MM-yyyy HH:mm"`

#### `FileUploadUtil.java`
A helper class for handling file uploads (e.g., donation photos).
- Write a static method `saveFile(MultipartFile file, String subDirectory)`
 that saves the uploaded file to a local `uploads/` directory
- Generate a unique filename using `UUID.randomUUID()` prepended to the
 original filename to avoid name conflicts
- Return the relative path of the saved file as a String
- Throw `IOException` if the operation fails

---

## WEEK 4 (Days 22–30): Service Layer + Controllers + Config

---

### Task 18 — Auth Service + Controller
**Files:** `service/AuthService.java`, `service/impl/AuthServiceImpl.java`,
 `controller/AuthController.java`
**Estimated Time:** 1.5 days

This is the entry point for all users — login and registration.

#### `AuthService.java` (interface)
Declare two methods:
- `login(LoginRequest)` — returns `LoginResponse`
- `register(RegisterRequest)` — returns `UserResponse`

#### `AuthServiceImpl.java`
Implement the `AuthService` interface. Annotate with `@Service`. Inject 
`UserRepository`, `PasswordEncoder`, `JwtService`, and `AuthenticationManager`.

**For the `login` method:**
- Use `AuthenticationManager` to authenticate the email and password
- If authentication fails, Spring Security automatically throws an exception — 
do not catch it
- Load the user from the repository by email
- Generate a JWT token using `JwtService`
- Build and return a `LoginResponse` with the token and user info

**For the `register` method:**
- Check if the email already exists using `userRepository.existsByEmail()` — 
if yes, throw `BadRequestException`
- Encode the raw password using `PasswordEncoder`
- Create and save a new `User` entity
- Return a `UserResponse` mapped from the saved user

#### `AuthController.java`
Annotate with `@RestController` and `@RequestMapping("/api/auth")`.
 Inject `AuthService`.

- `POST /api/auth/login` — calls `authService.login()`, returns
 `ResponseEntity` with 200 OK
- `POST /api/auth/register` — calls `authService.register()`,
 returns `ResponseEntity` with 201 CREATED

**Test this before moving on:** Use Postman to register a user and then
 log in. You should get a JWT token back. Save this token — you'll need 
it for all other endpoints.

---

### Task 19 — User Service + Controller
**Files:** `service/UserService.java`, `service/impl/UserServiceImpl.java`,
 `controller/UserController.java`
**Estimated Time:** 1 day

#### `UserService.java` (interface)
Declare these methods:
- Get user by ID — returns `UserResponse`
- Get all users — returns `List<UserResponse>` (admin only)
- Update user profile — takes user ID and updated data, returns `UserResponse`
- Delete user — takes user ID

#### `UserServiceImpl.java`
Implement the interface. For get/update operations, fetch by ID using the 
repository and throw `ResourceNotFoundException` if not found. Map entity 
to DTO before returning.

#### `UserController.java`
Annotate with `@RestController` and `@RequestMapping("/api/users")`.

- `GET /api/users/{id}` — get user by ID
- `GET /api/users` — get all users (restrict to ADMIN role using
 `@PreAuthorize("hasRole('ADMIN')")`)
- `PUT /api/users/{id}` — update user
- `DELETE /api/users/{id}` — delete user

---

### Task 20 — Donor Service + Controller
**Files:** `service/DonorService.java`, `service/impl/DonorServiceImpl.java`,
 `controller/DonorController.java`
**Estimated Time:** 1 day

#### `DonorService.java` (interface)
Declare:
- Register as donor — takes user ID and any extra info, returns `UserResponse`
- Get donor profile by user ID — returns donor info
- Update donor profile — takes user ID and update data

#### `DonorServiceImpl.java`
When registering as donor, check the user exists and doesn't already have a 
donor profile. Create the `Donor` entity and save it.

#### `DonorController.java`
Annotate with `@RestController` and `@RequestMapping("/api/donors")`.

- `POST /api/donors/register` — register the currently authenticated 
user as a donor
- `GET /api/donors/profile` — get the donor profile of the authenticated
 user
- `PUT /api/donors/profile` — update donor profile

---

### Task 21 — Donation Service + Controller
**Files:** `service/DonationService.java`, `service/impl/
DonationServiceImpl.java`,`controller/DonationController.java`
**Estimated Time:** 1.5 days

The core feature — creating and managing food donations.

#### `DonationService.java` (interface)
Declare:
- Create donation — takes `DonationRequest` and donor's user ID, returns
 `DonationResponse`
- Get donation by ID
- Get all donations
- Get donations by donor (for "my donations" view)
- Update donation status — takes donation ID and new status string
- Delete donation

#### `DonationServiceImpl.java`
For creation, look up the `Donor` entity by user ID, then create and save 
a `FoodDonation`. Set status to PENDING.

For status update, validate that the new status is a valid `DonationStatus`
 value before updating.

When checking expiry — use `DateUtil.isExpired()` if needed.

#### `DonationController.java`
Annotate with `@RestController` and `@RequestMapping("/api/donations")`.

- `POST /api/donations` — create a new donation
- `GET /api/donations` — get all donations (viewable by NGOs/volunteers)
- `GET /api/donations/{id}` — get single donation by ID
- `GET /api/donations/my` — get donations of the authenticated donor
- `PATCH /api/donations/{id}/status` — update donation status
- `DELETE /api/donations/{id}` — delete a donation

Use `Authentication` parameter in methods that need the current user's ID.

---

### Task 22 — Volunteer Service + Controller
**Files:** `service/VolunteerService.java`, `service/impl/
VolunteerServiceImpl.java`, `controller/VolunteerController.java`
**Estimated Time:** 1 day

#### `VolunteerService.java` (interface)
Declare:
- Register as volunteer — takes user ID and `VolunteerRequest`
- Get volunteer profile by user ID
- Update volunteer profile
- Get all available volunteers (for pickup assignment)
- Toggle availability status

#### `VolunteerController.java`
- `POST /api/volunteers/register` — register authenticated user as volunteer
- `GET /api/volunteers/profile` — get own volunteer profile
- `PUT /api/volunteers/profile` — update profile
- `GET /api/volunteers/available` — list all available volunteers
 (admin/system use)
- `PATCH /api/volunteers/availability` — toggle own availability

---

### Task 23 — NGO Service + Controller
**Files:** `service/NgoService.java`, `service/impl/NgoServiceImpl.java`, 
`controller/NgoController.java`
**Estimated Time:** 1 day

#### `NgoService.java` (interface)
Declare:
- Register as NGO — takes user ID and `NgoRequest`
- Get NGO profile by user ID
- Update NGO profile
- Verify an NGO — admin only, sets `verified = true`
- Get all NGOs
- Get all verified NGOs

#### `NgoController.java`
- `POST /api/ngos/register` — register authenticated user as NGO
- `GET /api/ngos/profile` — get own NGO profile
- `PUT /api/ngos/profile` — update profile
- `PUT /api/ngos/{id}/verify` — verify an NGO (admin only)
- `GET /api/ngos` — list all NGOs
- `GET /api/ngos/verified` — list verified NGOs

---

### Task 24 — Food Request Service + Controller
**Files:** `service/FoodRequestService.java`, `service/impl/
FoodRequestServiceImpl
.java`, `controller/FoodRequestController.java`
**Estimated Time:** 1 day

NGOs use this feature to request available food donations.

#### `FoodRequestService.java` (interface)
Declare:
- Create a food request — takes NGO's user ID and `FoodRequestDto`
- Get request by ID
- Get all requests by NGO
- Get all requests for a donation
- Approve a request — sets status to APPROVED, admin or donor
- Reject a request — sets status to REJECTED

#### `FoodRequestServiceImpl.java`
When creating a request, verify the NGO exists and is verified. Verify the
 donation exists and is in PENDING or ACCEPTED status. Prevent duplicate 
requests for the same donation by the same NGO.

#### `FoodRequestController.java`
- `POST /api/food-requests` — create a request
- `GET /api/food-requests/my` — get the authenticated NGO's requests
- `GET /api/food-requests/donation/{donationId}` — get all requests for
 a donation
- `PATCH /api/food-requests/{id}/approve` — approve a request
- `PATCH /api/food-requests/{id}/reject` — reject a request

---

### Task 25 — Pickup Service + Controller
**Files:** `service/PickupService.java`, `service/impl/PickupServiceImpl
.java`, `controller/PickupController.java`
**Estimated Time:** 1 day

Handles the logistics of food pickup by volunteers.

#### `PickupService.java` (interface)
Declare:
- Create a pickup request — takes donation ID and scheduled time
- Assign a volunteer to a pickup — takes pickup ID and volunteer user ID
- Update pickup status — takes pickup ID and new status
- Get pickups by volunteer
- Get pickup by donation ID
- Get all pickups (admin)

#### `PickupServiceImpl.java`
When assigning a volunteer, check they exist and are available. After
 assignment, set volunteer's `available` to false. When status changes
 to COMPLETED or FAILED, set volunteer back to available.

#### `PickupController.java`
- `POST /api/pickups` — create pickup request
- `PATCH /api/pickups/{id}/assign/{volunteerId}` — assign volunteer
- `PATCH /api/pickups/{id}/status` — update status
- `GET /api/pickups/my` — get pickups for authenticated volunteer
- `GET /api/pickups` — get all pickups (admin only)

---

### Task 26 — Notification Service + Controller
**Files:** `service/NotificationService.java`, `service/impl/
NotificationServiceImpl.java`, `controller/NotificationController.java`
**Estimated Time:** Half day

#### `NotificationService.java` (interface)
Declare:
- Send a notification — takes user ID, title, message, type
- Get all notifications for authenticated user
- Get unread notifications for authenticated user
- Mark a notification as read — takes notification ID
- Mark all notifications as read for a user

#### `NotificationController.java`
- `GET /api/notifications` — get all notifications for authenticated 
user
- `GET /api/notifications/unread` — get unread notifications
- `PATCH /api/notifications/{id}/read` — mark one as read
- `PATCH /api/notifications/read-all` — mark all as read

---

### Task 27 — Report Service + Controller
**Files:** `service/ReportService.java`, `service/impl/
ReportServiceImpl.java`, `controller/ReportController.java`
**Estimated Time:** Half day

Provides analytics for the admin dashboard.

#### `ReportService.java` (interface)
Declare:
- Get system-wide stats — total donations, pickups, requests,
 active volunteers, active NGOs
- Get donations report filtered by a date range

#### `ReportServiceImpl.java`
Use repository `count()` methods and custom queries to aggregate
 the numbers. Build and return a `ReportResponse` DTO.

#### `ReportController.java`
- `GET /api/reports/stats` — overall system stats (admin only)
- `GET /api/reports/donations?from=&to=` — donations between 
two dates (admin only)

---

### Task 28 — Admin Service + Controller
**Files:** `service/AdminService.java`, `service/impl/
AdminServiceImpl.java`, `controller/AdminController.java`
**Estimated Time:** 1 day

Admin-only operations for managing the platform.

#### `AdminService.java` (interface)
Declare:
- Get all users
- Enable or disable a user account — takes user ID, toggles 
the `enabled` field
- Verify an NGO — takes NGO ID, sets `verified = true`
- Get dashboard stats — returns `ReportResponse`
- Get all donations (system-wide view)
- Get all pickup requests (system-wide view)

#### `AdminController.java`
All routes in this controller are restricted to `ADMIN` role only.

- `GET /api/admin/users` — list all users
- `PUT /api/admin/users/{id}/toggle` — enable/disable a user
- `PUT /api/admin/ngos/{id}/verify` — verify an NGO
- `GET /api/admin/dashboard` — get dashboard statistics
- `GET /api/admin/donations` — all donations
- `GET /api/admin/pickups` — all pickups

---

### Task 29 — Config Classes
**Package:** `config/`
**Estimated Time:** Half day

#### `CorsConfig.java`
Configure Cross-Origin Resource Sharing so the frontend can communicate
 with this backend. Implement `WebMvcConfigurer` and override 
`addCorsMappings`.

- Allow requests from `http://localhost:3000` (React) and 
`http://localhost:4200` (Angular)
- Allow all HTTP methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
- Allow all headers
- Enable credentials

#### `DatabaseConfig.java`
Add the `@Configuration` annotation. This file can remain mostly
 empty for now — Spring Boot auto-configures the database from
 `application.properties`. It can be extended later for
 connection pool tuning.

#### `WebConfig.java`
Add the `@Configuration` annotation. Can be used later to configure 
static resource serving, message converters, or request interceptors
 if needed.

---

### Task 30 — Final Testing & Cleanup
**Estimated Time:** 2 days

#### End-to-End Testing Checklist (use Postman)

Go through each flow in order:

- [ ] Register a new user with role DONOR — check the
 `users` table in MySQL
- [ ] Login with that user — copy the JWT token from
 the response
- [ ] Add `Authorization: Bearer <token>` header to all 
subsequent requests
- [ ] Register the user as a donor — check the `donors` table
- [ ] Create a food donation — check `food_donations` table
- [ ] Register a second user as NGO
- [ ] Login as the Admin user and verify the NGO
- [ ] Login as the NGO user and create a food request for 
the donation
- [ ] Approve the food request as admin or donor
- [ ] Register a third user as a volunteer
- [ ] Create a pickup request for the donation and assign
 the volunteer
- [ ] Update the pickup status to IN_PROGRESS, then COMPLETED
- [ ] Check that the volunteer's `available` field is set
 back to true
- [ ] Get notifications for the donor user — should see 
relevant notifications
- [ ] Mark notifications as read
- [ ] Test Admin dashboard stats — numbers should match
 what's in the DB
- [ ] Try accessing `/api/admin/users` without a token —
 should get 401
- [ ] Try accessing `/api/admin/users` with a non-admin
 token — should get 403
- [ ] Try getting a donation with an ID that doesn't 
exist — should get 404 with proper error message

#### Code Cleanup Checklist
- [ ] Delete `Entity/Donar.java` (typo file — `Donor.java`
 is correct)
- [ ] Make sure every `@RestController` class has `@Request
Mapping` with a base path
- [ ] Make sure every `ServiceImpl` class has `@Service` 
annotation
- [ ] Make sure all passwords in the DB are BCrypt hashes,
 never plain text
- [ ] Make sure `application.properties` is listed in
 `.gitignore` (it contains DB credentials)
- [ ] Make sure `ResponseUtil` is used consistently
 across all controllers
- [ ] Remove any unused imports in all files

---

## Day-by-Day Schedule

| Day | Task |
|-----|------|
| 1 | Project setup, application.properties, Maven dependencies |
| 2 | All Enums |
| 3 | User entity |
| 4 | Address + Donor entities |
| 5 | Role + FoodDonation entities |
| 6 | FoodItem + Volunteer entities |
| 7 | Ngo + FoodRequest + PickupRequest + Notification entities |
| 8 | All Repositories |
| 9 | Buffer / Review entities and repositories |
| 10 | UserPrincipal + CustomUserDetailsService |
| 11 | JwtService + JwtAuthenticationFilter |
| 12 | SecurityConfig |
| 13 | All DTOs |
| 14 | Exception classes + GlobalExceptionHandler + Utility classes |
| 15 | Auth Service + AuthServiceImpl |
| 16 | AuthController + Test login/register with Postman |
| 17 | User Service + Controller |
| 18 | Donor Service + Controller |
| 19 | Donation Service + Controller |
| 20 | Volunteer Service + Controller |
| 21 | NGO Service + Controller |
| 22 | Food Request Service + Controller |
| 23 | Pickup Service + Controller |
| 24 | Notification Service + Controller |
| 25 | Report Service + Controller |
| 26 | Admin Service + Controller |
| 27 | Config classes (CORS, Web, Database) |
| 28 | Full end-to-end Postman testing |
| 29 | Bug fixes based on testing |
| 30 | Code cleanup, final review, submit |

---

## Quick File Reference

| You want to work on... | File location |
|------------------------|---------------|
| User table structure | `Entity/User.java` |
| Donor table structure | `Entity/Donor.java` |
| Food donation table | `Entity/FoodDonation.java` |
| NGO table | `Entity/Ngo.java` |
| Volunteer table | `Entity/Volunteer.java` |
| Pickup table | `Entity/PickupRequest.java` |
| JWT token logic | `security/JwtService.java` |
| Login/register logic | `service/impl/AuthServiceImpl.java` |
| Login/register API | `controller/AuthController.java` |
| Donation logic | `service/impl/DonationServiceImpl.java` |
| Donation API | `controller/DonationController.java` |
| Error handling | `exception/GlobalExceptionHandler.java` |
| API response format | `util/ResponseUtil.java` |
| Spring Security config | `security/SecurityConfig.java` |
| Database connection | `src/main/resources/application.properties` |
| Maven dependencies | `pom.xml` |
