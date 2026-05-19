# Database Scripts

This project uses PostgreSQL and Spring Data JPA.

For local development, tables are automatically created/updated by Hibernate using:

```yaml
spring.jpa.hibernate.ddl-auto=update
docker compose up -d postgres
Database: koins_db
Username: koins_user
Password: koins_password
Host: localhost
Port: 5433

---

## 3.2 `database/schema.sql`

Create:

```text
database/schema.sql
-- ==========================================================
-- Fintech Loan Wallet API - PostgreSQL Schema Reference
-- ==========================================================
-- Note:
-- The application uses Spring Data JPA/Hibernate for schema generation
-- during local development. This file is provided as a reference database
-- script for reviewers and manual setup.
-- ==========================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =========================
-- USERS
-- =========================
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(30) UNIQUE,
    password VARCHAR(255) NOT NULL,
    bvn_or_nin VARCHAR(50),
    role VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    otp VARCHAR(20),
    otp_expires_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_phone_number ON users(phone_number);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- =========================
-- WALLETS
-- =========================
CREATE TABLE IF NOT EXISTS wallets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    balance NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(10) NOT NULL DEFAULT 'NGN',
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_wallets_user_id ON wallets(user_id);
CREATE INDEX IF NOT EXISTS idx_wallets_status ON wallets(status);

-- =========================
-- TRANSACTIONS
-- =========================
CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    wallet_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    reference VARCHAR(150) NOT NULL UNIQUE,
    provider_reference VARCHAR(150),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_wallet_id ON transactions(wallet_id);
CREATE INDEX IF NOT EXISTS idx_transactions_reference ON transactions(reference);
CREATE INDEX IF NOT EXISTS idx_transactions_status ON transactions(status);
CREATE INDEX IF NOT EXISTS idx_transactions_type ON transactions(type);

-- =========================
-- LOANS
-- =========================
CREATE TABLE IF NOT EXISTS loans (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    loan_amount NUMERIC(19, 2) NOT NULL,
    interest_rate NUMERIC(5, 2) NOT NULL,
    duration_days INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_repayable_amount NUMERIC(19, 2) NOT NULL,
    amount_repaid NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    due_date DATE,
    approved_at TIMESTAMP,
    disbursed_at TIMESTAMP,
    repaid_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_loans_user_id ON loans(user_id);
CREATE INDEX IF NOT EXISTS idx_loans_status ON loans(status);
CREATE INDEX IF NOT EXISTS idx_loans_due_date ON loans(due_date);

-- =========================
-- LOAN REPAYMENT SCHEDULES
-- =========================
CREATE TABLE IF NOT EXISTS loan_repayment_schedules (
    id UUID PRIMARY KEY,
    loan_id UUID NOT NULL,
    installment_number INTEGER NOT NULL,
    amount_due NUMERIC(19, 2) NOT NULL,
    amount_paid NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    due_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_repayment_schedule_loan_id ON loan_repayment_schedules(loan_id);
CREATE INDEX IF NOT EXISTS idx_repayment_schedule_due_date ON loan_repayment_schedules(due_date);
CREATE INDEX IF NOT EXISTS idx_repayment_schedule_status ON loan_repayment_schedules(status);

-- =========================
-- NOTIFICATION LOGS
-- =========================
CREATE TABLE IF NOT EXISTS notification_logs (
    id UUID PRIMARY KEY,
    user_id UUID,
    reference VARCHAR(255),
    type VARCHAR(100) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    message TEXT NOT NULL,
    failure_reason TEXT,
    sent_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_notification_logs_user_id ON notification_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_notification_logs_type ON notification_logs(type);
CREATE INDEX IF NOT EXISTS idx_notification_logs_status ON notification_logs(status);
CREATE INDEX IF NOT EXISTS idx_notification_logs_reference ON notification_logs(reference);

-- =========================
-- OPTIONAL FOREIGN KEYS
-- =========================
-- These are optional for local development if Hibernate manages schema.
-- Uncomment if running schema manually and you want DB-level constraints.

-- ALTER TABLE wallets
-- ADD CONSTRAINT fk_wallets_user
-- FOREIGN KEY (user_id) REFERENCES users(id);

-- ALTER TABLE transactions
-- ADD CONSTRAINT fk_transactions_user
-- FOREIGN KEY (user_id) REFERENCES users(id);

-- ALTER TABLE transactions
-- ADD CONSTRAINT fk_transactions_wallet
-- FOREIGN KEY (wallet_id) REFERENCES wallets(id);

-- ALTER TABLE loans
-- ADD CONSTRAINT fk_loans_user
-- FOREIGN KEY (user_id) REFERENCES users(id);

-- ALTER TABLE loan_repayment_schedules
-- ADD CONSTRAINT fk_repayment_schedule_loan
-- FOREIGN KEY (loan_id) REFERENCES loans(id);

-- ALTER TABLE notification_logs
-- ADD CONSTRAINT fk_notification_logs_user
-- FOREIGN KEY (user_id) REFERENCES users(id);