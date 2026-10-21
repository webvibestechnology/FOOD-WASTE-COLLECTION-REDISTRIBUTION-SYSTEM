# Setup & Execution Guide

This document describes how to set up, configure, run, and test the **Food Waste Collection & Redistribution System** from source.

---

## 🐳 Docker One-Command Deployment (Recommended)

If you have **Docker** and **Docker Compose** installed, you can spin up the entire stack (MySQL 8, Spring Boot Backend, and Angular Nginx Frontend) with a single command:

```powershell
# From the project root
docker compose up --build -d
```

- **Frontend Application**: `http://localhost:80` (or `http://localhost:4200`)
- **Backend API**: `http://localhost:8080/api`
- **MySQL Database**: `localhost:3306`

To shut down the stack:
```powershell
docker compose down
```

---

## 📋 Prerequisites (For Local Non-Containerized Run)

Ensure the following tools are installed on your machine:
- **Java Development Kit (JDK) 21** or higher
- **Node.js 20+** and **npm**
- **MySQL 8.0+** running on `localhost:3306`

---

## 🗄️ 1. Database Setup

1. Log in to your MySQL server:
   ```sql
   mysql -u root -p
   ```
2. Create the application database:
   ```sql
   CREATE DATABASE IF NOT EXISTS food_waste_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. The application includes self-healing schema creation and idempotent seed data automatically on startup.

---

## ⚙️ 2. Backend Configuration & Run

### Environment Variables
Configure your database password and JWT secret using environment variables or inspect `Backend/food-waste-backend/.env.example`:

| Variable | Description | Default / Example |
| :--- | :--- | :--- |
| `DB_URL` | JDBC Connection URL | `jdbc:mysql://localhost:3306/food_waste_db` |
| `DB_USERNAME` | MySQL Username | `root` |
| `DB_PASSWORD` | MySQL Password | `7447Swati@` (or your root password) |
| `JWT_SECRET` | HMAC-SHA256 Base64 Secret | Minimum 256 bits |
| `PORT` | HTTP Server Port | `8080` |

### Running the Backend
From the root directory:
```powershell
cd Backend/food-waste-backend
./mvnw.cmd spring-boot:run
```
The REST API will be available at `http://localhost:8080/api`.

### Running Backend Tests
Execute JUnit 5 & Mockito test suites:
```powershell
cd Backend/food-waste-backend
./mvnw.cmd test
```
All 15 tests will execute and report `BUILD SUCCESS`.

---

## 💻 3. Frontend Configuration & Run

### Installing Dependencies
From the root directory:
```powershell
cd Frontend/food-waste-frontend
npm install
```

### Running the Angular Development Server
```powershell
cd Frontend/food-waste-frontend
npm start
```
Open your browser and navigate to `http://localhost:4200`.

### Running Frontend Tests
```powershell
cd Frontend/food-waste-frontend
npx ng test --watch=false
```
All 43 test suites will execute and pass.

### Running Production Build
```powershell
cd Frontend/food-waste-frontend
npm run build
```
Production bundle compiles into `dist/food-waste-frontend` with 0 errors and 0 warnings.

---

## 🔑 4. Pre-Configured Test Credentials

The database initializes with the following demo user accounts:

| Role | Email | Password | Status |
| :--- | :--- | :--- | :--- |
| **Platform Admin** | `admin@foodwaste.org` | `Password@123` | Active / Admin |
| **Food Donor** | `john.doe@example.com` | `Password@123` | Active / Donor Profile |
| **Verified NGO** | `jane.smith@ngo.org` | `Password@123` | Active / Verified NGO Profile |
| **Volunteer Driver** | `mike.johnson@volunteer.org` | `Password@123` | Active / Volunteer Profile |

---

## 🌐 5. Key REST API Endpoints

- **Public Landing Page Stats**: `GET /api/reports/stats`
- **Authentication**: `POST /api/auth/login`, `POST /api/auth/register`, `POST /api/auth/forgot-password`, `POST /api/auth/contact`
- **Donations**: `GET /api/donations`, `POST /api/donations`, `GET /api/donations/my-donations`, `DELETE /api/donations/{id}`
- **Redistribution Requests**: `POST /api/food-requests`, `GET /api/food-requests/my-requests`, `POST /api/food-requests/{id}/approve`, `POST /api/food-requests/{id}/reject`, `POST /api/food-requests/{id}/cancel`
- **Pickup & Delivery**: `GET /api/pickups/available`, `GET /api/pickups/my-pickups`, `POST /api/pickups/{id}/claim`, `PUT /api/pickups/{id}/status`
- **Admin**: `GET /api/admin/users`, `PUT /api/admin/ngos/{id}/verify`, `GET /api/admin/audit-logs`, `GET /api/reports/export/csv`
