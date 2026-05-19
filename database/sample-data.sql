-- ==========================================================
-- Fintech Loan Wallet API - Sample Queries
-- ==========================================================
-- This file contains useful queries for verifying local data.
-- The application seeds the default admin user automatically.
-- ==========================================================

-- View users
SELECT
    id,
    full_name,
    email,
    phone_number,
    role,
    status,
    created_at
FROM users
ORDER BY created_at DESC;

-- View wallets
SELECT
    id,
    user_id,
    balance,
    currency,
    status,
    created_at
FROM wallets
ORDER BY created_at DESC;

-- View transactions
SELECT
    id,
    user_id,
    wallet_id,
    type,
    amount,
    status,
    reference,
    provider_reference,
    created_at
FROM transactions
ORDER BY created_at DESC;

-- View loans
SELECT
    id,
    user_id,
    loan_amount,
    interest_rate,
    duration_days,
    status,
    total_repayable_amount,
    amount_repaid,
    due_date,
    approved_at,
    disbursed_at,
    repaid_at,
    created_at
FROM loans
ORDER BY created_at DESC;

-- View repayment schedules
SELECT
    id,
    loan_id,
    installment_number,
    amount_due,
    amount_paid,
    due_date,
    status,
    created_at
FROM loan_repayment_schedules
ORDER BY created_at DESC;

-- View notification logs
SELECT
    id,
    user_id,
    type,
    channel,
    status,
    recipient,
    reference,
    sent_at,
    failure_reason,
    created_at
FROM notification_logs
ORDER BY created_at DESC;

-- Force repayment schedules to become overdue for scheduler testing
-- UPDATE loan_repayment_schedules
-- SET due_date = CURRENT_DATE - INTERVAL '1 day',
--     status = 'PENDING'
-- WHERE status <> 'PAID';

-- Force repayment schedules to be due tomorrow for reminder testing
-- UPDATE loan_repayment_schedules
-- SET due_date = CURRENT_DATE + INTERVAL '1 day',
--     status = 'PENDING'
-- WHERE status <> 'PAID';