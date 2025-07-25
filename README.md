# Lockify Auth Backend

A secure authentication backend built with Spring Boot, supporting JWT-based authentication, password reset, email verification, and profile management.

## Features
- JWT authentication (header and cookie support)
- User registration and login
- Password reset via email OTP
- Email verification
- Profile management
- CORS support for frontend integration

## Tech Stack
- Java 17+
- Spring Boot
- PostgreSQL (default, can be changed)
- SMTP (Brevo) for email

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven
- PostgreSQL database (or update config for your DB)

### Setup
1. **Clone the repository:**
   ```bash
   git clone https://github.com/vipinshivhare/Lockify-Auth-Backend.git
   cd Lockify-Auth-Backend
   ```
2. **Configure environment variables:**
   Set the following variables in your environment or in your deployment platform (Render, etc.):
   - `DB_URL` (e.g., `jdbc:postgresql://...`)
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `JWT_SECRET_KEY`
   - `MAIL_USERNAME`
   - `MAIL_PASSWORD`
   - `MAIL_FROM`

3. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   The API will be available at `http://localhost:8080/api/v1.0` by default.

### API Endpoints
- `POST /api/v1.0/login` — User login (returns JWT)
- `POST /api/v1.0/register` — User registration
- `GET /api/v1.0/profile` — Get user profile (JWT required)
- `POST /api/v1.0/send-reset-otp` — Send password reset OTP
- `POST /api/v1.0/reset-password` — Reset password
- `POST /api/v1.0/send-otp` — Send email verification OTP
- `POST /api/v1.0/verify-otp` — Verify email
- `POST /api/v1.0/logout` — Logout
- `GET /api/v1.0/health` — Health check

### Deployment
- Supports deployment on Render, Heroku, or any cloud supporting Java/Spring Boot.
- Set all required environment variables in your deployment platform.

### CORS
- CORS is configured to allow requests from your frontend domain. Update `SecurityConfig.java` as needed.

## License
This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
