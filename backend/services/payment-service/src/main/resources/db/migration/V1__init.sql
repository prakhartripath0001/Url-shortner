-- V1__init.sql
-- Initial schema for the Payment Service (MySQL version)

CREATE TABLE payment_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    razorpay_order_id VARCHAR(255) NOT NULL UNIQUE,
    razorpay_payment_id VARCHAR(255),
    razorpay_signature VARCHAR(255),
    amount_in_paise BIGINT NOT NULL,
    currency VARCHAR(10) NOT NULL,
    plan_type VARCHAR(20) NOT NULL,
    receipt_id VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_razorpay_id ON payment_orders(razorpay_order_id);
CREATE INDEX idx_order_user_id ON payment_orders(user_id);
CREATE INDEX idx_order_status ON payment_orders(status);

CREATE TABLE subscriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    plan_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    period_start TIMESTAMP NULL,
    period_end TIMESTAMP NULL,
    cancelled_at TIMESTAMP NULL,
    last_payment_order_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_sub_status ON subscriptions(status);
CREATE INDEX idx_sub_period_end ON subscriptions(period_end);
