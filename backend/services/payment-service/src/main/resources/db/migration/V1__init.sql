CREATE TABLE payment_orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    razorpay_order_id VARCHAR(255) NOT NULL UNIQUE,
    razorpay_payment_id VARCHAR(255),
    razorpay_signature VARCHAR(255),
    amount_in_paise BIGINT NOT NULL,
    currency VARCHAR(10) NOT NULL,
    plan_type VARCHAR(20) NOT NULL,
    receipt_id VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_order_razorpay_id ON payment_orders(razorpay_order_id);
CREATE INDEX idx_order_user_id ON payment_orders(user_id);
CREATE INDEX idx_order_status ON payment_orders(status);

CREATE TABLE subscriptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    plan_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    period_start TIMESTAMP,
    period_end TIMESTAMP,
    cancelled_at TIMESTAMP,
    last_payment_order_id BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_sub_status ON subscriptions(status);
CREATE INDEX idx_sub_period_end ON subscriptions(period_end);
