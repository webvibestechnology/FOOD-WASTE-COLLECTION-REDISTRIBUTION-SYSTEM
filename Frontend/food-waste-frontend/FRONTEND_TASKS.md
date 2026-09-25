# Food Waste Collection & Redistribution System — Frontend
## Intern Task Guide (5 Days)

---

## Project Overview

This is an Angular 21 frontend connecting to the Spring Boot REST API backend.
The app supports four user roles: Donor, Volunteer, NGO, and Admin — each with their own dashboards and features.

**Tech Stack:** Angular 21, TypeScript, RxJS, Angular Router, Angular Forms, Angular HttpClient

**Backend API base URL:** `http://localhost:8080/api`

**Run the frontend with:** `npm start` (inside the `food-waste-frontend/` folder)

---

## How Angular Works (Quick Reference)

- **Component** — a TypeScript class + HTML template + CSS. Handles one piece of the UI.
- **Service** — a class that makes HTTP calls to the backend. Injected into components.
- **Model/Interface** — a TypeScript interface defining the shape of API data.
- **Guard** — runs before a route loads. Returns true or redirects.
- **Interceptor** — runs before every HTTP request. Used to attach the JWT token.
- **Routes** — defined in `app.routes.ts`. Maps URL paths to components.

---

## Important Rules

- Store JWT token in `localStorage` under the key `token`
- Store logged-in user info (id, name, role) in `localStorage` under the key `user`
- Always use Angular's `HttpClient` for API calls — never use `fetch` directly
- Use reactive forms (`FormBuilder`, `FormGroup`, `Validators`) for all forms
- Never store plain passwords in localStorage
- All routes that require login must be protected by `auth-guard.ts`
- Role-specific routes must also use their role guard

---

## DAY 1 — Foundation: App Config, Models, Interceptor, Auth Service, Guards, All Services

---

### Task 1 — App Configuration
**📂 Open this file:** `src/app/app.config.ts`

**What to do:**
The app config is the entry point for all Angular providers. Without this set up correctly, HttpClient and Router won't work anywhere in the app.

- Import `provideHttpClient` and `withInterceptors` from `@angular/common/http`
- Import `provideRouter` from `@angular/router`
- Import `routes` from `./app.routes`
- Import `authInterceptor` from `./interceptors/auth-interceptor`
- Inside the `providers` array of `appConfig`, add:
  - `provideRouter(routes)` — registers all your route definitions
  - `provideHttpClient(withInterceptors([authInterceptor]))` — enables HTTP with the JWT interceptor

**Done when:** The app compiles and starts at `http://localhost:4200` without "No provider for HttpClient" errors.

---

### Task 2 — TypeScript Models
**📂 Open these files one by one — all are inside `src/app/models/`**

All these files already exist as empty interfaces. You need to add the correct fields to each one. These shapes must match exactly what the backend API returns.

---

**File: `src/app/models/user.ts`**

Add these fields to the `User` interface:
- `id` — type: number
- `name` — type: string
- `email` — type: string
- `phone` — type: string, mark as optional with `?`
- `role` — type: string (possible values from backend: `'ADMIN'`, `'DONOR'`, `'VOLUNTEER'`, `'NGO'`)
- `createdAt` — type: string, mark as optional with `?`

---

**File: `src/app/models/donation.ts`**

Add these fields to the `Donation` interface:
- `id` — number
- `title` — string
- `description` — string, optional
- `quantity` — number
- `quantityUnit` — string (e.g., `'kg'`, `'servings'`, `'packets'`)
- `category` — string (e.g., `'COOKED_FOOD'`, `'FRUITS'`, etc.)
- `status` — string (values: `'PENDING'`, `'ACCEPTED'`, `'PICKED_UP'`, `'DELIVERED'`, `'CANCELLED'`, `'EXPIRED'`)
- `expiryTime` — string
- `donorId` — number
- `donorName` — string
- `createdAt` — string

---

**File: `src/app/models/food.ts`**

Add these fields to the `Food` interface:
- `id` — number
- `name` — string
- `quantity` — number
- `unit` — string
- `category` — string

---

**File: `src/app/models/food-request.ts`**

Add these fields to the `FoodRequest` interface:
- `id` — number
- `donationId` — number
- `donationTitle` — string
- `ngoId` — number
- `ngoName` — string
- `status` — string (values: `'PENDING'`, `'APPROVED'`, `'REJECTED'`, `'FULFILLED'`, `'CANCELLED'`)
- `notes` — string, optional
- `requestedAt` — string

---

**File: `src/app/models/ngo.ts`**

Add these fields to the `Ngo` interface:
- `id` — number
- `ngoName` — string
- `registrationNumber` — string
- `description` — string, optional
- `contactPerson` — string
- `verified` — boolean
- `registeredAt` — string
- `userId` — number

---

**File: `src/app/models/notification.ts`**

Add these fields to the `Notification` interface:
- `id` — number
- `title` — string
- `message` — string
- `type` — string (e.g., `'DONATION_CREATED'`, `'PICKUP_ASSIGNED'`, `'GENERAL'`)
- `isRead` — boolean
- `createdAt` — string

---

**File: `src/app/models/pickup.ts`**

Add these fields to the `Pickup` interface:
- `id` — number
- `donationTitle` — string
- `volunteerName` — string
- `status` — string (values: `'SCHEDULED'`, `'IN_PROGRESS'`, `'COMPLETED'`, `'FAILED'`, `'CANCELLED'`)
- `scheduledTime` — string
- `completedTime` — string, optional

---

**File: `src/app/models/volunteer.ts`**

Add these fields to the `Volunteer` interface:
- `id` — number
- `volunteerName` — string
- `available` — boolean
- `vehicleType` — string (e.g., `'bike'`, `'car'`, `'none'`)
- `operatingArea` — string
- `userId` — number

---

**Done when:** All 8 model files have their fields and compile without errors.

---

### Task 3 — Auth Interceptor
**📂 Open this file:** `src/app/interceptors/auth-interceptor.ts`

**What to do:**
This interceptor runs automatically before every single HTTP request and attaches the JWT token so the backend knows who is making the request.

- The file already has the class skeleton. Replace it with a functional interceptor using `HttpInterceptorFn` type
- The function receives two parameters: `req` (the outgoing request) and `next` (the handler to continue the chain)
- Read the token: `const token = localStorage.getItem('token')`
- If a token exists: clone the request and add the header — `Authorization: Bearer <token>` — then call `next(clonedRequest)`
- If no token exists: just call `next(req)` unchanged
- Export this as a named export `authInterceptor`

**Done when:** Opening browser DevTools → Network tab, making any API call while logged in shows `Authorization: Bearer <token>` in the request headers.

---

### Task 4 — Auth Service
**📂 Open this file:** `src/app/services/auth.ts`

**What to do:**
This is the most important service in the project. It handles login, registration, and session state throughout the app.

The class already exists with `@Injectable`. Add the following:

- Inject `HttpClient` in the constructor: `private http: HttpClient`
- Define: `private apiUrl = 'http://localhost:8080/api/auth'`

**Add these methods:**

`login(email: string, password: string)` — makes a POST request to `${this.apiUrl}/login` with body `{ email, password }`. Returns an Observable. The caller (component) will subscribe and on success receive a response containing `{ token, userId, name, email, role }`. The component will then save those to localStorage.

`register(name: string, email: string, password: string, phone: string, role: string)` — makes a POST to `${this.apiUrl}/register` with the registration data. Returns an Observable.

`logout()` — calls `localStorage.removeItem('token')` and `localStorage.removeItem('user')` to clear the session.

`isLoggedIn(): boolean` — returns `!!localStorage.getItem('token')` (true if token exists, false if not).

`getCurrentUser(): any` — reads the `user` JSON string from localStorage and parses it with `JSON.parse()`. Returns the user object, or `null` if nothing is stored.

`getUserRole(): string | null` — calls `getCurrentUser()` and returns `user?.role` or `null`.

`isAdmin(): boolean` — returns `this.getUserRole() === 'ADMIN'`

`isDonor(): boolean` — returns `this.getUserRole() === 'DONOR'`

`isNgo(): boolean` — returns `this.getUserRole() === 'NGO'`

`isVolunteer(): boolean` — returns `this.getUserRole() === 'VOLUNTEER'`

`saveSession(token: string, user: any): void` — saves the token and user to localStorage. Called from the login component after a successful login response.

**Done when:** Login works end-to-end — token is saved to localStorage, logout clears it.

---

### Task 5 — Route Guards
**📂 Open these files one by one — all are inside `src/app/guards/`**

Guards protect routes. If the guard returns false, Angular redirects the user away. All guards already have skeleton files.

---

**File: `src/app/guards/auth-guard.ts`**

- Import `CanActivateFn` from `@angular/router`
- Import `inject` from `@angular/core`
- Import `Router` from `@angular/router`
- Import `Auth` (your auth service) from `../services/auth`
- Write the guard function: get the auth service using `inject(Auth)` and the router using `inject(Router)`
- If `authService.isLoggedIn()` returns true → return `true`
- Otherwise → call `router.navigate(['/login'])` and return `false`
- Export it as `authGuard`

---

**File: `src/app/guards/admin-guard.ts`**

- Same structure as `auth-guard.ts`
- Check `authService.isAdmin()` instead
- If not admin → navigate to `'/'` (home page) and return false
- Export as `adminGuard`

---

**File: `src/app/guards/donor-guard.ts`**

- Check `authService.isDonor()`
- If not donor → navigate to `'/'` and return false
- Export as `donorGuard`

---

**File: `src/app/guards/ngo-guard.ts`**

- Check `authService.isNgo()`
- If not NGO → navigate to `'/'` and return false
- Export as `ngoGuard`

---

**File: `src/app/guards/volunteer-guard.ts`**

- Check `authService.isVolunteer()`
- If not volunteer → navigate to `'/'` and return false
- Export as `volunteerGuard`

---

**Done when:** Going to `/donor/dashboard` without being logged in redirects to `/login`. Going to `/admin/dashboard` as a donor redirects to `/`.

---

### Task 6 — All Remaining Services
**📂 Open these files one by one — all are inside `src/app/services/`**

Every service below follows the same pattern:
1. The class already has `@Injectable({ providedIn: 'root' })`
2. Inject `HttpClient` in the constructor
3. Define a `private baseUrl` pointing to the backend endpoint
4. Add the methods listed

---

**File: `src/app/services/donation.ts`**

`baseUrl = 'http://localhost:8080/api/donations'`

Methods:
- `createDonation(data: any)` — POST to `${baseUrl}` with `data` as the body
- `getAllDonations()` — GET `${baseUrl}`
- `getMyDonations()` — GET `${baseUrl}/my`
- `getDonationById(id: number)` — GET `${baseUrl}/${id}`
- `updateDonationStatus(id: number, status: string)` — PATCH to `${baseUrl}/${id}/status` with query param `?status=${status}` appended to the URL
- `deleteDonation(id: number)` — DELETE `${baseUrl}/${id}`

---

**File: `src/app/services/food.ts`**

`baseUrl = 'http://localhost:8080/api/food'`

Methods:
- `getFoodItems(donationId: number)` — GET `${baseUrl}?donationId=${donationId}`

---

**File: `src/app/services/ngo.ts`**

`baseUrl = 'http://localhost:8080/api/ngos'`

Methods:
- `registerAsNgo(data: any)` — POST `${baseUrl}/register` with `data` as body
- `getMyNgoProfile()` — GET `${baseUrl}/profile`
- `updateNgoProfile(data: any)` — PUT `${baseUrl}/profile` with `data` as body
- `getAllNgos()` — GET `${baseUrl}`
- `getVerifiedNgos()` — GET `${baseUrl}/verified`

---

**File: `src/app/services/volunteer.ts`**

`baseUrl = 'http://localhost:8080/api/volunteers'`

Methods:
- `registerAsVolunteer(data: any)` — POST `${baseUrl}/register` with `data` as body
- `getMyVolunteerProfile()` — GET `${baseUrl}/profile`
- `updateProfile(data: any)` — PUT `${baseUrl}/profile` with `data` as body
- `toggleAvailability()` — PATCH `${baseUrl}/availability` with empty body `{}`
- `getAvailableVolunteers()` — GET `${baseUrl}/available`

---

**File: `src/app/services/pickup.ts`**

`baseUrl = 'http://localhost:8080/api/pickups'`

Methods:
- `createPickup(donationId: number, scheduledTime: string)` — POST `${baseUrl}` with body `{ donationId, scheduledTime }`
- `getMyPickups()` — GET `${baseUrl}/my`
- `getAllPickups()` — GET `${baseUrl}`
- `updatePickupStatus(id: number, status: string)` — PATCH `${baseUrl}/${id}/status?status=${status}`
- `assignVolunteer(pickupId: number, volunteerId: number)` — PATCH `${baseUrl}/${pickupId}/assign/${volunteerId}` with empty body `{}`

---

**File: `src/app/services/notification.ts`**

`baseUrl = 'http://localhost:8080/api/notifications'`

Methods:
- `getMyNotifications()` — GET `${baseUrl}`
- `getUnreadNotifications()` — GET `${baseUrl}/unread`
- `markAsRead(id: number)` — PATCH `${baseUrl}/${id}/read` with empty body `{}`
- `markAllAsRead()` — PATCH `${baseUrl}/read-all` with empty body `{}`

---

**File: `src/app/services/report.ts`**

`baseUrl = 'http://localhost:8080/api/reports'`

Methods:
- `getSystemStats()` — GET `${baseUrl}/stats`

---

**File: `src/app/services/user.ts`**

`baseUrl = 'http://localhost:8080/api/users'`

Methods:
- `getUserById(id: number)` — GET `${baseUrl}/${id}`
- `updateUser(id: number, data: any)` — PUT `${baseUrl}/${id}` with `data` as body

---

**File: `src/app/services/admin.ts`**

`baseUrl = 'http://localhost:8080/api/admin'`

Methods:
- `getAllUsers()` — GET `${baseUrl}/users`
- `toggleUserStatus(id: number)` — PUT `${baseUrl}/users/${id}/toggle` with empty body `{}`
- `verifyNgo(ngoId: number)` — PUT `${baseUrl}/ngos/${ngoId}/verify` with empty body `{}`
- `getDashboardStats()` — GET `${baseUrl}/dashboard`
- `getAllDonations()` — GET `${baseUrl}/donations`
- `getAllPickups()` — GET `${baseUrl}/pickups`

---

**Done when:** All services compile. You can test by temporarily calling one service in a component and checking the browser network tab for the request.

---

## DAY 2 — Routing + Shared Navbar + Authentication Pages + Static Pages

---

### Task 7 — App Routes
**📂 Open this file:** `src/app/app.routes.ts`

**What to do:**
The `routes` array is currently empty. Add all the route definitions for the entire application.

Import all component classes at the top of the file. Then define the routes:

**Public routes (no guard):**
- path `''` → `HomeComponent`
- path `'login'` → `LoginComponent`
- path `'register'` → `RegisterComponent`
- path `'forgot-password'` → `ForgotPasswordComponent`
- path `'about'` → `AboutComponent`
- path `'contact'` → `ContactComponent`
- path `'**'` → `NotFoundComponent` — this MUST be the last route

**Auth-only route (needs `canActivate: [authGuard]`):**
- path `'notifications'` → `NotificationsComponent`

**Donor routes (needs `canActivate: [authGuard, donorGuard]`):**
- path `'donor/dashboard'` → `DonorDashboardComponent`
- path `'donor/profile'` → `DonorProfileComponent`
- path `'donor/donate'` → `FoodDonationComponent`
- path `'donor/history'` → `DonationHistoryComponent`

**NGO routes (needs `canActivate: [authGuard, ngoGuard]`):**
- path `'ngo/dashboard'` → `NgoDashboardComponent`
- path `'ngo/requests'` → `FoodRequestComponent`
- path `'ngo/available-food'` → `AvailableFoodComponent`

**Volunteer routes (needs `canActivate: [authGuard, volunteerGuard]`):**
- path `'volunteer/dashboard'` → `VolunteerDashboardComponent`
- path `'volunteer/pickups'` → `PickupRequestsComponent`
- path `'volunteer/pickups/:id'` → `PickupDetailsComponent`

**Admin routes (needs `canActivate: [authGuard, adminGuard]`):**
- path `'admin/dashboard'` → `AdminDashboardComponent`
- path `'admin/users'` → `UserManagementComponent`
- path `'admin/donations'` → `DonationManagementComponent`
- path `'admin/ngos'` → `NgoManagementComponent`
- path `'admin/volunteers'` → `VolunteerManagementComponent`
- path `'admin/reports'` → `ReportsComponent`

**Done when:** Navigating to each URL loads the correct component. Protected routes redirect to login when not logged in.

---

### Task 8 — Root App Shell
**📂 Open these files:**
- `src/app/app.ts`
- `src/app/app.html`
- `src/app/app.css`

**What to do in `src/app/app.ts`:**
- Make sure `RouterOutlet` is imported in the `imports` array of the component decorator
- After you create the Navbar component in Task 9, also import `NavbarComponent` here

**What to do in `src/app/app.html`:**
- Add `<app-navbar></app-navbar>` at the top — this is the shared navigation bar
- Add `<router-outlet></router-outlet>` below it — this is where route components will render
- Add a `<footer>` at the bottom with: app name ("FoodWaste Connect"), current year, and a short tagline

**What to do in `src/app/app.css`:**
- Add a minimum top padding to `router-outlet`'s parent so page content doesn't sit under the navbar

**Done when:** App loads, shows the navbar at top, and renders route content in the middle.

---

### Task 9 — Shared Navbar Component
**📂 Create these new files inside a new folder `src/app/shared/navbar/`:**
- `src/app/shared/navbar/navbar.ts` — TypeScript class
- `src/app/shared/navbar/navbar.html` — HTML template
- `src/app/shared/navbar/navbar.css` — styles

**What to do in `src/app/shared/navbar/navbar.ts`:**
- Create a standalone Angular component with selector `app-navbar`
- Inject `Auth` (auth service) and `Router`
- Import `RouterLink`, `NgIf` (or use `@if`) in the component's imports array
- Add properties:
  - `isLoggedIn: boolean` — set from `authService.isLoggedIn()` on init
  - `userRole: string | null` — set from `authService.getUserRole()`
  - `userName: string` — set from `authService.getCurrentUser()?.name`
- Add `logout()` method — calls `authService.logout()` then `router.navigate(['/login'])`
- Add computed getters: `get isAdmin()`, `get isDonor()`, `get isNgo()`, `get isVolunteer()`

**What to do in `src/app/shared/navbar/navbar.html`:**
- App logo/name on the left — wrapped in a `routerLink="/"` anchor
- Navigation links on the right, conditionally shown based on login state and role:
  - Not logged in → show: Home, About, Contact, Login button, Register button
  - Logged in as DONOR → show: My Dashboard (`/donor/dashboard`), Donate Food (`/donor/donate`), My Donations (`/donor/history`), Notifications (`/notifications`), Logout button
  - Logged in as NGO → show: My Dashboard (`/ngo/dashboard`), Available Food (`/ngo/available-food`), My Requests (`/ngo/requests`), Notifications, Logout
  - Logged in as VOLUNTEER → show: My Dashboard (`/volunteer/dashboard`), My Pickups (`/volunteer/pickups`), Notifications, Logout
  - Logged in as ADMIN → show: Dashboard (`/admin/dashboard`), Users (`/admin/users`), Donations (`/admin/donations`), NGOs (`/admin/ngos`), Volunteers (`/admin/volunteers`), Reports (`/admin/reports`), Logout

**Done when:** Navbar renders correctly and shows different links based on who is logged in.

---

### Task 10 — Login Component
**📂 Open these files:**
- `src/app/components/login/login.ts`
- `src/app/components/login/login.html`
- `src/app/components/login/login.css`

**What to do in `src/app/components/login/login.ts`:**
- Inject `FormBuilder`, `Router`, and `Auth` (auth service)
- Import `ReactiveFormsModule` in the component's imports array
- Create a reactive form named `loginForm` with:
  - `email` field — validators: required, email format
  - `password` field — validators: required, minLength(6)
- Add a `errorMessage: string = ''` property for showing API errors
- Add a `isLoading: boolean = false` property
- Write `onSubmit()` method:
  1. If form is invalid → mark all fields as touched and stop
  2. Set `isLoading = true`
  3. Extract `email` and `password` from form values
  4. Call `authService.login(email, password)` and subscribe
  5. On success: call `authService.saveSession(response.token, response)`, then check the role and navigate to the right dashboard (`/donor/dashboard`, `/ngo/dashboard`, `/volunteer/dashboard`, or `/admin/dashboard`)
  6. On error: set `errorMessage = 'Invalid email or password'`, set `isLoading = false`

**What to do in `src/app/components/login/login.html`:**
- Page heading: "Sign In"
- A `<form>` bound to `loginForm` using `[formGroup]` and `(ngSubmit)`
- Email input bound to `formControlName="email"` — show error text below if invalid and touched
- Password input bound to `formControlName="password"` — show error text below if invalid and touched
- Submit button: "Login" — disable it when `isLoading` is true
- Error message `<div>` that appears when `errorMessage` is not empty
- Link to `/register`: "Don't have an account? Register"
- Link to `/forgot-password`: "Forgot password?"

**Done when:** Login works end-to-end. Redirects to the correct dashboard based on role.

---

### Task 11 — Register Component
**📂 Open these files:**
- `src/app/components/register/register.ts`
- `src/app/components/register/register.html`
- `src/app/components/register/register.css`

**What to do in `src/app/components/register/register.ts`:**
- Inject `FormBuilder`, `Router`, `Auth`
- Import `ReactiveFormsModule`
- Create `registerForm` with fields:
  - `name` — required
  - `email` — required, email validator
  - `phone` — optional (no validators)
  - `password` — required, minLength(6)
  - `confirmPassword` — required
  - `role` — required (options: DONOR, NGO, VOLUNTEER)
- Add a form-level custom validator that checks `password === confirmPassword`. If they don't match, set error `{ passwordMismatch: true }` on the form group
- Write `onSubmit()`:
  1. Validate form
  2. Call `authService.register(name, email, password, phone, role)`
  3. On success: navigate to `/login` with a success message query param
  4. On error: show error message

**What to do in `src/app/components/register/register.html`:**
- Heading: "Create an Account"
- Form with fields: Full Name, Email, Phone (optional), Password, Confirm Password, Role dropdown
- Show field-level validation errors below each invalid field
- Show a form-level error "Passwords do not match" when the custom validator fails
- Submit button: "Register"
- Link to `/login`: "Already have an account? Sign In"

**Done when:** Registration creates a user in the database and redirects to login.

---

### Task 12 — Home Component
**📂 Open these files:**
- `src/app/components/home/home.ts`
- `src/app/components/home/home.html`
- `src/app/components/home/home.css`

**What to do in `src/app/components/home/home.ts`:**
- Inject `Auth` and `Router`
- In `ngOnInit`: check `authService.isLoggedIn()` — if true, redirect to the appropriate dashboard immediately so logged-in users never see the landing page

**What to do in `src/app/components/home/home.html`:**
- Hero section: big headline ("Reduce Food Waste, Feed More People"), subtext, two CTA buttons ("Donate Food" → `/register`, "Find Food" → `/register`)
- Features section: three cards — one for Donors (describe donating food), one for NGOs (describe requesting food), one for Volunteers (describe picking up food)

**Done when:** Home page renders. Logged-in users are redirected to their dashboard automatically.

---

### Task 13 — Static Pages
**📂 Open these files:**
- `src/app/components/not-found/not-found.html` and `not-found.ts`
- `src/app/components/forgot-password/forgot-password.html` and `forgot-password.ts`
- `src/app/components/about/about.html` and `about.ts`
- `src/app/components/contact/contact.html` and `contact.ts`

**`not-found` component:**
- Display "404 — Page Not Found"
- A link back to `/`: "Go back to Home"

**`forgot-password` component:**
- A form with a single email input
- On submit: show a message "If this email exists, a reset link will be sent" — no API call needed yet

**`about` component:**
- Static text explaining the platform's mission and how it works for each role

**`contact` component:**
- A form with Name, Email, Message fields
- On submit: show a success message — no API call needed yet

---

## DAY 3 — Donor Features + NGO Features

---

### Task 14 — Donor Dashboard
**📂 Open these files:**
- `src/app/components/donor-dashboard/donor-dashboard.ts`
- `src/app/components/donor-dashboard/donor-dashboard.html`
- `src/app/components/donor-dashboard/donor-dashboard.css`

**What to do in `donor-dashboard.ts`:**
- Inject `Donation` (donation service) and `Auth`
- Declare: `donations: any[] = []`, `isLoading = false`
- In `ngOnInit`: set `isLoading = true`, call `donationService.getMyDonations()`, subscribe — store result in `donations`, set `isLoading = false`
- Add computed getters:
  - `get totalDonations()` — returns `this.donations.length`
  - `get activeDonations()` — filters donations where status is `'PENDING'` or `'ACCEPTED'`
  - `get deliveredDonations()` — filters where status is `'DELIVERED'`
  - `get recentDonations()` — returns the last 5 items from `donations`

**What to do in `donor-dashboard.html`:**
- Welcome message: "Welcome, [userName]!"
- Three stat cards: Total Donations, Active Donations, Delivered Donations — each showing the count
- A "Create New Donation" button linking to `/donor/donate`
- A table showing `recentDonations` with columns: Title, Category, Status, Expiry Time

**Done when:** Dashboard loads and shows the donor's donation statistics.

---

### Task 15 — Food Donation Form
**📂 Open these files:**
- `src/app/components/food-donation/food-donation.ts`
- `src/app/components/food-donation/food-donation.html`
- `src/app/components/food-donation/food-donation.css`

**What to do in `food-donation.ts`:**
- Inject `FormBuilder`, `Donation` (donation service), `Router`
- Import `ReactiveFormsModule`
- Create `donationForm` with these fields:
  - `title` — required
  - `description` — optional
  - `quantity` — required, must be a number, minimum value 1
  - `quantityUnit` — required (options: kg, servings, packets)
  - `category` — required (options: COOKED_FOOD, RAW_VEGETABLES, FRUITS, BAKERY, DAIRY, PACKAGED, OTHER)
  - `expiryTime` — required, plus a custom validator that checks the date is in the future: `new Date(value) > new Date()`
- Declare: `successMessage = ''`, `errorMessage = ''`, `isLoading = false`
- Write `onSubmit()`:
  1. Validate form
  2. Call `donationService.createDonation(formData)`
  3. On success: set `successMessage = 'Donation submitted successfully!'`, then after 2 seconds navigate to `/donor/history`
  4. On error: set `errorMessage = 'Failed to submit donation. Please try again.'`

**What to do in `food-donation.html`:**
- Heading: "Create Food Donation"
- Form with all the fields above
- Show validation errors under each field
- Submit button: "Submit Donation"
- Show `successMessage` in a green box when not empty
- Show `errorMessage` in a red box when not empty

**Done when:** Submitting the form creates a donation in the database and redirects to donation history.

---

### Task 16 — Donation History
**📂 Open these files:**
- `src/app/components/donation-history/donation-history.ts`
- `src/app/components/donation-history/donation-history.html`
- `src/app/components/donation-history/donation-history.css`

**What to do in `donation-history.ts`:**
- Inject `Donation` (donation service)
- Declare: `donations: any[] = []`, `filterStatus = 'ALL'`
- In `ngOnInit`: call `donationService.getMyDonations()` and store results
- Add getter `get filteredDonations()` — if `filterStatus === 'ALL'` return all donations, otherwise filter where `status === filterStatus`

**What to do in `donation-history.html`:**
- Heading: "My Donations"
- A `<select>` dropdown bound to `filterStatus` for filtering: All, PENDING, ACCEPTED, PICKED_UP, DELIVERED, CANCELLED, EXPIRED
- A table showing `filteredDonations` with columns: Title, Category, Quantity, Status, Expiry Time, Created At
- Status displayed as a colored badge (use CSS classes for each status color)
- Empty state message: "No donations found" when `filteredDonations.length === 0`

**Done when:** Donation history loads and the status filter correctly shows/hides donations.

---

### Task 17 — Donor Profile
**📂 Open these files:**
- `src/app/components/donor-profile/donor-profile.ts`
- `src/app/components/donor-profile/donor-profile.html`
- `src/app/components/donor-profile/donor-profile.css`

**What to do in `donor-profile.ts`:**
- Inject `Auth`, `User` (user service), `FormBuilder`
- Import `ReactiveFormsModule`
- Declare: `currentUser: any = null`, `isEditing = false`, `successMessage = ''`
- In `ngOnInit`: set `currentUser = authService.getCurrentUser()`
- Create `editForm` with fields `name` (required) and `phone` (optional) — initialize with current user values
- `toggleEdit()` — flips `isEditing` boolean
- `onSave()` — calls `userService.updateUser(currentUser.id, editForm.value)`, on success: update the `user` key in localStorage with the new values, set `successMessage`, set `isEditing = false`

**What to do in `donor-profile.html`:**
- Profile card showing: Name, Email (not editable), Phone, Role, Created At
- "Edit Profile" button that sets `isEditing = true`
- When `isEditing` is true: show the edit form with Name and Phone inputs, a "Save" button, and a "Cancel" button
- Show `successMessage` when not empty

**Done when:** Profile loads, edit mode toggles, and saving updates the user info.

---

### Task 18 — NGO Dashboard
**📂 Open these files:**
- `src/app/components/ngo-dashboard/ngo-dashboard.ts`
- `src/app/components/ngo-dashboard/ngo-dashboard.html`
- `src/app/components/ngo-dashboard/ngo-dashboard.css`

**What to do in `ngo-dashboard.ts`:**
- Inject `FoodRequest` (food request service, you may need to rename class if conflict) and `Auth`
- In `ngOnInit`: call `foodRequestService.getMyRequests()`, store results
- Add getters: `totalRequests`, `pendingRequests`, `approvedRequests`, `fulfilledRequests` — each filters the list by status
- `get recentRequests()` — returns last 5 requests

**What to do in `ngo-dashboard.html`:**
- Welcome message with NGO user's name
- 4 stat cards: Total Requests, Pending, Approved, Fulfilled
- "Browse Available Food" button → `/ngo/available-food`
- A table of `recentRequests` with columns: Donation Title, Status, Requested At

**Done when:** NGO dashboard loads and shows correct request statistics.

---

### Task 19 — Available Food
**📂 Open these files:**
- `src/app/components/available-food/available-food.ts`
- `src/app/components/available-food/available-food.html`
- `src/app/components/available-food/available-food.css`

**What to do in `available-food.ts`:**
- Inject `Donation` (donation service), `FoodRequest` (food request service)
- In `ngOnInit`: call `donationService.getAllDonations()` — filter and keep only those with status `'PENDING'`
- Declare: `filterCategory = 'ALL'`
- Add getter `get filteredDonations()` — filter by category if `filterCategory !== 'ALL'`
- `requestDonation(donationId: number)` — calls `foodRequestService.createRequest(donationId, '')`, on success show a brief success message and refresh the list

**What to do in `available-food.html`:**
- Heading: "Available Food Donations"
- Category filter dropdown
- A card grid — each card shows: Title, Description, Quantity + Unit, Category, Expiry Time, Donor Name
- Each card has a "Request This Donation" button that calls `requestDonation(donation.id)`
- Empty state: "No food donations available right now"

**Done when:** NGO can browse available donations and submit a request.

---

### Task 20 — Food Request Component
**📂 Open these files:**
- `src/app/components/food-request/food-request.ts`
- `src/app/components/food-request/food-request.html`
- `src/app/components/food-request/food-request.css`

**What to do in `food-request.ts`:**
- Inject `FoodRequest` service (food request service)
- In `ngOnInit`: call `getMyRequests()` and store results in `requests: any[] = []`

**What to do in `food-request.html`:**
- Heading: "My Food Requests"
- Table with columns: Donation Title, Status (colored badge), Notes, Requested At
- Empty state: "You haven't made any food requests yet"

**Done when:** NGO's food request history loads and displays correctly.

---

## DAY 4 — Volunteer + Admin Features

---

### Task 21 — Volunteer Dashboard
**📂 Open these files:**
- `src/app/components/volunteer-dashboard/volunteer-dashboard.ts`
- `src/app/components/volunteer-dashboard/volunteer-dashboard.html`
- `src/app/components/volunteer-dashboard/volunteer-dashboard.css`

**What to do in `volunteer-dashboard.ts`:**
- Inject `Pickup` (pickup service), `Volunteer` (volunteer service), `Auth`
- Declare: `pickups: any[] = []`, `volunteerProfile: any = null`
- In `ngOnInit`: call both `pickupService.getMyPickups()` and `volunteerService.getMyVolunteerProfile()` — store results
- Add getters: `get scheduledPickups()`, `get completedPickups()`, `get recentPickups()` (last 5)
- `toggleAvailability()` — calls `volunteerService.toggleAvailability()`, on success re-fetch the profile to update the availability button text

**What to do in `volunteer-dashboard.html`:**
- Welcome message
- Stats: Total Pickups, Scheduled Pickups, Completed Pickups
- Availability toggle button — shows "Set Unavailable" if `volunteerProfile.available === true`, shows "Set Available" if false
- Table of `recentPickups` with columns: Donation Title, Status, Scheduled Time

**Done when:** Volunteer dashboard loads with correct stats and availability toggle works.

---

### Task 22 — Pickup Requests
**📂 Open these files:**
- `src/app/components/pickup-requests/pickup-requests.ts`
- `src/app/components/pickup-requests/pickup-requests.html`
- `src/app/components/pickup-requests/pickup-requests.css`

**What to do in `pickup-requests.ts`:**
- Inject `Pickup` (pickup service), `Router`
- Declare: `pickups: any[] = []`, `filterStatus = 'ALL'`
- In `ngOnInit`: call `pickupService.getMyPickups()` and store results
- Add getter `get filteredPickups()` — filter by status
- `updateStatus(id: number, status: string)` — calls `pickupService.updatePickupStatus(id, status)`, on success refresh the list

**What to do in `pickup-requests.html`:**
- Heading: "My Pickup Assignments"
- Status filter dropdown
- Table with columns: Donation Title, Scheduled Time, Status (badge), Actions
- Action buttons per row:
  - If status is `'SCHEDULED'` → show "Start Pickup" button (calls `updateStatus(id, 'IN_PROGRESS')`)
  - If status is `'IN_PROGRESS'` → show "Mark Complete" button (calls `updateStatus(id, 'COMPLETED')`) and "Report Failed" button (calls `updateStatus(id, 'FAILED')`)
- Each row title is a link to `/volunteer/pickups/${pickup.id}`

**Done when:** Volunteer can see their pickups and update status.

---

### Task 23 — Pickup Details
**📂 Open these files:**
- `src/app/components/pickup-details/pickup-details.ts`
- `src/app/components/pickup-details/pickup-details.html`
- `src/app/components/pickup-details/pickup-details.css`

**What to do in `pickup-details.ts`:**
- Inject `Pickup` (pickup service), `ActivatedRoute`, `Router`
- In `ngOnInit`: read the `id` from `this.route.snapshot.paramMap.get('id')` — convert to number using `Number()`
- Call some method to load pickup by ID — note: you may need to add a `getPickupById(id)` method to `pickup.ts` service: GET `${baseUrl}/${id}`
- Store result in `pickup: any = null`
- `updateStatus(newStatus: string)` — calls `pickupService.updatePickupStatus(this.pickup.id, newStatus)`, on success refresh pickup data

**What to do in `pickup-details.html`:**
- Heading: "Pickup Details"
- Detail rows: Donation Title, Scheduled Time, Status, Volunteer Name, Completed Time
- Buttons based on current status:
  - `SCHEDULED` → "Start Pickup" button
  - `IN_PROGRESS` → "Mark Complete" button + "Report Failed" button
  - `COMPLETED` / `FAILED` / `CANCELLED` → no action buttons, just show "This pickup is closed"
- A "← Back to Pickups" link → `/volunteer/pickups`

**Done when:** Navigating to `/volunteer/pickups/1` loads the correct pickup and status updates work.

---

### Task 24 — Admin Dashboard
**📂 Open these files:**
- `src/app/components/admin-dashboard/admin-dashboard.ts`
- `src/app/components/admin-dashboard/admin-dashboard.html`
- `src/app/components/admin-dashboard/admin-dashboard.css`

**What to do in `admin-dashboard.ts`:**
- Inject `Admin` (admin service), `Report` (report service)
- Declare: `stats: any = null`, `recentDonations: any[] = []`, `recentPickups: any[] = []`
- In `ngOnInit`:
  - Call `reportService.getSystemStats()` — store in `stats`
  - Call `adminService.getAllDonations()` — take the last 5, store in `recentDonations`
  - Call `adminService.getAllPickups()` — take the last 5, store in `recentPickups`

**What to do in `admin-dashboard.html`:**
- Heading: "Admin Dashboard"
- Stats row — 6 cards: Total Donations, Total Pickups, Total Requests, Active Volunteers, Active NGOs — pulled from `stats`
- Quick navigation buttons: Manage Users, Manage Donations, Manage NGOs, Manage Volunteers, View Reports
- Two side-by-side tables: Recent Donations (Title, Status, Created At) and Recent Pickups (Donation Title, Status, Scheduled Time)

**Done when:** Admin dashboard loads and displays system-wide statistics.

---

### Task 25 — User Management
**📂 Open these files:**
- `src/app/components/user-management/user-management.ts`
- `src/app/components/user-management/user-management.html`
- `src/app/components/user-management/user-management.css`

**What to do in `user-management.ts`:**
- Inject `Admin` (admin service)
- Declare: `users: any[] = []`, `searchQuery = ''`
- In `ngOnInit`: call `adminService.getAllUsers()` and store results
- Add getter `get filteredUsers()` — filters `users` where `name` or `email` contains `searchQuery` (case insensitive)
- `toggleStatus(userId: number)` — calls `adminService.toggleUserStatus(userId)`, on success refresh the user list by calling `getAllUsers()` again

**What to do in `user-management.html`:**
- Heading: "User Management"
- Search input bound to `searchQuery`
- Table showing `filteredUsers` with columns: Name, Email, Role, Status (green "Active" / red "Disabled" badge), Created At, Actions
- Actions column: a "Toggle Status" button per row that calls `toggleStatus(user.id)` — add a `window.confirm('Are you sure?')` check before calling it

**Done when:** Admin can view all users and enable/disable accounts.

---

### Task 26 — NGO Management
**📂 Open these files:**
- `src/app/components/ngo-management/ngo-management.ts`
- `src/app/components/ngo-management/ngo-management.html`
- `src/app/components/ngo-management/ngo-management.css`

**What to do in `ngo-management.ts`:**
- Inject `Ngo` (ngo service), `Admin` (admin service)
- Declare: `ngos: any[] = []`, `filterVerified = 'ALL'`
- In `ngOnInit`: call `ngoService.getAllNgos()` and store results
- Add getter `get filteredNgos()` — filter by `filterVerified`: 'ALL', 'VERIFIED', 'UNVERIFIED'
- `verifyNgo(ngoId: number)` — calls `adminService.verifyNgo(ngoId)`, on success refresh the list

**What to do in `ngo-management.html`:**
- Heading: "NGO Management"
- Filter toggle buttons or dropdown: All / Verified Only / Unverified Only
- Table with columns: NGO Name, Registration Number, Contact Person, Verified (badge), Registered At, Actions
- Actions column: "Verify" button (only visible on unverified rows) that calls `verifyNgo(ngo.id)`

**Done when:** Admin can view NGOs and verify them.

---

### Task 27 — Donation Management
**📂 Open these files:**
- `src/app/components/donation-management/donation-management.ts`
- `src/app/components/donation-management/donation-management.html`
- `src/app/components/donation-management/donation-management.css`

**What to do in `donation-management.ts`:**
- Inject `Admin` (admin service)
- Declare: `donations: any[] = []`, `filterStatus = 'ALL'`
- In `ngOnInit`: call `adminService.getAllDonations()` and store results
- Add getter `get filteredDonations()` — filter by status

**What to do in `donation-management.html`:**
- Heading: "Donation Management"
- Status filter dropdown
- Table with columns: Title, Donor Name, Category, Status (badge), Quantity, Created At
- Empty state message

**Done when:** Admin can view all donations system-wide.

---

### Task 28 — Volunteer Management
**📂 Open these files:**
- `src/app/components/volunteer-management/volunteer-management.ts`
- `src/app/components/volunteer-management/volunteer-management.html`
- `src/app/components/volunteer-management/volunteer-management.css`

**What to do in `volunteer-management.ts`:**
- Inject `Volunteer` (volunteer service)
- Declare: `volunteers: any[] = []`, `filterAvailable = 'ALL'`
- In `ngOnInit`: call `volunteerService.getAvailableVolunteers()` — note: if you need all volunteers you may add `getAllVolunteers()` to the service: GET `/api/volunteers`
- Add getter `get filteredVolunteers()` — filter by availability

**What to do in `volunteer-management.html`:**
- Heading: "Volunteer Management"
- Filter: All / Available / Unavailable
- Table with columns: Name, Email, Vehicle Type, Operating Area, Available (badge), Actions

**Done when:** Admin can view all volunteers filtered by availability.

---

## DAY 5 — Notifications, Reports, Shared Components, Styling, Testing

---

### Task 29 — Notifications Component
**📂 Open these files:**
- `src/app/components/notifications/notifications.ts`
- `src/app/components/notifications/notifications.html`
- `src/app/components/notifications/notifications.css`

**What to do in `notifications.ts`:**
- Inject `Notification` service (notification service — rename to avoid conflict with the browser's built-in Notification: `import { Notification as NotificationService } from '../services/notification'`)
- Declare: `notifications: any[] = []`
- In `ngOnInit`: call `notificationService.getMyNotifications()` — store results
- Add getter `get unreadCount()` — returns count of items where `isRead === false`
- `markRead(id: number)` — calls `notificationService.markAsRead(id)`, then find the notification in the local array and set its `isRead = true` (no re-fetch needed)
- `markAllRead()` — calls `notificationService.markAllAsRead()`, then set all notifications' `isRead = true` locally

**What to do in `notifications.html`:**
- Heading: "Notifications" with `({{unreadCount}})` badge next to it
- "Mark All as Read" button — only visible when `unreadCount > 0`
- A list of notifications — each item shows:
  - Title (bold)
  - Message text
  - Type badge
  - Date/time
  - A blue dot indicator on the left if `isRead === false`
  - Unread items should have a light blue background
- Clicking a notification item calls `markRead(notification.id)`
- Empty state: "You have no notifications"

**Done when:** Notifications load, unread count shows correctly, marking as read updates the UI without a page refresh.

---

### Task 30 — Reports Component
**📂 Open these files:**
- `src/app/components/reports/reports.ts`
- `src/app/components/reports/reports.html`
- `src/app/components/reports/reports.css`

**What to do in `reports.ts`:**
- Inject `Report` (report service)
- Declare: `stats: any = null`, `isLoading = false`
- In `ngOnInit`: set `isLoading = true`, call `reportService.getSystemStats()`, store result in `stats`, set `isLoading = false`

**What to do in `reports.html`:**
- Heading: "System Reports"
- A loading indicator while `isLoading` is true
- 5 stat cards when data is loaded: Total Donations, Total Pickups, Total Requests, Active Volunteers, Active NGOs — each pulling the value from `stats`
- A note at the bottom: "For chart-based reporting, integrate Chart.js or similar library in a future update"

**Done when:** Admin reports page loads and shows correct system-wide numbers.

---

### Task 31 — Shared Loading Spinner
**📂 Create these new files inside a new folder `src/app/shared/loading-spinner/`:**
- `src/app/shared/loading-spinner/loading-spinner.ts`
- `src/app/shared/loading-spinner/loading-spinner.html`
- `src/app/shared/loading-spinner/loading-spinner.css`

**What to do in `loading-spinner.ts`:**
- Create a standalone Angular component with selector `app-loading-spinner`
- Add an `@Input() isLoading: boolean = false` input property

**What to do in `loading-spinner.html`:**
- Use `@if (isLoading)` (or `*ngIf="isLoading"`) to wrap a spinner `<div>`
- Inside: a spinning circle animation — use CSS `border-radius: 50%` + `animation: spin` for the spinner

**Where to use it:**
- In `donor-dashboard.html` — wrap the table in `<app-loading-spinner [isLoading]="isLoading"></app-loading-spinner>`
- In `admin-dashboard.html`
- In any other component that has an `isLoading` property

**Done when:** A spinner appears briefly while data is loading, then disappears when data arrives.

---

### Task 32 — Shared Error Alert
**📂 Create these new files inside a new folder `src/app/shared/error-alert/`:**
- `src/app/shared/error-alert/error-alert.ts`
- `src/app/shared/error-alert/error-alert.html`
- `src/app/shared/error-alert/error-alert.css`

**What to do in `error-alert.ts`:**
- Standalone component with selector `app-error-alert`
- Add `@Input() message: string = ''`
- Add `@Output() dismiss = new EventEmitter<void>()`
- Add method `close()` that emits the `dismiss` event

**What to do in `error-alert.html`:**
- Show the div only when `message` is not empty
- Display the message text
- Add an × close button that calls `close()`

**Where to use it:**
- In `login.html` — replace the raw error div with `<app-error-alert [message]="errorMessage" (dismiss)="errorMessage = ''"></app-error-alert>`
- In `register.html` — same pattern
- In `food-donation.html` — same pattern

**Done when:** Error messages appear and can be dismissed by clicking ×.

---

### Task 33 — Global Styles
**📂 Open this file:** `src/styles.css`

Also open each component's own CSS file as needed.

**What to add in `src/styles.css`:**

CSS custom properties (variables) at the top:
- `--primary: #2d8a4e` (green)
- `--primary-dark: #1f6437`
- `--accent: #e07b2c` (orange)
- `--danger: #dc3545` (red)
- `--warning: #ffc107` (yellow)
- `--success: #198754` (green)
- `--text: #212529`
- `--bg: #f8f9fa`

Global resets:
- `* { box-sizing: border-box; margin: 0; padding: 0; }`
- `body` — set font family to `'Segoe UI', sans-serif`, background `var(--bg)`, color `var(--text)`

Button base styles:
- `.btn` — padding, border-radius, cursor, font-size
- `.btn-primary` — background `var(--primary)`, white text
- `.btn-danger` — background `var(--danger)`, white text
- `.btn-secondary` — background transparent, border `1px solid var(--primary)`

Status badge styles (used in tables):
- `.badge-pending` — yellow background, dark text
- `.badge-accepted` — blue background, white text
- `.badge-delivered`, `.badge-approved`, `.badge-fulfilled` — green
- `.badge-cancelled`, `.badge-rejected`, `.badge-failed` — red
- `.badge-in-progress`, `.badge-picked-up`, `.badge-scheduled` — orange

Card styles:
- `.card` — background white, padding 1.5rem, border-radius 8px, box-shadow

Table styles:
- `table` — width 100%, border-collapse
- `th` — background `var(--primary)`, white text, padding
- `td` — padding, border-bottom 1px solid #dee2e6
- `tr:hover` — light grey background

Form styles:
- `.form-group` — margin-bottom 1rem
- `label` — display block, font-weight 600, margin-bottom 0.25rem
- `input, select, textarea` — width 100%, padding, border, border-radius, font-size
- `.error-text` — color `var(--danger)`, font-size 0.8rem

**Done when:** The entire app looks consistent with green-themed colors, readable tables, styled forms, and status badges.

---

### Task 34 — Final End-to-End Testing
**📂 No specific file — use the browser and browser DevTools**

Run the frontend (`npm start`) with the backend running (`mvn spring-boot:run`). Go through every flow below:

**Auth Flow**
- [ ] Register a new Donor account at `/register`
- [ ] Login with those credentials — confirm redirect to `/donor/dashboard`
- [ ] Logout — confirm redirect to `/login`
- [ ] Try accessing `/donor/dashboard` while logged out — confirm redirect to `/login`
- [ ] Try accessing `/admin/dashboard` as a Donor — confirm redirect to `/`

**Donor Flow**
- [ ] Open `/donor/donate` — fill the form and submit — confirm "Donation submitted" message
- [ ] Open `/donor/history` — confirm the new donation appears
- [ ] Use the status filter — confirm it works
- [ ] Open `/donor/profile` — click Edit, change the name, save — confirm it updates

**NGO Flow**
- [ ] Register and login as an NGO user
- [ ] Open `/ngo/available-food` — confirm PENDING donations appear
- [ ] Click "Request This Donation" — confirm a success message appears
- [ ] Open `/ngo/requests` — confirm the request appears with PENDING status

**Volunteer Flow**
- [ ] Register and login as a Volunteer
- [ ] Open `/volunteer/pickups` — confirm assigned pickups appear
- [ ] Click "Start Pickup" — confirm status changes to IN_PROGRESS
- [ ] Click "Mark Complete" — confirm status changes to COMPLETED
- [ ] Check that the availability toggle on `/volunteer/dashboard` works

**Admin Flow**
- [ ] Login as Admin (manually set role in the DB if needed)
- [ ] Open `/admin/dashboard` — confirm stats cards show numbers
- [ ] Open `/admin/users` — search for a user by name — confirm filter works
- [ ] Click "Toggle Status" on a user — confirm it disables/enables
- [ ] Open `/admin/ngos` — click "Verify" on an unverified NGO — confirm badge changes

**Notifications**
- [ ] Perform an action that triggers a notification on the backend
- [ ] Open `/notifications` — confirm the notification appears with unread badge
- [ ] Click the notification — confirm it turns "read" and the badge count drops
- [ ] Click "Mark All as Read" — confirm all notifications are marked read

---

## Day-by-Day Summary

| Day | What to work on |
|-----|----------------|
| Day 1 | `app.config.ts`, all 8 model files, `auth-interceptor.ts`, `auth.ts` service, all 5 guard files, all 10 service files |
| Day 2 | `app.routes.ts`, `app.ts` + `app.html`, navbar component (new files), `login.ts`+`login.html`, `register.ts`+`register.html`, `home.ts`+`home.html`, 4 static page components |
| Day 3 | `donor-dashboard.ts`+`html`, `food-donation.ts`+`html`, `donation-history.ts`+`html`, `donor-profile.ts`+`html`, `ngo-dashboard.ts`+`html`, `available-food.ts`+`html`, `food-request.ts`+`html` |
| Day 4 | `volunteer-dashboard.ts`+`html`, `pickup-requests.ts`+`html`, `pickup-details.ts`+`html`, `admin-dashboard.ts`+`html`, `user-management.ts`+`html`, `ngo-management.ts`+`html`, `donation-management.ts`+`html`, `volunteer-management.ts`+`html` |
| Day 5 | `notifications.ts`+`html`, `reports.ts`+`html`, loading spinner (new files), error alert (new files), `styles.css`, end-to-end testing |

---

## File Location Quick Reference

| Task | File(s) to Open |
|------|----------------|
| App providers setup | `src/app/app.config.ts` |
| All route definitions | `src/app/app.routes.ts` |
| Root layout | `src/app/app.ts` + `src/app/app.html` |
| JWT token attachment | `src/app/interceptors/auth-interceptor.ts` |
| Login/logout/session | `src/app/services/auth.ts` |
| User data shape | `src/app/models/user.ts` |
| Donation data shape | `src/app/models/donation.ts` |
| Pickup data shape | `src/app/models/pickup.ts` |
| Notification data shape | `src/app/models/notification.ts` |
| Volunteer data shape | `src/app/models/volunteer.ts` |
| NGO data shape | `src/app/models/ngo.ts` |
| Food item data shape | `src/app/models/food.ts` |
| Food request data shape | `src/app/models/food-request.ts` |
| Donation API calls | `src/app/services/donation.ts` |
| Pickup API calls | `src/app/services/pickup.ts` |
| NGO API calls | `src/app/services/ngo.ts` |
| Volunteer API calls | `src/app/services/volunteer.ts` |
| Notification API calls | `src/app/services/notification.ts` |
| Report API calls | `src/app/services/report.ts` |
| Admin API calls | `src/app/services/admin.ts` |
| User API calls | `src/app/services/user.ts` |
| Login protection | `src/app/guards/auth-guard.ts` |
| Admin-only protection | `src/app/guards/admin-guard.ts` |
| Donor-only protection | `src/app/guards/donor-guard.ts` |
| NGO-only protection | `src/app/guards/ngo-guard.ts` |
| Volunteer-only protection | `src/app/guards/volunteer-guard.ts` |
| Shared navbar | `src/app/shared/navbar/navbar.ts` + `.html` + `.css` (create these) |
| Loading spinner | `src/app/shared/loading-spinner/loading-spinner.ts` + `.html` + `.css` (create these) |
| Error alert | `src/app/shared/error-alert/error-alert.ts` + `.html` + `.css` (create these) |
| Login page | `src/app/components/login/login.ts` + `login.html` + `login.css` |
| Register page | `src/app/components/register/register.ts` + `register.html` + `register.css` |
| Home landing page | `src/app/components/home/home.ts` + `home.html` + `home.css` |
| 404 page | `src/app/components/not-found/not-found.ts` + `not-found.html` |
| Forgot password page | `src/app/components/forgot-password/forgot-password.ts` + `forgot-password.html` |
| About page | `src/app/components/about/about.ts` + `about.html` |
| Contact page | `src/app/components/contact/contact.ts` + `contact.html` |
| Donor dashboard | `src/app/components/donor-dashboard/donor-dashboard.ts` + `.html` + `.css` |
| Donor profile | `src/app/components/donor-profile/donor-profile.ts` + `.html` + `.css` |
| Donate food form | `src/app/components/food-donation/food-donation.ts` + `.html` + `.css` |
| Donation history | `src/app/components/donation-history/donation-history.ts` + `.html` + `.css` |
| NGO dashboard | `src/app/components/ngo-dashboard/ngo-dashboard.ts` + `.html` + `.css` |
| Browse available food | `src/app/components/available-food/available-food.ts` + `.html` + `.css` |
| NGO food requests | `src/app/components/food-request/food-request.ts` + `.html` + `.css` |
| Volunteer dashboard | `src/app/components/volunteer-dashboard/volunteer-dashboard.ts` + `.html` + `.css` |
| Pickup list | `src/app/components/pickup-requests/pickup-requests.ts` + `.html` + `.css` |
| Pickup detail view | `src/app/components/pickup-details/pickup-details.ts` + `.html` + `.css` |
| Admin dashboard | `src/app/components/admin-dashboard/admin-dashboard.ts` + `.html` + `.css` |
| Admin user management | `src/app/components/user-management/user-management.ts` + `.html` + `.css` |
| Admin NGO management | `src/app/components/ngo-management/ngo-management.ts` + `.html` + `.css` |
| Admin donation view | `src/app/components/donation-management/donation-management.ts` + `.html` + `.css` |
| Admin volunteer view | `src/app/components/volunteer-management/volunteer-management.ts` + `.html` + `.css` |
| Notifications page | `src/app/components/notifications/notifications.ts` + `.html` + `.css` |
| Reports page | `src/app/components/reports/reports.ts` + `.html` + `.css` |
| Global styles | `src/styles.css` |
