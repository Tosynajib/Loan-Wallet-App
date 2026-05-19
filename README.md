# Fintech Loan Wallet API

A Java Spring Boot fintech backend application for user onboarding, wallet funding, loan processing, repayment tracking, transaction logging, payment webhook handling, scheduled loan monitoring, and asynchronous notifications.

---

## Overview

This project implements a secure fintech loan and wallet management system. Users can create accounts, automatically receive wallets, fund wallets through Paystack, apply for loans, repay loans, and view their transactions. Admin users can approve and disburse loans.

The system also supports RabbitMQ-based asynchronous notifications, notification logs, scheduled repayment reminders, overdue loan detection, Paystack webhook verification, Swagger/OpenAPI documentation, and Docker-based setup.

---

## Features

### Authentication and Security

- User signup
- Login with JWT
- Logout endpoint for stateless JWT clients
- Forgot password
- Password reset with OTP
- Resend OTP with expiry
- BCrypt password encryption
- Spring Security
- JWT authentication
- Role-based authorization
- Admin seeding
- Input validation
- Global exception handling

### Users

- Get authenticated user profile
- Update authenticated user profile
- User role and account status handling

### Wallet

- Wallet auto-created on signup
- Wallet balance check
- Wallet funding through Paystack
- Paystack payment initialization
- Paystack webhook confirmation
- Wallet credit after verified payment
- Wallet balance protection against negative balance

### Loans

- Loan application
- Loan amount rule: maximum 3× wallet balance
- Only funded-wallet users can apply
- Admin loan approval
- Admin loan disbursement
- User loan repayment
- Repayment schedule tracking
- Overdue loan marking
- Defaulted loan status handling

### Transactions

- Transaction history
- Single transaction lookup
- Transaction reference generation
- Transaction status tracking
- Transaction logs for wallet funding, loan disbursement, and repayment

### Notifications

- Notification logs
- RabbitMQ asynchronous notification processing
- Payment confirmation notification
- Loan approval notification
- Loan disbursement notification
- Successful repayment notification
- Repayment reminder notification
- SMS provider abstraction
- Termii SMS provider support
- Email provider abstraction
- JavaMail email provider support
- Local logging providers for development

### Scheduled Jobs

- Send loan repayment reminders
- Mark overdue repayment schedules
- Mark overdue loans as defaulted

### Documentation and DevOps

- Swagger/OpenAPI documentation
- Dockerfile
- Docker Compose for PostgreSQL, RabbitMQ, and application
- Environment-variable based configuration
- Postman collection support

---

## Tech Stack

- Java 17
- Spring Boot 3.5.x
- Spring Security
- Spring Data JPA
- PostgreSQL
- RabbitMQ
- Paystack
- Termii
- JavaMail
- JWT
- Docker
- Swagger/OpenAPI
- Maven

---

## Architecture

The application follows a clean layered structure:

```text
Controller → Facade → Service → Repository
```

External integrations are abstracted through provider interfaces:

```text
PaymentProvider → PaystackPaymentProvider
SmsProvider → LoggingSmsProvider / TermiiSmsProvider
EmailProvider → LoggingEmailProvider / JavaMailEmailProvider
```

Notifications are processed asynchronously:

```text
Business event
↓
NotificationService creates PENDING notification_logs
↓
RabbitMQ event is published
↓
Consumer sends SMS/email
↓
notification_logs updated to SENT or FAILED
```

---

## Main Business Flows

### Wallet Funding Flow

```text
User requests wallet funding
↓
Backend creates PENDING transaction
↓
Backend initializes Paystack payment
↓
User completes payment
↓
Paystack sends webhook
↓
Backend verifies webhook signature
↓
Backend verifies payment with Paystack
↓
Backend credits wallet
↓
Transaction becomes SUCCESSFUL
↓
Payment confirmation event is published to RabbitMQ
↓
Notification event is created
```

### Loan Lifecycle Flow

```text
User applies for loan
↓
System validates funded wallet
↓
System validates loan amount ≤ 3× wallet balance
↓
Loan is created as PENDING
↓
Admin approves loan
↓
Admin disburses loan
↓
User wallet is credited
↓
Transaction log is created
↓
User repays loan
↓
Wallet is debited
↓
Loan and repayment schedule are updated
```

### Notification Flow

```text
Loan approved / loan disbursed / payment confirmed / repayment completed
↓
NotificationService creates notification log as PENDING
↓
RabbitMQ receives notification event
↓
Notification consumer sends SMS/email
↓
notification_logs status becomes SENT or FAILED
```

---

## Project Structure

```text
src/main/java/com/tosin/koins
├── auth
├── common
├── integration
│   ├── email
│   ├── payment
│   └── sms
├── loan
├── notification
├── scheduler
├── transaction
├── user
├── wallet
└── webhook
```

---

## Running Locally Without Docker

Start PostgreSQL and RabbitMQ:

```bash
docker compose up -d postgres rabbitmq
```

Run the application:

```bash
mvn spring-boot:run
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

RabbitMQ UI:

```text
http://localhost:15672
```

RabbitMQ credentials:

```text
Username: koins
Password: koins_password
```

---

## Running With Docker

Build and start all services:

```bash
docker compose up --build
```

Stop services:

```bash
docker compose down
```

Stop and remove volumes:

```bash
docker compose down -v
```

Application URL:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

RabbitMQ UI:

```text
http://localhost:15672
```

---

## Default Admin Account

The application seeds an initial admin account on startup.

```text
Email: admin@koins.local
Password: AdminPassword123
Role: SUPER_ADMIN
```

Admin users can approve and disburse loans depending on their role.

---

## Environment Variables

| Variable | Description |
|---|---|
| `DB_URL` | PostgreSQL JDBC URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | JWT signing secret |
| `JWT_EXPIRATION_MINUTES` | JWT expiry time in minutes |
| `PAYSTACK_BASE_URL` | Paystack API base URL |
| `PAYSTACK_SECRET_KEY` | Paystack secret key |
| `PAYSTACK_CALLBACK_URL` | Paystack payment callback URL |
| `SMS_PROVIDER` | SMS provider: `logging` or `termii` |
| `TERMII_BASE_URL` | Termii API base URL |
| `TERMII_API_KEY` | Termii API key |
| `TERMII_SENDER_ID` | Termii sender ID |
| `TERMII_CHANNEL` | Termii SMS channel |
| `TERMII_MESSAGE_TYPE` | Termii message type |
| `EMAIL_PROVIDER` | Email provider: `logging` or `javamail` |
| `MAIL_HOST` | SMTP host |
| `MAIL_PORT` | SMTP port |
| `MAIL_USERNAME` | SMTP username |
| `MAIL_PASSWORD` | SMTP/app password |
| `MAIL_FROM_ADDRESS` | Email sender address |
| `MAIL_FROM_NAME` | Email sender name |
| `APP_ADMIN_EMAIL` | Initial admin email |
| `APP_ADMIN_PASSWORD` | Initial admin password |
| `APP_ADMIN_FULL_NAME` | Initial admin full name |
| `APP_ADMIN_PHONE` | Initial admin phone number |
| `APP_ADMIN_BVN_OR_NIN` | Initial admin BVN/NIN placeholder |

---

## Paystack Webhook Testing With Ngrok

Paystack cannot call `localhost` directly, so ngrok is used to expose the local backend during development.

Start the app locally:

```bash
mvn spring-boot:run
```

Start ngrok:

```bash
ngrok http 8080
```

Use the generated public URL as the Paystack webhook URL:

```text
https://your-ngrok-url/api/v1/webhooks/paystack
```

Example:

```text
https://example.ngrok-free.app/api/v1/webhooks/paystack
```

After a successful Paystack payment, Paystack sends a webhook to this endpoint. The backend verifies the webhook signature, verifies the transaction with Paystack, credits the wallet, and marks the transaction as successful.

---

## API Documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

---

## Main API Groups

### Authentication

```text
POST /api/v1/auth/signup
POST /api/v1/auth/login
POST /api/v1/auth/logout
POST /api/v1/auth/forgot-password
POST /api/v1/auth/resend-otp
POST /api/v1/auth/reset-password
```

### Users

```text
GET /api/v1/users/me
PUT /api/v1/users/me
```

### Wallet

```text
GET /api/v1/wallets/me
GET /api/v1/wallets/balance
POST /api/v1/wallets/fund
GET /api/v1/wallets/transactions
```

### Transactions

```text
GET /api/v1/transactions
GET /api/v1/transactions/{transactionId}
```

### Loans

```text
POST /api/v1/loans/apply
GET /api/v1/loans/me
GET /api/v1/loans/{loanId}
POST /api/v1/loans/{loanId}/repay
```

### Admin Loans

```text
GET /api/v1/admin/loans
PATCH /api/v1/admin/loans/{loanId}/approve
PATCH /api/v1/admin/loans/{loanId}/disburse
```

### Webhooks

```text
POST /api/v1/webhooks/paystack
```

---

## Testing

Run tests:

```bash
mvn test
```

Build without running tests:

```bash
mvn clean install -DskipTests
```

Run the application:

```bash
mvn spring-boot:run
```

---

## Postman Usage

Recommended environment variables:

```text
baseUrl=http://localhost:8080
accessToken=
adminAccessToken=
loanId=
transactionId=
paymentReference=
```

Protected user endpoints should use:

```text
Bearer {{accessToken}}
```

Admin endpoints should use:

```text
Bearer {{adminAccessToken}}
```

---

## Security Notes

- Passwords are hashed with BCrypt.
- JWT is used for stateless authentication.
- Admin endpoints are role-protected.
- Paystack webhook requests are verified using signature validation.
- Wallet balance is credited only after Paystack payment verification.
- Duplicate webhook processing is prevented using transaction status and database locking.
- Notification delivery is asynchronous through RabbitMQ.
- Secret keys must not be committed to GitHub.

---

## Local Development Notes

For local development, use logging providers:

```text
SMS_PROVIDER=logging
EMAIL_PROVIDER=logging
```

For real integrations:

```text
SMS_PROVIDER=termii
EMAIL_PROVIDER=javamail
```

Required secrets should be supplied through environment variables, not hardcoded into `application.yml`.

---

## Project Status

Implemented:

- Authentication
- Forgot/reset password with OTP
- Logout endpoint
- User profile update
- Wallet auto-creation
- Wallet funding
- Paystack integration
- Paystack webhook
- Transaction logging
- Loan application
- Loan approval
- Loan disbursement
- Loan repayment
- Repayment schedule tracking
- Scheduled repayment reminders
- Overdue/defaulted loan handling
- RabbitMQ async notifications
- Notification logs
- SMS provider abstraction
- Termii SMS support
- Email provider abstraction
- JavaMail support
- Swagger/OpenAPI documentation
- Docker setup

---

## Possible Future Improvements

- Add token blacklist for stronger JWT logout
- Add refresh tokens
- Add more admin roles and permissions using database tables
- Add full audit logs for admin actions
- Add notification retry mechanism
- Add payment reconciliation dashboard
- Add integration tests with Testcontainers
- Add CI/CD pipeline
- Add production-ready monitoring and metrics

---

## Database Scripts

Database reference scripts are available in:


```text 
database/schema.sql
database/sample-data.sql
```

## Author

Tosin Ajibade